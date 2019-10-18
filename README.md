# no-more-running-for-trains

Thanks guys for writing this: https://quarkus.io/guides/kafka-streams-guide

Some useful commands:

docker run --tty --rm -i --network ks debezium/tooling:1.0
kafkacat -b kafka:9092 -C -o beginning -q -t train-stations

docker-compose exec ksql-cli ksql http://ksql-server:8088

CREATE TABLE stations (id INTEGER, location STRING) WITH (KAFKA_TOPIC = 'train-stations', VALUE_FORMAT='JSON', KEY = 'id');
SET 'auto.offset.reset'='earliest';

CREATE STREAM tr_events (id INTEGER, name STRING, moment BIGINT, location INTEGER) WITH (KAFKA_TOPIC = 'train-events', VALUE_FORMAT = 'JSON', KEY='id', TIMESTAMP='moment');

SELECT * FROM tr_events;


CREATE STREAM tr_at_stations AS SELECT * FROM tr_events WHERE location % 5 = 0 PARTITION BY id;