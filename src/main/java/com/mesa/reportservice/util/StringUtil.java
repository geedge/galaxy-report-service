package com.mesa.reportservice.util;


import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>字符串处理工具类</p>
 *
 * @author 中电积至有限公司
 * @version 1.0 创建时间：2010-11-8 下午04:50:39
 */


public final class StringUtil extends StringUtils {

    /**
     * The empty String <code>""</code>.
     */
    public static final String EMPTY = "";
    /**
     * 空 <code>String</code> 数组.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    /**
     * ISO8859_1 编码集
     */
    public static final String CODE_ISO8859_1 = "ISO8859_1";
    /**
     * GB2312 编码集
     */
    public static final String CODE_GB2312 = "GB2312";
    /**
     * GBK 编码集
     */
    public static final String CODE_GBK = "GBK";
    /**
     * UTF-8 编码集
     */
    public static final String CODE_UTF_8 = "UTF-8";
    private static final String[] SIMPLIFIED_CASE = {"O", "一", "二", "三", "四", "五",
            "六", "七", "八", "九", "十"};
    private static final String[] TRADITIONAL_CASE = {"零", "壹", "贰", "叁", "肆", "伍",
            "陆", "柒", "捌", "玖", "拾"};
    private static final String PATTERN_EMPTY_FULL = "[\u4e00-\u9fa5]";
    private static final String PATTERN_EMPTY_CHARACTER = "\\s*|\t|\r|\n";
    private static int size = 0;


    /**
     * <p>Description:抑制默认的构造器,避免实例化对象 </p>
     */
    private StringUtil() {

    }

    /**
     * <p>判断一个对象是否为空</p>
     * <p>
     * <code>object</code>元素判断所有对象是否为空.
     * 另外对{@link String}、{@link Collection}及其子类 、{@link Map}及其子类、数组 进行长度验证,如果长度为0,视为空对象.
     * </p>
     * <pre>
     * String aa = " ";
     * List list = new ArrayList()
     * LinkedHashSet set = new LinkedHashSet();
     * StringUtil.isEmpty(aa) = true
     * StringUtil.isEmpty(list) = true
     * StringUtil.isEmpty(set) = true
     * StringUtil.isEmpty("\t") = true
     * </pre>
     *
     * @param object 对象元素
     * @return <code>true</code> 对象为<code>null</code>,<code>false</code> 对象不为<code>null</code>.
     */
    public static boolean isEmpty(Object object) {
        initSize(object);
        return size == 0;
    }


    /**
     * <p>判断一个对象不为空</p>
     * <p>
     * <code>object</code>元素判断不为空验证
     * 另外对{@link String}、{@link Collection}及其子类 、{@link Map}及其子类、数组 进行长度验证,如果长度为0,视为空对象.
     * </p>
     * <pre>
     * String aa = " ";
     * List list = new ArrayList()
     * LinkedHashSet set = new LinkedHashSet();
     * StringUtil.isNotEmpty(aa) = false
     * StringUtil.isEmpty(list) = false
     * StringUtil.isEmpty(set) = false
     * StringUtil.isEmpty("\t") = false
     * </pre>
     *
     * @param object 对象元素
     * @return <code>true</code> 对象为<code>null</code>,<code>false</code> 对象不为<code>null</code>.
     */
    public static boolean isNotEmpty(Object object) {
        initSize(object);
        return size != 0;
    }


    public static String bSubstring(String s, int length) throws Exception {

        byte[] bytes = s.getBytes("Unicode");
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++) {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1) {
                n++; // 在UCS2第二个字节时n加1
            } else {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0) {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1)

        {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
                // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }

        return new String(bytes, 0, i, "Unicode");
    }


    /**
     * 判断对象是否有数据存在? 不存在为0、存在不为0的值.
     *
     * @param object 对象值
     */
    private static void initSize(Object object) {
        if (object == null) {
            size = 0;
        } else {
            if (object instanceof String) {
                size = isBlank((String) object) ? 0 : 1;
            } else if (object instanceof Collection) {
                size = ((Collection) object).size();
            } else if (object instanceof Map) {
                size = ((Map) object).size();
            } else if (object instanceof Object[]) {
                Object[] objectArray = (Object[]) object;
                size = objectArray.length;
                //其他数据类型
            } else {
                size = 1;
            }

        }

    }


    /**
     * <p>判断字符串是否为空.
     * 为空条件:全角\半角\tab 等没有实际意义的字符.
     * 具体参看{@link Character#isWhitespace(char)}对空格的定义.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isBlank("  ") = true 为半角空格
     * StringUtils.isBlank("　　") = true 为全角空格
     * StringUtils.isBlank("	") = true 为tab键
     * </pre>
     *
     * @param str 字符串
     * @return <code>true</code> 字符串为空 ,<code>false</code> 不为空
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * <p>判断字符串不为空.
     * 为空条件:全角\半角\tab 等没有实际意义的字符.
     * 具体参看{@link Character#isWhitespace(char)}对空格的定义.
     * </p>
     * <pre>
     * StringUtils.isBlank("  ") = false 为半角空格
     * StringUtils.isBlank("　　") = false 为全角空格
     * StringUtils.isBlank("	") = false 为tab键
     * </pre>
     *
     * @param str 字符串
     * @return <code>true</code> 字符串为空 ,<code>false</code> 不为空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }


    /**
     * <p>
     * 获得文件扩展名
     * TXT,HTML,HTM,EML,DOC,DOCX,XLS,XLSX,PPT,PPTX,PDF
     * </p>
     *
     * @param filePath 文件路径
     * @return <code>string</code> 扩展名 ,<code>null</code> 无扩展名
     */
    public static String getFileExtendName(String filePath) {
        String extendName = null;
        if (filePath.lastIndexOf(".") != -1) {
            extendName = filePath.substring(filePath.lastIndexOf(".") + 1).trim().toUpperCase();
        }
        return extendName;
    }


    /**
     * <p>去掉字符串两边空格.
     * 去掉空格规则:
     * 半角\全角\tab等无实际意义的字符.
     * 具体参看{@link Character#isWhitespace(char)}对空格的定义.
     * </p>
     * <pre>
     * StringUtil.strip("　	aa　　").length() = 2
     * StringUtil.strip(" ").length() = 0
     * </pre>
     *
     * @param str 字符
     * @return <code>str</code> 两边去空后字符串,<code>""</code> 字符串为null时.
     */
    public static String strip(String str) {

        if (isEmpty(str)) {
            return EMPTY;
        }
        int start = 0;
        int end = str.length();
        //去掉前边空格
        while ((start != end) && Character.isWhitespace(str.charAt(start))) {
            start++;
        }
        //去掉后边空格
        while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
            end--;
        }

        return str.substring(start, end);
    }

    /**
     * <p>去掉字符串空格，如果为中文去掉所有空格，英文去两边空格。
     * 去掉空格规则:
     * 半角\全角\tab等无实际意义的字符.
     * <p>
     * </p>
     * <pre>
     * StringUtil.strip("　	a a　　").length() = 2
     * StringUtil.strip(" ").length() = 0
     * </pre>
     *
     * @param str 字符
     * @return <code>str</code> 去空后字符串,<code>""<code> 字符串为null时.
     */
    public static String stripAll(String str) {
        String dest = "";
        Pattern p = Pattern.compile(PATTERN_EMPTY_FULL);
        Matcher m = p.matcher(str);
        if (m.find()) {
            if (isNotBlank(str)) {
                p = Pattern.compile(PATTERN_EMPTY_CHARACTER);
                str = str.replace((char) 12288, ' ');
                Matcher matcher = p.matcher(str);
                dest = matcher.replaceAll("");
            }
        } else {
            dest = str.trim();
        }

        return dest;
    }


    /**
     * <p>如果对象为空时,返回默认值.</p>
     *
     * @param object       要判断是否为空的对象
     * @param defaultValue 为空时设的默认值
     * @return 获取处理后的数据
     * @see #isEmpty(Object)
     */
    public static Object setDefaultIfEmpty(Object object, Object defaultValue) {

        return isEmpty(object) ? defaultValue : object;
    }



    public static String getMoreString(String text, int length) {
        StringBuilder textBuilder = new StringBuilder();

        if (isEmpty(text)) {
            return EMPTY;
        }
        int endIndex = Math.min(length, text.length());
        if (endIndex == length) {
            text = text.substring(0, length);
            textBuilder.append(text).append("......");
        } else {
            textBuilder.append(text);
        }


        return textBuilder.toString();
    }


    /**
     * <p>通过给定url获取域名地址.</p>
     * <p>
     * 获得域名信息需去除 http/https/ftp 与 www 一些头信息,获取有效地址.
     * </p>
     * <pre>
     * StringUtil.getDomain("http://www.baidu.com") = "baidu.com"
     * </pre>
     *
     * @param webSiteUrl url 地址
     * @return 返回截取后的域名地址
     */


    /**
     * <p>按照规则解析字符串得到一个数组.</p>
     * <pre>
     * StringUtil.Split(null, "*") = null
     * StringUtil.Split("", *) = []
     * StringUtil.Split("a.b.c", ".") = ["a", "b", "c"]
     * StringUtil.Split("a\tb\nc", null) = []
     * StringUtil.Split("a b c", " ") = ["a", "b", "c"]
     * </pre>
     *
     * @param str   需要分隔的字符串
     * @param regex 分隔规则
     * @return 返回字符串数组, 如果分隔字符串为空返回<code>null</code>.
     */
    public static String[] split(String str, String regex) {

        if (str == null) {
            return null;
        }

        if (str.length() == 0 || regex == null) {
            return EMPTY_STRING_ARRAY;
        }


        return str.split(regex);

    }

    /**
     * <p>判断字符集编码是否在java中支持.</p>
     *
     * @param name 所要求的字符集
     * @return <code>true</code> java 虚拟机下支持该字符集,<code>false</code> 不支持该字符集
     */
    public static boolean isSupported(String name) {
        if (name == null) {
            return false;
        }
        try {
            new String(EMPTY_BYTE_ARRAY, name);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return true;
    }


    /**
     * <p>字符编码转换，需要提供字符串的源编码与目的编码格式.</p>
     * <p>
     * 字符串工具类中提供一些字符编码常量：
     * {@link #CODE_GB2312}\{@link #CODE_GBK}\{@link #CODE_ISO8859_1}\{@link #CODE_UTF_8}
     * </p>
     *
     * @param value              要编码的值
     * @param sourceCodingFormat 字符的原始编码格式,具体编码格式可看本类提供的编码样式.
     * @param destCodingFormat   要转换字符的编码格式,具体编码格式可看本类提供的编码样式.
     * @return 返回编码后的字符串.
     * @throws UnsupportedEncodingException
     */
    public static String getCodingConversionResult(String value, String sourceCodingFormat,
                                                   String destCodingFormat) throws UnsupportedEncodingException {

        if (!isSupported(sourceCodingFormat) || !isSupported(destCodingFormat)) {
            throw new UnsupportedEncodingException("This character set encoding is not supported under JVM");
        }

        if (isEmpty(value)) {
            return EMPTY;
        }


        return new String(value.getBytes(sourceCodingFormat), destCodingFormat);
    }


    /**
     * <p>判断字符串是否是数字格式(包括小数形式).</p>
     * <pre>
     * String a1 = "12";
     * String a2 = "0.01";
     * String a3 = "0.0.1";
     * String a4 = "123a";
     * StringUtil.isNumeric(a1) = true
     * StringUtil.isNumeric(a2) = true
     * StringUtil.isNumeric(a3) = false
     * StringUtil.isNumeric(a4) = false
     * </pre>
     *
     * @param numberString 数字格式字符串
     * @return <code>true</code> 符合数字格式(包括小数),<code>false</code> 不符合数字格式.
     */
    public static boolean isNumeric(String numberString) {

        if (isEmpty(numberString)) {
            return false;
        }

        if (numberString.startsWith(".") || numberString.endsWith(".")) {
            return false;
        }

        int length = numberString.split("\\.").length - 1; //判断小数点在字符串中出现的次数。


        if (length > 1) { //小数点大于1次，不符合数字规范

            return false;
        }


        for (int i = 0; i < numberString.length(); i++) {
            if (!Character.isDigit(numberString.charAt(i)) && !".".equals(String.valueOf(numberString.charAt(i)))) {
                return false;
            }
        }

        return true;
    }

    /**
     * <p>压缩字符串</p>
     * <p>
     * 将带有换行\回车等字符替换为空格格式.具体替换有：
     * \t,\n,\f,\r .
     * </p>
     * <pre>
     * StringUtil.compressString("ceshi\too\f") = "ceshi oo "
     * </pre>
     *
     * @param text 要替换的字符串,可能为<code>null</code>
     */
    public static String compressString(String text) {
        if (isBlank(text)) {
            return text;
        }
        return text.replaceAll("[ \t\n\f\r ]+", " ");
    }

    /**
     * <p>将查找的字符串全部替换为设置的字符串</p>
     * <p>
     * 当该方法无操作时,返回<code>null</code>引用.
     * </p>
     * <pre>
     * StringUtil.replace(null, "*","*") = null
     * StringUtil.replace("str", null, "*") = "str"
     * StringUtil.replace("ceshi", "c", "") = "eshi"
     * StringUtil.replace("aba", "a", "z") = "zbz"
     * </pre>
     *
     * @param text          需处理的字符串
     * @param searchString  查找的字符
     * @param replaceString 替换的字符
     * @return 返回替换后的字符串, 文本为空时返回null.
     * @see #replace(String, String, String, int)
     */
    public static String replace(String text, String searchString, String replaceString) {

        return replace(text, searchString, replaceString, -1);
    }

    /**
     * <p>将查找字符串替换为设置字符串,根据<code>count</code>决定替换几次.
     * 当<code>text</code> 为空不进行任何操作..
     * </p>
     * <pre>
     * StringUtil.replace("abaa", "a", null, -1) = "abaa"
     * StringUtil.replace("abaa", "a", "", -1)   = "b"
     * StringUtil.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtil.replace("abaa", "a", "z", 1)   = "zbaa"
     * </pre>
     *
     * @param text          需要处理的字符串
     * @param searchString  查找的字符
     * @param replaceString 替换字符
     * @param count         替换的总次数,<code>-1</code> 替换所有满足条件的字符,<code>0</code> 不进行替换.
     * @return 返回替换后的字符串, 文本为空时返回null.
     */
    public static String replace(String text, String searchString, String replaceString, int count) {

        if (isBlank(text) || isBlank(searchString) || replaceString == null || count == 0) {
            return text;
        }

        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();

        int increase = replaceString.length() - replLength;
        increase = (increase < 0 ? 0 : increase);
        increase *= (count < 0 ? 16 : (count > 64 ? 64 : count));
        StringBuffer buf = new StringBuffer(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(replaceString);
            start = end + replLength;
            if (--count == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();

    }


    /**
     * <p>将字符串数字转换为简体大写中文格式.</p>
     * <pre>
     * StringUtil.convertSimplifiedCase("325") = ”三二五"
     * </pre>
     *
     * @param numberString 数字字符串
     * @return 返回简体大写后的数字
     */
    public static String convertSimplifiedCase(String numberString) {

        StringBuilder simplifiedBuilder = new StringBuilder();

        if (isEmpty(numberString)) {
            return null;
        }


        for (int i = 0; i < numberString.length(); i++) {
            String tempNumberString = String.valueOf(numberString.charAt(i));
            if ("0123456789".indexOf(tempNumberString) >= 0) {
                int number = Integer.parseInt(tempNumberString);
                simplifiedBuilder.append(SIMPLIFIED_CASE[number]);
            } else {
                simplifiedBuilder.append(tempNumberString);
            }
        }

        return simplifiedBuilder.toString();
    }


    /**
     * <p>把字符串中的数字转换成繁体大写中文的格式.</p>
     * <pre>
     * StringUtil.convertTraditionalCase("325") = "叁贰伍"
     * </pre>
     *
     * @param numberString 数字字符串
     * @return 返回繁体大写后的数字
     */
    public static String convertTraditionalCase(String numberString) {

        StringBuilder simplifiedBuilder = new StringBuilder();

        if (isEmpty(numberString)) {
            return null;
        }


        for (int i = 0; i < numberString.length(); i++) {
            String tempNumberString = String.valueOf(numberString.charAt(i));
            if ("0123456789".indexOf(tempNumberString) >= 0) {
                int number = Integer.parseInt(tempNumberString);
                simplifiedBuilder.append(TRADITIONAL_CASE[number]);
            } else {
                simplifiedBuilder.append(tempNumberString);
            }
        }

        return simplifiedBuilder.toString();
    }

    /**
     * <p>创建唯一标识字符串.</p>
     * <pre>
     * StringUtil.createUUID() = "00000DAF3CFC4E0B8DF2D5BEACB14D75"
     * </pre>
     *
     * @return 返回标识符字符串
     */
    public static String createUUID() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid.toUpperCase();
    }


    /**
     * <p>字符串过滤,负责将html的textarea属性获得的字符转换成html格式的字符集.</p>
     *
     * @param str 要解析的字符串
     * @return 是解析后的字符串
     */
    public static String htmlFilter(String str) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {

                case 39:
                    stringbuffer.append("&#039;");
                    break;

                case 34:
                    stringbuffer.append("&quot;");
                    break;

                case 60:
                    stringbuffer.append("&lt;");
                    break;

                case 62:
                    stringbuffer.append("&gt;");
                    break;

                case 38:
                    stringbuffer.append("&amp;");
                    break;

                case 32:
                    stringbuffer.append("&#32;");
                    break;

                case 10:
                    stringbuffer.append("<br>");
                    break;

                case 8220:
                    stringbuffer.append("&ldquo;");
                    break;

                case 8221:
                    stringbuffer.append("&rdquo;");
                    break;

                default:
                    stringbuffer.append(c);
                    break;
            }
        }

        return stringbuffer.toString();
    }


    /**
     * 统一正则抽取字符方法
     *
     * @param str     原始值
     * @param pattern 正则字符串
     * @param index   取符合规范下标属性
     * @return String    返回类型
     * @author （darnell） 2017年8月21日 下午4:25:31
     */
    public static String getFromRegEx(String str, String pattern, int index) {
        String value = "";
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            value = matcher.group(index);
        }
        return value;
    }


    /**
     * 获取第一个括号对应右括号的索引以及包含括号的个数,向后匹配
     *
     * @param str
     * @param bracketL 支持：( OR [ OR {
     * @param bool     是否将传入的字符串全部匹配完成,若不需要则在匹配到第一个括号匹配完成时结束
     * @return int[0]:匹配括号的个数,int[1]:第一个括号对应右括号的索引;若无匹配括号返回{0,-1};若有其中一个不成对则返回{-1,index}
     */
    public static int[] getBracketsMatch(String str, String bracketL, boolean bool) {
        int[] result = {0, -1};
        if (str == null) {
            return result;
        }
        String bracketR = ")";
        if (bracketL == null) {
            bracketL = "(";
        } else if (bracketL.equals("[")) {
            bracketR = "]";
        } else if (bracketL.equals("{")) {
            bracketR = "}";
        }
        StringBuffer sb = new StringBuffer(str);
        int countL = 0, countR = 0;
        for (int a = 0; a < sb.length(); a++) {
            if (sb.indexOf(bracketR) == a) {
                ++countR;
                sb.replace(a, a + 1, "$");
            } else if (sb.indexOf(bracketL) == a) {
                ++countL;
                sb.replace(a, a + 1, "$");
            }
            if (countR > countL) {
                result[0] = -1;
                result[1] = -1;
                return result;
            }
            result[0] = countL;
            result[1] = a;
            if (countL != 0 && (countL == countR) && !bool) {
                return result;
            }
        }
        if (countR != countL) {
            result[0] = -1;
        }
        return result;
    }

}
