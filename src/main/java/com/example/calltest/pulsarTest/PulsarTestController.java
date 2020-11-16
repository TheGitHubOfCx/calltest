package com.example.calltest.pulsarTest;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class PulsarTestController {

    protected Log LOG = LogFactory.get();

    public static Map<String, List<PulsarConsumer>> consumerMap = new ConcurrentHashMap<>();

    public static Map<String, PulsarProducer> producerMap = new ConcurrentHashMap<>();

    private List<UUID> uuidList;

    @Value("${spring.pulsar.instance-servers}")
    private String pulsarBrokers;

    @Autowired
    PulsarClient pulsarClient;

    @Autowired
    PulsarAdmin pulsarAdmin;

    @PostConstruct
    public void getUUidList() {
        List<UUID> uuidList = new ArrayList<>();
        for (int i1 = 0; i1 < 20; i1++) {
            UUID uuid = UUID.randomUUID();
            uuidList.add(uuid);
        }
        this.uuidList = uuidList;
    }

    //创建分区主题
    @RequestMapping("/createPartitionTopic/{topicName}/{partSize}")
    public void createPartitionTopic(@PathVariable String topicName, @PathVariable int partSize) {
        String topic = "persistent://public/default/" + topicName;
        int numPartitions = partSize;
        try {
            pulsarAdmin.topics().createPartitionedTopic(topic, numPartitions);
        } catch (PulsarAdminException e) {
            LOG.error("创建分区主题失败:{}", topicName);
        }
    }

    @RequestMapping("/pulsarProducer/{msgNum}/{topicName}")
    public String producer(@PathVariable int msgNum, @PathVariable String topicName) {
        uuidList.forEach(value -> {
            System.err.println("hashCode值为：" + value.hashCode());
        });
        PulsarProducer pulsarProducer = producerMap.get(topicName);
        if (null != pulsarProducer) {
            pulsarProducer = producerMap.get(topicName);
            pulsarProducer.setMsgNum(msgNum);
        } else {
            pulsarProducer = new PulsarProducer(msgNum, pulsarClient, topicName, uuidList);
            producerMap.put(topicName, pulsarProducer);
        }
        pulsarProducer.start();
        return "主题：" + topicName + "的生成线程启动成功！";
    }

    @RequestMapping("/addConsumer/{topicName}/{consumerNum}")
    public void addConsumer(@PathVariable String topicName, @PathVariable int consumerNum) {
        String[] topics = new String[1];
        topics[0] = topicName;
        List<PulsarConsumer> pulsarConsumers = consumerMap.get(topicName);
        for (int i = 0; i < consumerNum; i++) {
            try {
                Consumer<String> subscribe = pulsarClient.newConsumer(Schema.STRING)
                        .topic(topics)
                        .subscriptionName("Test-Consumer")
                        .subscriptionType(SubscriptionType.Key_Shared)
                        .keySharedPolicy(KeySharedPolicy.autoSplitHashRange().setAllowOutOfOrderDelivery(true))
                        .subscribe();
                PulsarConsumer pulsarConsumer = new PulsarConsumer(topics, pulsarConsumers.size() + i, subscribe);
                pulsarConsumers.add(pulsarConsumer);
                pulsarConsumer.start();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/pulsarConsumer/{topicName}/{consumerNum}")
    public String consumer(@PathVariable String topicName, @PathVariable int consumerNum) {
        String[] topics = new String[1];
        topics[0] = topicName;
        List<PulsarConsumer> pulsarConsumers = consumerMap.get(topicName);
        if (null != pulsarConsumers && pulsarConsumers.size() > 0) {
            for (PulsarConsumer pulsarConsumer : pulsarConsumers) {
                pulsarConsumer.start();
            }
            if (pulsarConsumers.size() < consumerNum) {
                int num = consumerNum - pulsarConsumers.size();
                for (int i = 0; i < num; i++) {
                    try {
                        Consumer<String> subscribe = pulsarClient.newConsumer(Schema.STRING)
                                .topic(topics)
                                .subscriptionName("Test-Consumer")
                                .keySharedPolicy(KeySharedPolicy.autoSplitHashRange().setAllowOutOfOrderDelivery(true))
                                .subscriptionType(SubscriptionType.Key_Shared)
                                .subscribe();
                        PulsarConsumer pulsarConsumer = new PulsarConsumer(topics, pulsarConsumers.size() + i, subscribe);
                        pulsarConsumers.add(pulsarConsumer);
                        pulsarConsumer.start();
                    } catch (PulsarClientException e) {
                        e.printStackTrace();
                    }
                }
                consumerMap.put(topicName, pulsarConsumers);
            }
        } else {
            List<PulsarConsumer> pulsarConsumerList = new ArrayList<>();
            for (int i = 0; i < consumerNum; i++) {
                Consumer<String> subscribe = null;
                try {
                    subscribe = pulsarClient.newConsumer(Schema.STRING)
                            .topic(topics)
                            .subscriptionName("Test-Consumer")
                            .keySharedPolicy(KeySharedPolicy.autoSplitHashRange().setAllowOutOfOrderDelivery(true))
                            .subscriptionType(SubscriptionType.Key_Shared)
                            .subscribe();
                    PulsarConsumer pulsarConsumer = new PulsarConsumer(topics, i, subscribe);
                    pulsarConsumerList.add(pulsarConsumer);
                    pulsarConsumer.start();
                } catch (PulsarClientException e) {
                    e.printStackTrace();
                }
            }
            consumerMap.put(topicName, pulsarConsumerList);
        }
        return "主题：" + topicName + "的消费线程启动成功！";
    }

    @RequestMapping("/getConsumeInfo")
    public Map<String, Integer> getConsumeCountList() {
        return PulsarConsumer.consumeCountList;
    }

    @RequestMapping("/getProduceInfo")
    public Map<String, Integer> getProduceInfo() {
        return PulsarProducer.publishCountList;
    }

    @RequestMapping("/stopConsume/{topicName}/{index}")
    public void stopConsumer(@PathVariable String topicName, @PathVariable int index) {
        List<PulsarConsumer> pulsarConsumers = consumerMap.get(topicName);
        if (pulsarConsumers.size() != 0) {
            PulsarConsumer pulsarConsumer = pulsarConsumers.get(index);
            if (null != pulsarConsumer) {
                pulsarConsumer.stop();
            }
        }
    }

    @RequestMapping("/restartConsumer/{topicName}/{index}")
    public void restartConsumer(@PathVariable String topicName, @PathVariable int index) {
        List<PulsarConsumer> pulsarConsumers = consumerMap.get(topicName);
        if (pulsarConsumers.size() != 0) {
            PulsarConsumer pulsarConsumer = pulsarConsumers.get(index);
            if (null != pulsarConsumer) {
                pulsarConsumer.start();
            }
        }
    }

    /**
     * 获取某个主题所有消费者线程的状态
     *
     * @param topicName
     * @return
     */
    @RequestMapping("/threadStats/{topicName}")
    public Map<Integer, Boolean> getConsumeThreadStats(@PathVariable String topicName) {
        List<PulsarConsumer> pulsarConsumers = consumerMap.get(topicName);
        Map<Integer, Boolean> statsMap = new HashMap<>();
        for (PulsarConsumer pulsarConsumer : pulsarConsumers) {
            boolean currentThreadAlive = pulsarConsumer.isCurrentThreadAlive();
            int number = pulsarConsumer.getNumber();
            statsMap.put(number, currentThreadAlive);
        }
        return statsMap;
    }


}
