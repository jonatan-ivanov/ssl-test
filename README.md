# ssl-test

A simple cli to check SSL Handshake

## Run as a script

`./src/main/groovy/ssl-test.groovy --verbose httpbin.org:443`

## Build and run as a jar

`./gradlew clean build`
`java -jar build/libs/ssl-test.jar --verbose httpbin.org:443`
