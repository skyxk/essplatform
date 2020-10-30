package com.chen.core.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图片工具类
 * @author 水寒
 * 欢迎访问水寒的个人博客：http://www.sunhome.org.cn
 *
 */
public class ImageUtil {

    public static void main(String[] args) throws IOException {
        transparentImage("D:\\demo.gif","D:\\demo1.gif",100,"gif");
    }
    /**
     * 设置源图片为背景透明，并设置透明度
     * @param srcFile 源图片
     * @param desFile 目标文件
     * @param alpha 透明度
     * @param formatName 文件格式
     * @throws IOException
     */
    public static void transparentImage(String srcFile,String desFile,int alpha,String formatName) throws IOException {
        BufferedImage temp =  ImageIO.read(new File(srcFile));//取得图片
        transparentImage(temp, desFile, alpha, formatName);
    }
    /**
     * 设置源图片为背景透明，并设置透明度
     * @param srcImage 源图片
     * @param desFile 目标文件
     * @param alpha 透明度
     * @param formatName 文件格式
     * @throws IOException IO异常
     **/
    public static void transparentImage(BufferedImage srcImage,
                                        String desFile, int alpha, String formatName) throws IOException {
        int imgHeight = srcImage.getHeight();//取得图片的长和宽
        int imgWidth = srcImage.getWidth();
        int c = srcImage.getRGB(3, 3);
        //防止越位
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 10) {
            alpha = 10;
        }
        BufferedImage bi = new BufferedImage(imgWidth, imgHeight,
                BufferedImage.TYPE_4BYTE_ABGR);//新建一个类型支持透明的BufferedImage
        for(int i = 0; i < imgWidth; ++i)//把原图片的内容复制到新的图片，同时把背景设为透明
        {
            for(int j = 0; j < imgHeight; ++j)
            {
                // 0x00FF0000 红色  16777215白色FFFFFF
//                System.out.println(srcImage.getRGB(i, j));
//                System.out.println(0x00FF0000);
//                if(srcImage.getRGB(i, j) == 16777215){
//                    bi.setRGB(i, j, 0x00FF0000);
//                }
//                bi.setRGB(i, j, 0x00FF0000);
//                c = 16777215;
                //把背景设为透明
                if(srcImage.getRGB(i, j) == c || srcImage.getRGB(i, j) == 0x00ffffff ){
                    bi.setRGB(i, j, c & 0x00ffffff);
                }
                //设置透明度
                else{
                    int rgb = bi.getRGB(i, j);
                    rgb = ((alpha * 255 / 10) << 24) | (0x00FF0000 & 0x00ffffff);
                    bi.setRGB(i, j, rgb);
                }
            }
        }
        ImageIO.write(bi, formatName, new File(desFile));
    }
}