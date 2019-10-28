## COMMANDS
clear ; docker-compose -f .\docker-compose-ksql.yaml up

clear ; docker run --tty --rm -i --network ks debezium/tooling:1.0

kafkacat -P -b kafka -t i-pass-through -K:
JVL-WELL:{ "trainName" : "JVL-WELL", "stationName": "Ngaio" }

kafkacat -P -b kafka -t i-am-here -K:
def:{ "trainId": "def", "trainName": "JVL-WELL", "lat": 33.00, "lon": -112.01 }

--------------------------------------------------------------------------------

clear ; docker-compose -f .\docker-compose-ksql.yaml exec ksql-cli ksql http://ksql-server:8088
SET 'auto.offset.reset'='earliest';


CREATE STREAM i_pass_through_src (trainName STRING, stationName STRING) WITH (KAFKA_TOPIC = 'i-pass-through', VALUE_FORMAT = 'JSON');
CREATE STREAM i_pass_through_part AS SELECT * FROM i_pass_through_src PARTITION BY trainName;
CREATE TABLE i_pass_through_table (trainName VARCHAR, stationName VARCHAR) WITH (KAFKA_TOPIC='I_PASS_THROUGH_PART', VALUE_FORMAT='JSON', KEY='trainName');

CREATE STREAM i_am_here_src (trainId STRING, trainName STRING, lat DOUBLE, lon DOUBLE) WITH (KAFKA_TOPIC = 'i-am-here', VALUE_FORMAT = 'JSON');
CREATE STREAM i_am_here_stream AS SELECT * FROM i_am_here_src PARTITION BY trainName;

CREATE STREAM "i_go_to" AS
  SELECT i.trainId as trainId, i.trainName as trainName, i.lat, i.lon, pass.stationName as stationName FROM i_am_here_stream i
  JOIN i_pass_through_table pass ON i.trainName = pass.trainName
  PARTITION BY trainId;
