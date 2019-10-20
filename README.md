# no-more-running-for-trains

https://quarkus.io/guides/kafka-streams-guide
https://redis.io/topics/protocol
https://tile38.com/
https://github.com/lettuce-io/lettuce-core/wiki/Custom-commands,-outputs-and-command-mechanics
https://tile38.com/topics/replication/

Some useful commands:

>>> MVN
mvn clean package -f train-source/pom.xml

>> KAFKACAAT
docker run --tty --rm -i --network ks debezium/tooling:1.0
kafkacat -b kafka:9092 -C -o beginning -q -t trains_at_stations

>> KSQL
docker-compose exec ksql-cli ksql http://ksql-server:8088

CREATE TABLE stations (id INTEGER, location STRING) WITH (KAFKA_TOPIC = 'train-stations', VALUE_FORMAT='JSON', KEY = 'id');
SET 'auto.offset.reset'='earliest';

CREATE STREAM tr_events (id INTEGER, name STRING, moment BIGINT, location INTEGER) WITH (KAFKA_TOPIC = 'train-events', VALUE_FORMAT = 'JSON', KEY='id', TIMESTAMP='moment');

SELECT * FROM tr_events;
SELECT * FROM tr_events LEFT JOIN stations ON true WHERE location % 5 = 0; 

CREATE STREAM tr_at_stations AS SELECT * FROM tr_events WHERE location % 5 = 0 LEFT JOIN users ON true PARTITION BY id;

>> TILE38
docker run --net=host -it tile38/tile38 tile38-cli

SETHOOK trains_at_stations kafka://kafka:9092/trains_at_stations NEARBY trains FENCE ROAM stations * 10
