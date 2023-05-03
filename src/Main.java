public class Main {
    public static void main(String[] args) {
        AssociativeMemory am = new AssociativeMemory(16);
        am.randomize();
        System.out.println(am);
        System.out.println();
        AssociativeMemory diagonalised = am.diagonaliseCopy();
        System.out.println(diagonalised);
        System.out.println();
        System.out.println(am.summariseCopy(new boolean[]{true, false, true}));
    }
}