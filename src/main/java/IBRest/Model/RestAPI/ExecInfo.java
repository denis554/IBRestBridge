package IBRest.Model.RestAPI;

import IBRest.Model.IBRestModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

import static IBRest.Application.IBCon;

public class ExecInfo extends IBRestModel {

    private int orderId;

    public ExecInfo(int orderId, HttpServletRequest request) {
        super(request);
        this.orderId = orderId;
    }

    public JSONObject getResult() {
        if (!isAuthHost) {
            return retForbiddenHost();
        }
        JSONObject jStat = IBCon.getExecutionInfoBy(orderId);
        if (jStat == null) {
            jStat = new JSONObject();
            JSONArray orderIds = IBCon.getExecutionIds();
            if (orderIds.size() > 0) {
                jStat.put("AvailableOrderIds", orderIds);
            }
            jStat.put("orderId", orderId);
            jStat.put("msg", "No Exist Order!");
        }
        return jStat;
    }
}
