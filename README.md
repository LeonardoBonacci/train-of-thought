# no-more-running-for-trains

https://quarkus.io/guides/kafka-streams-guide
https://dev.to/skhmt/creating-a-native-executable-in-windows-with-graalvm-3g7f
http://karols.github.io/blog/2019/05/12/native-image-on-windows-10-x64/
https://redis.io/topics/protocol
https://tile38.com/
https://github.com/lettuce-io/lettuce-core/wiki/Custom-commands,-outputs-and-command-mechanics
https://tile38.com/topics/replication/

https://docs.confluent.io/current/ksql/docs/developer-guide/create-a-stream.html

Some useful commands:

>>> MVN
mvn clean package -f train-source/pom.xml ; docker-compose up --build


>> KAFKACAAT
docker run --tty --rm -i --network ks debezium/tooling:1.0
kafkacat -b kafka:9092 -C -o beginning -q -t trains_at_stations
kafkacat -b kafka:9092 -C -o beginning -q -t trains_at_stations -f "%k\n"

 
>> KSQL
docker-compose exec ksql-cli ksql http://ksql-server:8088

PRINT 'trains_at_stations' FROM BEGINNING;

SET 'auto.offset.reset'='earliest';
CREATE STREAM inner_tas (id STRING, fields MAP<STRING,STRING>) WITH (KAFKA_TOPIC = 'trains_at_stations', VALUE_FORMAT = 'JSON');
SHOW TOPICS;
PRINT 'inner_tas' FROM BEGINNING;        

CREATE STREAM i_have_arrived AS SELECT id, fields['MOMENT'] AS moment FROM inner_tas PARTITION BY id;


>> TILE38
docker run --net=host -it tile38/tile38 tile38-cli
SETHOOK trains_at_stations kafka://kafka:9092/trains_at_stations NEARBY trains FENCE ROAM stations * 10

SET stations JV POINT 33.01 -115.01
SET trains FOO123BAR FIELD SPEED 90 POINT 33.01 -115.01

SET trains 123 FIELD SPEED 90 POINT 33.01 -115.01