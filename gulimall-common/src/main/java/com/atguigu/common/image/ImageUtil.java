package com.atguigu.common.image;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Slf4j
public class ImageUtil {

    private static Double RATE = 1.785;

    public static BufferedImage readImageFile(File file, String targetPath) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();//图片宽度
        int height = image.getHeight();//图片高度
        if (width > height) {
            FileUtils.moveFile(file.getPath(), targetPath + file.getName());
            return null;
        }
        return image;
    }

    public static ImageModel initModel(ImageModel imageModel, BufferedImage image) throws IOException {
        if (image == null) {
            return null;
        }
        int width = image.getWidth();//图片宽度
        imageModel.setWidth(width);

        int height = image.getHeight();//图片高度
        imageModel.setHeight(height);

        double targetWidth = height * RATE;
        int pictureNum = (int) targetWidth / width + 1;
        int eachWidth = (int) targetWidth / pictureNum;
        imageModel.setTargetWidth(targetWidth);
        imageModel.setPictureNum(pictureNum);
        imageModel.setEachWidth(eachWidth);

//        int[] imageArray = new int[eachWidth * height];
//        imageArray = image.getRGB(0, 0, width, height, imageArray, 0, width);
//        imageModel.setImageArray(imageArray);

        return imageModel;
    }

    public static void combineNewImage(ImageModel imageModel, String targetPath, BufferedImage image) throws IOException {
        if(imageModel == null) {
            return;
        }
        int targetWidth = (int) imageModel.getTargetWidth() + 10;
        BufferedImage imageNew = new BufferedImage(targetWidth, imageModel.getHeight(), BufferedImage.TYPE_INT_RGB);

        int num = imageModel.getPictureNum();

        int indexWidth = 0;
        for (int i = 0; i < num; i++) {
            int[] imageArray = new int[imageModel.getEachWidth() * imageModel.getHeight()];
            imageArray = image.getRGB(0, 0, imageModel.getEachWidth(), imageModel.getHeight(), imageArray, 0, imageModel.getEachWidth());
            imageNew.setRGB(indexWidth, 0, imageModel.getEachWidth(), imageModel.getHeight(), imageArray, 0, imageModel.getEachWidth());
            indexWidth = imageModel.getEachWidth() + indexWidth;
        }

        File outFile = new File(targetPath + "\\" + System.currentTimeMillis() + "." + imageModel.getSuffix());
        ImageIO.write(imageNew, imageModel.getSuffix(), outFile);//写图片
    }

}
