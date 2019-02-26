package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class OrderStatus extends IBRestModel {

    private int orderId;

    public OrderStatus(int id, HttpServletRequest request) {
        super(request);
        orderId = id;
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        return IBCon.getOrderAllStatus(orderId);
    }
}
