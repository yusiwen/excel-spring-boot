package cn.yusiwen.excel.handler;

import javax.servlet.http.HttpServletResponse;

import cn.yusiwen.excel.annotation.ExportExcel;

/**
 * @author yusiwen
 */
public interface SheetWriteHandler {

    /**
     * 是否支持
     *
     * @param obj
     *            Object
     * @return Support or not
     */
    boolean support(Object obj);

    /**
     * 校验
     *
     * @param exportExcel
     *            注解
     */
    void check(ExportExcel exportExcel);

    /**
     * 返回的对象
     *
     * @param o
     *            obj
     * @param response
     *            输出对象
     * @param exportExcel
     *            注解
     */
    void export(Object o, HttpServletResponse response, ExportExcel exportExcel);

    /**
     * 写成对象
     *
     * @param o
     *            obj
     * @param response
     *            输出对象
     * @param exportExcel
     *            注解
     */
    void write(Object o, HttpServletResponse response, ExportExcel exportExcel);

}
