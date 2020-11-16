package com.example.calltest.pulsarDemo;

import com.example.calltest.monitor.Sys;
import com.example.calltest.pulsarDemo.service.ConsumerService;
import com.example.calltest.pulsarDemo.service.FlowConsumeService;
import com.example.calltest.pulsarDemo.service.ProducerService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.Random;

@RestController
public class TestPulsarController {

    @Autowired
    ProducerTest producerTest;

    @Autowired
    ConsumerTest consumerTest;

    /**
     * 测试发布
     */
    @RequestMapping("/testProducer/{topicNum}/{msgNum}/{regTopicName}")
    public String testProducer(@PathVariable int topicNum, @PathVariable int msgNum, @PathVariable String regTopicName) {
        return producerTest.testProducer(topicNum, msgNum, regTopicName);
    }

    /**
     * 停止生产
     *
     * @param topicNum
     * @param regTopicName
     * @return
     */
    @RequestMapping("/stopProducer")
    public String stopProducer(@PathVariable int topicNum, @PathVariable String regTopicName) {
        producerTest.stopProducer(topicNum, regTopicName);
        return "stop producer success";
    }

    /**
     * 测试消费
     *
     * @param topicNum
     */
    @RequestMapping("/testConsumer/{topicNum}/{regTopicName}")
    public void testConsumer(@PathVariable int topicNum, @PathVariable String regTopicName) {
        consumerTest.testConsume(topicNum, regTopicName);
    }


    @RequestMapping("/testFlowConsumer")
    public void testFlowConsumer() {
        consumerTest.testFlowConsumer();
    }

    @RequestMapping("/testBigConsumer/{topicNum}")
    public void testBigConsumer(@PathVariable int topicNum) {
        consumerTest.testBigConsumer(topicNum);
    }

    @RequestMapping("/testSingleProducer/{topic}/{msgNum}")
    public Map<String, Integer> testSingleProducer(@PathVariable String topic, @PathVariable int msgNum) {
        return producerTest.testSingleProducer(topic, msgNum);
    }

    @RequestMapping("/testSingleProducer2/{topic}/{msgNum}")
    public Map<String, Integer> testSingleProducer2(@PathVariable String topic, @PathVariable int msgNum) {
        return producerTest.testSingleProducer2(topic, msgNum);
    }

    @RequestMapping("/testSingleProducer3/{topic}/{msgNum}")
    public Map<String, Integer> testSingleProducer3(@PathVariable String topic, @PathVariable int msgNum) {
        return producerTest.testSingleProducer3(topic, msgNum);
    }


    @RequestMapping("/stopConsumer")
    public String stopConsumer() {
        consumerTest.stopConsumer();
        return "stop consumer success";
    }


    @RequestMapping("/getConsumeCount")
    public Map<String, Integer> getConsumeCount() {
        Map<String, Integer> consumeCountList = ConsumerService.consumeCountList;
        return consumeCountList;
    }

    @RequestMapping("/getFlowConsumeCount")
    public Map<String, Integer> getFlowConsumeCount() {
        Map<String, Integer> consumeCountList = FlowConsumeService.consumeCountList;
        return consumeCountList;
    }

    @RequestMapping("/getPublishCount")
    public Map<String, Integer> getPublish() {
        Map<String, Integer> publishCountList = ProducerService.publishCountList;
        return publishCountList;
    }

//    @Test
//    public void getRandomNum(){
////        Random r=new Random();
////        for (int i = 0; i < 20; i++) {
////            int i1 = r.nextInt(2000000);
////            System.err.println(i1);
////        }
//        long l = System.currentTimeMillis();
////        try {
////            Thread.sleep(2000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        long l1 = System.currentTimeMillis();
//        Duration duration = Duration.ofMillis(l1-l);
//        long seconds = duration.getSeconds();
//        System.err.println("消费耗时间：" + seconds + "秒");
//    }


}
