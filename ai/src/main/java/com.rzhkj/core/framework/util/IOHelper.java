package com.rzhkj.core.framework.util;

import java.io.*;

/**
 * @author lixin
 * @email hegaoye@qq.com
 */
public class IOHelper {

    public static void copy(Reader in, Writer out) throws IOException {
        int c = -1;
        while ((c = in.read()) != -1) {
            out.write(c);
        }
    }

    public static String readFile(File file) throws IOException {
        Reader in = new FileReader(file);
        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }

    public static void saveFile(File file, String content) throws IOException {
        Writer writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

}
