package top.eviltuzki.difflib;

import java.util.*;

/**
 * 简易版 Python difflib.SequenceMatcher Java实现
 * 参考:
 *  https://github.com/python/cpython/blob/3.7/Lib/difflib.py
 *  https://github.com/mhv2109/difflib/blob/master/src/main/kotlin/com/mhv2109/difflib/SequenceMatcher.kt
 * 功能:
 *  将字符串a经过增删改转换为b，并计算相似率，同时可以获取对应的具体操作
 *
 */
public class SequenceMatcher {
    /**
     * 存储第一个字符串的char数组
     */
    private char[] a = new char[0];
    /**
     * 存储第二个字符串的char数组
     */
    private char[] b = new char[0];
    /**
     * 转换后具体有哪些操作
     */
    private List<Opcode> opcodes = new ArrayList<>();
    /**
     * Match集合，其中 a数组的 ( match.a ~ match.a + match.size) = b数组 ( match.b ~ match.b + match.size)
     */
    private List<Match> matchingBlocks = new ArrayList<>();
    /**
     *
     */
    private Map<Character, List<Integer>> b2j = new HashMap<>();

    /**
     *
     */
    private Set<Character> bpopular = new HashSet<>();

    /**
     * 获取匹配后的Match集合
     * @return Match集合
     */
    public final List<Match> getMatchingBlocks() {
        if (!matchingBlocks.isEmpty())
            return matchingBlocks;
        final int la = a.length;
        final int lb = b.length;
        final List<QueueElem> queue = new LinkedList<>(Collections.singletonList(new QueueElem(0, la, 0, lb)));
        matchingBlocks.clear();
        while (!queue.isEmpty()) {
            QueueElem elem = queue.remove(queue.size() - 1);
            Match match = findLongestMatch(elem.alo, elem.ahi, elem.blo, elem.bhi);
            if (match.size > 0) { // if size == 0, there was no matching block.
                matchingBlocks.add(match);
                if (elem.alo < match.a && elem.blo < match.b)
                    queue.add(new QueueElem(elem.alo, match.a, elem.blo, match.b));
                if (match.a + match.size < elem.ahi && match.b + match.size < elem.bhi)
                    queue.add(new QueueElem(match.a + match.size, elem.ahi, match.b + match.size, elem.bhi));
            }
        }
        matchingBlocks.sort((o1, o2) -> {
            if (o1.a != o2.a)
                return Integer.compare(o1.a, o2.a);
            if (o1.b != o2.b)
                return Integer.compare(o1.b, o2.b);
            return Integer.compare(o1.size, o2.size);

        });
        // collapse adjacent equal blocks
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        List<Match> nonAdjacent = new ArrayList<>();
        for (Match it : matchingBlocks) {
            if (i1 + k1 == it.a && j1 + k1 == it.b)
                k1 += it.size;
            else {
                if (k1 > 0)
                    nonAdjacent.add(new Match(i1, j1, k1));
                i1 = it.a;
                j1 = it.b;
                k1 = it.size;
            }
        }
        if (k1 > 0)
            nonAdjacent.add(new Match(i1, j1, k1));
        nonAdjacent.add(new Match(la, lb, 0));
        matchingBlocks = nonAdjacent;
        return matchingBlocks;
    }

    private Match findLongestMatch(int alo, int ahi, int blo, int bhi) {
        int besti = alo;
        int bestj = blo;
        int bestSize = 0;

        // find longest junk-free match
        Map<Integer, Integer> j2len = new HashMap<>();
        for (int i = alo; i < ahi; i++) {
            Map<Integer, Integer> newj2len = new HashMap<>();
            List<Integer> list = b2j.get(a[i]);
            if (list == null)
                list = new ArrayList<>();
            for (Integer j : list) {
                if (j < blo)
                    continue;
                if (j >= bhi)
                    break;
                Integer integer = j2len.get(j - 1);
                if (integer == null)
                    integer = 0;
                Integer k = integer + 1;
                newj2len.put(j, k);
                if (k > bestSize) {
                    besti = i - k + 1;
                    bestj = j - k + 1;
                    bestSize = k;
                }
            }
            j2len = newj2len;
        }

        // Extend the best by non-junk elements on each end
        while (besti > alo &&
                bestj > blo &&
                (a[besti - 1] == b[bestj - 1])) {

            besti -= 1;
            bestj -= 1;
            bestSize += 1;

        }

        while ((besti + bestSize < ahi) &&
                (bestj + bestSize < bhi)  &&
                (a[besti + bestSize] == b[bestj + bestSize])) {

            bestSize += 1;

        }


        return new Match(besti, bestj, bestSize);
    }

    /**
     * 计算相似率
     * @return 相似率
     */
    public final double ratio() {
        int matches = getMatchingBlocks().stream().map(x -> x.size).mapToInt(x -> x).sum();
        return calculateRatio(matches, a.length + b.length);
    }

    private double calculateRatio(int matches, int len) {
        if (len > 0)
            return 2.0 * matches / len;
        return 1.0;
    }

    /**
     * 返回对应的操作方法集合
     * @return Opcode操作方法集合
     */
    public final List<Opcode> getOpcodes() {
        if (!opcodes.isEmpty())
            return opcodes;
        int i = 0;
        int j = 0;
        for (Match it : getMatchingBlocks()) {
            Tag tag = null;
            if (i < it.a && j < it.b)
                tag = Tag.REPLACE;
            else if (i < it.a)
                tag = Tag.DELETE;
            else if (j < it.b)
                tag = Tag.INSERT;
            if (tag != null)
                opcodes.add(new Opcode(tag, i, it.a, j, it.b));
            i = it.a + it.size;
            j = it.b + it.size;
            if (it.size > 0)
                opcodes.add(new Opcode(Tag.EQUAL, it.a, i, it.b, j));
        }

        return opcodes;
    }

    /**
     *
     * 构造SequenceMatcher，直接传入对应的字符串数组
     * @param a 对比字符串a
     * @param b 对比字符串b
     */
    public SequenceMatcher(char[] a, char[] b) {
        this.a = a;
        this.b = b;
    }

    /**
     * 构造SequenceMatcher,后续通过setNew方法进行初始化
     */
    public SequenceMatcher() {
    }

    /**
     * 构造SequenceMatcher，直接传入对应的字符串
     * @param a 对比字符串a
     * @param b 对比字符串b
     */
    public SequenceMatcher(String a, String b) {
        setNew(a, b);
    }

    /**
     * 设置新的字符串，复用SequenceMatcher
     * @param a 对比字符串a
     * @param b 对比字符串b
     */
    public void setNew(String a, String b) {
        this.b2j.clear();
        this.bpopular.clear();
        this.opcodes.clear();
        this.matchingBlocks.clear();
        this.a = a.toCharArray();
        this.b = b.toCharArray();
        chainB();
    }

    private void chainB() {
        b2j.clear();
        for (int i = 0; i < b.length; i++) {
            char c = b[i];
            if (b2j.containsKey(c)) {
                b2j.get(c).add(i);
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                b2j.put(c, list);
                b2j.get(c).add(i);
            }
        }

        // purge popular elements that are not junk
        bpopular.clear();
        int n = b.length;
        if (n >= 200) {
            int ntest = n / 100 + 1;
            for (Map.Entry<Character, List<Integer>> entry : b2j.entrySet()) {
                if (entry.getValue().size() > ntest)
                    bpopular.add(entry.getKey());
            }
            bpopular.forEach(b2j::remove);
        }
    }
}


