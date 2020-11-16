package com.example.calltest.pulsarDemo;

import com.example.calltest.monitor.Sys;
import com.example.calltest.pulsarDemo.service.ProducerService;
import com.example.calltest.pulsarDemo.service.SingleProducerService;
import org.apache.pulsar.client.api.PulsarClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ProducerTest {


    @Value("${spring.pulsar.instance-servers}")
    private String pulsarBrokers;

    @Autowired
    PulsarClient pulsarClient;

    private List<UUID> uuidList;

    @PostConstruct
    public void getUUidList() {
        List<UUID> uuidList = new ArrayList<>();
        for (int i1 = 0; i1 < 20; i1++) {
            UUID uuid = UUID.randomUUID();
            uuidList.add(uuid);
        }
        this.uuidList = uuidList;
    }

    /**
     * corePoolSize: 核心线程数为 5。
     * maximumPoolSize ：最大线程数 10
     * keepAliveTime : 等待时间为 1L。
     * unit: 等待时间的单位为 TimeUnit.SECONDS。
     * workQueue：任务队列为 ArrayBlockingQueue，并且容量为 100;
     * handler:饱和策略为 CallerRunsPolicy。
     */
    private static final int CORE_POOL_SIZE = 20;
    private static final int MAX_POOL_SIZE = 40;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final int QUEUE_CAPACITY = 400;

    private ThreadPoolExecutor threadPoolExecutor1 = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private ThreadPoolExecutor threadPoolExecutor2 = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private ThreadPoolExecutor threadPoolExecutor3 = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 测试生产者发布
     */
    public String testProducer(int totalTopicNum, int msgNum, String regTopicName) {
        long startTime;
        uuidList.forEach(value -> {
            System.err.println("hashCode值为：" + value.hashCode());
        });
        if (totalTopicNum < 20) {
            startTime = System.currentTimeMillis();
            ProducerService producerService = new ProducerService(0, totalTopicNum, msgNum, pulsarClient, regTopicName,uuidList);
            threadPoolExecutor1.execute(producerService);
        } else {
            int topicNum = totalTopicNum / 20;
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 20; i++) {
//                int num = r.nextInt(msgNum);// 随机发送固定数量数据到不同主题
                int start = i * topicNum;
                int end = (i + 1) * topicNum;
                ProducerService producerService = new ProducerService(start, end, msgNum, pulsarClient, regTopicName,uuidList);
                threadPoolExecutor1.execute(producerService);
            }
        }

        //终止线程池
        threadPoolExecutor1.shutdown();
        //判断所有任务是否已经提交
        while (!threadPoolExecutor1.isTerminated()) {
        }
        long endTime = System.currentTimeMillis();
        Duration duration = Duration.ofMillis(endTime - startTime);
        long seconds = duration.getSeconds();
        return seconds + "秒";
    }

    public void stopProducer(int totalTopicNum, String regTopicName) {
        for (int i = 0; i < totalTopicNum; i++) {
            ProducerService.stopProducer(regTopicName + "-" + i);
        }
    }

    public Map<String, Integer> testSingleProducer(String topic, int msgNum) {
        SingleProducerService singleProducerService1 = new SingleProducerService(msgNum, topic, pulsarClient);
        threadPoolExecutor1.execute(singleProducerService1);
        //终止线程池
        threadPoolExecutor1.shutdown();
        //判断所有任务是否已经提交
        while (!threadPoolExecutor1.isTerminated()) {
        }
        return SingleProducerService.publishCountList;
    }

    public Map<String, Integer> testSingleProducer2(String topic, int msgNum) {
        SingleProducerService singleProducerService1 = new SingleProducerService(msgNum, topic, pulsarClient);
        threadPoolExecutor2.execute(singleProducerService1);
        //终止线程池
        threadPoolExecutor2.shutdown();
        //判断所有任务是否已经提交
        while (!threadPoolExecutor2.isTerminated()) {
        }
        return SingleProducerService.publishCountList;
    }

    public Map<String, Integer> testSingleProducer3(String topic, int msgNum) {
        SingleProducerService singleProducerService1 = new SingleProducerService(msgNum, topic, pulsarClient);
        threadPoolExecutor3.execute(singleProducerService1);
        //终止线程池
        threadPoolExecutor3.shutdown();
        //判断所有任务是否已经提交
        while (!threadPoolExecutor3.isTerminated()) {
        }
        return SingleProducerService.publishCountList;
    }

//    @Test
//    public void test() {
//        int topicNum = 300 / 20;
//        Random r = new Random();
//        for (int i = 0; i < 20; i++) {
//            int num = r.nextInt(300); //随机发送固定数量数据到不同主题
//            int start = i * topicNum;
//            int end = (i + 1) * topicNum;
//            System.err.println(start + "-" + end + "-" + num);
//        }
//    }

}
