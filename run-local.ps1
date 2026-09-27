$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$runDir = Join-Path $root '.run'
$jdk = Get-ChildItem (Join-Path $root '.tools') -Directory | Where-Object Name -Like 'jdk-17*' | Select-Object -First 1
$maven = Join-Path $root '.tools\apache-maven-3.9.9\bin\mvn.cmd'
$node = 'C:\Users\Anurag\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe'
$jar = Join-Path $root 'backend\target\library-api-1.0.0.jar'
$passwordLine = Get-Content (Join-Path $runDir 'demo-credentials.txt') | Where-Object { $_ -like 'Password:*' } | Select-Object -First 1
$demoPassword = $passwordLine.Substring('Password:'.Length).Trim()

if (-not $jdk -or -not (Test-Path $maven) -or -not (Test-Path $node)) { throw 'The local Java, Maven, or Node runtime is missing from .tools or the Codex runtime.' }
$env:JAVA_HOME = $jdk.FullName
$env:Path = (Join-Path $env:JAVA_HOME 'bin') + ';' + $env:Path
$env:SPRING_PROFILES_ACTIVE = 'local'
$env:DEMO_LIBRARIAN_EMAIL = 'admin@library.com'
$env:DEMO_LIBRARIAN_PASSWORD = $demoPassword
$env:DEMO_MEMBER_PASSWORD = $demoPassword
$env:CORS_ORIGIN = 'http://localhost:5173'
New-Item -ItemType Directory -Force -Path $runDir,(Join-Path $root 'backend\data') | Out-Null

if (-not (Test-Path $jar)) { & $maven -q -f (Join-Path $root 'backend\pom.xml') -DskipTests package }
if (-not (Get-NetTCPConnection -State Listen -LocalPort 8080 -ErrorAction SilentlyContinue)) {
    Start-Process -FilePath (Join-Path $env:JAVA_HOME 'bin\java.exe') -ArgumentList @('-jar',$jar) -WorkingDirectory (Join-Path $root 'backend') -WindowStyle Hidden -RedirectStandardOutput (Join-Path $runDir 'backend.log') -RedirectStandardError (Join-Path $runDir 'backend-error.log') | Out-Null
}
if (-not (Get-NetTCPConnection -State Listen -LocalPort 5173 -ErrorAction SilentlyContinue)) {
    $env:VITE_API_URL = 'http://localhost:8080/api'
    Start-Process -FilePath $node -ArgumentList @('node_modules/vite/bin/vite.js','--host','127.0.0.1','--port','5173','--strictPort') -WorkingDirectory (Join-Path $root 'frontend') -WindowStyle Hidden -RedirectStandardOutput (Join-Path $runDir 'frontend.log') -RedirectStandardError (Join-Path $runDir 'frontend-error.log') | Out-Null
}
Write-Output 'Library site: http://localhost:5173'
Write-Output 'API: http://localhost:8080'
