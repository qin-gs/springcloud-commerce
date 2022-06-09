package com.example.commerce;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = KafkaApplication.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    @Test
    public void loadContext() {
    }

    /**
     * 在测试方法中使用事务，默认回滚，使用 @Rollback(false) 可以关闭回滚
     * 事务失效场景
     * <pre>
     *     1. 非 public 方法
     *     2. propagation 不正确
     *     3. rollbackFor 不正确
     *     4. 同一个类中方法调用
     *     5. 主动 catch
     *     6. 数据库本身不支持事务
     * </pre>
     */
    @Transactional
    @Rollback(false)
    public void txTest() {

    }
}
