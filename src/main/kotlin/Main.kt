import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import kotlinx.cli.vararg

fun main(cliArgs: Array<String>) {
  val parser = ArgParser("jmtrace")
  val jar by parser.option(
    ArgType.String,
    shortName = "jar",
    description = "Input jar package"
  ).required()
  val args by parser.argument(ArgType.String).vararg()

  parser.parse(cliArgs)

  println("Jar: $jar")

  println("Run: $args")
}
