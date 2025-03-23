package com.atguigu.gulimall.gulimallproduct.vo;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateGroup;
import com.atguigu.common.valid.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

@Data
public class BrandVo {
//    private Long brandId;
//    private String brandName;
//    private String logo;
//    private String descript;
//    private Integer showStatus;
//    private String firstLetter;
//    private Integer sort;

    private Long brandId;
    private String  brandName;
}
