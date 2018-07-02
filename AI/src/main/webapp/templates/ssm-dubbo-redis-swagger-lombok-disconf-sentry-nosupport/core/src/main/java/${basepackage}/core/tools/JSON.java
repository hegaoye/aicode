/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */

package ${basePackage}.core.tools;

import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON 格式化工具类
 * Created by lixin on 2017/8/19.
 */
public class JSON {
    public static void main(String[] args) {
        String jsonStr = "{\"id\":\"1\",\"name\":\"a1\",\"obj\":{\"id\":11,\"name\":\"a11\",\"array\":[{\"id\":111,\"name\":\"a111\"},{\"id\":112,\"name\":\"a112\"}]}}";
        Map<String, Object> map = new HashMap<>();
        map.put("a", new Date());
        map.put("b", 12);
        map.put("c", 1235.34f);
        String fotmatStr = JSON.toJSONString(map);
//      fotmatStr = fotmatStr.replaceAll("\n", "<br/>");
//      fotmatStr = fotmatStr.replaceAll("\t", "    ");
        System.out.println(fotmatStr);
    }

    /**
     * 把对象转为json后格式化为可视化
     *
     * @param obj
     * @return
     */
    public static String toJSONString(Object obj) {
        return toJSONString(com.alibaba.fastjson.JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat));
    }

    public static String toJSONString(Object obj, SerializerFeature... features) {
        return toJSONString(com.alibaba.fastjson.JSON.toJSONString(obj, features));
    }

    /**
     * 格式化JSON 字符串数据
     * 退格用\t
     * 换行用\r
     *
     * @param json
     * @return 格式化JSON数据
     */
    public static String toJSONString(String json) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }

        return jsonForMatStr.toString();

    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

}
