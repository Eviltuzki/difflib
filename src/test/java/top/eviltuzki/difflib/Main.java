package top.eviltuzki.difflib;

public class Main {
    public static void main(String[] args) {
        SequenceMatcher matcher = new SequenceMatcher("2055516", "205/55R16");
        System.out.println(matcher.ratio()+"\t" + matcher.getOpcodes());
        matcher.setNew("abcd","bcde");
        System.out.println(matcher.ratio()+"\t" + matcher.getOpcodes());
        matcher.setNew("abc","abc");
        System.out.println(matcher.ratio()+"\t" + matcher.getOpcodes());
    }
}
