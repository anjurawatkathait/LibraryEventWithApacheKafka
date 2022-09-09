Embedded Kafka - 
        Its an in memory Kafka, its mainly used in the scope of integration test.
        We would not need any zookeeper or cluster anything up n running.
        Embedded Kafka will get launch up and vanish after test complete.
git push --force https://github.com/anjurawatkathait/LibraryEventWithApacheKafka.git

same key will go to only one partition always, 




Setup :

If not able to setup zookeeper and broker then go to /tmp/logs and delete kafka-logs file 

1. zoopkeeper
./zookeeper-server-start.sh ../config/zookeeper.properties
2. Broker
   Add the below properties in the server.properties
   listeners=PLAINTEXT://localhost:9092
   auto.create.topics.enable=false
   Then run below command for setting up broker
   ./kafka-server-start.sh ../config/server.properties
3. 