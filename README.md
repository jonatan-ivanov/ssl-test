# ssl-test

A simple cli to check SSL Handshake

## Build and run as a jar

```shell
./gradlew clean assemble
java -jar build/libs/ssl-test.jar --verbose httpbin.org:443
```
