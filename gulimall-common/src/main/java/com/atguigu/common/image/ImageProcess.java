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

    public static final String inputPath = "D:\\a";
    public static final String outputPath = "D:\\a";

    public static void main(String[] args) {
        List<File> files = getAllFile(inputPath);
        if (!files.isEmpty()) {
            for (File file : files) {
                try {
                    String extension = FilenameUtils.getExtension(file.getPath());
                    ImageModel model = new ImageModel(extension);
                    BufferedImage bufferedImage = ImageUtil.readImageFile(file, outputPath);
                    model = initModel(model, bufferedImage);
                    combineNewImage(model, outputPath, bufferedImage);
                    log.info("process done with file = {}", file.getPath());
                } catch (Exception e) {
                    log.error("can't process with file = {}", file.getPath());
                }
            }
        }
    }
}
