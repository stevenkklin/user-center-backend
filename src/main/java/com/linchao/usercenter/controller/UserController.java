package com.linchao.usercenter.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linchao.usercenter.common.BaseResponse;
import com.linchao.usercenter.common.ErrorCode;
import com.linchao.usercenter.common.ResultUtils;
import com.linchao.usercenter.exception.BusinessException;
import com.linchao.usercenter.model.domain.User;
import com.linchao.usercenter.model.domain.request.UserLoginRequest;
import com.linchao.usercenter.model.domain.request.UserRegisterRequest;
import com.linchao.usercenter.service.UserService;
import com.linchao.usercenter.utils.DownExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.linchao.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.linchao.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author Steven
 * @create 2023-06-26-9:19
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long id = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(id);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> list = userService.list(queryWrapper);
        List<User> collect = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 判断用户权限
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTag(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        // 获取缓存
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        User loginUser = userService.getCurrentUser(request);
        String redisKey = String.format("linpao:user:recommend:%s", loginUser.getId());
        Page<User> userPage = (Page<User>)valueOperations.get(redisKey);
        // 命中缓存则直接返回
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        // 无缓存则查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        // 写缓存
        try {
            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return ResultUtils.success(userPage);
    }

    //导出为Excel
//    @GetMapping("/get/excel")
//    public void getExcel(HttpServletResponse response) {
//        // 数据库读取数据
//        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        wrapper.last("limit 0,10");
//        List<User> list = userService.list(wrapper);
//
//        try {
//            DownExcel.download(response,User.class,list);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        }
////        String fileName = "D:\\IdeaProjects\\user-center\\src\\main\\resources\\prodExcel.xlsx" + System.currentTimeMillis() + ".xlsx";
////        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
////        // 如果这里想使用03 则 传入excelType参数即可
////        EasyExcel.write(fileName, User.class)
////                .sheet("模板")
////                .doWrite(() -> {
////                    // 分页查询数据
////                    return list;
////                });
//
////        response.addHeader("Content-Disposition", "attachment;filename=" + System.currentTimeMillis() + "huyuqiao.xlsx" );
////        response.setContentType("application/vnd.ms-excel;charset=gb2312");
////        try {
//////            从HttpServletResponse中获取OutputStream输出流
////            ServletOutputStream outputStream = response.getOutputStream();
////            /*
////             * EasyExcel 有多个不同的read方法，适用于多种需求
////             * 这里调用EasyExcel中通过OutputStream流方式输出Excel的write方法
////             * 它会返回一个ExcelWriterBuilder类型的返回值
////             * ExcelWriterBuilde中有一个doWrite方法，会输出数据到设置的Sheet中
////             */
////            EasyExcel.write(outputStream, User.class).sheet("测试数据" + System.currentTimeMillis()).doWrite(list);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
}
