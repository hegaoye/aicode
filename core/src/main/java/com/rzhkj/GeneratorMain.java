package com.rzhkj;

/**
 * @author lixin
 * @email hegaoye@qq.com
 */

public class GeneratorMain {
    public static void main(String[] args) throws Exception {
        Generator g = new Generator();

        g.clean();
        g.generateTable("blog");
//		g.generateAllTable();
    }
}
