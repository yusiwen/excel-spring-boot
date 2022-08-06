package cn.yusiwen.excel.handler;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.ObjectProvider;

import cn.yusiwen.excel.annotation.ExportExcel;
import cn.yusiwen.excel.annotation.Sheet;
import cn.yusiwen.excel.config.ExcelConfigProperties;
import cn.yusiwen.excel.enhance.WriterBuilderEnhancer;
import cn.yusiwen.excel.kit.ExcelException;

/**
 * @author yusiwen
 */
public class ManySheetWriteHandler extends AbstractSheetWriteHandler {

    public ManySheetWriteHandler(ExcelConfigProperties configProperties,
        ObjectProvider<List<Converter<?>>> converterProvider, WriterBuilderEnhancer excelWriterBuilderEnhance) {
        super(configProperties, converterProvider, excelWriterBuilderEnhance);
    }

    /**
     * 当且仅当List不为空且List中的元素也是List 才返回true
     *
     * @param obj 返回对象
     * @return boolean
     */
    @Override
    public boolean support(Object obj) {
        if (obj instanceof List) {
            List<?> objList = (List<?>)obj;
            return !objList.isEmpty() && objList.get(0) instanceof List;
        } else {
            throw new ExcelException("@ResponseExcel 返回值必须为List类型");
        }
    }

    @Override
    public void write(Object obj, HttpServletResponse response, ExportExcel exportExcel) {
        List<?> objList = (List<?>)obj;
        ExcelWriter excelWriter = getExcelWriter(response, exportExcel);

        Sheet[] sheets = exportExcel.sheets();
        WriteSheet sheet;
        for (int i = 0; i < sheets.length; i++) {
            List<?> eleList = (List<?>)objList.get(i);
            Class<?> dataClass = eleList.get(0).getClass();
            // 创建sheet
            sheet = this.sheet(sheets[i], dataClass, exportExcel.template(), exportExcel.headGenerator());
            // 填充 sheet
            if (exportExcel.fill()) {
                excelWriter.fill(eleList, sheet);
            } else {
                // 写入sheet
                excelWriter.write(eleList, sheet);
            }
        }
        excelWriter.finish();
    }

}
