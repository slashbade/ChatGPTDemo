# Generate the .rapc file for the application
Write-Host "Generating .rapc file for $appName"
[xml]$blackberryAppDescriptor = Get-Content -Path "Blackberry_App_Descriptor.xml"
$lines = @(
  "MIDlet-Name: $appName"
  "MIDlet-Version: $($blackberryAppDescriptor.Properties.General.Version)"
  "MIDlet-Vendor: $($blackberryAppDescriptor.Properties.General.Vendor)"
  "MIDlet-Jar-URL: $appName.jar"
  "MIDlet-Jar-Size: 0"
  "MicroEdition-Profile: MIDP-2.0"
  "MicroEdition-Configuration: CLDC-1.1"
  "MIDlet-1: $($blackberryAppDescriptor.Properties.General.Title),$($blackberryAppDescriptor.Properties.Resources.Icons.Icon.CanonicalFileName)"
  "RIM-MIDlet-Flags-1: 1"
)
$rapcFilePath = Join-Path "deliverables\Standard\7.1.0" "$appName.rapc"
Set-Content -Path $rapcFilePath -Value $lines