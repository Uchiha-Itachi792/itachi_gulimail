package com.atguigu.common.image;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static com.atguigu.common.image.FileUtils.getAllFile;
import static com.atguigu.common.image.ImageUtil.combineNewImage;
import static com.atguigu.common.image.ImageUtil.initModel;
@Slf4j
public class ImageProcess {

    public static final String inputPath = "D:\\a\\";
    public static final String outputPath = "D:\\a\\completed\\";

    public static void main(String[] args) {
        List<File> files = getAllFile(inputPath);
        int num = 0;
        if (!files.isEmpty()) {
            for (File file : files) {
                try {
                    if(!file.isDirectory()) {
                        String extension = FilenameUtils.getExtension(file.getPath());
                        ImageModel model = new ImageModel(extension);
                        BufferedImage bufferedImage = ImageUtil.readImageFile(file, outputPath);
                        model = initModel(model, bufferedImage);
                        combineNewImage(model, outputPath, bufferedImage);
                        num++;
                        log(num);
                    }
                } catch (Exception e) {
                    log.error("can't process with file = {}, cause by  {}", file.getPath(), e);
                }
            }
        }
    }

    public static void log(int fileNum) {
        if(fileNum % 100 ==0) {
            log.info("process done with fileNum = {}",fileNum);
        }
    }
}
