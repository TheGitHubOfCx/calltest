package com.example.calltest.pulsarTest;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.pulsar.client.api.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PulsarConsumer implements Runnable {

    protected Log LOG = LogFactory.get();

    /**
     * 内部参数
     */
    private AtomicBoolean running = new AtomicBoolean(false); //控制当前线程运行的标志位

    public static Map<String, Integer> consumeCountList = new ConcurrentHashMap<>();

    Thread currentThread; //当前线程

    Thread waitingThread; //当前线程停止后, 下一个可能会运行的新线程

    private String[] topics;

    private int number; //消费者编号

    private Consumer<String> consumer;

    private final static String threadName = "Pulsar-Consumer";


    public PulsarConsumer(String[] topics, int number, Consumer<String> consumer) {
        this.topics = topics;
        this.number = number;
        this.consumer = consumer;
    }

    public void start() {
        if (waitingThread != null) {
            LOG.info("pulsar消费者 - 已经有新线程在等待开启");
            return;
        }
        if (isCurrentThreadAlive()) {
            LOG.info("pulsar消费者 - 新线程等待当前线程关闭后开启");
            waitingThread = new Thread(this);
            waitingThread.setName(threadName);
            return;
        }
        running.set(true);
        currentThread = new Thread(this);
        currentThread.setName(threadName);
        currentThread.start();
        LOG.info("pulsar消费者 - 成功开启消费者");
    }

    public void stop() {
        if (running.get()) {
            LOG.info(topics[0] + "的消费者" + number + " - 准备关闭");
        }
        running.set(false);
        if (waitingThread != null) {
            waitingThread = null;
            LOG.info("pulsar消费者 - 正在等待开启的新线程将不再开启");
        }
        LOG.info(topics[0] + "的消费者" + number + " - 成功关闭");
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                Message msg = consumer.receive();
//                MessageId lastMessageId = consumer.getLastMessageId();
                long l = System.currentTimeMillis();
                try {
                    String key = msg.getKey();
                    System.err.println(number + "::" + key);
                    Integer integer = consumeCountList.get(number + "-" + key);
                    if (consumeCountList.get(number + "-" + key) == null) {
                        consumeCountList.put(number + "-" + key, 1);
                    } else {
                        consumeCountList.put(number + "-" + key, ++integer);
                    }
                    consumer.acknowledge(msg);
//                    Thread.sleep(600);
                } catch (Exception e) {
                    consumer.negativeAcknowledge(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isCurrentThreadAlive() {
        if (currentThread == null) {
            return false;
        }
        return running.get();
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Consumer<String> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    public static String getThreadName() {
        return threadName;
    }
}
