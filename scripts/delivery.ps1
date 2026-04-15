#Requires -Version 7.0
[CmdletBinding()]
param(
  [Parameter(Mandatory)]
  [ValidateSet('win32-x86','win32-x86-64','linux-x86-64','linux-x86-32','macos-x86_64','macos-aarch64','android-arm')]
  [string]$Platform,
  [string]$AppName       = 'lionheart-remake',
  [string]$Architecture  = 'x64',
  [string]$GpgB64        = $env:GPG_B64,
  [string]$GpgPassphrase = $env:GPG_PASSPHRASE,
  [string]$KeystoreB64   = $env:KEYSTORE_B64,
  [string]$KeystoreAlias = $env:KEYSTORE_ALIAS,
  [string]$KeystorePass  = $env:KEYSTORE_PASS
)

$ErrorActionPreference = 'Stop'

# ── Environment ───────────────────────────────────────────────────────────────
$IsWin = ($env:RUNNER_OS -eq 'Windows') -or $IsWindows
$IsLin = ($env:RUNNER_OS -eq 'Linux')   -or $IsLinux
$IsMac = ($env:RUNNER_OS -eq 'macOS')   -or $IsMacOS

$Root = if ($env:GITHUB_WORKSPACE) {
    $env:GITHUB_WORKSPACE
} elseif ($PSScriptRoot) {
    Resolve-Path "$PSScriptRoot\.." | Select-Object -ExpandProperty Path
} else {
    (Get-Location).Path
}

$TempDir = $env:RUNNER_TEMP ?? $env:TEMP ?? [IO.Path]::GetTempPath()

# ── Dot-source helpers and steps ──────────────────────────────────────────────
. "$Root/scripts/helpers.ps1"
. "$Root/scripts/steps/Install-Dependencies.ps1"
. "$Root/scripts/steps/Resolve-Version.ps1"
. "$Root/scripts/steps/Compile-Java.ps1"
. "$Root/scripts/steps/Build-Jre.ps1"
. "$Root/scripts/steps/Build-App.ps1"
. "$Root/scripts/steps/Make-Archive.ps1"
. "$Root/scripts/steps/Package-Editor.ps1"

# ── Shared context (hashtable = reference type; steps can write back into it) ─
$ctx = @{
  Platform      = $Platform
  AppName       = $AppName
  Architecture  = $Architecture
  GpgB64        = $GpgB64
  GpgPassphrase = $GpgPassphrase
  KeystoreB64   = $KeystoreB64
  KeystoreAlias = $KeystoreAlias
  KeystorePass  = $KeystorePass
  IsWin         = $IsWin
  IsLin         = $IsLin
  IsMac         = $IsMac
  Root          = $Root
  TempDir       = $TempDir
  # Populated by Resolve-Version, consumed by later steps:
  AppVersion    = $null
  BaseVersion   = $null
  AppVersionI   = $null
}

# ── Pipeline ──────────────────────────────────────────────────────────────────
# NOTE: Step{} blocks live here (script scope) so $ctx is directly visible.
#       Each function receives $ctx as a parameter.
Step "Install dependencies" { Invoke-InstallDeps    $ctx }
Step "Resolve version"      { Invoke-ResolveVersion $ctx }
Step "Compile Java"         { Invoke-CompileJava    $ctx }
Step "Build JRE"            { Invoke-BuildJre       $ctx }
Step "Build App"            { Invoke-BuildApp       $ctx }
Step "Make archive"         { Invoke-MakeArchive    $ctx }
Step "Package editor"       { Invoke-PackageEditor  $ctx }

Write-Host "`n✔ DONE $AppName $($ctx.AppVersion) ($Platform)" -ForegroundColor Green
