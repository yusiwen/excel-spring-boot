package cn.yusiwen.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Set;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;

/**
 * 集合转换
 *
 * @author yusiwen
 */
public class SetConverter implements Converter<Set<?>> {

    private final ConversionService conversionService;

    SetConverter() {
        this.conversionService = DefaultConversionService.getSharedInstance();
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return Set.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Set<?> convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty,
        GlobalConfiguration globalConfiguration) {
        String[] value = StringUtils.delimitedListToStringArray(cellData.getStringValue(), ",");
        return (Set<?>)conversionService.convert(value, TypeDescriptor.valueOf(String[].class),
            new TypeDescriptor(contentProperty.getField()));
    }

    @Override
    public WriteCellData<String> convertToExcelData(Set<?> value, ExcelContentProperty contentProperty,
        GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(StringUtils.collectionToCommaDelimitedString(value));
    }

}
