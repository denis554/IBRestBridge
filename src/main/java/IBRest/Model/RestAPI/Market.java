package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.Types;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class Market extends IBRestModel {

    private int orderId;

    public Market(String action, double quantity, String stock, String symbol, String currencyUnit, String exchange, HttpServletRequest request) {
        super(request);
        if (!isAuthHost) {
            return;
        }
        Order order = new Order();
        order.action(action.equalsIgnoreCase("BUY")?Types.Action.BUY:Types.Action.SELL);
        order.orderType("MKT");
        order.totalQuantity(quantity);

        Contract contract = new Contract();
//        contract.secType("STK");
//        contract.symbol("SAP");
//        contract.currency("EUR");
//        contract.exchange("FWB");
        contract.secType(stock);
        contract.symbol(symbol);
        contract.currency(currencyUnit);
        contract.exchange(exchange);

        orderId = IBCon.currentOrderId++;
        IBCon.reqOpenOrder(orderId, contract, order);
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        return IBCon.getOrderResult(orderId);
    }
}
