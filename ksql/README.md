## COMMANDS
clear ; docker-compose -f .\docker-compose-ksql.yaml up

clear ; docker run --tty --rm -i --network ks debezium/tooling:1.0

kafkacat -P -b kafka -t i-pass-through -K:
JVL-WELL:{ "trainname" : "JVL-WELL", "stationname": "Ngaio" }

kafkacat -P -b kafka -t i-am-here -K:
abc:{ "trainid": "abc", "trainname": "JVL-WELL", "otherfield": "bla" }

--------------------------------------------------------------------------------

clear ; docker-compose -f .\docker-compose-ksql.yaml exec ksql-cli ksql http://ksql-server:8088
SET 'auto.offset.reset'='earliest';


CREATE STREAM i_pass_through_src (trainname STRING, stationname STRING) WITH (KAFKA_TOPIC = 'i-pass-through', VALUE_FORMAT = 'JSON');
CREATE STREAM i_pass_through_part AS SELECT * FROM i_pass_through_src PARTITION BY trainname;
CREATE TABLE i_pass_through_table (trainname VARCHAR, stationname VARCHAR) WITH (KAFKA_TOPIC='I_PASS_THROUGH_PART', VALUE_FORMAT='JSON', KEY='trainname');

CREATE STREAM i_am_here_src (trainid STRING, trainname STRING, otherfield STRING) WITH (KAFKA_TOPIC = 'i-am-here', VALUE_FORMAT = 'JSON');
CREATE STREAM i_am_here_stream AS SELECT * FROM i_am_here_src PARTITION BY trainname;

CREATE STREAM "i_go_to" AS
  SELECT i.trainid, i.otherfield, pass.trainname, pass.stationname FROM i_am_here_stream i
  JOIN i_pass_through_table pass ON i.trainname = pass.trainname
  PARTITION BY trainId;
