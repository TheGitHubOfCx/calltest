package com.example.calltest.pulsarDemo.service;

import com.example.calltest.monitor.Sys;
import org.apache.pulsar.client.api.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class ConsumerService implements Runnable {


    private String topic;

    private PulsarClient pulsarClient;

    private int index;

    private boolean running = true;


    public static Map<String, Integer> consumeCountList = new ConcurrentHashMap<>();

    public static Map<String, Consumer<String>> consumerList = new ConcurrentHashMap<>();

    public static Map<String, Producer<String>> producerList = new ConcurrentHashMap<>();

    public ConsumerService(PulsarClient pulsarClient, String topic, int i) {
        this.pulsarClient = pulsarClient;
        this.topic = topic;
        this.index = i;
    }

    public void stop() {
        if (running) {
            running = false;
        }
    }


    @Override
    public void run() {
        try {
            Consumer<String> consumer = pulsarClient.newConsumer(Schema.STRING)
                    .topic(topic)
                    .subscriptionName(topic)
                    .subscriptionType(SubscriptionType.Key_Shared)
                    .subscribe();
            consumerList.put(consumer.getConsumerName(), consumer);
            int consumeCount = 0;
            while (running) {
                // Wait for a message
                Message msg = consumer.receive();
                long l = System.currentTimeMillis();
                try {
                    // Do something with the message
                    String key = msg.getKey();
                    System.err.println(index + "-" + key + "-" + msg.getValue());
//                    Producer<String> producer = producerList.get("flowId" + "-" + key);
//                    if (null == producer) {
//                        producer = pulsarClient.newProducer(Schema.STRING)
//                                .topic("flowId" + "-" + key)
//                                .enableBatching(false) //关闭批处理
//                                .producerName("生产者-" + key)
//                                .sendTimeout(5, TimeUnit.SECONDS)
//                                .create();
//                        producerList.put("flowId" + "-" + key, producer);
//                        System.out.println("集成流ID生产者创建成功==" + "生产者-" + key);
//                    }
//                    producer.send(String.valueOf(msg.getValue()));
                    // Acknowledge the message so that it can be deleted by the message broker
                    Integer integer = consumeCountList.get(topic + "-" + index + "-" + key);
                    if (consumeCountList.get(topic + "-" + index + "-" + key) == null) {
                        consumeCountList.put(topic + "-" + index + "-" + key, 1);
                    } else {
                        consumeCountList.put(topic + "-" + index + "-" + key, ++integer);
                    }
                    consumer.acknowledge(msg);
//                    long l1 = System.currentTimeMillis();
//                    long l2 = l1 - l;
//                    System.err.println("分发耗时：" + l2 + "毫秒");
//                    ++consumeCount;
//                    consumeCountList.put(topic + "-" + key, consumeCount);
                    Thread.sleep(2);
                } catch (Exception e) {
                    // Message failed to process, redeliver later
                    consumer.negativeAcknowledge(msg);
                }
            }
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
