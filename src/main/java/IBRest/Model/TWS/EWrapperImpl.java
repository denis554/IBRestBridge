package IBRest.Model.TWS;

import com.ib.client.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EWrapperImpl implements EWrapper {
    private EReaderSignal readerSignal;
    private EClientSocket clientSocket;
    public int currentOrderId = -1;
    protected Map<Integer, JSONObject> statusMap = new HashMap<>();
    protected Map<Integer, JSONObject> execInfoMap = new HashMap<>();
    protected Map<String, JSONObject> commisionMap = new HashMap<>();
    protected Map<Integer, Order> orderMap = new HashMap<>();
    protected Map<Integer, Contract> contractMap = new HashMap<>();
    protected Map<Integer, Double> mapPrice = new HashMap<>();
    protected Map<Integer, Integer> mapSize = new HashMap<>();

    protected JSONArray balance = new JSONArray();

    private boolean endedOpenOrder;
    private boolean endedExec;
    private boolean endedPosition;
    private boolean endedReqMarketData;

    private boolean isError;
    private JSONObject errorMsg = new JSONObject();

    public EWrapperImpl() {
        readerSignal = new EJavaSignal();
        clientSocket = new EClientSocket(this, readerSignal);

        clientSocket.eConnect("127.0.0.1", 7497, 2);

        final EReader reader = new EReader(clientSocket, readerSignal);
        reader.start();

        mapPrice.clear();
        mapSize.clear();

        //An additional thread is created in this program design to empty the messaging queue
        new Thread(() -> {
            while (clientSocket.isConnected()) {
                readerSignal.waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    System.out.println("Exception: "+e.getMessage());
                }
            }
        }).start();
    }

    /**
     * get socket client
     * @return
     */
    public EClientSocket getClient() {
        return clientSocket;
    }

    /**
     * get the order status by id
     * @param orderId
     * @return
     */
    public JSONObject getOrderStatusBy(int orderId) {
        return statusMap.get(orderId);
    }

    /**
     * get the excution status by the order id
     * @param orderId
     * @return
     */
    public JSONObject getExecutionInfoBy(int orderId) {
        return execInfoMap.get(orderId);
    }

    /**
     * get the excution status by the order id
     * @param execId
     * @return
     */
    public JSONObject getCommissionBy(String execId) {
        return commisionMap.get(execId);
    }

    /**
     * get the balance of the account
     * @return
     */
    public JSONObject getBalance() {
        JSONObject jObj = new JSONObject();
        jObj.put("balances", balance);
        return jObj;
    }

    /**
     * get the result for the orderId
     * @param orderId
     * @return
     */
    public JSONObject getCancelOrderResult(int orderId) {
        waitForError();
        JSONObject jObj = getErrorMsg();
        int code = (int) jObj.get("ErrCode");
        if (code != 202) {
            jObj = getErrorMsg();
            jObj.put("Order", getOrder(orderId));
        } else {
            jObj.put("Order", getOrder(orderId));
            jObj.put("Message", "Order was Canceled!");
        }
        return jObj;
    }

    /**
     * get the result for the orderId
     *
     * @return
     */
    public JSONObject getOrderResult(int orderId) {
        waitForOrderResult(orderId);
        JSONObject jObj = new JSONObject();
        if (!isError()) {
            reqExecution();
            waitForExecResult();
            jObj.put("Id", orderId);
            jObj.put("Message", "Success");
        } else {
            jObj = getErrorMsg();
        }
        return jObj;
    }

    /**
     * get the result for the Market Data
     *
     * @return
     */
    public JSONObject getMarketDataResult() {
        waitForMarketData();
        JSONObject jObj = new JSONObject();
        if (!isError()) {
            jObj.put("bidPrice", mapPrice.get(1));
            jObj.put("bidSize", mapSize.get(0));
            jObj.put("askPrice", mapPrice.get(2));
            jObj.put("askSize", mapSize.get(3));
        } else {
            jObj = getErrorMsg();
        }
        return jObj;
    }

    /**
     * get the order status for the params
     *
     * @param stock
     * @param symbol
     * @param currencyUnit
     * @param exchange
     * @return
     */
    public JSONObject getOrderAllStatus(String stock, double price, String symbol, String currencyUnit, String exchange) {
        JSONObject jObj = new JSONObject();
        ArrayList<Integer> orderIds = getOrderId(stock, price, symbol, currencyUnit, exchange);
        if (orderIds.size() > 0) {
            JSONArray jAry = new JSONArray();
            for (int orderId: orderIds) {
                JSONObject jOrder = new JSONObject();
                jOrder.put("Order", getOrder(orderId));
                jOrder.put("Status", getOrderStatus(orderId));
                JSONObject jObjExecInfo = getExecDetail(orderId);
                jOrder.put("ExectionDetail", jObjExecInfo);
                if (jObjExecInfo != null) {
                    jOrder.put("Commission", getCommissionBy((String) jObjExecInfo.get("ExecId")));
                }
                jAry.add(jOrder);
            }
            jObj.put("StatusArray", jAry);
        } else {
            jObj.put("Message", "There is no Order Status!");
        }
        return jObj;
    }

    /**
     * get the order id from the param
     * @param aStock
     * @param aSymbol
     * @param aCurrencyUnit
     * @param aExchange
     * @return
     */
    private ArrayList<Integer> getOrderId(String aStock, double aPrice, String aSymbol, String aCurrencyUnit, String aExchange) {
        Set<Integer> keyset = execInfoMap.keySet();
        ArrayList<Integer> orders = new ArrayList<>();
        if (keyset.size() == 0) {
            reqExecution();
            waitForExecResult();
        }
        if (keyset.size() > 0) {
            for (Integer key: keyset) {
                JSONObject jObj = execInfoMap.get(key);
                Types.SecType stock = (Types.SecType) jObj.get("SecType");
                System.out.println("stock = " + stock.getApiString());
                String symbol = (String) jObj.get("Symbol");
                double price = (double) jObj.get("Price");
                String currency = (String) jObj.get("Currency");
                String exchange = (String) jObj.get("Exchange");
                if (stock.getApiString().equalsIgnoreCase(aStock) &&
                    price == aPrice &&
                    symbol.equalsIgnoreCase(aSymbol) &&
                    currency.equalsIgnoreCase(aCurrencyUnit) &&
                    exchange.equalsIgnoreCase(aExchange)) {
                    orders.add((Integer) jObj.get("OrderId"));
                }
            }
        }
        return orders;
    }

    /**
     * get the order all status
     * @param orderId
     * @return
     */
    public JSONObject getOrderAllStatus(int orderId) {
//        JSONObject jObj = new JSONObject();
        JSONObject jObj = getOrder(orderId);
//        jObj.put("Order", getOrder(orderId));
//        jObj.put("Status", getOrderStatus(orderId));
//        JSONObject jObjExecInfo = getExecDetail(orderId);
//        jObj.put("ExectionDetail", jObjExecInfo);
//        if (jObjExecInfo != null) {
//            jObj.put("Commission", getCommissionBy((String) jObjExecInfo.get("ExecId")));
//        }
        return jObj;
    }

    public JSONObject getOrder(int orderId) {
        Order order = orderMap.get(orderId);
        JSONObject jObj = new JSONObject();
        jObj.put("Id", orderId);
        if (order != null) {
            jObj.put("Action", order.action());
            jObj.put("OrderType", order.orderType());
            if (order.orderType().getApiString().equalsIgnoreCase("LMT")) {
                jObj.put("LimitPrice", order.lmtPrice());
            }
            jObj.put("Quantity", order.totalQuantity());

//            jObj.put("Status", getOrderStatus(orderId));
//            JSONObject jObjExecInfo = getExecDetail(orderId);
//            jObj.put("ExectionDetail", jObjExecInfo);
//            if (jObjExecInfo != null) {
//                jObj.put("Commission", getCommissionBy((String) jObjExecInfo.get("ExecId")));
//            }
            JSONObject jStat = getOrderStatus(orderId);
            if (jStat != null) {
                jObj.put("Status", jStat.get("Status"));
                jObj.put("Filled", jStat.get("Filled"));
                jObj.put("AvgFillPrice", jStat.get("AvgFillPrice"));
                jObj.put("LastFillPrice", jStat.get("LastFillPrice"));
                jObj.put("Remaining", jStat.get("Remaining"));
            }

            Contract contract = contractMap.get(orderId);
            jObj.put("Stock", contract.secType());
            jObj.put("Symbol", contract.symbol());
            jObj.put("Currency", contract.currency());
            jObj.put("Exchange", contract.exchange());

            JSONObject jObjExecInfo = getExecDetail(orderId);
            if (jObjExecInfo != null) {
                jObj.put("Price", jObjExecInfo.get("Price"));
                jObj.put("AvgPrice", jObjExecInfo.get("AvgPrice"));
                if (jObjExecInfo != null) {
                    jObj.put("Commission", getCommissionBy((String) jObjExecInfo.get("ExecId")));
                }
            }

        } else {
            jObj.put("Message", "There is no Order");
//            JSONArray orderIds = getOpenedOrderIds();
//            if (orderIds.size() > 0) {
//                jObj.put("AvailableOpenedOrderIds", orderIds);
//            }
        }
        return jObj;
    }

    /**
     * get the order status
     * @param orderId
     * @return
     */
    public JSONObject getOrderStatus(int orderId) {
        JSONObject jStat = getOrderStatusBy(orderId);
        if (jStat == null) {
            jStat = new JSONObject();
            jStat.put("Id", orderId);
//            JSONArray orderIds = getOrderIds();
//            if (orderIds.size() > 0) {
//                jStat.put("AvailableOrderIds", orderIds);
//            }
            jStat.put("Message", "There is no Order Status!");
        }
        return jStat;
    }

    /**
     * get the order execution detail information
     * @param orderId
     * @return
     */
    public JSONObject getExecDetail(int orderId) {
        JSONObject jStat = getExecutionInfoBy(orderId);
        if (jStat == null) {
            reqExecution();
            waitForExecResult();
            jStat = getExecutionInfoBy(orderId);
            if (jStat == null) {
                jStat = new JSONObject();
                JSONArray orderIds = getExecutionIds();
                if (orderIds.size() > 0) {
                    jStat.put("AvailableOrderIds", orderIds);
                }
                jStat.put("Message", "No Execution Detail Information!");
            }
        }
        return jStat;
    }

    /**
     * check the error
     * @return
     */
    public boolean isError() {
        return isError;
    }

    /**
     * get the error message
     * @return
     */
    public JSONObject getErrorMsg() {
        return errorMsg;
    }

    /**
     * request the order
     * @param orderId
     * @param contract
     * @param order
     */
    public void reqOpenOrder(int orderId, Contract contract, Order order) {
        isError = false;
        endedOpenOrder = false;
        errorMsg.clear();
        orderMap.put(orderId, order);
        contractMap.put(orderId, contract);
        getClient().placeOrder(orderId, contract, order);
    }

    /**
     * execution the orders
     */
    public void reqExecution() {
        isError = false;
        endedExec = false;
        errorMsg.clear();
        getClient().reqExecutions(10001, new ExecutionFilter());
    }

    /**
     * request the Market Data
     * @param reqId
     * @param contract
     */
    public void reqMarketData(int reqId, Contract contract) {
        endedReqMarketData = false;
        errorMsg.clear();
        if (isEmptyMarketData()) {
            getClient().reqMktData(1001, contract, "", false, null);
        }
    }

    public boolean isEmptyMarketData() {
        return mapPrice.isEmpty() && mapSize.isEmpty();
    }

    public void reqBalance() {
        isError = false;
        endedPosition = false;
        balance.clear();
        errorMsg.clear();
        getClient().reqPositions();
    }

    public void reqCancelOrder(int orderId) {
        isError = false;
        errorMsg.clear();
        getClient().cancelOrder(orderId);
    }

    public void waitForMarketData() {
        int iCnt = 0;
        while(isEmptyMarketData()) {
            try {
                Thread.sleep(10);
                iCnt++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (iCnt >= 500) {//time out 5 seconds
                break;
            }
        }
    }

    public void waitForError() {
        int iCnt = 0;
        while(!isError) {
            try {
                Thread.sleep(10);
                iCnt++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (iCnt >= 1000) {//time out 10 seconds
                break;
            }
        }
    }

    public void waitForPosition() {
        int iCnt = 0;
        while(!endedPosition) {
            try {
                Thread.sleep(10);
                iCnt++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (endedPosition || isError) {
                break;
            }
            if (iCnt >= 1000) {//time out 10 seconds
                break;
            }
        }
    }

    public void waitForOrderResult(int orderId) {
        int iCnt = 0;
        while(!endedOpenOrder) {
            try {
                Thread.sleep(10);
                iCnt++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (endedOpenOrder || isError) {
                break;
            }
            if (iCnt >= 12000) {//time out 120 seconds
                break;
            }
        }
    }

    public void waitForExecResult() {
        int iCnt = 0;
        while(!endedExec) {
            try {
                Thread.sleep(10);
                iCnt++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (endedExec || isError) {
                break;
            }
            if (iCnt >= 5000) {//time out 30 seconds
                break;
            }
        }
    }

    public JSONArray getExecutionIds() {
        Set<Integer> keyset = execInfoMap.keySet();
        JSONArray ret = new JSONArray();
        if (keyset.size() > 0) {
            for (Integer key: keyset) {
                ret.add(key);
            }
        }
        return ret;
    }

    /**
     * get order ids
     * @return
     */
    public JSONArray getOpenedOrderIds() {
        Set<Integer> keyset = orderMap.keySet();
        JSONArray ret = new JSONArray();
        if (keyset.size() > 0) {
            for (Integer key: keyset) {
                ret.add(key);
            }
        }
        return ret;
    }

    /**
     * get order ids
     * @return
     */
    public JSONArray getOrderIds() {
        Set<Integer> keyset = statusMap.keySet();
        JSONArray ret = new JSONArray();
        if (keyset.size() > 0) {
            for (Integer key: keyset) {
                ret.add(key);
            }
        }
        return ret;
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        System.out.println("Tick Price. Ticker Id:"+tickerId+", Field: "+field+", Price: "+price+", CanAutoExecute: "+ canAutoExecute);
        mapPrice.put(field, price);
        isError = false;
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        System.out.println("Tick Size. Ticker Id:" + tickerId + ", Field: " + field + ", Size: " + size);
        mapSize.put(field, size);
        isError = false;
    }

    @Override
    public void tickOptionComputation( int tickerId, int field, double impliedVol,
                                       double delta, double optPrice, double pvDividend,
                                       double gamma, double vega, double theta, double undPrice) {

    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        System.out.println("Tick Generic. Ticker Id:" + tickerId + ", Field: " + TickType.getField(tickType) + ", Value: " + value);
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
        System.out.println("Tick string. Ticker Id:" + tickerId + ", Type: " + tickType + ", Value: " + value);
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints,
                        String formattedBasisPoints, double impliedFuture, int holdDays,
                        String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {

    }

    @Override
    public void orderStatus(int orderId, String status, double filled,
                            double remaining, double avgFillPrice, int permId, int parentId,
                            double lastFillPrice, int clientId, String whyHeld) {

        String stat = "OrderStatus. Id: "+orderId+", Status: "+status+", Filled: "+filled+", Remaining: "+remaining
                +", AvgFillPrice: "+avgFillPrice+", PermId: "+permId+", ParentId: "+parentId+", LastFillPrice: "+lastFillPrice+
                ", ClientId: "+clientId+", WhyHeld: "+whyHeld;

        JSONObject jObj = new JSONObject();
        jObj.put("OrderId", orderId);
        jObj.put("Status", status);
        jObj.put("Filled", filled);
        jObj.put("Remaining", remaining);
        jObj.put("AvgFillPrice", avgFillPrice);
        jObj.put("PermId", permId);
        jObj.put("ParentId", parentId);
        jObj.put("LastFillPrice", lastFillPrice);
        jObj.put("ClientId", clientId);
        jObj.put("WhyHeld", whyHeld);

        statusMap.put(orderId, jObj);

        System.out.println(stat);

        endedOpenOrder = true;
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        System.out.println(EWrapperMsgGenerator.openOrder(orderId, contract, order, orderState));
    }

    @Override
    public void openOrderEnd() {
        System.out.println("OpenOrderEnd");
        endedOpenOrder = true;
    }

    @Override
    public void updateAccountValue(String s, String s1, String s2, String s3) {

    }

    @Override
    public void updatePortfolio(Contract contract, double v, double v1, double v2, double v3, double v4, double v5, String s) {

    }

    @Override
    public void updateAccountTime(String s) {

    }

    @Override
    public void accountDownloadEnd(String s) {

    }

    @Override
    public void nextValidId(int orderId) {
        System.out.println("Next Valid Id: ["+orderId+"]");
        currentOrderId = orderId;
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        System.out.println(EWrapperMsgGenerator.contractDetails(reqId, contractDetails));
    }

    @Override
    public void bondContractDetails(int i, ContractDetails contractDetails) {

    }

    @Override
    public void contractDetailsEnd(int reqId) {
        System.out.println("ContractDetailsEnd. "+reqId+"\n");
    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {

        String info = "ExecDetails. "+reqId+
                " - [" +
                contract.symbol() + "], [" +
                contract.secType() + "], [" +
                contract.currency() + "], [" +
                contract.exchange() + "], [" +
                execution.execId() + "], [" +
                execution.orderId() +"], [" +
                execution.price() + "], [" +
                execution.avgPrice() + "], [" +
                execution.shares() + "]";

        JSONObject jObj = new JSONObject();
        jObj.put("RequestId", reqId);
        jObj.put("OrderId", execution.orderId());
        jObj.put("Price", execution.price());
        jObj.put("AvgPrice", execution.avgPrice());
        jObj.put("SecType", contract.secType());
        jObj.put("Symbol", contract.symbol());
        jObj.put("ExecId", execution.execId());
        jObj.put("Currency", contract.currency());
        jObj.put("Exchange", contract.exchange());

        execInfoMap.put(execution.orderId(), jObj);

        System.out.println(info);
    }

    @Override
    public void execDetailsEnd(int reqId) {
        System.out.println("ExecDetailsEnd. "+reqId+"\n");
        endedExec = true;
    }

    @Override
    public void updateMktDepth(int i, int i1, int i2, int i3, double v, int i4) {

    }

    @Override
    public void updateMktDepthL2(int i, int i1, String s, int i2, int i3, double v, int i4) {

    }

    @Override
    public void updateNewsBulletin(int i, int i1, String s, String s1) {

    }

    @Override
    public void managedAccounts(String s) {

    }

    @Override
    public void receiveFA(int i, String s) {

    }

    @Override
    public void historicalData(int i, String s, double v, double v1, double v2, double v3, int i1, int i2, double v4, boolean b) {

    }

    @Override
    public void scannerParameters(String s) {

    }

    @Override
    public void scannerData(int i, int i1, ContractDetails contractDetails, String s, String s1, String s2, String s3) {

    }

    @Override
    public void scannerDataEnd(int i) {

    }

    @Override
    public void realtimeBar(int i, long l, double v, double v1, double v2, double v3, long l1, double v4, int i1) {

    }

    @Override
    public void currentTime(long l) {

    }

    @Override
    public void fundamentalData(int i, String s) {

    }

    @Override
    public void deltaNeutralValidation(int i, DeltaNeutralContract deltaNeutralContract) {

    }

    @Override
    public void tickSnapshotEnd(int i) {

    }

    @Override
    public void marketDataType(int i, int i1) {

    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {
        System.out.println("CommissionReport. [" +
                commissionReport.m_execId + "] - [" +
                commissionReport.m_commission + "] [" +
                commissionReport.m_currency + "] RPNL [" +
                commissionReport.m_realizedPNL + "]");

        JSONObject jObj = new JSONObject();
        jObj.put("ExecId", commissionReport.m_execId);
        jObj.put("Commission", commissionReport.m_commission);
        jObj.put("Currency", commissionReport.m_currency);
        jObj.put("RPNL", commissionReport.m_realizedPNL);
        commisionMap.put(commissionReport.m_execId, jObj);
    }

    @Override
    public void position(String account, Contract contract, double pos, double avgCost) {
        String ret = "Position. "+account+" - Symbol: "+contract.symbol()+", SecType: "+contract.secType()+", Currency: "+contract.currency()+", Position: "+pos+", Avg cost: "+avgCost;

        JSONObject jObj = new JSONObject();
        jObj.put("Account", account);
        jObj.put("Symbol", contract.symbol());
        jObj.put("SecType", contract.secType());
        jObj.put("Currency", contract.currency());
        jObj.put("Position", pos);
        jObj.put("Avg cost", avgCost);
        balance.add(jObj);

        System.out.println(ret);
    }

    @Override
    public void positionEnd() {
        endedPosition = true;
        System.out.println("PositionEnd \n");
    }

    @Override
    public void accountSummary(int i, String s, String s1, String s2, String s3) {

    }

    @Override
    public void accountSummaryEnd(int i) {

    }

    @Override
    public void verifyMessageAPI(String s) {

    }

    @Override
    public void verifyCompleted(boolean b, String s) {

    }

    @Override
    public void verifyAndAuthMessageAPI(String s, String s1) {

    }

    @Override
    public void verifyAndAuthCompleted(boolean b, String s) {

    }

    @Override
    public void displayGroupList(int i, String s) {

    }

    @Override
    public void displayGroupUpdated(int i, String s) {

    }

    @Override
    public void error(Exception e) {
        isError = true;
        e.printStackTrace();
        System.out.println("exception = " + e.getMessage());
        errorMsg.put("ErrMsg", e.getMessage());
        errorMsg.put("ErrCode", -1);
    }

    @Override
    public void error(String msg) {
        isError = true;
        System.out.println("error = " + msg);
        errorMsg.put("ErrMsg", msg);
        errorMsg.put("ErrCode", -1);
    }

    @Override
    public void error(int id, int errorCode, String msg) {
        isError = true;
        System.out.println("error = " + msg + ", id = " + id + ", errorCode = " + errorCode);
        errorMsg.put("ErrMsg", msg);
        errorMsg.put("Id", id);
        errorMsg.put("ErrCode", errorCode);
    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectAck() {

    }

    @Override
    public void positionMulti(int i, String s, String s1, Contract contract, double v, double v1) {

    }

    @Override
    public void positionMultiEnd(int i) {

    }

    @Override
    public void accountUpdateMulti(int i, String s, String s1, String s2, String s3, String s4) {

    }

    @Override
    public void accountUpdateMultiEnd(int i) {

    }

    @Override
    public void securityDefinitionOptionalParameter(int i, String s, int i1, String s1, String s2, Set<String> set, Set<Double> set1) {

    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int i) {

    }

    @Override
    public void softDollarTiers(int i, SoftDollarTier[] softDollarTiers) {

    }

}
