package com.example.calltest.pulsarDemo.service;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SingleProducerService implements Runnable {

    private int msgNum;

    private String topicName;

    private PulsarClient pulsarClient;

    public static Map<String, Integer> publishCountList = new ConcurrentHashMap<>();

    public SingleProducerService(int msgNum, String topicName, PulsarClient pulsarClient) {
        this.msgNum = msgNum;
        this.topicName = topicName;
        this.pulsarClient = pulsarClient;
    }

    @Override
    public void run() {
        try {
            Producer<String> producer = pulsarClient.newProducer(Schema.STRING)
                    .topic(topicName)
                    .enableBatching(false) //关闭批处理
                    .producerName("生产者-" + topicName)
                    .sendTimeout(5, TimeUnit.SECONDS)
                    .create();
            for (int i = 0; i < msgNum; i++) {
                int key = (int) (Math.random() * 3); //0-2数字随机获取一个key
                producer.newMessage().key(key + "").value(String.valueOf(key)).send();
                Integer integer = publishCountList.get(topicName + "-" + key);
                if (publishCountList.get(topicName + "-" + key) == null) {
                    publishCountList.put(topicName + "-" + key, 1);
                } else {
                    publishCountList.put(topicName + "-" + key, ++integer);
                }
            }
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    public int getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public PulsarClient getPulsarClient() {
        return pulsarClient;
    }

    public void setPulsarClient(PulsarClient pulsarClient) {
        this.pulsarClient = pulsarClient;
    }
}
