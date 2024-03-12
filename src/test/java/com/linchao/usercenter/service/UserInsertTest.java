package com.linchao.usercenter.service;
import java.util.Arrays;
import java.util.Date;

import com.linchao.usercenter.model.domain.User;
import com.linchao.usercenter.model.domain.request.UserLoginRequest;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Steven
 * @create 2024-02-05-14:18
 */
@SpringBootTest
public class UserInsertTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    @Test
    public void insertTest() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int batchSize = 50000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;

                User user = new User();
                user.setUsername("假数据");
                user.setUserAccount("fakeAccount");
                user.setAvatarUrl("https://s1.hdslb.com/bfs/static/jinkela/space/assets/icon-auth.png");
                user.setProfile("");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123321123");
                user.setUserStatus(0);
                user.setEmail("123453212@qq.com");
                user.setTags("[]");
                user.setUserRole(0);
                user.setPlanetCode("12363");
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行批量插入
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName:" + Thread.currentThread().getName());
                userService.saveBatch(userList);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
