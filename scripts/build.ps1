# Temporarily change path to use local java instead of system one
$javaHomePath = "C:\Program Files (x86)\Java\jdk1.6.0_45"
$env:JAVA_HOME = $javaHomePath
$env:PATH = "$javaHomePath\bin;" + $env:PATH
Write-Host "Using Java from: $env:JAVA_HOME"
$componentPackPath = "C:\Eclipse\plugins\net.rim.ejde.componentpack7.1.0_7.1.0.10\components\"
$rapcPath = Join-Path $componentPackPath "bin\rapc.exe"
$importPath = Join-Path $componentPackPath "lib\net_rim_api.jar"
$appName = "ChatGPTDemo"
$codeName = "deliverables\Standard\7.1.0\$appName"
. .\scripts\generateDotRapc.ps1
# Get all source files in the src directory
$sourceFiles = Get-ChildItem -Path "src" -Filter "*.java" -Recurse
$resourceFiles = Get-ChildItem -Path "res" -Recurse

& $rapcPath -quiet codename="$codeName" -midlet import="$importPath" "$codeName.rapc" $sourceFiles $resourceFiles
