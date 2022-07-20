package cn.yusiwen.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.yusiwen.excel.handler.AbstractListAnalysisEventListener;
import cn.yusiwen.excel.handler.DefaultAnalysisEventListener;

/**
 * 导入excel
 *
 * @author yusiwen
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportExcel {

    /**
     * 前端上传字段名称 file
     *
     * @return File name
     */
    String fileName() default "file";

    /**
     * 读取的监听器类
     *
     * @return readListener
     */
    Class<? extends AbstractListAnalysisEventListener<?>> readListener() default DefaultAnalysisEventListener.class;

    /**
     * 是否跳过空行
     *
     * @return 默认跳过
     */
    boolean ignoreEmptyRow() default false;

}
