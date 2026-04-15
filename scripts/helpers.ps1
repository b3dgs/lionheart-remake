# ─────────────────────────────────────────────────────────────────────────────
# Helpers shared across all delivery steps
# ─────────────────────────────────────────────────────────────────────────────

# Prints a step header and re-throws failures with a clear message.
function Step ([string]$Name, [scriptblock]$Block) {
  Write-Host "`n── $Name" -ForegroundColor Cyan
  try   { & $Block }
  catch { Write-Host "✖ '$Name' failed: $_" -ForegroundColor Red; throw }
}

# Runs an external process and throws on non-zero exit code.
function Invoke-Proc {
  param(
    [Parameter(Mandatory)][string]  $Name,
    [Parameter(Mandatory)][string]  $Exe,
    [string[]]                      $Arguments = @(),
    [string]                        $WorkDir   = ''
  )

  Write-Host "`n── $Name"
  Write-Host "   $Exe $($Arguments -join ' ')"

  if ($WorkDir) { Push-Location $WorkDir }
  try {
    & $Exe @Arguments
    if ($LASTEXITCODE -ne 0) { throw "$Name failed (exit $LASTEXITCODE)" }
  } finally {
    if ($WorkDir) { Pop-Location }
  }
}

# Locates the highest-versioned signtool.exe in the Windows SDK.
function Get-SignTool {
  foreach ($arch in @('x64', 'x86')) {
    $found = Get-ChildItem "C:\Program Files (x86)\Windows Kits\10\bin\*\$arch\signtool.exe" `
               -ErrorAction SilentlyContinue |
             Sort-Object FullName -Descending |
             Select-Object -First 1 -ExpandProperty FullName
    if ($found) { return $found }
  }
  throw "signtool.exe not found in Windows Kits"
}

# Signs one or more files with a PFX certificate (Windows only).
# Skips gracefully when no certificate is provided.
function Invoke-SignFiles {
  param(
    [string[]] $Files,
    [string]   $CertBase64,
    [string]   $CertPwd,
    [string]   $TempDir
  )

  if (-not $CertBase64) {
    Write-Warning "Signing skipped: no certificate provided"
    return
  }

  $pfxPath  = Join-Path $TempDir "cert_$(New-Guid).pfx"
  [IO.File]::WriteAllBytes($pfxPath, [Convert]::FromBase64String($CertBase64))
  $signtool = Get-SignTool

  try {
    foreach ($file in $Files) {
      if (-not (Test-Path $file)) { throw "Missing file to sign: $file" }
      Write-Host "  Signing: $file"
      Invoke-Proc -Name "signtool ($([IO.Path]::GetFileName($file)))" `
                  -Exe $signtool `
                  -Arguments @(
                    'sign',
                    '/f', $pfxPath,
                    '/p', $CertPwd,
                    '/fd', 'SHA256',
                    '/tr', 'http://timestamp.digicert.com',
                    '/td', 'SHA256',
                    '/a', $file
                  )
    }
  } finally {
    Remove-Item $pfxPath -Force -ErrorAction SilentlyContinue
  }
}

# Compresses $SourceDir into an archive named $Target.<ext> using the
# platform-appropriate tool (7z on Windows, zip on macOS, tar+xz on Linux).
# Optionally writes a .sha256sum file (Windows only).
function Compress-Artifact {
  param(
    [Parameter(Mandatory)][string] $Target,
    [Parameter(Mandatory)][string] $SourceDir,
    [bool]   $IsWin,
    [bool]   $IsMac,
    [bool]   $WithSha256 = $false,
    [string] $Label      = ''
  )

  $tag = if ($Label) { " ($Label)" } else { '' }

  if ($IsWin) {
    $zip = "$Target.zip"
    Remove-Item $zip -Force -ErrorAction SilentlyContinue
    Invoke-Proc -Name "7z$tag" -Exe '7z' -Arguments @(
      'a', '-tzip', '-m0=deflate', '-mx=9', $zip, $SourceDir
    )
    if ($WithSha256) {
      $hash = Get-FileHash $zip -Algorithm SHA256
      "$($hash.Hash) *$Target.zip" | Out-File "$zip.sha256sum" -Encoding ascii
    }
  } elseif ($IsMac) {
    Invoke-Proc -Name "zip$tag" -Exe 'zip' -Arguments @('-9', '-r', "$Target.zip", $SourceDir)
  } else {
    $tar = "$Target.tar"
    Invoke-Proc -Name "tar$tag" -Exe 'tar' -Arguments @('-cf', $tar, $SourceDir)
    Invoke-Proc -Name "xz$tag"  -Exe 'xz'  -Arguments @('-1ze', '-T0', $tar)
  }
}
