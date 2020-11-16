package com.example.calltest.pulsarTest;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PulsarProducer implements Runnable {

    protected Log LOG = LogFactory.get();

    private int msgNum;

    private String topicName;

    private PulsarClient pulsarClient;

    public static Map<String, Integer> publishCountList = new ConcurrentHashMap<>();

    private static Map<String, Producer<String>> producerList = new ConcurrentHashMap<>();

    private List<UUID> uuidList;

    private final static String threadName = "Pulsar-Producer";


    Thread currentThread; //当前线程

    Thread waitingThread; //当前线程停止后, 下一个可能会运行的新线程


    public void start() {
        if (waitingThread != null) {
            LOG.info("pulsar生产者 - 已经有新线程在等待开启");
            return;
        }
        if (isCurrentThreadAlive()) {
            LOG.info("pulsar生产者 - 新线程等待当前线程关闭后开启");
            waitingThread = new Thread(this);
            waitingThread.setName(threadName);
            return;
        }
        currentThread = new Thread(this);
        currentThread.setName(threadName);
        currentThread.start();
    }

    public PulsarProducer(int msgNum, PulsarClient pulsarClient, String topicName, List<UUID> uuidList) {
        this.msgNum = msgNum;
        this.pulsarClient = pulsarClient;
        this.topicName = topicName;
        this.uuidList = uuidList;
    }


    @Override
    public void run() {
        try {
            Producer<String> producer = producerList.get(topicName);
            if (null == producer) {
                producer = pulsarClient.newProducer(Schema.STRING)
                        .topic(topicName)
                        .enableBatching(false) //关闭批处理
                        .producerName("生产者-" + topicName)
                        .sendTimeout(5, TimeUnit.SECONDS)
                        .create();
                producerList.put(topicName,producer);
                System.out.println("生产者创建成功==" + "生产者-" + topicName);
            }
            for (int i1 = 0; i1 < msgNum; i1++) {
                int key = (int) (Math.random() * 20); //0-2数字随机获取一个key
                producer.newMessage().key(uuidList.get(key) + "").value(String.valueOf(uuidList.get(key))).send();
                Integer integer = publishCountList.get(uuidList.get(key) + "");
                if (publishCountList.get(uuidList.get(key) + "") == null) {
                    publishCountList.put(uuidList.get(key) + "", 1);
                } else {
                    publishCountList.put(uuidList.get(key) + "", ++integer);
                }
            }
        } catch (PulsarClientException e) {
            LOG.error("生产者消费异常：{}", "生产者-" + topicName);
        }
    }

    private boolean isCurrentThreadAlive() {
        if (currentThread == null) {
            return false;
        }
        return currentThread.isAlive();
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

    public static Map<String, Integer> getPublishCountList() {
        return publishCountList;
    }

    public static void setPublishCountList(Map<String, Integer> publishCountList) {
        PulsarProducer.publishCountList = publishCountList;
    }

    public List<UUID> getUuidList() {
        return uuidList;
    }

    public void setUuidList(List<UUID> uuidList) {
        this.uuidList = uuidList;
    }

    public static String getThreadName() {
        return threadName;
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    public void setCurrentThread(Thread currentThread) {
        this.currentThread = currentThread;
    }

    public Thread getWaitingThread() {
        return waitingThread;
    }

    public void setWaitingThread(Thread waitingThread) {
        this.waitingThread = waitingThread;
    }
}
