package cn.yusiwen.excel.head;

import java.util.List;
import java.util.Set;

import lombok.Data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author yusiwen
 */
@Data
@SuppressFBWarnings("USBR_UNNECESSARY_STORE_BEFORE_RETURN")
public class HeadMeta {

    /**
     * <p>
     * 自定义头部信息
     * </p>
     * 实现类根据数据的class信息，定制Excel头<br/>
     * 具体方法使用参考：<a href="https://www.yuque.com/easyexcel/doc/write#b4b9de00">...</a>
     */
    private List<List<String>> head;

    /**
     * 忽略头对应字段名称
     */
    private Set<String> ignoreHeadFields;

}
