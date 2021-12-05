import kotlinx.cli.*

fun main(cliArgs: Array<String>) {
  val parser = ArgParser("jmtrace")
  val jar by parser.option(
    ArgType.String,
    shortName = "jar",
    description = "Input jar package"
  ).required()
  val verbose by parser.option(
    ArgType.Boolean,
    shortName = "v",
    description = "Enable verbose log"
  ).default(false)
  val args by parser.argument(ArgType.String, description = "Args for running jar").optional().vararg()

  parser.parse(cliArgs)

  if (verbose) {
    println("Jar: $jar")
    println("Run: $args")
  }

  val traceLoader = TraceLoader(jar)
  traceLoader.verbose = verbose
  traceLoader.run(args)
}
