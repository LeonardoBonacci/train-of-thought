# no more need to run for trains!

##MANUAL STEPS (for now..)
docker run --net=host -it tile38/tile38 tile38-cli
SETHOOK trains_at_stations kafka://kafka:9092/i-have-arrived NEARBY trains FENCE ROAM stations * 50

docker-compose exec ksql-cli ksql http://ksql-server:8088
SET 'ksql.sink.partitions'='3';
CREATE STREAM "arahants" (id STRING, time STRING, nearby MAP<STRING,STRING>) WITH (KAFKA_TOPIC = 'i-have-arrived', VALUE_FORMAT = 'JSON');
CREATE STREAM "i-am-home" AS SELECT id, time as "moment", nearby['id'] AS "station" FROM "arahants" WHERE nearby IS NOT NULL PARTITION BY id;


https://quarkus.io/guides/kafka-streams-guide
https://dev.to/skhmt/creating-a-native-executable-in-windows-with-graalvm-3g7f
http://karols.github.io/blog/2019/05/12/native-image-on-windows-10-x64/
https://redis.io/topics/protocol
https://tile38.com/
https://github.com/lettuce-io/lettuce-core/wiki/Custom-commands,-outputs-and-command-mechanics
https://tile38.com/topics/replication/
https://docs.confluent.io/current/ksql/docs/installation/install-ksql-with-docker.html 
https://dev.to/skhmt/creating-a-native-executable-in-windows-with-graalvm-3g7f
http://karols.github.io/blog/2019/05/12/native-image-on-windows-10-x64/
https://docs.confluent.io/current/ksql/docs/developer-guide/create-a-stream.html

Some useful commands:

>>> MVN
mvn clean package -f source/pom.xml ; docker-compose up --build
mvn clean package -f arrival-processor/pom.xml ; docker-compose up --build


>> KAFKACAAT
docker run --tty --rm -i --network ks debezium/tooling:1.0
kafkacat -b kafka:9092 -C -o beginning -q -t average-arrival-times
kafkacat -b kafka:9092 -C -o beginning -q -t average-arrival-times -f "%k\n"

 
>> KSQL
docker-compose exec ksql-cli ksql http://ksql-server:8088

PRINT 'I_HAVE_ARRIVED' FROM BEGINNING;

SET 'auto.offset.reset'='earliest';
CREATE STREAM arahants (id STRING, time STRING) WITH (KAFKA_TOPIC = 'I_HAVE_ARRIVED', VALUE_FORMAT = 'JSON');
CREATE STREAM I_AM_HOME AS SELECT id, time as moment FROM arahants PARTITION BY id;


>> TILE38
docker run --net=host -it tile38/tile38 tile38-cli
SETHOOK trains_at_stations kafka://kafka:9092/I_HAVE_ARRIVED NEARBY trains FENCE ROAM stations * 10

SET stations JV POINT 33.01 -115.01
SET trains FOO123BAR FIELD SPEED 90 POINT 33.01 -115.01

SET trains 123 FIELD SPEED 90 POINT 33.01 -115.01