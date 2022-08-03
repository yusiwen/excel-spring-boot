package cn.yusiwen.excel.processor;

import java.lang.reflect.Method;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author yusiwen
 */
public class NameSpelExpressionProcessor implements NameProcessor {

    /**
     * 参数发现器
     */
    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * Express语法解析器
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @Override
    @SuppressFBWarnings({"NP_NONNULL_PARAM_VIOLATION", "SPEL_INJECTION"})
    public String doDetermineName(Object[] args, Method method, String key) {

        if (!key.contains("#")) {
            return key;
        }

        EvaluationContext context = new MethodBasedEvaluationContext(null, method, args, NAME_DISCOVERER);
        final Object value = PARSER.parseExpression(key).getValue(context);
        return value == null ? null : value.toString();
    }

}
