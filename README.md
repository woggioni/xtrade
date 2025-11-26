# Overview

This application is built using SpringBoot 3, there are 3 hardcoded users named `user`,
`user2` and `user3`, all with password `password` that can be used to authenticate at the `/login` endpoint.

The list of financial instruments code is also harcoded in `src/main/resources/db/instruments.txt`,
a new order book can be created using `/api/order-book` endpoint with the `PUT` methog.

After that an new order can be added to the book using the `/api/order` endpoint with the `PUT` method

Then the order book can be closed using the `/api/order-book` endpoint with the `POST` method.

Finally executions can be added to an order using the `/api/execution` endpoint with the `PUT` method,
the `orderId` can be retrieved from the `/api/order` endpoint with the `GET` method.

# Build

> [!WARNING]
> This project requires JDK 25 to build

```bash
./gradlew build
```

# Run

```bash
docker compose up -d --build
```

Then navigate to the [Swagger UI](http://localhost:8080/swagger-ui/index.html) and login first 
with credentials username='user' password='password'

A [Kibana web UI](http://localhost:5601/app/observability/overview) is available to view exported
Opentelemetry data.

# Benchmark
```bash
docker compose --profile benchmark up -d rbcs-client
```