package com.linchao.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linchao.usercenter.common.BaseResponse;
import com.linchao.usercenter.common.ErrorCode;
import com.linchao.usercenter.common.ResultUtils;
import com.linchao.usercenter.exception.BusinessException;
import com.linchao.usercenter.model.domain.User;
import com.linchao.usercenter.model.domain.Vote;
import com.linchao.usercenter.model.domain.request.UserLoginRequest;
import com.linchao.usercenter.model.domain.request.UserRegisterRequest;
import com.linchao.usercenter.service.UserService;
import com.linchao.usercenter.service.VoteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.linchao.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author Steven
 * @create 2023-06-26-9:19
 */
@RestController
@RequestMapping("/vote")
public class VoteController {

    @Resource
    private UserService userService;

    @Resource
    private VoteService voteService;
    @GetMapping("/all")
    public BaseResponse<Map<String, Integer>> getAllVote() {
        List<Vote> list = voteService.list();

        Map<String, Integer> teamCounts = new HashMap<>();

        for (Vote vote : list) {
            teamCounts.merge("teamOne", vote.getTeamOne(), Integer::sum);
            teamCounts.merge("teamTwo", vote.getTeamTwo(), Integer::sum);
            teamCounts.merge("teamThree", vote.getTeamThree(), Integer::sum);
            teamCounts.merge("teamFour", vote.getTeamFour(), Integer::sum);
            teamCounts.merge("teamFive", vote.getTeamFive(), Integer::sum);
            teamCounts.merge("teamSix", vote.getTeamSix(), Integer::sum);
        }
        return ResultUtils.success(teamCounts);
    }


    @GetMapping("/team")
    public BaseResponse<Boolean> voteTeam(@RequestParam Integer teamNumber) {
        Vote vote = new Vote();
        switch (teamNumber) {
            case 1:
                vote.setTeamOne(1);
                break;
            case 2:
                vote.setTeamTwo(1);
                break;
            // Add more cases if needed for other team numbers
            case 3:
                vote.setTeamThree(1);
                break;
            case 4:
                vote.setTeamFour(1);
                break;
            case 5:
                vote.setTeamFive(1);
                break;
            case 6:
                vote.setTeamSix(1);
                break;
            default:
                // Handle the default case if teamNumber is not 1 or 2
                break;
        }
        boolean save = voteService.save(vote);
        return ResultUtils.success(save);
    }
//    @PostMapping("/register")
//    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
//        if (userRegisterRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        String userAccount = userRegisterRequest.getUserAccount();
//        String userPassword = userRegisterRequest.getUserPassword();
//        String checkPassword = userRegisterRequest.getCheckPassword();
//        String planetCode = userRegisterRequest.getPlanetCode();
//        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
//        return ResultUtils.success(result);
//    }
//
//    @PostMapping("/login")
//    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
//        if (userLoginRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        String userAccount = userLoginRequest.getUserAccount();
//        String userPassword = userLoginRequest.getUserPassword();
//        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
//            return null;
//        }
//        User user = userService.userLogin(userAccount, userPassword, request);
//        return ResultUtils.success(user);
//    }
//
//    @PostMapping("/logout")
//    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
//        if (request == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        int result = userService.userLogout(request);
//        return ResultUtils.success(result);
//    }
//
//    @GetMapping("/current")
//    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser = (User) userObj;
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN);
//        }
//        Long id = currentUser.getId();
//        // TODO 校验用户是否合法
//        User user = userService.getById(id);
//        User safetyUser = userService.getSafetyUser(user);
//        return ResultUtils.success(safetyUser);
//    }
//
//    @GetMapping("/search")
//    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        if (StringUtils.isNotBlank(username)) {
//            queryWrapper.like("username", username);
//        }
//        List<User> list = userService.list(queryWrapper);
//        List<User> collect = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
//        return ResultUtils.success(collect);
//    }
//
//    @PostMapping("/delete")
//    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH);
//        }
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        boolean result = userService.removeById(id);
//        return ResultUtils.success(result);
//    }
//
//    @PostMapping("/update")
//    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
//        // 判断用户权限
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH);
//        }
//        // 校验参数是否为空
//        if (user == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getCurrentUser(request);
//        int result = userService.updateUser(user, loginUser);
//        return ResultUtils.success(result);
//    }
//
//    @GetMapping("/search/tags")
//    public BaseResponse<List<User>> searchUserByTag(@RequestParam(required = false) List<String> tagNameList) {
//        if (CollectionUtils.isEmpty(tagNameList)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        List<User> userList = userService.searchUserByTags(tagNameList);
//        return ResultUtils.success(userList);
//    }
}
