package com.linchao.usercenter.service;

import com.linchao.usercenter.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Steven
 * @create 2024-03-06-21:28
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Test
    void testWatchDog() {
        RLock lock = redissonClient.getLock("linpao:precachejob:docache:lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Thread.sleep(300000L);
                System.out.println("getLock:" + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    @Test
    void testRedisson() {
        // list，数据存在本地JVM内存中
        List<String> list = new ArrayList<>();
        list.add("yupi");
        System.out.println("list:" + list.get(0));

//        list.remove(0);

        // 数据存在内存中
        RList<Object> rlist = redissonClient.getList("test-list");
        rlist.add("yupi");
        System.out.println("rlist:" + rlist.get(0));


    }

    @Test
    void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("yupiString", "dog");
        valueOperations.set("yupiInt", 1);
        valueOperations.set("yupiDouble", 2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("yupi");
        valueOperations.set("yupiUser", user);
        // 查
        Object yupiString = valueOperations.get("yupiString");
        Assert.assertTrue("dog".equals((String)yupiString));
        Object yupiInt = valueOperations.get("yupiInt");
        Assert.assertTrue(1 == (Integer)yupiInt);
        Object yupiDouble = valueOperations.get("yupiDouble");
        Assert.assertTrue(2.0 == (Double)yupiDouble);
        System.out.println(valueOperations.get("yupiUser"));
        System.out.println((String)yupiString);

    }
}
