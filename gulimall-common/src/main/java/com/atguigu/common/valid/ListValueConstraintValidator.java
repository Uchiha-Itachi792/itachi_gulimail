package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

//两个泛型，第一个就是自定义校验注解ListValue
//第二个就是被校验字段的属性
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    Set<Integer> set = new HashSet<>();

    /**
     * 初始化
     * @param constraintAnnotation 自定义校验注解对象
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        //通过自定义校验注解对象，获取到校验规则，此例中也就是[1,0]
        int[] vals = constraintAnnotation.vals();
        for (int val : vals) {
            set.add(val);
        }
    }

    /**
     * 真正的校验规则
     * 判断是否校验成功
     *
     * @param value 被检验字段的值
     * @param constraintValidatorContext 校验上下文
     * @return true代表校验成功
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        //校验，判断字段的值是否是1或者0
        return set.contains(value);
    }
}