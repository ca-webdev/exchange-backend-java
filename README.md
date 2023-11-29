## Build and run

require JDK 17+ to build and run.

run by 

```shell
./gradlew bootRun
```

Goto http://localhost:8080/ to see the result.

## APIs

### Websocket (with STOMP)

#### Connection URL:

ws://localhost:8080/exchange-websocket

#### topics:

- Order book updates (both bid and ask order book) in `/topic/orderbookupdates`
- Bid order book updates in `/topic/bidorderbook`
- Ask order book updates in `/topic/askorderbook`
- Recent trade updates in `/topic/recenttrades`
- Latest 1-minute open high low close price in `/topic/ohlc`
- User trade updates in `/topic/usertrades`
- Order updates for the orders by the web user in `/topic/orderupdates`. Order status would include orderId, side, price, and size of the order, and also status, such as InsertAccepted, PartiallyFilled, FullyFilled, Cancelled, etc. Examples of order updates would be:

```json
{
  "orderId": "b407036c-8684-410b-b941-13bfc4665c57",
  "orderUpdateTime": 1700929159,
  "side": "buy",
  "price": 15.0,
  "size": 10,
  "filledPrice": "NaN",
  "filledSize": 0,
  "orderStatus": "InsertAccepted"
}
```

```json
{
  "orderId": "b407036c-8684-410b-b941-13bfc4665c57",
  "orderUpdateTime": 1700929159,
  "side": "buy",
  "price": 15.0,
  "size": 10,
  "filledPrice": 15.0,
  "filledSize": 3,
  "orderStatus": "PartiallyFilled"
}
```

- Position and profit and loss updates in `/topic/positionpnl` and examples as below:

```json
{
    "position": 5,
    "averageEntryPrice": 14.957999999999998,
    "marketPrice": 14.9,
    "unrealizedPnL": -0.29,
    "realizedPnL": 0.0,
    "totalPnL": -0.29,
    "initialBalance": 100.0,
    "portfolioValue": 99.71,
    "portfolioValueChange": -0.0028999999999999027
}
```

```json
{
  "position": 3,
  "averageEntryPrice": 15.186666666666667,
  "marketPrice": 15.32,
  "unrealizedPnL": 0.4,
  "realizedPnL": -0.16,
  "totalPnL": 0.24,
  "initialBalance": 100.0,
  "portfolioValue": 100.24,
  "portfolioValueChange": 0.0023999999999999577
}
```

### REST

- GET request to `http://localhost:8080/orderbook` for both the bid and ask order book at the moment
- GET request to `http://localhost:8080/bidorderbook` for both the bid order book at the moment
- GET request to `http://localhost:8080/askorderbook` for both the ask order book at the moment
- GET request to `http://localhost:8080/recenttrades` for all recent trades
- GET request to `http://localhost:8080/ohlc` for the 1-minute open high low close prices
- GET request to `http://localhost:8080/usertrades` for the trades for the web user
- GET request to `http://localhost:8080/positionpnl` for the current position and profit and loss for the web user
- POST request to `http://localhost:8080/orderinsert` with payload as example below for inserting limit order
```json
{
  "side": "buy",
  "price": 15.0,
  "size": 1
}
```

and will get a response like:

```json
{
  "orderId": "45aa2620-b038-4b1f-80a3-e40186417623",
  "message": "buy order (price 15.00 and size 1) is successfully inserted"
}
```

- POST request to `http://localhost:8080/ordercancel` with payload as example below for cancelling a limit order

```json
{
  "orderId": "45aa2620-b038-4b1f-80a3-e40186417623",
  "message": "order cancel successful. orderId=45aa2620-b038-4b1f-80a3-e40186417623"
}
```

If the order is successfully cancelled, the order status will be changed to Cancelled in the websocket topic `/topic/orderupdates`

- GET request to `http://localhost:8080/orderupdates` for the orders by the web user. Example as below:

```json
{
  "a6673720-1107-4110-8a36-a306dc74cf4e": [
    {
      "orderId": "a6673720-1107-4110-8a36-a306dc74cf4e",
      "orderUpdateTime": 1701199076,
      "side": "sell",
      "price": 11.0,
      "size": 3,
      "filledPrice": 15.21,
      "filledSize": 3,
      "orderStatus": "FullyFilled"
    },
    {
      "orderId": "a6673720-1107-4110-8a36-a306dc74cf4e",
      "orderUpdateTime": 1701199076,
      "side": "sell",
      "price": 11.0,
      "size": 3,
      "filledPrice": 15.24,
      "filledSize": 1,
      "orderStatus": "PartiallyFilled"
    },
    {
      "orderId": "a6673720-1107-4110-8a36-a306dc74cf4e",
      "orderUpdateTime": 1701199076,
      "side": "sell",
      "price": 11.0,
      "size": 3,
      "filledPrice": "NaN",
      "filledSize": 0,
      "orderStatus": "InsertAccepted"
    }
  ],
  "98fd63f6-a2bb-4dbc-85ee-7940f0c47ecd": [
    {
      "orderId": "98fd63f6-a2bb-4dbc-85ee-7940f0c47ecd",
      "orderUpdateTime": 1701199591,
      "side": "buy",
      "price": 15.0,
      "size": 3,
      "filledPrice": 15.0,
      "filledSize": 3,
      "orderStatus": "FullyFilled"
    },
    {
      "orderId": "98fd63f6-a2bb-4dbc-85ee-7940f0c47ecd",
      "orderUpdateTime": 1701199065,
      "side": "buy",
      "price": 15.0,
      "size": 3,
      "filledPrice": "NaN",
      "filledSize": 0,
      "orderStatus": "InsertAccepted"
    }
  ],
  "31fe3a98-5c43-46f7-bf3f-c28c3711e75d": [
    {
      "orderId": "31fe3a98-5c43-46f7-bf3f-c28c3711e75d",
      "orderUpdateTime": 1701199070,
      "side": "buy",
      "price": 11.0,
      "size": 3,
      "filledPrice": "NaN",
      "filledSize": 0,
      "orderStatus": "InsertAccepted"
    }
  ]
}
```