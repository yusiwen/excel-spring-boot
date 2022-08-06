package cn.yusiwen.excel;

import com.alibaba.excel.converters.Converter;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

import cn.yusiwen.excel.aop.ExportExcelReturnValueHandler;
import cn.yusiwen.excel.config.ExcelConfigProperties;
import cn.yusiwen.excel.enhance.DefaultWriterBuilderEnhancer;
import cn.yusiwen.excel.enhance.WriterBuilderEnhancer;
import cn.yusiwen.excel.handler.ManySheetWriteHandler;
import cn.yusiwen.excel.handler.SheetWriteHandler;
import cn.yusiwen.excel.handler.SingleSheetWriteHandler;
import cn.yusiwen.excel.head.I18nHeaderCellWriteHandler;

/**
 * @author yusiwen
 * @version 1.0
 */
@RequiredArgsConstructor
public class ExcelHandlerConfiguration {

    /**
     * ExcelConfigProperties
     */
    private final ExcelConfigProperties configProperties;

    /**
     * ObjectProvider
     */
    private final ObjectProvider<List<Converter<?>>> converterProvider;

    /**
     * ExcelBuild增强
     *
     * @return DefaultWriterBuilderEnhancer 默认什么也不做的增强器
     */
    @Bean
    @ConditionalOnMissingBean
    public WriterBuilderEnhancer writerBuilderEnhancer() {
        return new DefaultWriterBuilderEnhancer();
    }

    /**
     * 单sheet 写入处理器
     *
     * @return SingleSheetWriteHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public SingleSheetWriteHandler singleSheetWriteHandler() {
        return new SingleSheetWriteHandler(configProperties, converterProvider, writerBuilderEnhancer());
    }

    /**
     * 多sheet 写入处理器
     *
     * @return ManySheetWriteHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public ManySheetWriteHandler manySheetWriteHandler() {
        return new ManySheetWriteHandler(configProperties, converterProvider, writerBuilderEnhancer());
    }

    /**
     * 返回Excel文件的 response 处理器
     *
     * @param sheetWriteHandlerList 页签写入处理器集合
     * @return ResponseExcelReturnValueHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public ExportExcelReturnValueHandler
        responseExcelReturnValueHandler(List<SheetWriteHandler> sheetWriteHandlerList) {
        return new ExportExcelReturnValueHandler(sheetWriteHandlerList);
    }

    /**
     * excel 头的国际化处理器
     *
     * @param messageSource 国际化源
     * @return I18nHeaderCellWriteHandler
     */
    @Bean
    @ConditionalOnBean(MessageSource.class)
    @ConditionalOnMissingBean
    public I18nHeaderCellWriteHandler i18nHeaderCellWriteHandler(MessageSource messageSource) {
        return new I18nHeaderCellWriteHandler(messageSource);
    }

}
