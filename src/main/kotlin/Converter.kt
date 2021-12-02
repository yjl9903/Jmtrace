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
          long threadId = Thread.currentThread().getId();
          String objId = Integer.toHexString(System.identityHashCode(obj));
          System.out.println("R " + threadId + " " + objId + " $type[" + index + "]");
          $type[] arr = ($type []) obj;
          return arr[index];
        }
      """.trimIndent(), ctClass)
    }

    fun createWriteMethod(type: String, method: String): CtMethod {
      return CtMethod.make("""
        public static void $method(java.lang.Object obj, int index, $type value) {
          long threadId = Thread.currentThread().getId();
          String objId = Integer.toHexString(System.identityHashCode(obj));
          System.out.println("W " + threadId + " " + objId + " $type[" + index + "]");
          $type[] arr = ($type []) obj;
          arr[index] = value;
        }
      """.trimIndent(), ctClass)
    }

    ctClass.addMethod(CtMethod.make("""
      public static byte ${methodNames.byteOrBooleanRead()}(java.lang.Object obj, int index) {
        long threadId = Thread.currentThread().getId();
        String objId = Integer.toHexString(System.identityHashCode(obj));
        try {
          byte[] arr = (byte []) obj;
          System.out.println("R " + threadId + " " + objId + " byte[" + index + "]");
          return arr[index];
        } catch (Exception ex) {
          boolean[] arr = (boolean []) obj;
          System.out.println("R " + threadId + " " + objId + " boolean[" + index + "]");
          return arr[index];
        }
      }
    """.trimIndent(), ctClass))
    ctClass.addMethod(CtMethod.make("""
        public static void ${methodNames.byteOrBooleanWrite()}(java.lang.Object obj, int index, byte value) {
          long threadId = Thread.currentThread().getId();
          String objId = Integer.toHexString(System.identityHashCode(obj));
          try {
            byte[] arr = (byte []) obj;
            System.out.println("W " + threadId + " " + objId + " byte[" + index + "]");
            arr[index] = value;
          } catch (Exception ex) {
            boolean[] arr = (boolean []) obj;
            System.out.println("W " + threadId + " " + objId + " boolean[" + index + "]");
            arr[index] = value > 0;
          }
        }
      """.trimIndent(), ctClass))

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
