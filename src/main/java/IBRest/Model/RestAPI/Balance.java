package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class Balance extends IBRestModel {

    public Balance(HttpServletRequest request) {
        super(request);
        if (isAuthHost) {
            IBCon.reqBalance();
        }
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        IBCon.waitForPosition();
        return IBCon.getBalance();
    }
}
