package top.eviltuzki.difflib;

public enum Tag {
    REPLACE('r'),
    INSERT('i'),
    DELETE('d'),
    EQUAL('e');

    private final char ch;


    public final char getCh() {
        return this.ch;
    }

    private Tag(char ch) {
        this.ch = ch;
    }
}
