import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class AssociativeMemory {

    private final int size;
    private final boolean[][] memory;
    private boolean isDiagonalised = false;

    public AssociativeMemory(int size) {
        this.size = size;
        this.memory = new boolean[size][size];
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isDiagonalised() {
        return this.isDiagonalised;
    }

    public void write(int x, int y, boolean val) {
        this.memory[y][x] = val;
    }

    public void writeWord(int addr, boolean[] val) {
        if (!isDiagonalised()) {
            if (Math.min(val.length, this.size) >= 0)
                System.arraycopy(val, 0, this.memory[addr], 0, Math.min(val.length, this.size));
        } else {
            for (int i = 0; i < addr; i++) {
                this.memory[i][addr] = val[val.length - 1 - addr + i];
            }
            for (int i = addr; i < this.size; i++) {
                this.memory[i][addr] = val[i];
            }
        }
    }

    public void writeColumn(int addr, boolean[] val) {
        if (!isDiagonalised()) {
            for (int i = 0; i < this.size; i++) {
                this.memory[i][addr] = val[i];
            }
        } else {
            for (int i = 0; i < addr; i++) {
                this.memory[i][this.size - 1 - addr + i] = val[addr + i];
            }
            for (int i = addr; i < this.size; i++) {
                this.memory[i][i - addr] = val[i - addr];
            }
        }
    }

    public boolean read(int x, int y) {
        return this.memory[y][x];
    }

    public boolean[] readWord(int addr) {
        if (!isDiagonalised())
            return this.memory[addr];
        else {
            boolean[] word = new boolean[this.size];
            for (int i = 0; i < this.size; i++) {
                word[i] = this.memory[i][addr];
            }
            word = Util.shift(word, -addr);
            return word;
        }
    }

    public boolean[] readColumn(int addr) {
        boolean[] res = new boolean[this.size];
        if (!isDiagonalised()) {
            for (int i = 0; i < this.size; i++) {
                res[i] = this.memory[i][addr];
            }
        } else {
            for (int i = 0; i < addr; i++) {
                res[i] = Util.shift(this.memory[i], addr - i)[0];
            }
            for (int i = addr; i < this.size; i++) {
                res[i] = Util.shift(this.memory[i], -(i - addr))[0];
            }
            res = Util.shift(res, -addr);
        }
        return res;
    }

    public void randomize() {
        for (int y = 0; y < this.size * 3; y++) {
            for (int x = 0; x < this.size * 3; x++) {
                this.memory
                        [new Random(System.nanoTime()).nextInt(this.size)]
                        [new Random(System.nanoTime()).nextInt(this.size)]
                        ^= new Random(System.nanoTime()).nextBoolean();
            }
        }
    }

    private static boolean[] binaryAdd(boolean[] addTo, boolean[] toAdd) {
        int opLength = Math.max(addTo.length, toAdd.length);
        boolean v1, v2, v3, C = false;
        boolean[] X = new boolean[opLength + 1];
        boolean[] Y = new boolean[opLength + 1];
        System.arraycopy(addTo, 0, X, 0, addTo.length);
        System.arraycopy(toAdd, 0, Y, 0, toAdd.length);
        for (int i = 0; i < opLength; i++) {
            v1 = X[i] ^ Y[i];
            v2 = X[i] & Y[i];
            v3 = v1 & C;
            X[i] = v1 ^ C;
            C = v2 | v3;
        }
        X[opLength] |= C;
        return X;
    }

    public AssociativeMemory summariseCopy(boolean[] mask) {
        AssociativeMemory res = new AssociativeMemory(this.size);
        for (int i = 0; i < this.size; i++) {
            boolean[] word = readWord(i);
            boolean[] V = Arrays.copyOfRange(word, 0, 3);
            if (Arrays.equals(V, mask)) {
                boolean[] A = Arrays.copyOfRange(word, 3, 7);
                boolean[] B = Arrays.copyOfRange(word, 7, 11);
                boolean[] sum = binaryAdd(A, B);
                boolean[] newWord = new boolean[this.size];

                System.arraycopy(V, 0, newWord, 0, 3);
                System.arraycopy(A, 0, newWord, 3, 4);
                System.arraycopy(B, 0, newWord, 7, 4);
                System.arraycopy(sum, 0, newWord, 11, 5);

                res.writeWord(i, newWord);
            } else {
                res.writeWord(i, word);
            }
        }
        return res;
    }

    public AssociativeMemory transposeCopy() {
        AssociativeMemory result = new AssociativeMemory(this.size);
        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                result.write(x, y, this.memory[x][y]);
            }
        }
        return result;
    }

    public AssociativeMemory diagonaliseCopy() {
        AssociativeMemory result = new AssociativeMemory(this.size);
        for (int addr = 0; addr < this.size; addr++) {
            result.writeColumn(addr,
                    Util.circularShift(readWord(addr), addr)
            );
        }
        result.isDiagonalised = true;
        return result;
    }

    private boolean[] columnOperation(int colIndex1, int colIndex2, BiFunction<Boolean, Boolean, Boolean> function) {
        boolean[] res = new boolean[this.size];
        boolean[] col1 = readColumn(colIndex1);
        boolean[] col2 = readColumn(colIndex2);
        for (int i = 0; i < this.size; i++) {
            res[i] = function.apply(col1[i], col2[i]);
        }
        return res;
    }

    public boolean[] f2(int colIndex1, int colIndex2) {
        return columnOperation(colIndex1, colIndex2, (f, s) -> f & !s);
    }

    public boolean[] f7(int colIndex1, int colIndex2) {
        return columnOperation(colIndex1, colIndex2, (f, s) -> f || s);
    }

    public boolean[] f8(int colIndex1, int colIndex2) {
        return columnOperation(colIndex1, colIndex2, (f, s) -> !(f || s));
    }

    public boolean[] f13(int colIndex1, int colIndex2) {
        return columnOperation(colIndex1, colIndex2, (f, s) -> !f || s);
    }

    private static GLFlagPair compareByMSB(boolean[] first, boolean[] second) {
        for (int bit = Math.min(first.length, second.length) - 1; bit >= 0; bit--) {
            if (first[bit] ^ second[bit]) {
                if (first[bit])
                    return new GLFlagPair(true, false);
                else
                    return new GLFlagPair(false, true);
            }
        }
        return new GLFlagPair(false, false);
    }

    private static boolean[][] searchInRange(boolean[][] array, boolean[] lower, boolean[] upper) {
        List<boolean[]> result = new ArrayList<>();
        for (boolean[] entry : array) {
            if (!compareByMSB(entry, upper).g() && !compareByMSB(entry, lower).l())
                result.add(entry);
        }
        return result.toArray(boolean[][]::new);
    }

    public boolean[][] searchWordsInRange(boolean[] lower, boolean[] upper) {
        return searchInRange(this.memory, lower, upper);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.memory)
                .replaceAll("], ", "\n")
                .replaceAll("false", "0")
                .replaceAll("true", "1")
                .replaceAll("[\\[\\]]", "")
                .replaceAll(",", " ");
    }

    record GLFlagPair(boolean g, boolean l) {
    }
}