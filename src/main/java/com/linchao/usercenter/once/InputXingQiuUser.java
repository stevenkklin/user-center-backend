package com.linchao.usercenter.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Steven
 * @create 2023-07-31-10:14
 */
public class InputXingQiuUser {
    public static void main(String[] args) {
        String fileName = "D:\\IdeaProjects\\user-center\\src\\main\\resources\\prodExcel.xlsx";
        synchronousRead(fileName);
    }


    /**
     * 同步读取
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        List<XingQiuTableUserInfo> userInfoList = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        System.out.println("userInfoList" + userInfoList);
        System.out.println("总数 = " + userInfoList.size());
        Map<String, List<XingQiuTableUserInfo>> listMap =
                userInfoList
                        .stream()
                        .filter(item -> StringUtils.isNotEmpty(item.getUsername()))
                        .collect(Collectors.groupingBy(XingQiuTableUserInfo::getUsername));

        for (Map.Entry<String, List<XingQiuTableUserInfo>> stringListEntry : listMap.entrySet()) {
            if (stringListEntry.getValue().size() > 1) {
                System.out.println("key: " + stringListEntry.getKey());
            }
        }
        System.out.println("不重复昵称数 = " + listMap.keySet().size());

    }
}
