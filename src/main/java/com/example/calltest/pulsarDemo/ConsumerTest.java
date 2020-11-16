package com.example.calltest.pulsarDemo;

import com.example.calltest.pulsarDemo.service.ConsumerService;
import com.example.calltest.pulsarDemo.service.FlowConsumeService;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ConsumerTest {

    @Autowired
    PulsarClient pulsarClient;

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

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());


    public void testConsume(int topicNum, String regTopicName) {
        for (int i = 0; i < topicNum; i++) {
            String topic = regTopicName + "-" + i;
//            ConsumerService consumerService = new ConsumerService(pulsarClient, topic, i);
//            threadPoolExecutor.execute(consumerService);
            for (int i1 = 0; i1 < 5; i1++) {
                // 每个主题三个消费者
                ConsumerService consumerService = new ConsumerService(pulsarClient, topic, i1);
                threadPoolExecutor.execute(consumerService);
            }
        }
        //终止线程池
        threadPoolExecutor.shutdown();
        //判断所有任务是否已经提交
        while (!threadPoolExecutor.isTerminated()) {
        }
        System.out.println("Finished create Consume======================================================");
    }

    public void testFlowConsumer() {
        for (int i = 0; i < 3; i++) {  //三个集成流主题
            for (int i1 = 0; i1 < 3; i1++) {  //每个集成流主题三个消费者
                String[] strings = new String[1];
                strings[1] = "flowId" + "-" + i;
                FlowConsumeService flowConsumeService = new FlowConsumeService(strings, pulsarClient, i1);
                threadPoolExecutor.execute(flowConsumeService);
            }
        }
        //终止线程池
        threadPoolExecutor.shutdown();
        //判断所有任务是否已经提交
        while (!threadPoolExecutor.isTerminated()) {
        }
    }

    public void testBigConsumer(int topicNum) {
//        for (int i1 = 0; i1 < 3; i1++) {  //每个集成流主题三个消费者
        String[] topicArray = new String[topicNum];
        for (int i = 0; i < topicNum; i++) {
            topicArray[i] = "0930TEST2" + "-" + i;
        }
        FlowConsumeService flowConsumeService = new FlowConsumeService(topicArray, pulsarClient, 0);
        threadPoolExecutor.execute(flowConsumeService);
//        }
        //终止线程池
        threadPoolExecutor.shutdown();
        //判断所有任务是否已经提交
        while (!threadPoolExecutor.isTerminated()) {
        }
    }

    public void stopConsumer() {
        Map<String, Consumer<String>> consumerList = ConsumerService.consumerList;
        consumerList.forEach((key, value) -> {
            try {
                value.close();
                consumerList.remove(key);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        });
        threadPoolExecutor.shutdown();
    }


}
