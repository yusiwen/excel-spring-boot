package cn.yusiwen.excel.handler;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.ObjectProvider;

import cn.yusiwen.excel.annotation.ExportExcel;
import cn.yusiwen.excel.config.ExcelConfigProperties;
import cn.yusiwen.excel.enhance.WriterBuilderEnhancer;
import cn.yusiwen.excel.kit.ExcelException;

/**
 * @author yusiwen
 */
public class SingleSheetWriteHandler extends AbstractSheetWriteHandler {

    public SingleSheetWriteHandler(ExcelConfigProperties configProperties,
        ObjectProvider<List<Converter<?>>> converterProvider, WriterBuilderEnhancer excelWriterBuilderEnhance) {
        super(configProperties, converterProvider, excelWriterBuilderEnhance);
    }

    /**
     * obj 是List 且list不为空同时list中的元素不是是List 才返回true
     *
     * @param obj 返回对象
     * @return boolean
     */
    @Override
    public boolean support(Object obj) {
        if (obj instanceof List) {
            List<?> objList = (List<?>)obj;
            return !objList.isEmpty() && !(objList.get(0) instanceof List);
        } else {
            throw new ExcelException("@ResponseExcel 返回值必须为List类型");
        }
    }

    @Override
    public void write(Object obj, HttpServletResponse response, ExportExcel exportExcel) {
        List<?> list = (List<?>)obj;
        ExcelWriter excelWriter = getExcelWriter(response, exportExcel);

        // 有模板则不指定sheet名
        Class<?> dataClass = list.get(0).getClass();
        WriteSheet sheet =
            this.sheet(exportExcel.sheets()[0], dataClass, exportExcel.template(), exportExcel.headGenerator());

        // 填充 sheet
        if (exportExcel.fill()) {
            excelWriter.fill(list, sheet);
        } else {
            // 写入sheet
            excelWriter.write(list, sheet);
        }
        excelWriter.finish();
    }

}
