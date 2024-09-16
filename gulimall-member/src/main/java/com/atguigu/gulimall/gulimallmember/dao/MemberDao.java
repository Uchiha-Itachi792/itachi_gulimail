package com.atguigu.gulimall.gulimallmember.dao;

import com.atguigu.gulimall.gulimallmember.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-16 15:34:26
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
