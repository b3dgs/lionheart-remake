function Invoke-MakeArchive ([hashtable]$ctx) {

  $targetDir = $ctx.AppName
  $target    = "$($ctx.AppName)-$($ctx.AppVersion)_$($ctx.Platform)"
  $build     = Join-Path $ctx.Root "build"

  Push-Location $build
  try {
    if ($ctx.Platform -match 'android') {
      # Copy the signed APK to a predictable name, then zip the app folder.
      $apk = Join-Path $build "$targetDir/app-release-signed.apk"
      Copy-Item $apk (Join-Path $build "$($ctx.AppName)-$($ctx.AppVersion).apk") -Force
      Invoke-Proc -Name 'zip (android)' -Exe 'zip' -Arguments @('-9', '-r', "$target.zip", $targetDir)
    } else {
      # Win=7z+sha256  |  macOS=zip  |  Linux=tar+xz
      Compress-Artifact -Target $target -SourceDir $targetDir `
        -IsWin $ctx.IsWin -IsMac $ctx.IsMac -WithSha256 $ctx.IsWin
    }
  } finally {
    Pop-Location
  }

  Remove-Item (Join-Path $build $targetDir) -Recurse -Force -ErrorAction SilentlyContinue
}
