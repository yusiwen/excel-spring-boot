package cn.yusiwen.excel.example.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.yusiwen.excel.annotation.ExportExcel;
import cn.yusiwen.excel.annotation.ImportExcel;
import cn.yusiwen.excel.annotation.Sheet;
import cn.yusiwen.excel.example.entity.DemoData1;
import cn.yusiwen.excel.vo.ErrorMessage;

/**
 * Demo1演示Controller
 *
 * @author yusiwen
 */
@RestController
@RequestMapping("/demo1")
@Slf4j
public class DemoController1 {

    @GetMapping("/export")
    @ExportExcel(name = "demo1", sheets = @Sheet(sheetName = "demoSheet1"))
    public List<DemoData1> export1() {
        List<DemoData1> dataList = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            DemoData1 data = new DemoData1();
            data.setUsername("tr1" + i);
            data.setPassword("tr2" + i);
            dataList.add(data);
        }
        return dataList;
    }

    @PostMapping("/import")
    public String import1(@ImportExcel List<DemoData1> data1List, BindingResult bindingResult) {
        List<ErrorMessage> errorMessageList = (List<ErrorMessage>)bindingResult.getTarget();
        if (errorMessageList != null) {
            log.error("Binding errors: {}", errorMessageList);
        }
        return Arrays.toString(data1List.toArray());
    }
}
