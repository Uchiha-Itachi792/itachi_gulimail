package com.atguigu.gulimall.gulimallproduct.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpuItemAttrGroupVO {
    private String groupName;
    private List<Attr> attrs;
}
