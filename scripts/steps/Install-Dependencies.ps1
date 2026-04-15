function Invoke-InstallDeps ([hashtable]$ctx) {

  if (-not $ctx.IsLin) {
    Write-Host "  skip (not Linux)"
    return
  }

  $pkgs = @(
    'build-essential', 'xvfb', 'libasound2-dev', 'alsa-utils', 'libxtst6', 'libxrender1',
    'libxi6', 'libxext6', 'libx11-6', 'libgtk-3-0', 'libglib2.0-0',
    'libpango-1.0-0', 'libcairo2', 'libgdk-pixbuf-2.0-0',
    'gnupg', 'ca-certificates'
  )

  if ($ctx.Architecture -eq 'x86') { $pkgs += 'gcc-multilib' }

  $pkgList = $pkgs -join ' '

  Invoke-Proc -Name 'apt update'  -Exe 'bash' -Arguments @('-c', 'sudo apt-get update -y')
  Invoke-Proc -Name 'apt install' -Exe 'bash' -Arguments @(
    '-c', "sudo DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends $pkgList"
  )
}
