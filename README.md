## Build and run

require JDK 17+ to build and run.

run by 

```shell
$ ./gradlew bootRun
```

Goto http://localhost:8080/ to see the result.

## APIs

### Websocket

#### Connection URL:

ws://localhost:8080/exchange-websocket

#### topics:

- Orderbook updates in `/topic/orderbookupdates`
- Recent trade updates in `/topic/recenttrades`
- Latest 1-minute open high low close price in `/topic/ohlc`

### REST

- GET request to `http://localhost:8080/recenttrades` for all recent trades
- GET request to `http://localhost:8080/ohlc` for the 1-minute open high low close prices 