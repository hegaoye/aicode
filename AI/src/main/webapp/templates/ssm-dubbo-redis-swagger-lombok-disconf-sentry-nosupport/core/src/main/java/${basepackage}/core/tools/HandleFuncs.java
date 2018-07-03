/**
 * @公司名称：仁中和科技
 * @文件名：HandleFunc.java
 * @作者：wangchuan 版本号：1.0
 * @生成日期：Mar 16, 2010
 */
package ${basePackage}.core.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleFuncs {
    static Logger logger = LoggerFactory.getLogger(HandleFuncs.class);


    // ////////////////////////////////////基本操作/////////////////////////////////////////////

    /**
     * 判断是否是数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否是手机号码
     */
    public static boolean isPhoneNumber(String str) {
        Pattern pattern = Pattern.compile("^1\\d{10}$");
        return pattern.matcher(str).matches();
    }

    /**
     * 格式化彩信标题
     */
    public String fomartMmsTitle(String title) {
        if (title == null || title.length() == 0) {
            title = "彩信";
        }
        return "[" + title + "]";
    }

    /**
     * 生成唯一的uuid
     */
    @SuppressWarnings("unused")
    public String uuidGenerate() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    /**
     * 文件转化为字节数组
     */
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 数组去重复
     */
    public static String[] toDiffArray(String[] s) {
        Set<String> set = new HashSet<String>();
        for (String sa : s) {
            set.add(sa);
        }
        return set.toArray(new String[]{});
    }

    /**
     * 字符串数组去重复
     */
    public static String toDiffString(String s) {
        if (s == null || !s.contains(",")) {
            return null;
        }
        // 去重复
        String[] str_arr = toDiffArray(s.split(","));
        // 循环组合
        StringBuffer sb = new StringBuffer();
        for (String string : str_arr) {
            sb.append("," + string);
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString().substring(1);
    }

    // ////////////////////////////////////文件操作////////////////////////////////////////////

    /**
     * 文件copy
     *
     * @throws IOException
     */
    public void copyTo(File in, File out) throws IOException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }

    /**
     * 函数介绍：得到扩展名 参数： 返回值：
     */
    public String getExtention(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos);
    }

    /**
     * 函数介绍：组合编码 参数：MdsConsumerArea Area 返回值：ExtMessage
     */
    public String returnNewCode(String code, String parCode) {
        if (parCode == null || parCode.equals("0")) {
            code = code + ".";
        } else {
            code = parCode + code + ".";
        }
        return code;
    }

    public boolean chkEmail(String email) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     *
     * @Enclosing_Method : getRemoteIp
     * @Written by : liyanping
     * @Creation Date : 2010-7-9 下午03:01:53
     * @version : v1.00
     * @Description :
     * @return : String
     *
     * @return
     *
     */
//	public static String getRemoteIp() {
//		String remoteIp = null;
//		try {
//
//			MessageContext context = MessageContext.getCurrentMessageContext();
//			HttpServletRequest request = (HttpServletRequest) context
//					.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
//			remoteIp = request.getRemoteAddr();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return remoteIp;
//	}

    /**
     * 获取本机的类
     */
    public String getCurrentClassPath() {
        try {
            String str = this.getClass().getClassLoader().getResource("").getPath();
            URLDecoder ud = new URLDecoder();
            str = ud.decode(str, "utf-8");
            int pos = str.indexOf("/WEB-INF/classes/");
            if (pos > 0) {
                str = str.substring(0, pos);
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据日志类型获取日志的模板
     * @param logtype
     * @return
     */
//	public String getLogTypeModel(String logtype)
//	{
//		return (String)StaticUtil.logtype_map.get(logtype);
//	}


    /**
     * 根据service类型获取service地址
     * @param logtype
     * @return
     */
//	public String getServiceUri(String type)
//	{
//		//返回值
//		return (String)StaticUtil.service_uri_map.get(type);
//	}

    /**
     * 获取本级的IP地址
     */
    public String getLocalIpAddress() {
        try {
            InetAddress inet = InetAddress.getLocalHost();
            String ip = inet.getHostAddress();
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 生成随机数
     *
     * @return
     */
    public static String getRandomString() {
        int random = new Random().nextInt(100);
        String str = Long.toString(System.currentTimeMillis()) + random;
        return str;
    }

    /**
     * @param
     * @return
     * @author 立坤 更新于2015.04.07
     * 生成订单编号
     */
    public String genOrderCode() {
//    public String genOrderCode(String phone){
//        int randomNumber = new Random().nextInt(8999) + 1000;
//        long time_sub = System.currentTimeMillis() - DateUtil.stringToDate("20130101000000").getTime();
//        String str=Long.toString(time_sub)+randomNumber+phone.substring(8);

        int r1 = (int) (Math.random() * (10));//产生2个0-9的随机数
        int r2 = (int) (Math.random() * (10));
        long now = System.currentTimeMillis();//一个13位的时间戳
        String paymentID = String.valueOf(r1) + String.valueOf(r2) + String.valueOf(now);// 订单ID
        return paymentID;
    }

    /**
     * @return
     * @author 立坤更新于 2015.05.25
     * 生成登录号（账号）
     */
    public String genLoginCode() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++) {
            result = result * 10 + array[i];
        }
        String str = result + "";
        logger.info(str.length() + "");
        if (str.length() < 4) {
            genLoginCode();
        }
        int random0 = (int) Math.round(Math.random() * 25 + 97);
        int random1 = (int) Math.round(Math.random() * 25 + 97);
        char temp0 = (char) random0;
        char temp1 = (char) random1;
        return temp0 + "" + temp1 + result;
    }


    public static void main(String[] args) {
        HandleFuncs hand = new HandleFuncs();
        logger.info(hand.genOrderCode() + "**");
        logger.info(hand.genOrderCode() + "**");
        logger.info(hand.genOrderCode() + "**");
        logger.info(hand.genOrderCode() + "**");
        logger.info(hand.genOrderCode() + "**");

//        String phone = "13460005569";
//        HandleFuncs hand = new HandleFuncs();
//        logger.info("hand-------" + hand.genOrderCode("13460005569"));
//        int randomNumber = new Random().nextInt(8999) + 1000;
//        logger.info("randoe----" + randomNumber);
//        // 获取当前年
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int date = c.get(Calendar.DATE);
//        logger.info("year--" + year);
//        logger.info("month--" + month);
//        logger.info("date--" + date);
//        c.set(year, month, date-1, 23, 59, 59);
//        logger.info("c---" + DateUtil.dateToNum14(c.getTime()));
//        // 获得当前年的上一秒
//        Date prev_second = c.getTime();
//        long time_sub = new Date().getTime() - prev_second.getTime();
//        logger.info("time_sub---" + time_sub);
//        String str =(year - 2012) + "" +  month + "" + date + "" + Long.toString(time_sub) + randomNumber + phone.substring(7);
//        logger.info("str:" + str);
    }

}
