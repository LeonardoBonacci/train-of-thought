# no more need to run for trains!

## COMMANDS
clear ; mvn clean package ; docker-compose -f .\docker-compose-predictor.yaml up --build
clear ; docker run --tty --rm -i --network ks debezium/tooling:1.0

kafkacat -L -b kafka

kafkacat -P -b kafka -t so-long -K:
JVL-WELL-9myhjbx-Crofton:{ "trainName" : "JVL-WELL", "geoHash" : "9myhjbx", "gotoId" : 7, "togo" : 112358 }
JVL-WELL-9myhjbx-Ngaio:{ "trainName" : "JVL-WELL", "geoHash" : "9myhjbx", "gotoId" : 7, "togo" : 112359 }

kafkacat -P -b kafka -t on-my-way -K:
tr>1:{ "trainId" : "tr>1", "trainName" : "JVL-WELL", "lat" : 33.05, "lon" : -115.05, "gotoId" : 7, "gotoName" : "Crofton" }
tr>1:{ "trainId" : "tr>1", "trainName" : "JVL-WELL", "lat" : 33.05, "lon" : -115.05, "gotoId" : 8, "gotoName" : "Ngaio" }

kafkacat -C -b kafka -t homeward-bound
kafkacat -C -b kafka -t homeward-bound -f "%k\n"

clear ; docker-compose -f .\docker-compose-predictor.yaml stop predictor ; mvn clean package ; docker-compose -f .\docker-compose-predictor.yaml up --build -d predictor