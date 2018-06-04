package ${basePackage}.core.tools;

import com.alibaba.fastjson.JSON;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class StringTools {

	/**
     * 判断字符串不为空
     *
     * @param str
     * @return boolean
     */
	public static boolean isNotEmpty(String str){
		boolean flag=true;
		if(str==null){
			flag=false;
		}else if(str.trim().length()<1){
			flag=false;
		}
		return flag; 
	}

    /**
     * 判断对象为空
     *
     * @param obj
     * @return boolean
     */
    public static boolean isEmpty(Object obj){
        boolean flag=true;
        if(null==obj){
            return flag;
        }else if(obj.toString().trim().length()<1){
            return flag;
        }
        return false;
    }
	
	/**
     * 将字符串中的中文转化为拼音,其他不变
     * @param inputString
     * @return String
     */
    public static String getPinYin(String inputString){
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);//小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//不知道这个是什么意思 写完查api看看
        format.setVCharType(HanyuPinyinVCharType.WITH_V);//
         
        char[] input = inputString.trim().toCharArray();
        String output="";
         
        try {
            for(int i = 0; i < input.length; i++){
                if(Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")){
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i],format);
                        output+=temp[0];
                }else{
                    output+=input[i];
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
    /**
     * 获取汉字串拼音首字母，英文字符不变
     * @param chinese  汉字串 
     * @return String
     */
    public static String getFirstSpell(String chinese){
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
         
        for(int i = 0;i<arr.length;i++){
            if(arr[i]>128){
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i],defaultFormat);
                    if(temp!=null){
                        pybf.append(temp[0].charAt(0));
                    }else{
                        pybf.append(arr[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
            	 pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }
    /**
     * 获取汉字串拼音，英文字符不变  
     * @param chinese  
     * @return String 
     */
    public static String getFullSpell(String chinese){
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
         
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
         
        try {
            for(int i = 0;i<arr.length;i++){
                if(arr[i]>128){
                        pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                }else{
                    pybf.append(arr[i]);
                }
            }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            return pybf.toString();
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|", "'", "\""};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
    /**
     * 转义sql特殊字符
     * @param keyword
     * @return
     */
    public static String escapSqlSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|","'", "\"","\b","\n","\t","\f" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    /**
     * 把 Object2 中的值设置到 Object1 中
     * @param Object1
     * @param Object2
     * @param calsz
     * @return
     */
    public static Object mergeObject(Object Object1,Object Object2,Class calsz){
        Map newPara=(Map) JSON.toJSON(Object1);
        Map oldPara=(Map) JSON.toJSON(Object2);
        for(Object key:newPara.keySet()){
            if(newPara.get(key)==null||newPara.get(key).toString().length()<1){
                if(oldPara.get(key)!=null&&oldPara.get(key).toString().length()>0){
                    newPara.put(key,oldPara.get(key));
                }
            }
        }
        return JSON.parseObject(JSON.toJSONString(newPara),calsz);
    }

}
