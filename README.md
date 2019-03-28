# Java Spring IBRestBridge
Integration of the InteractiveBroker Trading API and Java Spring RestFull API

IB REST API based on Java Spring.

## REST API Endpoints

All inputs and outputs use JSON format.

```
/MARKET
  GET /Order/Market/Action/{a}/Quantity/{q}/Stock/{st}/Symbol/{s}/CurrencyUnit/{c}/Exchange/{e}

/LIMIT ORDER
  GET /Order/LimitOrder/Action/{a}/Price/{p}/Quantity/{q}/Stock/{st}/Symbol/{s}/CurrencyUnit/{c}/Exchange/{e}

/ORDER STATUS BY ID
  GET /OrderStatus/{id}
  
/BALANCE
 GET /Balance/ReqId/{id}/Mode/{m}/Params/{p}
 
/CANCEL_ORDER
 GET /CancelOrder/{id}
 
/CANCEL_ALL_ORDERS
 GET /CancelAllOrders
 
/REQUEST MARKET DATA
 GET /ReqMarketData/ReqId/{id}/Stock/{st}/Symbol/{s}/CurrencyUnit/{c}/Exchange/{e}

/POSITIONS
  GET /Positions

```
