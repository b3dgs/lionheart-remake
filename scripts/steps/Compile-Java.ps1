function Invoke-CompileJava ([hashtable]$ctx) {

  # ── Import GPG key ────────────────────────────────────────────────────────
  if ($ctx.GpgB64) {
    $gpgFile = Join-Path $ctx.TempDir "gpg_$(New-Guid).key"
    [IO.File]::WriteAllBytes(
      $gpgFile,
      [Convert]::FromBase64String(($ctx.GpgB64 -replace '\s', ''))
    )
    try {
      Invoke-Proc -Name 'gpg import' -Exe 'gpg' -Arguments @(
        '--batch', '--yes', '--pinentry-mode', 'loopback', '--import', $gpgFile
      )
    } finally {
      Remove-Item $gpgFile -Force -ErrorAction SilentlyContinue
    }
  }

  # ── Write keystore to disk ────────────────────────────────────────────────
  if (-not $ctx.KeystoreB64) { throw "KeystoreB64 missing" }

  $keystore = Join-Path $ctx.Root "keystore.pfx"
  [IO.File]::WriteAllBytes($keystore, [Convert]::FromBase64String(($ctx.KeystoreB64 -replace '\s', '')))
  Write-Host "  Keystore: $keystore"

  # ── Maven ─────────────────────────────────────────────────────────────────
  $platformProfileMap = @{
    "win32-x86"    = "windows-x86"
    "win32-x86-64" = "windows"
    "linux-x86-64" = "linux"
    "linux-x86-32" = "linux"
    "macos-x86-64" = "macos"
    "macos-aarch64" = "macos-arm64"
  }

  $profiles = @(
    if ($ctx.Platform -eq 'android-arm') { 'game' } else { 'release' }
    $platformProfileMap[$ctx.Platform]
  ) | Where-Object { $_ }

  $mvnArgs = @(
    'clean', 'package',
    '-Djdk.xml.totalEntitySizeLimit=10000000',
    '-Djdk.xml.maxGeneralEntitySizeLimit=10000000',
    '-U', '-B',
    "-P$($profiles -join ',')",
    "-Dkeystore=$keystore",
    '-Dstoretype=PKCS12',
    "-Dkeystore.alias=$($ctx.KeystoreAlias)",
    "-Dkeystore.pass=$($ctx.KeystorePass)",
    "-Dkeystore.key=$($ctx.KeystorePass)"
  )

  if ($ctx.IsMac) { $mvnArgs += '-Dtycho.testArgLine=-XstartOnFirstThread' }

  $env:MAVEN_GPG_PASSPHRASE = $ctx.GpgPassphrase

  Invoke-Proc -Name 'Maven' -Exe 'mvn' -Arguments $mvnArgs -WorkDir $ctx.Root

  # ── Gradle (Android only) ─────────────────────────────────────────────────
  if ($ctx.Platform -eq 'android-arm') {
    Invoke-Proc -Name 'Gradle' -Exe './gradlew' -WorkDir $ctx.Root -Arguments @(
      'assembleRelease',
      "-Dkeystore=$keystore",
      '-Dstoretype=PKCS12',
      "-Dkeystore.alias=$($ctx.KeystoreAlias)",
      "-Dkeystore.pass=$($ctx.KeystorePass)",
      "-Dkeystore.key=$($ctx.KeystorePass)"
    )
  }
}
