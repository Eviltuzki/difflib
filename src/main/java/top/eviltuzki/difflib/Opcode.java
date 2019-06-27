package top.eviltuzki.difflib;

public class Opcode {
    private final Tag tag;
    private final int alo;
    private final int ahi;
    private final int blo;
    private final int bhi;

    public Opcode(Tag tag, int alo, int ahi, int blo, int bhi) {
        this.tag = tag;
        this.alo = alo;
        this.ahi = ahi;
        this.blo = blo;
        this.bhi = bhi;
    }

    public final Tag getTag() {
        return this.tag;
    }

    public final int getAlo() {
        return this.alo;
    }

    public final int getAhi() {
        return this.ahi;
    }

    public final int getBlo() {
        return this.blo;
    }

    public final int getBhi() {
        return this.bhi;
    }

    @Override
    public String toString() {
        return "Opcode{" +
                "tag=" + tag +
                ", alo=" + alo +
                ", ahi=" + ahi +
                ", blo=" + blo +
                ", bhi=" + bhi +
                '}';
    }
}
