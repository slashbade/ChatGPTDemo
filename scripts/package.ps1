$componentPackPath = "C:\Eclipse\plugins\net.rim.ejde.componentpack7.1.0_7.1.0.10\components\"
$env:PATH = "$componentPackPath\bin;" + $env:PATH
$importPath = Join-Path $componentPackPath "lib\net_rim_api.jar"
$AppName = "ChatGPTDemo"
& rapc.exe `
    -convertpng `
    -quiet `
    -codename="deliverables\Standard\7.1.0\$AppName" `
    -sourceroot="src;res" `
    -import="$importPath" `
    "deliverables\Standard\7.1.0\$AppName.rapc" `
    "bin"