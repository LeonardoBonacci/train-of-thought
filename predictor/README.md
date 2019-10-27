# no more need to run for trains!

## COMMANDS
clear ; mvn clean package ; docker-compose -f .\docker-compose-predictor.yaml up --build
clear ; docker run --tty --rm -i --network ks debezium/tooling:1.0

kafkacat -L -b kafka
kafkacat -C -b kafka -t so-long

kafkacat -P -b kafka -t so-long -K:
"somename-9myhjbx-Crofton":{ "trainName" : "JVL-WELL", "geoHash" : "9myhjbx", "avgTime" : 112358, "gotoId" : 7 }

"somename-9myhjbx-Crofton":{ "trainName" : "JVL-WELL" }
"1234":{ "trainName" : "some name" }

kafkacat -P -b kafka -t on-my-way -K:
"tr>1":{ "trainId" : "tr>1", "trainName" : "JVL-WELL", "lat" : 33.05, "lon" : -115.05, "gotoId" : 7, "gotoName" : "Crofton" }
"tr>1":{ "trainId" : "tr>1", "trainName" : "JVL-WELL", "lat" : 33.05, "lon" : -115.05, "gotoId" : 8, "gotoName" : "Ngaio" }
"tr>1":{ "trainId" : "tr>1", "trainName" : "JVL-WELL", "lat" : 33.05, "lon" : -115.05, "gotoId" : 9, "gotoName" : "Wellington Station" }




clear ; docker-compose -f .\docker-compose-predictor.yaml stop predictor ; mvn clean package ; docker-compose -f .\docker-compose-predictor.yaml up --build -d predictor