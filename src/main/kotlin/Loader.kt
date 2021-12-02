import javassist.*

class TraceLoader(private val jar: String) {
  var verbose: Boolean = false

  fun run(args: List<String>) {
    val classPool = ClassPool.getDefault()
    classPool.insertClassPath(jar)
    val classLoader = Loader(classPool)

    val translator = TraceTranslator()
    translator.verbose = verbose
    classLoader.addTranslator(classPool, translator)

    val mainClass = "com.company.Main"
    if (verbose) {
      println("Main class: $mainClass")
    }

    classLoader.run(mainClass, args.toTypedArray())
  }
}

class TraceTranslator : Translator {
  var verbose: Boolean = false

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
    for (method in ctClass.methods) {
      // Do not modify abstract or nativ class
      val modifiers = method.modifiers
      if (!Modifier.isAbstract(modifiers) && !Modifier.isNative(modifiers)) {
        if (verbose) {
          println("Method: ${method.longName}")
          method.insertAfter("{ System.out.println(\"Modified: ${method.longName}\"); }")
        }
        val converter = TraceConverter()
        // TODO: make convert here
        method.instrument(converter)
      }
    }

    if (verbose) {
      println("---------")
    }
  }
}

class TraceConverter : CodeConverter() {
  var verbose: Boolean = false
}
