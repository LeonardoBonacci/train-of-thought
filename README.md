# no more need to run for trains!

## TODO
finish services
use struct: https://docs.confluent.io/current/ksql/docs/developer-guide/syntax-reference.html#struct-overview
make some ksql udf
make kafka tile38 connector
make train simulator of wellington


## MANUAL STEPS (for now..)
* docker run --net=host -it tile38/tile38 tile38-cli
* SETHOOK trains_at_stations kafka://kafka:9092/i-have-arrived NEARBY trains FENCE ROAM stations * 50

* docker-compose exec ksql-cli ksql http://ksql-server:8088
* SET 'ksql.sink.partitions'='3';
* CREATE STREAM arahants (id STRING, time STRING, nearby MAP<STRING,STRING>) WITH (KAFKA_TOPIC = 'I_HAVE_ARRIVED', VALUE_FORMAT = 'JSON');
* CREATE STREAM i_am_home AS SELECT id, time as moment, nearby['id'] as station from arahants WHERE nearby IS NOT NULL PARTITION BY id;

### useful links
* https://medium.com/@coderunner/debugging-with-kafkacat-df7851d21968
* https://sookocheff.com/post/kafka/kafka-in-a-nutshell/
* https://blog.newrelic.com/engineering/apache-kafka-event-processing/ 
> If possible, the best partitioning strategy to use is random.
> null keys may provide better distribution and prevent potential hot spotting issues in cases where some keys may appear more than others.
* https://blog.newrelic.com/engineering/effective-strategies-kafka-topic-partitioning/
* https://docs.confluent.io/current/app-development/kafkacat-usage.html
* https://berlinbuzzwords.de/sites/berlinbuzzwords.de/files/media/documents/geo-analytics-with-kafka-v1.0.pdf
* https://www.confluent.io/blog/build-udf-udaf-ksql-5-0
* https://github.com/gschmutz/various-demos/tree/master/kafka-geofencing
* https://hackernoon.com/writing-your-own-sink-connector-for-your-kafka-stack-fa7a7bc201ea
* https://github.com/skynyrd/kafka-connect-elastic-sink
* https://quarkus.io/guides/kafka-streams-guide
* https://github.com/quarkusio/quarkus-quickstarts
* https://docs.confluent.io/current/ksql/docs/developer-guide/query-with-structured-data.html
* https://www.confluent.io/blog/data-wrangling-apache-kafka-ksql
* https://dev.to/skhmt/creating-a-native-executable-in-windows-with-graalvm-3g7f
* http://karols.github.io/blog/2019/05/12/native-image-on-windows-10-x64/
* https://redis.io/topics/protocol
* https://tile38.com/
* https://github.com/lettuce-io/lettuce-core/wiki/Custom-commands,-outputs-and-command-mechanics
* https://tile38.com/topics/replication/
* https://docs.confluent.io/current/ksql/docs/installation/install-ksql-with-docker.html 
* https://dev.to/skhmt/creating-a-native-executable-in-windows-with-graalvm-3g7f
* http://karols.github.io/blog/2019/05/12/native-image-on-windows-10-x64/
* https://docs.confluent.io/current/ksql/docs/developer-guide/create-a-stream.html

### MVN 
* mvn clean package -f source/pom.xml ; docker-compose up --build
* mvn clean package -f arrival-processor/pom.xml ; docker-compose up --build


### debezium
* docker run --tty --rm -i --network ks debezium/tooling:1.0
* kafkacat -b kafka:9092 -C -o beginning -q -t average-arrival-times
* kafkacat -b kafka:9092 -C -o beginning -q -t average-arrival-times -f "%k\n"
* http sink:8080/train-stations/meta-data
* http --follow 3372b22d21bf:8080/train-stations/data/0

 
### KSQL

check readme in ksql folder

