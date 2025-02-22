package com.atguigu.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented //无意义，但必须标注
@Constraint(validatedBy = {ListValueConstraintValidator.class}) //结合校验规则
//可以标注的地方
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {

    //发生校验失败之后的默认提示信息
    String message() default "检验失败!";

    Class<?>[] groups() default {}; //校验分组

    Class<? extends Payload>[] payload() default {};

    int[] vals() default {}; //用于接受自定义校验规则

}