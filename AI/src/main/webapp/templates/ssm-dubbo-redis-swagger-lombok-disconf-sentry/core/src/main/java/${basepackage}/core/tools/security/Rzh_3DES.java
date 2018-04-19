/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package ${basePackage}.core.tools.security;

import ${basePackage}.core.tools.ConfigUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public enum Rzh_3DES {
    INSTANCE;

    //示例
    public static void main(String[] a) {
        try {
            Map map = new HashMap();
            map.put("email", "wc09@213.com");
            map.put("userCode", "wc0903");
            String string = "我是明文，是否乱码？";
            Rzh_3DES des3 = Rzh_3DES.INSTANCE;
            //加密
            System.out.println(des3.encrypt("我是中国人"));


            //解密
            System.out.println(des3.decrypt("53e9044bda74b6bd840ed5512b98b35ef48145ea3d01f6034b2fb9b42c6a66d6"));

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private int isEncrypt = -1;// 1为加密，0为解密
    private String message; // 要加密/解密信息（解密时需为十六进制显示的字符串）

    /**
     * Base64+3des
     *
     * @param text 明文
     * @return 密文
     */
    public String encryptBase64(String text) {
        String encode = encrypt(text);
        return Rzh_3DESUtils.encodeBase64(encode);
    }

    /**
     * 3des加密
     *
     * @param text 需要加密的文本
     * @return 密文
     */
    public String encrypt(String text) {
        text = Cn2UniCode(text);
        Rzh_3DES rzh_3DES = Rzh_3DES.INSTANCE;
        rzh_3DES.setMessage(text);
        rzh_3DES.setIsEncrypt(1);
        return rzh_3DES.rzh3DESChiper();
    }

    /**
     * base64解密+3des解密
     *
     * @param text 密文
     * @return 明文
     */
    public String decryptBase64(String text) {
        String encode = Rzh_3DESUtils.decodeBase64(text);
        return decrypt(encode);
    }

    /**
     * 3de解密
     *
     * @param code 密文
     * @return 明文文本
     */
    public String decrypt(String code) {
        Rzh_3DES rzh_3DES = Rzh_3DES.INSTANCE;
        rzh_3DES.setMessage(code);
        rzh_3DES.setIsEncrypt(0);
        return Unicode2Cn(rzh_3DES.rzh3DESChiper());
    }


    public String rzh3DESChiper() {
        String keyStr = ConfigUtil.getValue("3deskey", "3des.properties");
        if (keyStr == null || keyStr.length() % 8 != 0) {
            new Exception("读不到key");
        }
        SecretKey key = new SecretKeySpec(keyStr.getBytes(), "DESede");

        byte[] text = null;
        byte[] bmessage = null;
        String returnStr = null;
        try {
            Cipher cipher = Cipher.getInstance("DESede/CFB/PKCS5Padding", "BC");
            AlgorithmParameterSpec algorithmparameterspec = new IvParameterSpec("12345678".getBytes());
            if (isEncrypt == 1) {
                bmessage = message.getBytes();
                cipher.init(Cipher.ENCRYPT_MODE, key, algorithmparameterspec);
            } else if (isEncrypt == 0) {
                bmessage = Rzh_3DESUtils.decodeHex(message);
                cipher.init(Cipher.DECRYPT_MODE, key, algorithmparameterspec);
            } else {
                return null;
            }
            text = cipher.doFinal(bmessage);
            Rzh_3DESUtils.encodeHex(text);
            if (isEncrypt == 1) {
                returnStr = Rzh_3DESUtils.encodeHex(text);
            } else if (isEncrypt == 0) {
                returnStr = new String(text, "gbk");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStr;
    }


    // cn -> unicode
    public static String Cn2UniCode(String string) {
        StringBuffer sb = new StringBuffer();
        char[] charArray = string.toCharArray();
        for (char ch : charArray) {
            if (ch > 255) {
                String str = Integer.toHexString(ch);
                if (str.length() == 1) {
                    sb.append("\\u000" + str);
                } else if (str.length() == 2) {
                    sb.append("\\u00" + str);
                } else if (str.length() == 3) {
                    sb.append("\\u0" + str);
                } else if (str.length() == 4) {
                    sb.append("\\u" + str);
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    //Unicode -> Cn
    public static String Unicode2Cn(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed      encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }


    public void setIsEncrypt(int isEncrypt) {
        this.isEncrypt = isEncrypt;
    }


    public void setMessage(String message) {
        this.message = message;
    }
}

/**
 * 3des封装
 */
class Rzh_3DESUtils {

    private Rzh_3DESUtils() {
    }

    public static final String encodeHex(byte bytes[]) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString(bytes[i] & 0xff, 16));
        }

        return buf.toString();
    }

    public static final byte[] decodeHex(String hex) {
        char chars[] = hex.toCharArray();
        byte bytes[] = new byte[chars.length / 2];
        int byteCount = 0;
        for (int i = 0; i < chars.length; i += 2) {
            int newByte = 0;
            newByte |= hexCharToByte(chars[i]);
            newByte <<= 4;
            newByte |= hexCharToByte(chars[i + 1]);
            bytes[byteCount] = (byte) newByte;
            byteCount++;
        }

        return bytes;
    }

    private static final byte hexCharToByte(char ch) {
        switch (ch) {
            case 48: // '0'
                return 0;

            case 49: // '1'
                return 1;

            case 50: // '2'
                return 2;

            case 51: // '3'
                return 3;

            case 52: // '4'
                return 4;

            case 53: // '5'
                return 5;

            case 54: // '6'
                return 6;

            case 55: // '7'
                return 7;

            case 56: // '8'
                return 8;

            case 57: // '9'
                return 9;

            case 97: // 'a'
                return 10;

            case 98: // 'b'
                return 11;

            case 99: // 'c'
                return 12;

            case 100: // 'd'
                return 13;

            case 101: // 'e'
                return 14;

            case 102: // 'f'
                return 15;

            case 58: // ':'
            case 59: // ';'
            case 60: // '<'
            case 61: // '='
            case 62: // '>'
            case 63: // '?'
            case 64: // '@'
            case 65: // 'A'
            case 66: // 'B'
            case 67: // 'C'
            case 68: // 'D'
            case 69: // 'E'
            case 70: // 'F'
            case 71: // 'G'
            case 72: // 'H'
            case 73: // 'I'
            case 74: // 'J'
            case 75: // 'K'
            case 76: // 'L'
            case 77: // 'M'
            case 78: // 'N'
            case 79: // 'O'
            case 80: // 'P'
            case 81: // 'Q'
            case 82: // 'R'
            case 83: // 'S'
            case 84: // 'T'
            case 85: // 'U'
            case 86: // 'V'
            case 87: // 'W'
            case 88: // 'X'
            case 89: // 'Y'
            case 90: // 'Z'
            case 91: // '['
            case 92: // '\\'
            case 93: // ']'
            case 94: // '^'
            case 95: // '_'
            case 96: // '`'
            default:
                return 0;
        }
    }

    public static String encodeBase64(String data) {
        byte bytes[] = null;
        try {
            bytes = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
        }
        return encodeBase64(bytes);
    }

    public static String decodeBase64(String data) {
        byte[] bytes = null;
        try {
            bytes = data.getBytes("UTF-8");
            return new String(decodeBase64(bytes), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return "";
    }

    public static String encodeBase64(byte data[]) {
        boolean lineSep = false;
        int sLen = data == null ? 0 : data.length;
        if (sLen == 0) {
            return new String("");
        }
        int eLen = (sLen / 3) * 3;
        int cCnt = (sLen - 1) / 3 + 1 << 2;
        int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0);
        char dArr[] = new char[dLen];
        int s = 0;
        int d = 0;
        int cc = 0;
        do {
            if (s >= eLen) {
                break;
            }
            int i = (data[s++] & 0xff) << 16 | (data[s++] & 0xff) << 8 | data[s++] & 0xff;
            dArr[d++] = CA[i >>> 18 & 0x3f];
            dArr[d++] = CA[i >>> 12 & 0x3f];
            dArr[d++] = CA[i >>> 6 & 0x3f];
            dArr[d++] = CA[i & 0x3f];
            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = '\r';
                dArr[d++] = '\n';
                cc = 0;
            }
        } while (true);
        int left = sLen - eLen;
        if (left > 0) {
            int i = (data[eLen] & 0xff) << 10 | (left != 2 ? 0 : (data[sLen - 1] & 0xff) << 2);
            dArr[dLen - 4] = CA[i >> 12];
            dArr[dLen - 3] = CA[i >>> 6 & 0x3f];
            dArr[dLen - 2] = left != 2 ? '=' : CA[i & 0x3f];
            dArr[dLen - 1] = '=';
        }
        return new String(dArr);
    }

    public static final byte[] decodeBase64(byte bytes[]) {
        int sLen = bytes.length;
        int sepCnt = 0;
        for (int i = 0; i < sLen; i++) {
            if (IA[bytes[i] & 0xff] < 0) {
                sepCnt++;
            }
        }

        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int i = sLen;
        do {
            if (i <= 1 || IA[bytes[--i] & 0xff] > 0) {
                break;
            }
            if (bytes[i] == 61) {
                pad++;
            }
        } while (true);
        int len = ((sLen - sepCnt) * 6 >> 3) - pad;
        byte dArr[] = new byte[len];
        int s = 0;
        int d = 0;
        do {
            if (d >= len) {
                break;
            }
            i = 0;
            for (int j = 0; j < 4; j++) {
                int c = IA[bytes[s++] & 0xff];
                if (c >= 0) {
                    i |= c << 18 - j * 6;
                } else {
                    j--;
                }
            }

            dArr[d++] = (byte) (i >> 16);
            if (d < len) {
                dArr[d++] = (byte) (i >> 8);
                if (d < len) {
                    dArr[d++] = (byte) i;
                }
            }
        } while (true);
        return dArr;
    }


    private static final char CA[];
    private static final int IA[];
    private static final BitSet allowed_query;

    static {
        CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        IA = new int[256];
        Arrays.fill(IA, -1);
        int i = 0;
        for (int iS = CA.length; i < iS; i++) {
            IA[CA[i]] = i;
        }

        IA[61] = 0;
        allowed_query = new BitSet(256);
        for (i = 48; i <= 57; i++) {
            allowed_query.set(i);
        }

        for (i = 97; i <= 122; i++) {
            allowed_query.set(i);
        }

        for (i = 65; i <= 90; i++) {
            allowed_query.set(i);
        }

        allowed_query.set(45);
        allowed_query.set(95);
        allowed_query.set(46);
        allowed_query.set(33);
        allowed_query.set(126);
        allowed_query.set(42);
        allowed_query.set(39);
        allowed_query.set(40);
        allowed_query.set(41);
    }
}
