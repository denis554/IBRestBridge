package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class CancelAllOrders extends IBRestModel {

    public CancelAllOrders(HttpServletRequest request) {
        super(request);
        if (isAuthHost) {
            IBCon.getClient().reqGlobalCancel();
        }
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        JSONObject jObj = new JSONObject();
        jObj.put("result", "All Orders was Canceled!");
        return jObj;
    }
}
