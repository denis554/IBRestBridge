package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import com.ib.client.Contract;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class ReqMarketData extends IBRestModel {

    private int reqId;

    public ReqMarketData(String stock, String symbol, String currencyUnit, String exchange, HttpServletRequest request) {
        super(request);

        Contract contract = new Contract();
//        contract.secType("STK");

//        contract.symbol("SAP");
//        contract.currency("EUR");
//        contract.exchange("FWB");

//        contract.symbol("IBKR");
//        contract.currency("USD");
//        //In the API side, NASDAQ is always defined as ISLAND
//        contract.exchange("ISLAND");

        contract.secType(stock);
        contract.symbol(symbol);
        contract.currency(currencyUnit);
        contract.exchange(exchange);

        if (isAuthHost) {

            reqId = 1001;

            IBCon.reqMarketData(reqId, contract);

//            IBCon.reqMarketData(USStockWithPrimaryExch());
//            IBCon.reqMarketData(reqId, BondWithCusip());
//            IBCon.reqMarketData(reqId, Bond());
//            IBCon.reqMarketData(MutualFund());
//            IBCon.reqMarketData(reqId, Commodity());
//            IBCon.reqMarketData(reqId, EurGbpFx());
//            IBCon.reqMarketData(Index());
//            IBCon.reqMarketData(CFD());
//            IBCon.reqMarketData(EuropeanStock());
//            IBCon.reqMarketData(OptionAtIse());
//            IBCon.reqMarketData(USStock());
//            IBCon.reqMarketData(USStockAtSmart());
//            IBCon.reqMarketData(USOptionContract());
//            IBCon.reqMarketData(OptionAtBOX());
//            IBCon.reqMarketData(OptionWithTradingClass());
//            IBCon.reqMarketData(OptionWithLocalSymbol());
//            IBCon.reqMarketData(SimpleFuture());
//            IBCon.reqMarketData(FutureWithLocalSymbol());
//            IBCon.reqMarketData(FutureWithMultiplier());
//            IBCon.reqMarketData(WrongContract());
//            IBCon.reqMarketData(FuturesOnOptions());
//            IBCon.reqMarketData(ByISIN());
//            IBCon.reqMarketData(OptionForQuery());
//            IBCon.reqMarketData(OptionComboContract());
//            IBCon.reqMarketData(StockComboContract());
//            IBCon.reqMarketData(FutureComboContract());
//            IBCon.reqMarketData(InterCmdtyFuturesContract());
//            IBCon.reqMarketData(NewsFeedForQuery());
//            IBCon.reqMarketData(BTbroadtapeNewsFeed());
//            IBCon.reqMarketData(BZbroadtapeNewsFeed());
//            IBCon.reqMarketData(FLYbroadtapeNewsFeed());
//            IBCon.reqMarketData(MTbroadtapeNewsFeed());
        }
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        return IBCon.getMarketDataResult();
    }
}
