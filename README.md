# RSO: Loans microservice

## Run application's database in docker

```bash
docker run -d --name loans-db -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=loans -p 5432:5432 postgres:13
```

## Build and run application
```bash
mvn clean package
cd api/target
java -jar loans-api-1.0.0-SNAPSHOT.jar
```
Available at: localhost:8080/v1/loans


## Build app's docker image and push it to repo
```bash
docker build -t app-img .
docker tag app-img efodx/uniborrow-loans
docker push efodx/uniborrow-loans 
```

## Create a docker network and run the app's database and app through it
```bash
docker build -t app-img
docker network create loans
docker run -d --name loans-db -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=loans -p 5432:5432 --network loans postgres:13
docker run -p 8080:8080 --network rso -e KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://loans-db:5432/loans app-img
```
