/**
 * 缩略图实现，将图片(jpg、bmp、png、gif等等)真实的变成想要的大小
 */
package ${basePackage}.core.tools;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*******************************************************************************
 * 缩略图类（通用） 本java类能将jpg、bmp、png、gif图片文件，进行等比或非等比的大小转换。 具体使用方法
 * compressPic(大图片路径,生成小图片路径,大图片文件名,生成小图片文名,生成小图片宽度,生成小图片高度,是否等比缩放(默认为true))
 */
public enum CompressPic {
    INSTANCE;
    protected Log logger = LogFactory.getLog(CompressPic.class);

    // 图片处理
    public String compressPic(String inputDir, String outputDir, String inputFileName, String outputFileName, int width, int height, boolean gp) {
        try {
            // 输入图路径
            inputDir += "/";
            // 输出图路径
            outputDir += "/";

            // 获得源文件
            File file = new File(inputDir + inputFileName);
            if (!file.exists()) {
                return "";
            }
            Image img = ImageIO.read(file);
            // 判断图片格式是否正确
            if (img.getWidth(null) == -1) {
                return "no";
            } else {
                int newWidth;
                int newHeight;
                // 判断是否是等比缩放
                if (gp) {
                    //新修改代码，伯谦修改-2013-11-01 begin
                    //获得老的宽高
                    double oldwidth = img.getWidth(null);
                    double oldheight = img.getHeight(null);
                    // 为等比缩放计算输出的图片宽度及高度
                    double rate1 = (double) width / ((double) oldwidth);
                    double rate2 = (double) height / ((double) oldheight);
                    if (oldwidth <= width && oldheight <= height) {
                        //宽度和高度均比限定小
                        newWidth = (int) oldwidth;
                        newHeight = (int) oldheight;
                    } else if (oldwidth > width && oldheight <= height) {
                        //宽度溢出
                        newWidth = width;
                        newHeight = (int) (oldheight * rate1);
                        logger.info(oldheight + "==" + rate1);
                    } else if (oldwidth <= width && oldheight > height) {
                        //高度溢出
                        newWidth = (int) (oldwidth * rate2);
                        newHeight = height;
                    } else {
                        //宽度和高度均溢出
                        if (rate1 > rate2) {  //高度缩放比例小，则按照高度缩放
                            newWidth = (int) (oldwidth * rate2);
                            newHeight = height;
                        } else {
                            //宽度缩放比例小，则按照宽度缩放
                            newWidth = width;
                            newHeight = (int) (oldheight * rate1);
                        }
                    }
                    //新修改代码，伯谦修改-2013-11-01 end

                } else {
                    newWidth = width; // 输出的图片宽度
                    newHeight = height; // 输出的图片高度
                }
                BufferedImage tag = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_RGB);

				/*
                 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
				 */
                tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
                FileOutputStream out = new FileOutputStream(outputDir + outputFileName);
                // JPEGImageEncoder可适用于其他图片类型的转换
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                encoder.encode(tag);
                out.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return outputDir + outputFileName;
    }
}
