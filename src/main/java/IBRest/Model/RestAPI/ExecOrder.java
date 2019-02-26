package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class ExecOrder extends IBRestModel {

    public ExecOrder(HttpServletRequest request) {
        super(request);
        if (isAuthHost) {
            IBCon.reqExecution();
        }
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        IBCon.waitForExecResult();
        JSONObject jObj = new JSONObject();
        jObj.put("result", "Execution Ended!");
        return jObj;
    }
}
