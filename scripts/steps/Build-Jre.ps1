function Invoke-BuildJre ([hashtable]$ctx) {

  $modules = @(
    'java.base', 'java.xml', 'java.desktop',
    'java.logging', 'java.prefs',
    'jdk.xml.dom', 'jdk.unsupported'
  ) -join ','

  $buildPath = Join-Path $ctx.Root "build"
  Remove-Item $buildPath -Recurse -Force -ErrorAction SilentlyContinue

  $out = Join-Path $ctx.Root "build/jre"
  Write-Host "  Output: $out"

  Invoke-Proc -Name 'jlink' -Exe 'jlink' -Arguments @(
    '--compress=0',
    '--strip-debug',
    '--no-header-files',
    '--no-man-pages',
    "--add-modules=$modules",
    "--output=$out"
  )
}
