package cn.yusiwen.excel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import cn.yusiwen.excel.aop.DynamicNameAspect;
import cn.yusiwen.excel.aop.ExportExcelReturnValueHandler;
import cn.yusiwen.excel.aop.ImportExcelArgumentResolver;
import cn.yusiwen.excel.config.ExcelConfigProperties;
import cn.yusiwen.excel.processor.NameProcessor;
import cn.yusiwen.excel.processor.NameSpelExpressionProcessor;

/**
 * @author yusiwen
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Import(ExcelHandlerConfiguration.class)
@EnableConfigurationProperties(ExcelConfigProperties.class)
public class ResponseExcelAutoConfiguration {

    /**
     * RequestMappingHandlerAdapter
     */
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * ResponseExcelReturnValueHandler
     */
    private final ExportExcelReturnValueHandler exportExcelReturnValueHandler;

    /**
     * SPEL 解析处理器
     *
     * @return NameProcessor excel名称解析器
     */
    @Bean
    @ConditionalOnMissingBean
    public NameProcessor nameProcessor() {
        return new NameSpelExpressionProcessor();
    }

    /**
     * Excel名称解析处理切面
     *
     * @param nameProcessor
     *            SPEL 解析处理器
     * @return DynamicNameAspect
     */
    @Bean
    @ConditionalOnMissingBean
    public DynamicNameAspect dynamicNameAspect(NameProcessor nameProcessor) {
        return new DynamicNameAspect(nameProcessor);
    }

    /**
     * 追加 Excel返回值处理器 到 springmvc 中
     */
    @PostConstruct
    public void setReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter
                .getReturnValueHandlers();

        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(exportExcelReturnValueHandler);
        assert returnValueHandlers != null;
        newHandlers.addAll(returnValueHandlers);
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }

    /**
     * 追加 Excel 请求处理器 到 springmvc 中
     */
    @PostConstruct
    public void setRequestExcelArgumentResolver() {
        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        if (argumentResolvers == null) {
            return;
        }
        List<HandlerMethodArgumentResolver> resolverList = new ArrayList<>();
        resolverList.add(new ImportExcelArgumentResolver());
        resolverList.addAll(argumentResolvers);
        requestMappingHandlerAdapter.setArgumentResolvers(resolverList);
    }

}
