import javassist.ClassPool
import javassist.CodeConverter
import javassist.CtClass
import javassist.CtMethod

class ArrayConverter(classPool: ClassPool) : CodeConverter() {
  companion object {
    const val Classname = "__MTrace_Array__"
  }

  private val ctClass: CtClass = classPool.makeClass(Classname)

  init {
    val methodNames = DefaultArrayAccessReplacementMethodNames()

    replaceArrayAccess(ctClass, methodNames)

    fun createReadMethod(type: String, method: String): CtMethod {
      return CtMethod.make("""
        public static $type $method(java.lang.Object obj, int index) {
          $type[] arr = ($type []) obj;
          long threadId = Thread.currentThread().getId();
          String objId = Integer.toHexString(System.identityHashCode(obj));
          System.out.println("R " + threadId + " " + objId + " $type[" + index + "]");
          return arr[index];
        }
      """.trimIndent(), ctClass)
    }

    fun createWriteMethod(type: String, method: String): CtMethod {
      return CtMethod.make("""
        public static void $method(java.lang.Object obj, int index, $type value) {
          $type[] arr = ($type []) obj;
          long threadId = Thread.currentThread().getId();
          String objId = Integer.toHexString(System.identityHashCode(obj));
          System.out.println("W " + threadId + " " + objId + " $type[" + index + "]");
          arr[index] = value;
        }
      """.trimIndent(), ctClass)
    }

    ctClass.addMethod(createReadMethod("byte", methodNames.byteOrBooleanRead()))
    ctClass.addMethod(createWriteMethod("byte",
      methodNames.byteOrBooleanWrite()))

    ctClass.addMethod(createReadMethod("char", methodNames.charRead()))
    ctClass.addMethod(createWriteMethod("char", methodNames.charWrite()))

    ctClass.addMethod(createReadMethod("double", methodNames.doubleRead()))
    ctClass.addMethod(createWriteMethod("double", methodNames.doubleWrite()))

    ctClass.addMethod(createReadMethod("float", methodNames.floatRead()))
    ctClass.addMethod(createWriteMethod("float", methodNames.floatWrite()))

    ctClass.addMethod(createReadMethod("short", methodNames.shortRead()))
    ctClass.addMethod(createWriteMethod("short", methodNames.shortWrite()))

    ctClass.addMethod(createReadMethod("int", methodNames.intRead()))
    ctClass.addMethod(createWriteMethod("int", methodNames.intWrite()))

    ctClass.addMethod(createReadMethod("long", methodNames.longRead()))
    ctClass.addMethod(createWriteMethod("long", methodNames.longWrite()))

    ctClass.addMethod(createReadMethod("java.lang.Object",
      methodNames.objectRead()))
    ctClass.addMethod(createWriteMethod("java.lang.Object",
      methodNames.objectWrite()))
  }
}
