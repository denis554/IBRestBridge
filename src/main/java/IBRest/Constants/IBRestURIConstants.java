package IBRest.Constants;

public class IBRestURIConstants {

    public static final String[] WHITE_HOST_LIST = {"52.23.231.180", "77.120.142.23"};
    public static final String HEADER_API_KEY = "x-api-key";
    public static final String HEADER_API_NONCE = "x-api-nonce";
    public static final String HEADER_SIGNATURE = "x-api-sign";
    
    public static final String API_KEY = "30DE3B4A5C3A30BCA64DB9B63BA04ECC";
    public static final String SECRET = "7B242B4B6D626F0D73228100F4FB1639";

    public static final String MARKET = "/Order/Market/Action/{a}/Quantity/{q}/Stock/{st}/Symbol/{s}/CurrencyUnit/{c}/Exchange/{e}";
    public static final String LIMIT_ORDER = "/Order/LimitOrder/Action/{a}/Price/{p}/Quantity/{q}/Stock/{st}/Symbol/{s}/CurrencyUnit/{c}/Exchange/{e}";
    public static final String ORDER_STATUS_BY_ID = "/OrderStatus/{id}";
    public static final String BALANCE = "/Balance";
    public static final String CANCEL_ALL_ORDERS = "/CancelAllOrders";
    public static final String CANCEL_ORDER = "/CancelOrder/{id}";
    public static final String REQ_MARKET_DATA = "/ReqMarketData/Stock/{st}/Symbol/{s}/CurrencyUnit/{c}/Exchange/{e}";
    //for test
    public static final String ORDER_EXEC_INFO = "/ExecInfo/{id}";
    public static final String EXECUTION_ORDER = "/ExecOrder";

}
