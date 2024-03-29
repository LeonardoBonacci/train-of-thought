## COMMANDS
clear ; docker-compose -f .\docker-compose-ksql.yaml up

### TILE38
clear ; docker run --net=host -it tile38/tile38 tile38-cli
SETHOOK trains_at_stations kafka://host.docker.internal:9092/I_HAVE_ARRIVED NEARBY trains FENCE ROAM stations * 50
SET stations 0 POINT 33.01 -115.01
SET trains fooid FIELD route 66 POINT 33.01 -115.01
SET trains barid FIELD route 66 POINT 33.01 -115.01

### KAFKACAT

clear ; docker run --tty --rm -i --network ks debezium/tooling:1.0

kafkacat -b kafka -C -o beginning -q -t I_HAVE_ARRIVED
kafkacat -b kafka -C -o beginning -q -t I_HAVE_ARRIVED -f "%k\n"

kafkacat -P -b kafka -t I_HAVE_ARRIVED -K:
trains-foo:{"command":"set","group":"5db7d69c5a4fd90001f3bb3c","detect":"roam","hook":"trains_at_stations","key":"trains","time":"2019-10-29T06:05:16.5265376Z","id":"fooid","object":{"type":"Point","coordinates":[-115.01,33.01]},"fields":{"route":66},"nearby":{"key":"stations","id":"JV","object":{"type":"Point","coordinates":[-115.01,33.01]},"meters":0}}

kafkacat -P -b kafka -t I_AM_HERE -K:
abc:{ "trainId": "abc", "route": 66, "trainName": "JVL-WELL", "lat": 33.00, "lon": -112.01 }

### KSQL

clear ; docker-compose -f .\docker-compose-ksql.yaml exec ksql-cli ksql http://ksql-server:8088
SET 'auto.offset.reset'='earliest';
SET 'ksql.sink.partitions'='3';
PRINT 'I_HAVE_ARRIVED' FROM BEGINNING;

CREATE STREAM i_have_arrived_src (	id STRING,
							 		time STRING,
							 		fields STRUCT<route INTEGER>,
			                 		nearby STRUCT<
				                    	  	key STRING,
				                      	  	id STRING,
				                      	  	object STRING,
				                      		meters INT>)
        WITH (KAFKA_TOPIC='I_HAVE_ARRIVED', VALUE_FORMAT='JSON');

CREATE STREAM i_am_home2 AS 	SELECT id, time as "moment", mystringtoint(nearby->id) as "station" 
						 	FROM i_have_arrived_src 
						 	WHERE nearby IS NOT NULL 
						 	PARTITION BY id;

CREATE STREAM passes AS SELECT fields->route AS "route", mystringtoint(nearby->id) AS "station" 
						FROM i_have_arrived_src 
					 	WHERE nearby IS NOT NULL;
