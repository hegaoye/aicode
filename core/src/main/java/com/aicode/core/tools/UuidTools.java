package com.aicode.core.tools;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * uuid工具
 *
 * @author mm
 */
public class UuidTools {

  static final char[] digits = {
    '0', '1', '2', '3', '4', '5', '6', '7',
    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
    'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P',
    'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z'
  };

  /**
   * 将十进制的数字转换为指定进制的字符串。
   *
   * @param i 十进制的数字。
   * @param system 指定的进制，常见的2/8/16。
   * @return 转换后的字符串。
   */
  public static String numericToString(long i, int system) {
    long num = 0;
    if (i < 0) {
      num = ((long) 2 * 0x7fffffff) + i + 2;
    } else {
      num = i;
    }
    char[] buf = new char[32];
    int charPos = 32;
    while ((num / system) > 0) {
      buf[--charPos] = digits[(int) (num % system)];
      num /= system;
    }
    buf[--charPos] = digits[(int) (num % system)];
    return new String(buf, charPos, (32 - charPos));
  }

  public static String numericToString(int i, int system) {
    return numericToString((long) i, system);
  }

  /**
   * 将其它进制的数字（字符串形式）转换为十进制的数字。
   *
   * @param s 其它进制的数字（字符串形式）
   * @param system 指定的进制，常见的2/8/16。
   * @return 转换后的数字。
   */
  public static long stringToNumeric(String s, int system) {
    char[] buf = new char[s.length()];
    s.getChars(0, s.length(), buf, 0);
    long num = 0;
    for (int i = 0; i < buf.length; i++) {
      for (int j = 0; j < digits.length; j++) {
        if (digits[j] == buf[i]) {
          num += j * Math.pow(system, buf.length - i - 1);
          break;
        }
      }
    }
    return (long) num;
  }

  /**
   * 获得uuid字符串
   *
   * @return
   */
  public static String getUUIDString() {
    //  Auto-generated method stub
    String uuid =
        numericToString(System.currentTimeMillis(), 32)
            + numericToString(new Random().nextInt(100000000), 32);
    return uuid;
  }

  /**
   * 根据参数,生成新的jobNumber
   *
   * @param oldJobNumber
   * @return String JobNumber
   */
  public static String getJobNumber(String oldJobNumber) {
    if (StringUtils.isBlank(oldJobNumber)) {
      return null;
    }
    int jobNumberInt = Integer.parseInt(oldJobNumber);
    jobNumberInt = jobNumberInt + 1;
    String jobNumber;
    int numLen = String.valueOf(jobNumberInt).length();
    switch (numLen) {
      case 3:
        jobNumber = String.valueOf(jobNumberInt);
        break;
      case 2:
        jobNumber = "0" + String.valueOf(jobNumberInt);
        break;
      case 1:
        jobNumber = "00" + String.valueOf(jobNumberInt);
        break;
      default:
        jobNumber = null;
        break;
    }
    return jobNumber;
  }
}
