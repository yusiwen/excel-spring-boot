package cn.yusiwen.excel.vo;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 校验错误信息
 *
 * @author yusiwen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressFBWarnings("USBR_UNNECESSARY_STORE_BEFORE_RETURN")
public class ErrorMessage {

    /**
     * 行号
     */
    private Long lineNum;

    /**
     * 错误信息
     */
    private Set<String> errors = new HashSet<>();

    public ErrorMessage(Set<String> errors) {
        this.errors = errors;
    }

    public ErrorMessage(String error) {
        HashSet<String> objects = new HashSet<>();
        objects.add(error);
        this.errors = objects;
    }

}
