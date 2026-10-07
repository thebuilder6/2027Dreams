# Shows who holds what. Read this before concluding that a build or sim is stuck.
#
#   powershell -File tools/lock/status.ps1
param([switch]$AsJson)
$ErrorActionPreference = "Stop"
Import-Module (Join-Path $PSScriptRoot "Lock.psm1") -Force
$rows = Get-LockStatus
if ($AsJson) { $rows | ConvertTo-Json -Depth 3; exit 0 }

Write-Output ("{0,-13} {1,-20} {2,-26} {3,-10} {4,-9} {5}" -f `
    "RESOURCE", "STATE", "OWNER", "PIDS", "HELD(s)", "REASON")
Write-Output ("-" * 110)
foreach ($r in $rows) {
  Write-Output ("{0,-13} {1,-20} {2,-26} {3,-10} {4,-9} {5}" -f `
      $r.Resource, $r.State, $r.Owner, $r.Pids, $r.HeldSec, $r.Reason)
}

$dash = Test-DashboardRunning
if ($dash.Count -gt 0) {
  Write-Output ""
  Write-Warning ("A dashboard is running ({0}). Fine for a GUI sim, but it shares the robot's NT namespace" -f `
      (($dash | ForEach-Object { "$($_.ProcessName)#$($_.Id)" }) -join ', '))
  Write-Warning "including the Auto Mission chooser -- keep it away from headless measurements (see docs/COORDINATION.md)."
}
exit 0
