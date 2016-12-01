package com.github.yu000hong.spring.common.util

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

class StringUtil {

    /**
     * 截断字符串,最大长度len
     * @param text 字符串
     * @param len 最大长度
     * @return
     */
    public static String truncate(String text, int len) {
        if (text == null || text.length() <= len) {
            return text
        }
        return text.substring(0, len)
    }

    /**
     * 过滤掉4字节字符，因为MySQL5.1不支持，详见：
     * http://info.michael-simons.eu/2013/01/21/java-mysql-and-multi-byte-utf-8-support/
     * @param str
     * @return
     */
    public static String filter(String str) {
        if (str) {
            def sb = new StringBuilder()
            for (int index = 0; index < str.length(); index++) {
                char c = str.charAt(index)
                //c is the special char
                if (index < str.length() - 1 && Character.isSurrogatePair(c, str.charAt(index + 1))) {
                    index++
                } else {
                    sb.append(c)
                }
            }
            return sb.toString()
        } else {
            return str
        }
    }

    /**
     * 将对象输出到JSP文件时进行的转义操作
     */
    public static String escape(String json) {
        if (json) {
            return json.replaceAll(/\\n/, '<br>')
                    .replaceAll(/\\t/, '    ')
                    .replaceAll(/\\r/, '')
                    .replaceAll('\\\\', '\\\\\\\\')
                    .replaceAll(/'/, "\\\\'")
        } else {
            return json
        }
    }

    /**
     * 截取最大max长度的字符串
     * @param text
     * @param max
     * @return
     */
    public static String maxlen(String text, int max) {
        if (!text || text.length() <= max) {
            return text
        } else {
            return text.substring(0, max)
        }
    }

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat()
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE)
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE)
        format.setVCharType(HanyuPinyinVCharType.WITH_V)

        char[] input = inputString.trim().toCharArray()
        String output = ''

        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches('[\\u4E00-\\u9FA5]+')) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format)
                    output += temp[0]
                } else
                    output += Character.toString(input[i])
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace()
        }
        return output.toUpperCase()
    }
    /**
     * 获取汉字串拼音首字母，英文字符不变
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer()
        char[] arr = chinese.toCharArray()
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat()
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE)
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE)
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0))
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace()
                }
            } else {
                pybf.append(arr[i])
            }
        }
        return pybf.toString().replaceAll('\\W', '').trim().toUpperCase()
    }
    /**
     * 获取汉字串拼音，英文字符不变
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    public static String getFullSpell(String chinese) {
        StringBuffer pybf = new StringBuffer()
        char[] arr = chinese.toCharArray()
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat()
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE)
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE)
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0])
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace()
                }
            } else {
                pybf.append(arr[i])
            }
        }
        return pybf.toString().toUpperCase()
    }

    /**
     * 生成任意位随机数字字符串
     * @param bits
     * @return
     */
    public static String randomDigitString(int bits) {
        def random = new Random()
        def text = ''
        //可用字符为48-90这43个字符
        bits.times {
            def i = random.nextInt(999999) % 9
            text += "$i"
        }
        return text
    }

}
