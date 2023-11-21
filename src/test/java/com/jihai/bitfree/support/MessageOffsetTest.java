package com.jihai.bitfree.support;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jihai.bitfree.App;
import com.jihai.bitfree.IntegrationTest;
import com.jihai.bitfree.dao.MessageDAO;
import com.jihai.bitfree.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Test MessageOffset func in concurrent env
 *
 * @author zhan yan
 **/
class MessageOffsetTest extends IntegrationTest {

    private static final Logger LOG = getLogger(MessageOffsetTest.class);

    @Resource
    private MessageService messageService;

    @Mock
    private MessageDAO messageDAO;

    @Autowired
    TestRestTemplate restTemplate;

    private final ThreadPoolExecutor sendMessagePool = new ThreadPoolExecutor(
            5,
            5,
            100,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactoryBuilder()
                    .setNameFormat("SendPool---%s")
                    .build());

    private final ThreadPoolExecutor deleteMessagePool = new ThreadPoolExecutor(
            3,
            3,
            100,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactoryBuilder()
                    .setNameFormat("DeletePool---%s")
                    .build());

    @Test
    @DisplayName("测试并发环境下消息偏移量功能")
    void doTestConcurrentEnv(){

        doReturn(9L).when(messageDAO).getRecentMessageId();
//
//        new Thread(() ->{
//            for (int j = 0; j < 20; j++){
//                int sendNum = (int) (Math.random() * 1000);
//                int delNum = (int) (Math.random() * 1000);
//
//                Future<Boolean> sendTask = sendMessagePool.submit(() -> {
//                    for (int i = 0; i < sendNum; i++){
//                        int finalOffset = i;
//                        sendMessagePool.execute(() -> messageService.refreshOffset((long) finalOffset));
//                    }
//
//                    return true;
//                });
//
//                Future<Boolean> deleteTask = deleteMessagePool.submit(() -> {
//                    for (int i = 0; i < delNum; i++){
//                        int finalOffset = i;
//                        deleteMessagePool.execute(() -> messageService.rollbackOffset((long) finalOffset));
//                    }
//                    return true;
//                });
//
//                assertDoesNotThrow(() -> {
//                    LOG.info("执行结果: Send-{}, Delete-{}", sendTask.get(10, TimeUnit.SECONDS),  deleteTask.get(10, TimeUnit.SECONDS));
//                    LOG.info("Final latest offset is: {}", messageService.getLatestOffset());
//                });
//
//                assertEquals(Long.valueOf(sendNum), messageService.getLatestOffset());
//            }
//        }).start();

    }




}
