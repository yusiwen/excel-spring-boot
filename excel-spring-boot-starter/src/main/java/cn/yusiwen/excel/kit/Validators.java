package cn.yusiwen.excel.kit;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 校验工具
 *
 * @author yusiwen
 */
public final class Validators {

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    /**
     * Validator
     */
    private static final Validator VALIDATOR;

    private Validators() {
    }

    /**
     * Validates all constraints on {@code object}.
     *
     * @param object
     *            object to validate
     * @param <T>
     *            the type of the object to validate
     * @return constraint violations or an empty set if none
     * @throws IllegalArgumentException
     *             if object is {@code null} or if {@code null} is passed to the varargs groups
     * @throws ValidationException
     *             if a non recoverable error happens during the validation process
     */
    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return VALIDATOR.validate(object);
    }

}
