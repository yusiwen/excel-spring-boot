package cn.yusiwen.excel.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author yusiwen
 */
@Data
@ConfigurationProperties(prefix = ExcelConfigProperties.PREFIX)
@SuppressFBWarnings("USBR_UNNECESSARY_STORE_BEFORE_RETURN")
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
