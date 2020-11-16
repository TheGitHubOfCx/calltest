package com.example.calltest.pulsarDemo.service;

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

public class ProducerService implements Runnable {

    private Log LOG = LogFactory.get();

    private int start;

    private int end;

    private int msgNum;

    private String regTopicName;

    private PulsarClient pulsarClient;

    public static Map<String, Integer> publishCountList = new ConcurrentHashMap<>();

    private static Map<String, Producer<String>> producerList = new ConcurrentHashMap<>();

    private List<UUID> uuidList;

    public ProducerService(int start, int end, int msgNum, PulsarClient pulsarClient, String regTopicName, List<UUID> uuidList) {
        this.start = start;
        this.end = end;
        this.msgNum = msgNum;
        this.pulsarClient = pulsarClient;
        this.regTopicName = regTopicName;
        this.uuidList = uuidList;
    }

    @Override
    public void run() {
        send(start, end, msgNum);
    }

    public static void stopProducer(String topic) {
        Producer<String> producer = producerList.get(topic);
        if (null != producer) {
            try {
                producer.close();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(int start, int end, int msgNum) {
//        for (int i = start; i < end; i++) {
//            try {
//                Producer<String> producer = producerList.get(regTopicName + "-" + i);
//                if (null == producer) {
//                    producer = pulsarClient.newProducer(Schema.STRING)
//                            .topic(regTopicName + "-" + i)
//                            .enableBatching(false) //关闭批处理
//                            .producerName("生产者-" + i)
//                            .sendTimeout(5, TimeUnit.SECONDS)
//                            .create();
//                    producerList.put(regTopicName + "-" + i, producer);
//                    System.out.println("生产者创建成功==" + "生产者-" + i);
//                }
//                for (int i1 = 0; i1 < msgNum; i1++) {
//                    int key = (int) (Math.random() * 20); //0-2数字随机获取一个key
//                    producer.newMessage().key(uuidList.get(key)+"").value(String.valueOf(key)).send();
//                    Integer integer = publishCountList.get(uuidList.get(key)+"");
//                    if (publishCountList.get(uuidList.get(key)+"") == null) {
//                        publishCountList.put(uuidList.get(key)+"", 1);
//                    } else {
//                        publishCountList.put(uuidList.get(key)+"", ++integer);
//                    }
//                }
////                publishCountList.put(producer.getTopic(), msgNum);
//            } catch (PulsarClientException e) {
//                LOG.error("生产者消费异常：{}", "生产者-" + i);
//            }
//        }
    }

    public List<UUID> getUuidList() {
        return uuidList;
    }

    public void setUuidList(List<UUID> uuidList) {
        this.uuidList = uuidList;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }

    public PulsarClient getPulsarClient() {
        return pulsarClient;
    }

    public void setPulsarClient(PulsarClient pulsarClient) {
        this.pulsarClient = pulsarClient;
    }
}
