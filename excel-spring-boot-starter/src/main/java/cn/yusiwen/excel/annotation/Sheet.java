package cn.yusiwen.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.yusiwen.excel.head.HeadGenerator;

/**
 * @author yusiwen
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sheet {

    int sheetNo() default -1;

    /**
     * Sheet name
     *
     * @return Sheet name
     */
    String sheetName();

    /**
     * 包含字段
     *
     * @return Includes
     */
    String[] includes() default {};

    /**
     * 排除字段
     *
     * @return Excludes
     */
    String[] excludes() default {};

    /**
     * 头生成器
     *
     * @return HeadGenerator classes
     */
    Class<? extends HeadGenerator> headGenerateClass() default HeadGenerator.class;

}
