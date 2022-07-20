package cn.yusiwen.excel.example.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;

import lombok.Data;

/**
 * Demo1演示数据
 *
 * @author yusiwen
 */
@Data
public class DemoData1 {
    /**
     * User name
     */
    @ColumnWidth(30)  // 定义宽度
    @ExcelProperty("用户名") // 定义列名称
    @ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    private String username;

    /**
     * Password
     */
    @ExcelProperty("密码")
    private String password;
}
