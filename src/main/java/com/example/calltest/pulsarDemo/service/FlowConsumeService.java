package com.example.calltest.pulsarDemo.service;

import org.apache.pulsar.client.api.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlowConsumeService implements Runnable {

    private String[] topics;

    private PulsarClient pulsarClient;

    private int index;

    public static Map<String, Integer> consumeCountList = new ConcurrentHashMap<>();

    public FlowConsumeService(String[] topics, PulsarClient pulsarClient, int index) {
        this.topics = topics;
        this.pulsarClient = pulsarClient;
        this.index = index;
    }

    @Override
    public void run() {
        try {
            Consumer<String> subscribe = pulsarClient.newConsumer(Schema.STRING)
                    .topic(topics)
                    .subscriptionName("testSub" + index)
                    .subscriptionType(SubscriptionType.Key_Shared)
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscribe();
            while (true) {
                Message<String> receive = subscribe.receive();
                try {
                    String topic = receive.getTopicName();
                    System.err.println(topic + ":" + receive.getValue());
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscribe.acknowledge(receive);
                    Integer integer = consumeCountList.get(topic);
                    if (consumeCountList.get(topic) == null) {
                        consumeCountList.put(topic, 1);
                    } else {
                        consumeCountList.put(topic, ++integer);
                    }
                } catch (Exception e) {
                    subscribe.negativeAcknowledge(receive);
                }
            }
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public PulsarClient getPulsarClient() {
        return pulsarClient;
    }

    public void setPulsarClient(PulsarClient pulsarClient) {
        this.pulsarClient = pulsarClient;
    }
}
