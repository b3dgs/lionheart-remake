function Invoke-BuildApp ([hashtable]$ctx) {

  $work = Join-Path $ctx.TempDir "jpackage-tmp_$(New-Guid)"
  New-Item -ItemType Directory -Force -Path $work | Out-Null

  # jre path resolved here so it can be cleaned up in finally even if jpackage fails.
  $jre = Join-Path $ctx.Root "build/jre"

  try {
    # Platform-specific temp dir and assets
    if ($ctx.IsWin) {
      $env:TEMP   = $work
      $env:TMP    = $work
      $icon       = "$($ctx.Root)/java/lionheart-pc/src/main/resources/com/b3dgs/lionheart/icon.ico"
      $splash     = '-splash:app/splash.png'
    } else {
      $env:TMPDIR = $work
      $icon       = "$($ctx.Root)/java/lionheart-pc/src/main/resources/com/b3dgs/lionheart/icon-256.png"
      $splash     = '-splash:../lib/app/splash.png'
    }

    # Prepare jpackage input directory
    $inputDir = Join-Path $ctx.Root "java/lionheart-pc/target/jpackage-input"
    Remove-Item $inputDir -Recurse -Force -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Path $inputDir | Out-Null
    Copy-Item "$($ctx.Root)/java/lionheart-pc/target/lionheart-pc-$($ctx.AppVersion).jar" $inputDir
    Copy-Item "$($ctx.Root)/java/lionheart-pc/src/main/resources/com/b3dgs/lionheart/splash.png" $inputDir

    Invoke-Proc -Name 'jpackage' -Exe 'jpackage' -Arguments @(
      '--type',          'app-image'
      '--name',          $ctx.AppName
      '--app-version',   $ctx.BaseVersion
      '--vendor',        'Byron 3D Games Studio'
      '--description',   'https://lionheart.b3dgs.com'
      '--copyright',     'Copyright © 2026 (GPL v3)'
      '--input',         $inputDir
      '--main-jar',      "lionheart-pc-$($ctx.AppVersion).jar"
      '--main-class',    'com.b3dgs.lionheart.Main'
      '--java-options',  $splash
      '--icon',          $icon
      '--runtime-image', $jre
      '--dest',          (Join-Path $ctx.Root "build")
    )

    # Windows: sign the produced executable
    if ($ctx.IsWin) {
      $exe = Join-Path $ctx.Root "build/$($ctx.AppName)/$($ctx.AppName).exe"
      Invoke-Proc       -Name 'attrib -r' -Exe 'attrib' -Arguments @('-r', $exe)
      Invoke-SignFiles  -Files @($exe) -CertBase64 $ctx.KeystoreB64 -CertPwd $ctx.KeystorePass -TempDir $ctx.TempDir
      Invoke-Proc       -Name 'attrib +r' -Exe 'attrib' -Arguments @('+r', $exe)
    }

  } finally {
    Remove-Item $work -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item $jre  -Recurse -Force -ErrorAction SilentlyContinue
  }
}
