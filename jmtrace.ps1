$JarPath = $PSScriptRoot + "\build\libs\jmtrace-0.1.0-all.jar"

if (-not(Test-Path -Path $JarPath -PathType Leaf)) {
    echo "Build Jmtrace ..."
    Push-Location $JarPath
    .\gradlew build fatJar
    Pop-Location
}

java -jar $JarPath $args
