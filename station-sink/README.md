# The Station Sink

write something here...

Incoming:
* train events with estimated time-to-station information

# Commands For Testing

* clear ; docker-compose -f .\docker-compose-sink.yaml up

* clear ; docker run --tty --rm -i --network ks debezium/tooling:1.0

* kafkacat -P -b kafka:9092 -t STATIONS -K:
* 1:{ "id": "1", "name": "home", "lat": 33.00, "lon": -112.01 }

* kafkacat -P -b kafka:9092 -t HOMEWARD_BOUND -K:
* foo:{ "id": "foo", "route": 66, "name": "JVL-WELL", "togo": 10020, "_goto": "1" }
* bar:{ "id": "bar", "route": 40, "name": "WELL-JVL", "togo": 10001, "_goto": "1" }

* http sink:8080/train-stations/data/1
