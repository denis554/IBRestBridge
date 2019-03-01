package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class Positions extends IBRestModel {

    public Positions(HttpServletRequest request) {
        super(request);
        if (isAuthHost) {
            IBCon.reqPositions();
        }
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        IBCon.waitForPosition();
        return IBCon.getPositions();
    }
}
