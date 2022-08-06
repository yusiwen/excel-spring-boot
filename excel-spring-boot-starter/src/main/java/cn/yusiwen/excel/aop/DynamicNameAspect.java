package cn.yusiwen.excel.aop;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import cn.yusiwen.excel.annotation.ExportExcel;
import cn.yusiwen.excel.kit.ExcelException;
import cn.yusiwen.excel.processor.NameProcessor;

/**
 * @author yusiwen
 */
@Aspect
@RequiredArgsConstructor
public class DynamicNameAspect {

    /**
     * Excel name key
     */
    public static final String EXCEL_NAME_KEY = "__EXCEL_NAME_KEY__";

    /**
     * NameProcessor
     */
    private final NameProcessor processor;

    @Before("@annotation(excel)")
    public void around(JoinPoint point, ExportExcel excel) {
        MethodSignature ms = (MethodSignature)point.getSignature();

        String name = excel.name();
        // 当配置的 excel 名称为空时，取当前时间
        if (!StringUtils.hasText(name)) {
            name = LocalDateTime.now().toString();
        } else {
            name = processor.doDetermineName(point.getArgs(), ms.getMethod(), excel.name());
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new ExcelException("RequestAttributes is null");
        }
        requestAttributes.setAttribute(EXCEL_NAME_KEY, name, RequestAttributes.SCOPE_REQUEST);
    }

}
