# Temporarily change path to use local java instead of system one
$javaHomePath = "C:\Program Files (x86)\Java\jdk1.6.0_45"
$componentPackPath = "C:\Eclipse\plugins\net.rim.ejde.componentpack7.1.0_7.1.0.10\components\"
$simulatorPath = Join-Path $componentPackPath "simulator"

$env:JAVA_HOME = $javaHomePath
$env:PATH = "$javaHomePath\bin;" + $env:PATH
$env:PATH = "$componentPackPath\bin;" + $env:PATH

$importPath = Join-Path $componentPackPath "lib\net_rim_api.jar"
$appName = "ChatGPTDemo"
$codeName = "deliverables\Standard\7.1.0\$appName"
$rapcFilePath = $codeName + ".rapc"
. .\scripts\generateDotRapc.ps1
# Get all source files in the src directory
$sourceFiles = Get-ChildItem -Path "src" -Filter "*.java" -Recurse
$resourceFiles = Get-ChildItem -Path "res" -Recurse

& rapc.exe `
    -quiet `
    -codename="$codeName" `
    -midlet `
    -import="$importPath" `
    $rapcFilePath `
    $sourceFiles `
    $resourceFiles


# Copy all files under deliverables/7.1.0/ to simulator directory
Copy-Item -Path "deliverables\Standard\7.1.0\*" -Destination "$simulatorPath" -Recurse
