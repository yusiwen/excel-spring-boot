package cn.yusiwen.excel.handler;

import com.alibaba.excel.event.AnalysisEventListener;

import java.util.List;

import cn.yusiwen.excel.vo.ErrorMessage;

/**
 * list analysis EventListener
 *
 * @author yusiwen
 * @param <T>
 *            EventListener
 */
public abstract class AbstractListAnalysisEventListener<T> extends AnalysisEventListener<T> {

    /**
     * 获取 excel 解析的对象列表
     *
     * @return 集合
     */
    public abstract List<T> getList();

    /**
     * 获取异常校验结果
     *
     * @return 集合
     */
    public abstract List<ErrorMessage> getErrors();

}
