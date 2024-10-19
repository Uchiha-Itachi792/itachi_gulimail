package com.atguigu.common.image;

import lombok.Data;

@Data
public class ImageModel {
    private int width;
    private int height;
    private double targetWidth;
    private int pictureNum;
    private int eachWidth;
    //private  int[] imageArray;
    private String suffix;

    public ImageModel(int width, int height, double targetWidth, int pictureNum, int eachWidth) {
        this.width = width;
        this.height = height;
        this.targetWidth = targetWidth;
        this.pictureNum = pictureNum;
        this.eachWidth = eachWidth;
    }

    public ImageModel(String suffix){
        this.suffix =suffix;
    }
}
