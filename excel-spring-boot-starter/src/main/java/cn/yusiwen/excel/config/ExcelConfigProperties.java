package cn.yusiwen.excel.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yusiwen
 */
@Data
@ConfigurationProperties(prefix = ExcelConfigProperties.PREFIX)
public class ExcelConfigProperties {

    /**
     * Prefix
     */
    static final String PREFIX = "excel";

    /**
     * 模板路径
     */
    private String templatePath = PREFIX;

}
