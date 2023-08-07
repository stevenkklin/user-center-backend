package com.linchao.usercenter.once;

import com.alibaba.excel.EasyExcel;

import java.io.File;
import java.util.List;

/**
 * @author Steven
 * @create 2023-07-31-10:14
 */
public class ReadDemo {
    public static void main(String[] args) {
        String fileName = "D:\\IdeaProjects\\user-center\\src\\main\\resources\\testExcel.xlsx";
        synchronousRead(fileName);
    }

    /**
     * 监听器读取
     * @param fileName
     */
    public static void readByListener(String fileName) {
        EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
    }

    /**
     * 同步读取
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<DemoData> list = EasyExcel.read(fileName).head(DemoData.class).sheet().doReadSync();
        System.out.println("sync");
        for (DemoData demoData : list) {
            System.out.println(demoData);
        }
    }
}
