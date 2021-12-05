# Jmtrace

Trace Java memory access.

## Installation

Build Jmtrace.

```bash
./gradlew build fatJar
```

For Windows users, you can add `path\to\jmtrace\bin` to your PATH environment variable. Also, you can add the following config to your PowerShell `$PROFILE` file (like `.bashrc` in Linux). It will also register the command `jmtrace` globally.

```powershell
Set-Alias -Name jmtrace -Value "path\to\jmtrace\bin\jmtrace.ps1"
```

For Linux / MacOS users, you can `path/to/jmtrace/bin` direcotry to to your PATH environment variable.

## Usage

```text
$ jmtrace --help
Usage: jmtrace options_list
Arguments: 
    args -> Args for running jar (optional) { String }
Options: 
    --jar, -jar -> Input jar package (always required) { String }
    --verbose, -v [false] -> Enable verbose Log 
    --help, -h -> Usage info
```
