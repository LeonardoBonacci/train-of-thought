# no more need to run for trains!

## TODO
* add design drawings to this readme
* integrate services
* use KTable instead of GlobalKTable in 'station-sink' and 'predictor' services
* execute ksql statements at docker/container start-up
* execute tile38 statements at docker/container start-up
* use structs: https://docs.confluent.io/current/ksql/docs/developer-guide/syntax-reference.html#struct-overview
* use testcontainers from integration test
* use google's truth test library for assertions 
* make a ksql geo udf (no use case yet)
* make kafka tile38 connector
* make train simulator of wellington

### useful links
* https://medium.com/@coderunner/debugging-with-kafkacat-df7851d21968
* https://medium.com/test-kafka-based-applications/https-medium-com-testing-kafka-based-applications-85d8951cec43
* https://www.testcontainers.org/
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

### KSQL

check readme in ksql folder

