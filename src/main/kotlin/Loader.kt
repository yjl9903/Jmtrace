import javassist.ClassPool
import javassist.Loader

class TraceLoader(val jar: String) {
  fun run(args: List<String>) {
    val classPool = ClassPool.getDefault()
    classPool.insertClassPath(jar)
    val classLoader = Loader(classPool)
    classLoader.run("com.company.Main", args.toTypedArray())
  }
}
