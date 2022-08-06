package cn.yusiwen.excel.handler;

import static com.alibaba.excel.util.StringUtils.isEmpty;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import cn.yusiwen.excel.annotation.ExportExcel;
import cn.yusiwen.excel.annotation.Sheet;
import cn.yusiwen.excel.aop.DynamicNameAspect;
import cn.yusiwen.excel.config.ExcelConfigProperties;
import cn.yusiwen.excel.converters.LocalDateStringConverter;
import cn.yusiwen.excel.converters.LocalDateTimeStringConverter;
import cn.yusiwen.excel.enhance.WriterBuilderEnhancer;
import cn.yusiwen.excel.head.HeadGenerator;
import cn.yusiwen.excel.head.HeadMeta;
import cn.yusiwen.excel.head.I18nHeaderCellWriteHandler;
import cn.yusiwen.excel.kit.ExcelException;

/**
 * @author yusiwen
 */
@RequiredArgsConstructor
public abstract class AbstractSheetWriteHandler implements SheetWriteHandler, ApplicationContextAware {

    /**
     * ExcelConfigProperties
     */
    private final ExcelConfigProperties configProperties;

    /**
     * ObjectProvider
     */
    private final ObjectProvider<List<Converter<?>>> converterProvider;

    /**
     * WriterBuilderEnhancer
     */
    private final WriterBuilderEnhancer excelWriterBuilderEnhance;

    /**
     * ApplicationContext
     */
    private ApplicationContext applicationContext;

    /**
     * I18nHeaderCellWriteHandler
     */
    @Getter
    @Setter
    @Autowired(required = false)
    private I18nHeaderCellWriteHandler i18nHeaderCellWriteHandler;

    @Override
    public void check(ExportExcel exportExcel) {
        if (exportExcel.sheets().length == 0) {
            throw new ExcelException("@ResponseExcel sheet 配置不合法");
        }
    }

    @Override
    @SneakyThrows(UnsupportedEncodingException.class)
    public void export(Object o, HttpServletResponse response, ExportExcel exportExcel) {
        check(exportExcel);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String name = null;
        if (requestAttributes != null) {
            name = (String)requestAttributes.getAttribute(DynamicNameAspect.EXCEL_NAME_KEY,
                RequestAttributes.SCOPE_REQUEST);
        }
        if (isEmpty(name)) {
            name = UUID.randomUUID().toString();
        }
        String fileName = String.format("%s%s", URLEncoder.encode(name, StandardCharsets.UTF_8.name()),
            exportExcel.suffix().getValue());
        // 根据实际的文件类型找到对应的 contentType
        String contentType =
            MediaTypeFactory.getMediaType(fileName).map(MediaType::toString).orElse("application/vnd.ms-excel");
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        write(o, response, exportExcel);
    }

    /**
     * 通用的获取ExcelWriter方法
     *
     * @param response HttpServletResponse
     * @param exportExcel ResponseExcel注解
     * @return ExcelWriter
     */
    @SneakyThrows(IOException.class)
    public ExcelWriter getExcelWriter(HttpServletResponse response, ExportExcel exportExcel) {
        ExcelWriterBuilder writerBuilder =
            EasyExcel.write(response.getOutputStream()).registerConverter(LocalDateStringConverter.INSTANCE)
                .registerConverter(LocalDateTimeStringConverter.INSTANCE).autoCloseStream(Boolean.TRUE)
                .excelType(exportExcel.suffix()).inMemory(exportExcel.inMemory());

        if (StringUtils.hasText(exportExcel.password())) {
            writerBuilder.password(exportExcel.password());
        }

        if (exportExcel.include().length != 0) {
            writerBuilder.includeColumnFiledNames(Arrays.asList(exportExcel.include()));
        }

        if (exportExcel.exclude().length != 0) {
            writerBuilder.excludeColumnFiledNames(Arrays.asList(exportExcel.exclude()));
        }

        if (exportExcel.writeHandler().length != 0) {
            for (Class<? extends WriteHandler> clazz : exportExcel.writeHandler()) {
                writerBuilder.registerWriteHandler(BeanUtils.instantiateClass(clazz));
            }
        }

        // 开启国际化头信息处理
        if (exportExcel.i18nHeader() && i18nHeaderCellWriteHandler != null) {
            writerBuilder.registerWriteHandler(i18nHeaderCellWriteHandler);
        }

        // 自定义注入的转换器
        registerCustomConverter(writerBuilder);

        if (exportExcel.converter().length != 0) {
            for (Class<? extends Converter> clazz : exportExcel.converter()) {
                writerBuilder.registerConverter(BeanUtils.instantiateClass(clazz));
            }
        }

        String templatePath = configProperties.getTemplatePath();
        if (StringUtils.hasText(exportExcel.template())) {
            ClassPathResource classPathResource =
                new ClassPathResource(templatePath + File.separator + exportExcel.template());
            InputStream inputStream = classPathResource.getInputStream();
            writerBuilder.withTemplate(inputStream);
        }

        writerBuilder = excelWriterBuilderEnhance.enhanceExcel(writerBuilder, response, exportExcel, templatePath);

        return writerBuilder.build();
    }

    /**
     * 自定义注入转换器 如果有需要，子类自己重写
     *
     * @param builder ExcelWriterBuilder
     */
    public void registerCustomConverter(ExcelWriterBuilder builder) {
        converterProvider.ifAvailable(converters -> converters.forEach(builder::registerConverter));
    }

    /**
     * 获取 WriteSheet 对象
     *
     * @param sheet sheet annotation info
     * @param dataClass 数据类型
     * @param template 模板
     * @param bookHeadEnhancerClass 自定义头处理器
     * @return WriteSheet
     */
    public WriteSheet sheet(Sheet sheet, Class<?> dataClass, String template,
        Class<? extends HeadGenerator> bookHeadEnhancerClass) {

        // Sheet 编号和名称
        Integer sheetNo = sheet.sheetNo() >= 0 ? sheet.sheetNo() : null;
        String sheetName = sheet.sheetName();

        // 是否模板写入
        ExcelWriterSheetBuilder writerSheetBuilder =
            StringUtils.hasText(template) ? EasyExcel.writerSheet(sheetNo) : EasyExcel.writerSheet(sheetNo, sheetName);

        // 头信息增强 1. 优先使用 sheet 指定的头信息增强 2. 其次使用 @ResponseExcel 中定义的全局头信息增强
        Class<? extends HeadGenerator> headGenerateClass = null;
        if (isNotInterface(sheet.headGenerateClass())) {
            headGenerateClass = sheet.headGenerateClass();
        } else if (isNotInterface(bookHeadEnhancerClass)) {
            headGenerateClass = bookHeadEnhancerClass;
        }
        // 定义头信息增强则使用其生成头信息，否则使用 dataClass 来自动获取
        if (headGenerateClass != null) {
            fillCustomHeadInfo(dataClass, bookHeadEnhancerClass, writerSheetBuilder);
        } else if (dataClass != null) {
            writerSheetBuilder.head(dataClass);
            if (sheet.excludes().length > 0) {
                writerSheetBuilder.excludeColumnFiledNames(Arrays.asList(sheet.excludes()));
            }
            if (sheet.includes().length > 0) {
                writerSheetBuilder.includeColumnFiledNames(Arrays.asList(sheet.includes()));
            }
        }

        // sheetBuilder 增强
        writerSheetBuilder = excelWriterBuilderEnhance.enhanceSheet(writerSheetBuilder, sheetNo, sheetName, dataClass,
            template, headGenerateClass);

        return writerSheetBuilder.build();
    }

    private void fillCustomHeadInfo(Class<?> dataClass, Class<? extends HeadGenerator> headEnhancerClass,
        ExcelWriterSheetBuilder writerSheetBuilder) {
        HeadGenerator headGenerator = this.applicationContext.getBean(headEnhancerClass);
        Assert.notNull(headGenerator, "The header generated bean does not exist.");
        HeadMeta head = headGenerator.head(dataClass);
        writerSheetBuilder.head(head.getHead());
        writerSheetBuilder.excludeColumnFiledNames(head.getIgnoreHeadFields());
    }

    /**
     * 是否为Null Head Generator
     *
     * @param headGeneratorClass 头生成器类型
     * @return true 已指定 false 未指定(默认值)
     */
    private boolean isNotInterface(Class<? extends HeadGenerator> headGeneratorClass) {
        return !Modifier.isInterface(headGeneratorClass.getModifiers());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
