该项目基于kafka_2.11_2.0.0
# 常用命令
启动zookeeper
>bin/zookeeper-server-start.sh config/zookeeper.properties &

启动kafka-server
>bin/kafka-server-start.sh config/server.properties &

查看所有topic
>bin/kafka-topics.sh --zookeeper localhost:2181 --describe
>bin/kafka-topics.sh --list --zookeeper localhost:2181

创建一个topic
>bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

删除一个topic
>bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic test

创建一个生产者
>bin/kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input

创建一个消费者
>bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test1 --from-beginning
>bin/kafka-console-consumer.sh --bootstrap-server localhost:9092     --topic streams-wordcount-output     --from-beginning     --formatter kafka.tools.DefaultMessageFormatter     --property print.key=true     --property print.value=true     --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer     --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

关闭kafka-server
>bin/kafka-server-stop.sh

