package ${basePackage}.core.tools;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.*;

public class StringTools {

    /**
     * 判断对象不为空
     *
     * @param obj
     * @return boolean
     */
    public static boolean isEmpty(Object obj){
        boolean flag=false;
        if(obj==null){
            flag=true;
        }else if(obj.toString().length()<1){
            flag=true;
        }
        return flag;
    }


    /**
     * 判断对象不为空
     *
     * @param obj
     * @return boolean
     */
    public static boolean isNotEmpty(Object obj){
        boolean flag=true;
        if(obj==null){
            flag=false;
        }else if(obj.toString().length()<1){
            flag=false;
        }
        return flag;
    }
	
	
	 /** 
     * 去除Hql or Sql的select 子句，未考虑union的情况
     */ 
	public static String removeSelectToPageQuery(String shql) { 
        int beginPos = shql.toLowerCase().indexOf("from"); 
        return shql.substring(beginPos); 
    }
		
	
    /** 
     * 去除Hql or Sql的orderby 子句
     */ 
	public static String removeOrdersToPageQuery(String shql) { 
		//匹配正则表达式必须使用的是'\\'，而不是'//'
        Pattern p = compile("order\\s*by\\s*[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(shql); 
        StringBuffer sb = new StringBuffer(); 
        while (m.find()) { 
            m.appendReplacement(sb, ""); 
        } 
        m.appendTail(sb); 
        return sb.toString(); 
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
     * 封装模糊搜索的关键字
     * @param keywords
     * @param keys
     * @return
     */
    public static String packkeywords(String keywords,Object... keys){
        if(StringUtils.isBlank(keywords))return null;
        if(keys==null)return null;
        keywords=StringTools.escapSqlSpecialWord(keywords);
        StringBuffer sqlsb=new StringBuffer();
        sqlsb.append(" 1=2 ");
        for(Object key:keys){
            sqlsb.append(" or ");
            sqlsb.append(key+" like '%"+keywords+"%'");
        }
        return sqlsb.toString();
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
