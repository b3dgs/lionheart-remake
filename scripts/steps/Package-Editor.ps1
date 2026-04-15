function Invoke-PackageEditor ([hashtable]$ctx) {

  $targetDir = "$($ctx.AppName)-$($ctx.AppVersion)"
  $target    = "${targetDir}_$($ctx.Platform)"
  $work      = Join-Path $ctx.Root "build/$targetDir"
  $base      = Join-Path $ctx.Root "build"
  $editorDst = Join-Path $work "data/editor"

  New-Item -ItemType Directory -Force -Path `
    (Join-Path $work "data/assets"),
    (Join-Path $editorDst "plugins") | Out-Null

  $editorSrc = "$($ctx.Root)/releng/com.b3dgs.lionheart.editor.product/target/products/com.b3dgs.lionheart.editor.product"

  if ($ctx.IsWin) {
    Copy-Item "$editorSrc/win32/win32/x86_64/*" $editorDst -Recurse -Force
  } else {
    Copy-Item "$editorSrc/linux/gtk/x86_64/*"   $editorDst -Recurse -Force
  }

  Push-Location $base
  try {
    # Win=7z  |  macOS=zip  |  Linux=tar+xz  (label "editor" appears in log names)
    Compress-Artifact -Target "${target}_editor" -SourceDir $targetDir `
      -IsWin $ctx.IsWin -IsMac $ctx.IsMac -Label 'editor'
  } finally {
    Pop-Location
  }

  Remove-Item $work -Recurse -Force -ErrorAction SilentlyContinue
}
