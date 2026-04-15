function Invoke-ResolveVersion ([hashtable]$ctx) {

  $pom = Join-Path $ctx.Root "pom.xml"
  [xml]$xml = Get-Content $pom -Raw

  $v = $xml.project.version
  if (-not $v) { $v = $xml.project.parent.version }
  if (-not $v) { throw "Version not found in pom.xml" }
  if ($v -match '\$\{.*\}') { throw "Unresolved Maven placeholder in version: $v" }

  # Write back into the shared context so later steps can consume these values.
  $ctx.AppVersion  = $v.Trim()
  $ctx.BaseVersion = $ctx.AppVersion -replace '-.*$', ''
  $ctx.AppVersionI = $ctx.BaseVersion -replace '\.', ','

  Write-Host "  Version : $($ctx.AppVersion)"
  Write-Host "  VersionI: $($ctx.AppVersionI)"
}
