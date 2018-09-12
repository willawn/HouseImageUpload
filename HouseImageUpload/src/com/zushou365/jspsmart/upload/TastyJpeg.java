package com.zushou365.jspsmart.upload;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class TastyJpeg {
        private int wideth;
        private int height;
        private String t = null;

        public void setT(String t) {
                this.t = t;
        }

        public void setWideth(int wideth) {
                // wideth=320;
                this.wideth = wideth;
        }

        public int getWideth() {
                return this.wideth;
        }

        public void setHeight(int height) {
                // height=240;
                this.height = height;
        }

        public int getHeight(int w, int h) // former images size
        {
                // int hhh;
                if (w > wideth) {
                        float ww;
                        ww = (float) w / (float) wideth;
                        float hh = h / ww;
                        return (int) hh;
                } else {
                        this.setWideth(w);
                        return h;
                }

        }

        public void proce(String fpath) throws Exception {
                File _file = new File(fpath);
                Image src = javax.imageio.ImageIO.read(_file);
                int wideth = src.getWidth(null);
                int height = src.getHeight(null);
                int h = this.getHeight(wideth, height);
                BufferedImage tag = new BufferedImage(this.getWideth(), h,
                                BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.drawImage(src, 0, 0, this.getWideth(), h, null);
                if (t != null) {
                        g.setColor(new Color(242, 242, 242));
                        g.fillRect(this.getWideth() - 120, h - 18, 120, 18);
                        g.setColor(new Color(180, 180, 180));
                        g.drawRect(this.getWideth() - 120, h - 18, 119, 17);
                        g.setColor(new Color(255, 102, 0));
                        g.drawString(t, this.getWideth() - 100, h - 5);
                }
                FileOutputStream out = new FileOutputStream(fpath);
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                encoder.encode(tag);
                out.close();
        }

        /**
         * 压缩图片方法
         * 
         * @param oldFile
         *            将要压缩的图片
         * @param width
         *            压缩宽
         * @param height
         *            压缩长
         * @param quality
         *            压缩清晰度 <b>建议为1.0</b>
         * @param smallIcon
         *            压缩图片后,添加的扩展名
         * @return
         */
        public String proce1(String oldFile, int width, int height, float quality,
                        String smallIcon) {
                if (oldFile == null) {
                        return null;
                }
                String newImage = null;
                try {
                        File file = new File(oldFile);
                        if (!file.exists()) // 文件不存在时
                                return null;
                        /** 对服务器上的临时文件进行处理 */
                        Image srcFile = ImageIO.read(file);

                        /** 宽,高设定 */
                        BufferedImage tag = new BufferedImage(width, height,
                                        BufferedImage.TYPE_INT_RGB);
                        tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
                        newImage = oldFile;
                        // newImage = smallIcon;
                        /** 压缩之后临时存放位置 */
                        FileOutputStream out = new FileOutputStream(newImage);

                        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                        JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
                        /**
                         * 压缩质量 创建替代当前已建量化表的新量化表。它也将 Component QTable 映射更新为当前已编码 COLOR_ID
                         * 的缺省值。根据质量参数的不同，创建的量化表介于压缩比很高但质量很差 (0.0) 与压缩比低但质量很好 (1.0) 之间。
                         * 
                         * 在 1.0 的质量水平，表的值将都是 1。这样就不会由于量化而丢失数据，但色度二次抽样（如果使用）和 DCT
                         * 中的舍入误差会在某种程度上降低图像质量。
                         * 
                         * 以下为标准 Chrominance Q-Table 的线性操作。
                         * 
                         * Some guidelines: 0.75 high quality 0.5 medium quality 0.25 low
                         * quality
                         */
                        jep.setQuality(quality, true);
                        encoder.encode(tag, jep);

                        out.close();
                        srcFile.flush();

                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return newImage;
        }

        //
        public static void main(String str[]) {
                TastyJpeg ps = new TastyJpeg();
                try {
                        System.out.println(ps.proce1("D:\\training\\house\\pic\\201041210555121682.jpg", 120, 160,
                                        0.6f, "1"));
                       /* System.out.println(ps.proce1("d:/temp/test.jpg", 450, 500,
                                        1, "2"));*/
                        System.out.print("成功哦");
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
}