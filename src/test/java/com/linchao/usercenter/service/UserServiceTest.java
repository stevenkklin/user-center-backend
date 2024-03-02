package com.linchao.usercenter.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linchao.usercenter.model.domain.User;
import com.linchao.usercenter.once.DemoData;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 * 用户服务测试
 *
 * @author Steven
 * @create 2023-06-25-14:03
 */

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void noModelWrite() {
        String fileName = "D:\\IdeaProjects\\user-center\\src\\main\\resources\\prodExcel.xlsx" + System.currentTimeMillis() + ".xlsx";
        // 写法1
        //  String fileName = TestFileUtil.getPath() + "noModelWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName).head(head()).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("模板").doWrite(dataList());
    }

    private List<String> createHeader(String type, Boolean isMonth) {
        List<String> header = ListUtils.newArrayList();
        header.add("月度汇总 统计日期：");
        header.add("报表生成时间");
        if (isMonth) {
            header.add("考勤结果");
            header.add(type);
        } else {
            header.add(type);
            header.add(type);
        }
        return header;
    }

    private List<List<String>> head() {
        List<List<String>> list = ListUtils.newArrayList();
        list.add(createHeader("姓名", false));
        list.add(createHeader("部门", false));
        list.add(createHeader("出勤天数", false));
        list.add(createHeader("休息天数", false));
        list.add(createHeader("工作时长", false));
        list.add(createHeader("迟到次数", false));
        list.add(createHeader("迟到时长", false));
        list.add(createHeader("早退次数", false));
        list.add(createHeader("早退时长", false));
        list.add(createHeader("上班缺卡次数", false));
        list.add(createHeader("下班缺卡次数", false));
        list.add(createHeader("旷工天数", false));
        for (int i = 1; i <= 31; i++) {
            list.add(createHeader(String.valueOf(i), true));
        }
        return list;
    }

    private List<List<Object>> dataList() {
        List<List<Object>> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            List<Object> data = ListUtils.newArrayList();
            data.add("字符串" + i);
            data.add(0.56);
            data.add(new Date().toString());
            list.add(data);
        }
        return list;
    }


//    private List<List<String>> head() {
//        List<List<String>> list = ListUtils.newArrayList();
//        List<String> head0 = ListUtils.newArrayList();
//        head0.add("月度汇总 统计日期：");
//        head0.add("报表生成时间");
//        head0.add("字符串" + System.currentTimeMillis());
//        List<String> head1 = ListUtils.newArrayList();
//        head1.add("月度汇总 统计日期：");
//        head1.add("报表生成时间");
//        head1.add("数字" + System.currentTimeMillis());
//        List<String> head2 = ListUtils.newArrayList();
//        head2.add("月度汇总 统计日期：");
//        head2.add("报表生成时间");
//        head2.add("日期" + System.currentTimeMillis());
//        list.add(head0);
//        list.add(head1);
//        list.add(head2);
//        return list;
//    }



    @Test
    void testSomething() {
        // 获取当前日期
        Date date = new Date();
//        Calendar c=Calendar.getInstance();
//        c.setTime(date);
//        int weekday=c.get(Calendar.DAY_OF_WEEK);
        // 获取今天是星期几
        int dayOfWeek = DateUtil.dayOfWeek(date);
        //new方式创建
//        DateTime time = new DateTime(date);
        System.out.println("日期：" + date);
        System.out.println("今天是星期" + (dayOfWeek - 1));

    }

    @Test
    void testWriteExcel() {

        // 数据库读取数据
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.last("limit 0,10");
        List<User> list = userService.list(wrapper);

        // 读取的数据写入excel
        // 注意 simpleWrite在数据量不大的情况下可以使用（5000以内，具体也要看实际情况），数据量大参照 重复多次写入

        // 写法1 JDK8+
        // since: 3.0.0-beta1
        String fileName = "D:\\IdeaProjects\\user-center\\src\\main\\resources\\prodExcel.xlsx" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, User.class)
                .sheet("模板")
                .doWrite(() -> {
                    // 分页查询数据
                    return list;
                });


        System.out.println(list);
    }

    @Test
     void testAddUser() {
        User user = new User();
        user.setUsername("doglin");
        user.setUserAccount("123");
        user.setAvatarUrl("https://space.bilibili.com/14911136?spm_id_from=333.337.0.0");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yu pi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "dogYupi";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yupi";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void searchUserByTags() {
        List<String> list = Arrays.asList("c++");
        List<User> userList = userService.searchUserByTags(list);
        Assert.assertNotNull(userList);
    }
}