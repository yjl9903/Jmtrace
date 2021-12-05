import javassist.*
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess
import java.util.jar.JarFile

class TraceLoader(private val jar: String) {
  var verbose: Boolean = false

  fun run(args: List<String>) {
    val classPool = ClassPool.getDefault()
    classPool.insertClassPath(jar)
    val classLoader = Loader(classPool)

    val translator = TraceTranslator(classPool)
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

class TraceTranslator(pool: ClassPool) : Translator {
  var verbose: Boolean = false

  private val arrayConverter = ArrayConverter(pool)

  private val fieldConverter = FieldConverter(pool)

  private val excludeClass = listOf(ArrayConverter.Classname, FieldConverter.Classname)

  private fun isExclude(ctClass: CtClass): Boolean {
    for (excludeClass in excludeClass) {
      if (ctClass.name.startsWith(excludeClass)) {
        return true
      }
    }
    return false
  }

  override fun start(pool: ClassPool) {
    if (verbose) {
      println("--- Start running JAR ---")
    }
  }

  override fun onLoad(pool: ClassPool, classname: String) {
    val ctClass = pool.get(classname)

    if (isExclude(ctClass)) {
      return
    }

    if (verbose) {
      println("--- Load class $classname ---")
    }

    for (method in ctClass.methods) {
      // Do not modify abstract or native class
      val modifiers = method.modifiers
      if (!Modifier.isAbstract(modifiers) && !Modifier.isNative(modifiers)) {
        if (verbose) {
          println("Method: ${method.longName}")
          method.insertAfter("{ System.out.println(\"Modified: ${method.longName}\"); }")
        }

        method.instrument(arrayConverter)

        val reader = HashSet<CtField>()
        val writer = HashSet<CtField>()
        method.instrument(object : ExprEditor() {
          override fun edit(f: FieldAccess) {
            if (f.isReader) {
              reader.add(f.field)
            } else if (f.isWriter) {
              writer.add(f.field)
            } else {
              assert(false)
            }
          }
        })
        method.instrument(fieldConverter.build(reader, writer))
      }
    }

    if (verbose) {
      println("---------")
    }
  }
}
