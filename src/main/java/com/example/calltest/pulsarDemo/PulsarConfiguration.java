package com.example.calltest.pulsarDemo;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PulsarConfiguration {

    @Value("${spring.pulsar.instance-servers}")
    private String pulsarBrokers;

    private Log LOG = LogFactory.get();

    /**
     * “serviceUrl” : “pulsar://localhost:6650”, //broker集群地址
     * “operationTimeoutMs” : 30000, //操作超时设置
     * “statsIntervalSeconds” : 60, //设置每个统计信息之间的间隔（默认值：60秒）统计信息将以正值激活状态间隔秒数应设置为至少1秒
     * “numIoThreads” : 1,//设置用于处理与broker的连接的线程数（默认值：1个线程）
     * “numListenerThreads” : 1,// 设置要用于消息侦听器的线程数（默认值：1个线程）
     * “connectionsPerBroker” : 1, //设置客户端库将向单个broker打开的最大连接数。
     * “useTcpNoDelay” : true, //配置是否在连接上使用延迟tcp,默认为true。无延迟功能确保数据包尽快发送到网络上，实现低延迟发布至关重要。另一方面，发送大量的小数据包可能会限制整体吞吐量。
     * “useTls” : false, // 启用ssl,在serviceurl中使用“pulsar+ssl://”启用
     * “tlsTrustCertsFilePath” : “”,//设置受信任的TLS证书文件的路径
     * “tlsAllowInsecureConnection” : false, //配置pulsar客户端是否接受来自broker的不受信任的TLS证书（默认值：false）
     * “tlsHostnameVerificationEnable” : false,//它允许在客户端通过TLS连接到代理时验证主机名验证
     * “concurrentLookupRequest” : 5000,//允许在每个broker连接上发送的并发查找请求数，以防止代理过载。
     * “maxLookupRequest” : 50000,//为防止broker过载，每个broker连接上允许的最大查找请求数。
     * “maxNumberOfRejectedRequestPerConnection” : 50,//设置在特定时间段（30秒）内被拒绝的broker请求的最大数目，在此时间段后，当前连接将关闭，客户端将创建一个新连接，以便有机会连接其他broker（默认值：50）
     * “keepAliveIntervalSeconds” : 30 //为每个客户端broker连接设置以秒为单位的心跳检测时间
     *
     * @return
     */
    @Bean
    public PulsarClient pulsarClient() {
        PulsarClient client = null;
        try {
            client = PulsarClient.builder()
                    .serviceUrl(pulsarBrokers)
                    .build();
        } catch (PulsarClientException e) {
            LOG.error("创建客户端失败：{}", pulsarBrokers);
            e.printStackTrace();
        }
        return client;
    }

    @Bean
    public PulsarAdmin pulsarAdmin() {
        String url = "http://192.168.51.111:8080,192.168.51.112:8080,192.168.51.113:8080";
// Pass auth-param if auth-plugin class requires it
        boolean tlsAllowInsecureConnection = false;
        String tlsTrustCertsFilePath = null;
        PulsarAdmin admin = null;
        try {
            admin = PulsarAdmin.builder()
                    .serviceHttpUrl(url)
                    .tlsTrustCertsFilePath(tlsTrustCertsFilePath)
                    .allowTlsInsecureConnection(tlsAllowInsecureConnection)
                    .build();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return admin;
    }

    /**
     * “topicName” : “persistent://public/pulsar-cluster/default/my-topic”, //topicName 由四部分组成 [topic类型://租户名/命名空间/主题名]
     * “producerName” : “my-producer”, //生产者名称
     * “sendTimeoutMs” : 30000, //发送超时时间，默认 30s
     * “blockIfQueueFull” : false, //消息队列已满时是否阻止发送操作 默认false,当消息队列满，发送操作将立即失败
     * “maxPendingMessages” : 1000,//设置等待接收来自broker的确认消息的队列的最大大小,队列满试,blockIfQueueFull=true才有效
     * “maxPendingMessagesAcrossPartitions” : 50000,//设置所有分区的最大挂起消息数
     * “messageRoutingMode” : “CustomPartition”, //消息分发路由模式 CustomPartition；RoundRobinPartition 环形遍历分区；SinglePartition 随机选择一个分区 //参考http://pulsar.apache.org/docs/zh-CN/2.2.0/cookbooks-partitioned/
     * “hashingScheme” : “JavaStringHash”,//更改用于选择在何处发布特定消息的分区的哈希方案
     * “cryptoFailureAction” : “FAIL”,//为失效的生产者指定一个默认的特定值
     * “batchingMaxPublishDelayMicros” : 1000,//设置发送的消息将被成批处理的时间段默认值：如果启用了成批消息，则为1毫秒。
     * “batchingMaxMessages” : 1000, //设置批处理中允许的最大消息数
     * “batchingEnabled” : true, //控制是否为生产者启用消息的自动批处理。
     * “compressionType” : “NONE”, //设置生产者的压缩类型
     * “initialSequenceId” : null, //为生产者发布的消息设置序列ID的基础值
     * “properties” : { } //为生产者设置属性
     *
     * @return
     */
//    @Bean
//    public Producer producer() {
//        PulsarClient pulsarClient = pulsarClient();
//        Producer producer = null;
//        try {
//                producer = pulsarClient.newProducer(Schema.STRING)
//                        .topic("0922-P")
//                        .sendTimeout(5, TimeUnit.SECONDS)
//                        .create();
//        } catch (PulsarClientException e) {
//            LOG.error("创建客户端失败：{}", pulsarBrokers);
//            e.printStackTrace();
//        }
//        return producer;
//    }


    /**
     * 消费者consumer：
     *  “topicNames” : [ ], //消费者订阅的主题
     *  “topicsPattern” : null, //指定此使用者将订阅的主题的模式。它接受正则表达式，并将在内部编译为模式。例如：“persistent://prop/use/ns abc/pattern topic-.*”
     *  “subscriptionName” : “my-subscription”, //消费者的订阅名
     *  “subscriptionType” : “Exclusive”,//选择订阅主题时要使用的订阅类型。 Exclusive 独占；Failover 故障转移 ；Shared 共享
     *  “receiverQueueSize” : 3,//设置消费者接收队列的大小。
     *  “acknowledgementsGroupTimeMicros” : 100000, //按指定时间对消费者分组
     *  “maxTotalReceiverQueueSizeAcrossPartitions” : 10, //设置跨分区的最大总接收器队列大小
     *  “consumerName” : “my-consumer”, //消费者的名字
     *  “ackTimeoutMillis” : 10000,//设置未确认消息的超时
     *  “priorityLevel” : 0, //为共享订阅使用者设置优先级级别，broker 在调度消息时向其提供更高的优先级。
     *  “cryptoFailureAction” : “FAIL”,//为失效的消费者指定一个默认的特定值
     *  “properties” : { }, //设置属性值
     *  “readCompacted” : false, //如果启用，消费者将从压缩的主题中读取消息，而不是读取主题的完整消息积压。
     *  “subscriptionInitialPosition” : “Latest”, //设置消费者的订阅初始位置 Earliest 从最早的位置，即第一条消息。 Latest 从最后的位置，即最后一条消息。
     *  “patternAutoDiscoveryPeriod” : 1, //为主题消费者使用模式时设置主题自动发现周期。
     *  “subscriptionTopicsMode” : “PERSISTENT”,//确定此消费者应订阅哪些主题-持久性主题、非持久性主题或两者都应订阅。
     *  “deadLetterPolicy” : null //死信策略 为消费者设置死信策略，某些消息将尽可能多次重新传递。通过使用死信机制，消息将具有最大重新传递计数，当消息超过最大重新传递数时，消息将发送到死信主题并自动确认。您可以通过设置死信策略来启用死信机制。
     */
}
