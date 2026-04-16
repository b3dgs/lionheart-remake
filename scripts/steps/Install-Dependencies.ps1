function Invoke-InstallDeps ([hashtable]$ctx) {

  if (-not $ctx.IsLin) {
    Write-Host "  skip (not Linux)"
    return
  }

  # enable 32-bit architecture support if needed
  if ($ctx.Architecture -eq 'x86') {
    Invoke-Proc -Name 'dpkg add arch' -Exe 'bash' -Arguments @(
      '-c', 'sudo dpkg --add-architecture i386'
    )
    Invoke-Proc -Name 'apt update (i386)' -Exe 'bash' -Arguments @(
      '-c', 'sudo apt-get update -y'
    )
  }

  $pkgs = @(
    'build-essential',
    'xvfb',
    'libasound2-dev',
    'alsa-utils',
    'libxtst6',
    'libxrender1',
    'libxi6',
    'libxext6',
    'libx11-6',
    'libgtk-3-0',
    'libglib2.0-0',
    'libpango-1.0-0',
    'libcairo2',
    'libgdk-pixbuf-2.0-0',
    'gnupg',
    'ca-certificates'
  )

  # 32-bit runtime libs
  if ($ctx.Architecture -eq 'x86') {
    $pkgs += @(
      'zlib1g:i386',
      'libstdc++6:i386',
      'libgcc-s1:i386',
      'libx11-6:i386',
      'libxext6:i386',
      'libxrender1:i386',
      'libxtst6:i386',
      'libxi6:i386',
      'libasound2:i386'
    )
  }

  $pkgList = $pkgs -join ' '

  Invoke-Proc -Name 'apt install' -Exe 'bash' -Arguments @(
    '-c', "sudo DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends $pkgList"
  )
}