public class Util {

    public static boolean[] shift(boolean[] arr, int shift) {
        boolean[] res = new boolean[arr.length];
        if (shift >= 0) {
            System.arraycopy(arr, 0, res, shift, arr.length - shift);
        } else {
            System.arraycopy(arr, -shift, res, 0, arr.length + shift);
        }
        return res;
    }

    public static boolean[] circularShift(boolean[] arr, int shift) {
        boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = (shift(arr, shift)[i]) || (shift(arr, shift - Integer.signum(shift) * arr.length))[i];
        }
        return res;
    }

    public static boolean[] reverse(boolean[] arr) {
        final int l = arr.length;
        boolean[] res = new boolean[l];
        for (int i = 0; i < l; i++) {
            res[i] = arr[l - 1 - i];
        }
        return res;
    }

    public static String stringifyArray(boolean[][] array) {
        StringBuilder builder = new StringBuilder();
        for (boolean[] entry : array) {
            StringBuilder entryBuilder = new StringBuilder();
            for (boolean bit : entry) {
                entryBuilder.append(bit ? "1 " : "0 ");
            }
            builder.append(entryBuilder.reverse()).append(";\n");
        }
        return builder.toString();
    }
}
