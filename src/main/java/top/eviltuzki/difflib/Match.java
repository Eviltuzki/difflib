package top.eviltuzki.difflib;

public class Match {
    public int a;
    public int b;
    public int size;


    public Match(int a, int b, int size) {
        this.a = a;
        this.b = b;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Match{" +
                "a=" + a +
                ", b=" + b +
                ", size=" + size +
                '}';
    }
}
