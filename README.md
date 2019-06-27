# Difflib


用Java实现的简易版difflib.SequenceMatcher，目前功能主要是计算两个字符串的相似率以及从字符串a转换到字符串b所需要进行哪些操作

## 主要类 SequenceMatcher

### 使用方法:

```
        SequenceMatcher matcher = new SequenceMatcher("2055516", "205/55R16");
        System.out.println(matcher.ratio()+"\t" + matcher.getOpcodes());
        matcher.setNew("abcd","bcde");
        System.out.println(matcher.ratio()+"\t" + matcher.getOpcodes());
        matcher.setNew("abc","abc");
        System.out.println(matcher.ratio()+"\t" + matcher.getOpcodes());
```

### 参考

- https://github.com/mhv2109/difflib    （
    - 补充这个版本的findLongestMatch方法有bug
    - var bestj = ahi 应该是 var bestj = blo
    - 附python版本：besti, bestj, bestsize = alo, blo, 0
- https://github.com/python/cpython/blob/3.7/Lib/difflib.py