package cn.yusiwen.excel.handler;

import com.alibaba.excel.context.AnalysisContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import lombok.extern.slf4j.Slf4j;

import cn.yusiwen.excel.kit.Validators;
import cn.yusiwen.excel.vo.ErrorMessage;

/**
 * 默认的 AnalysisEventListener
 *
 * @author yusiwen
 */
@Slf4j
public class DefaultAnalysisEventListener extends AbstractListAnalysisEventListener<Object> {

    /**
     * List of EventListener
     */
    private final List<Object> list = new ArrayList<>();

    /**
     * List of error messages
     */
    private final List<ErrorMessage> errorMessageList = new ArrayList<>();

    /**
     * Line number
     */
    private Long lineNum = 1L;

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        lineNum++;

        Set<ConstraintViolation<Object>> violations = Validators.validate(o);
        if (!violations.isEmpty()) {
            Set<String> messageSet = violations.stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
            errorMessageList.add(new ErrorMessage(lineNum, messageSet));
        } else {
            list.add(o);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.debug("Excel read analysed");
    }

    @Override
    public List<Object> getList() {
        return list;
    }

    @Override
    public List<ErrorMessage> getErrors() {
        return errorMessageList;
    }

}
