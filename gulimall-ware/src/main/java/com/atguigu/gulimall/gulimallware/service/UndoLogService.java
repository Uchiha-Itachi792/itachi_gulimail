package com.atguigu.gulimall.gulimallware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.gulimallware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-16 16:45:03
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

