package com.sourcecode.Algorithm;

public class Algorithm {

    /**
     * 将一个N元一维向量左旋i个位置，例如当n=8且i=3时，向量abcdefgh旋转为defghabc。你能否仅用数十个额外字节的存储空间，在正比于n的时间内完成向量的旋转
     */
    public static void swap(String s, int index) {
        Integer n = s.length() + 1;
        for (int i = 0; i < index; i++) {
            // s = s.substring(1, n - 1) + s.substring(0, 1);
            s = subString(s, 1, n - 1) + subString(s, 0, 1);
        }
        System.out.println(s);
    }

    public static String subString(String s, int begin, int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = begin; i < length; i++) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        swap("abcdefgh", 3);
    }
}
