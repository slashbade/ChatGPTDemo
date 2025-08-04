$componentPackPath = "C:\Eclipse\plugins\net.rim.ejde.componentpack7.1.0_7.1.0.10\components\"
$simulatorPath = Join-Path $componentPackPath "simulator"
Set-Location $simulatorPath
fledge.exe /handheld=9900 /app-param=JvmAlxConfigFile:9900.xml /pin=0x2100000A /data-port=0x4d44 /data-port=0x4d4e /session=9900 /app-param=launch=ChatGPTDemo /app="$simulatorPath\Jvm.dll" /app-param=DisableRegistration