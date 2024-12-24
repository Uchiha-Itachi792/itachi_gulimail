package com.atguigu.common.image;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FileUtils {

    public static List<File> getAllFile(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            return new ArrayList<>(Arrays.asList(files));
        }
        return null;
    }

    public static void moveFile(String sourcePath, String targetPath) {

        File source = new File(sourcePath);
        File target = new File(targetPath);
        if (!source.renameTo(target)) {
            log.error("can't move with sourcePath {}", sourcePath);
        }

    }

    public static void main(String[] args) {
        log.info("hahaha");
        String path = "D:\\a\\a.jpg";
        String topath = "D:\\a\\a.jpg";
        new FileUtils().joinImagesHorizontal(path, path, "jpg", topath);

    }

    public void joinImagesHorizontal(String firstSrcImagePath, String secondSrcImagePath, String imageFormat, String toPath) {
        try {
            //读取第一张图片
            File fileOne = new File(firstSrcImagePath);
            if (fileOne.isFile() && fileOne.exists()) {
                System.out.println("");
            }
            BufferedImage imageOne = ImageIO.read(fileOne);
            int width = imageOne.getWidth();//图片宽度
            int height = imageOne.getHeight();//图片高度
            //从图片中读取RGB
            int[] imageArrayOne = new int[width * height];
            imageArrayOne = imageOne.getRGB(0, 0, width, height, imageArrayOne, 0, width);

            //对第二张图片做相同的处理
            File fileTwo = new File(secondSrcImagePath);
            BufferedImage imageTwo = ImageIO.read(fileTwo);
            int width2 = imageTwo.getWidth();
            int height2 = imageTwo.getHeight();
            int[] ImageArrayTwo = new int[width2 * height2];
            ImageArrayTwo = imageTwo.getRGB(0, 0, width, height, ImageArrayTwo, 0, width);

            //生成新图片
            //int height3 = (height>height2 || height==height2)?height:height2;
            BufferedImage imageNew = new BufferedImage(width * 2, height, BufferedImage.TYPE_INT_RGB);
            //BufferedImage  imageNew  =  new  BufferedImage(width+width2,height3,BufferedImage.TYPE_INT_RGB);
            imageNew.setRGB(0, 0, width, height, imageArrayOne, 0, width);//设置左半部分的RGB
            imageNew.setRGB(width, 0, width, height, ImageArrayTwo, 0, width);//设置右半部分的RGB
            //imageNew.setRGB(width,0,width2,height2,ImageArrayTwo,0,width2);//设置右半部分的RGB

            File outFile = new File(toPath);
            ImageIO.write(imageNew, imageFormat, outFile);//写图片
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
