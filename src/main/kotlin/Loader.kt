import javassist.ClassPool
import javassist.Loader
import javassist.Modifier
import javassist.Translator
import java.util.jar.JarFile

class TraceLoader(private val jar: String) {
  var verbose: Boolean = false

  fun run(args: List<String>) {
    val classPool = ClassPool.getDefault()
    classPool.insertClassPath(jar)
    val classLoader = Loader(classPool)

    val translator = TraceTranslator()
    translator.verbose = verbose
    classLoader.addTranslator(classPool, translator)

    val mainClass = getMainClass()

    classLoader.run(mainClass, args.toTypedArray())
  }

  private fun getMainClass(): String {
    val jarFile = JarFile(jar)
    val manifest = jarFile.manifest
    val classname = manifest.mainAttributes.getValue("Main-Class")
    if (verbose) {
      println("Main class: $classname")
    }
    return classname
  }
}

class TraceTranslator : Translator {
  var verbose: Boolean = false

  val exclude = listOf(ArrayConverter.Classname)

  override fun start(pool: ClassPool) {
    if (verbose) {
      println("--- Start running JAR ---")
    }
  }

  override fun onLoad(pool: ClassPool, classname: String) {
    if (verbose) {
      println("--- Load class $classname ---")
    }

    val ctClass = pool.get(classname)

    if (exclude.contains(ctClass.name)) {
      return
    }

    for (method in ctClass.methods) {
      // Do not modify abstract or nativ class
      val modifiers = method.modifiers
      if (!Modifier.isAbstract(modifiers) && !Modifier.isNative(modifiers)) {
        if (verbose) {
          println("Method: ${method.longName}")
          method.insertAfter("{ System.out.println(\"Modified: ${method.longName}\"); }")
        }

        val arrayConverter = ArrayConverter(pool)
        method.instrument(arrayConverter)
      }
    }

    if (verbose) {
      println("---------")
    }
  }
}
