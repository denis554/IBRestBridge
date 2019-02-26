package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class CancelOrder extends IBRestModel {

    private int orderId;

    public CancelOrder(int orderId, HttpServletRequest request) {
        super(request);
        this.orderId = orderId;
        if (isAuthHost) {
            IBCon.reqCancelOrder(orderId);
        }
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        return IBCon.getCancelOrderResult(orderId);
    }
}
