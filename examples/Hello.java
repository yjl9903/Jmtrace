public class Hello {
  public static void main(String[] args) {
    byte[] bytes = new byte[10];
    bytes[0] = 1;
    bytes[1] = bytes[0];

    boolean[] booleans = new boolean[10];
    booleans[1] = false;
    booleans[0] = !booleans[1];

    char[] chars = new char[10];
    chars[0] = 'A';
    chars[1] = 'B';
    chars[2] = 'C';
    chars[3] = chars[0];

    double[] doubles = new double[10];
    doubles[0] = 1.0;
    doubles[1] = 2.0;
    doubles[3] = doubles[0] / doubles[1];

    float[] floats = new float[10];
    floats[0] = 3.0f;
    floats[1] = 5.0f;
    floats[3] = floats[0] / floats[1];

    short[] shorts = new short[10];
    shorts[0] = 1;
    shorts[1] = (short) (shorts[0] + 127);

    int[] ints = new int[10];
    ints[0] = 1;
    ints[1] = 2;
    ints[2] = ints[0] + ints[1];

    long[] longs = new long[10];
    longs[0] = ints[2] * ints[2];
    longs[1] = longs[0] * longs[0];

    Object[] objects = new Object[10];
    objects[0] = new Hello();
    Hello a = (Hello) objects[0];
  }
}
