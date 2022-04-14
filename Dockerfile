FROM openjdk:11
ADD build/libs/wallet-app-0.0.1-SNAPSHOT.jar wallet.jar
COPY build/resources/main/users.csv /resources/
ENTRYPOINT ["java","-jar","wallet.jar"]