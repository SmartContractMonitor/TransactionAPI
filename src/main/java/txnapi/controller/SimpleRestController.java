package txnapi.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleRestController {

    @Autowired
    private DBHelper dbHelper;

    @RequestMapping("/methods")
    public String getTopMethodForDate(
            @Param(value = "date") String date,
            @Param(value = "endDate") String endDate,
            @Param(value = "num") String num) {

        System.out.println(date + " " + num);
        String methods = dbHelper.topMethodForDate(date, endDate, num);

        JSONObject response = new JSONObject();
        if (methods.startsWith("Invalid")) {
            response.put("error", methods);
            response.put("status", "ERROR");
        } else {
            response.put("result", convertToJSON(methods));
            response.put("status", "OK");
        }
        return response.toString();

    }

    private JSONArray convertToJSON(String methods) {
        JSONArray raw = new JSONArray(methods);
        JSONArray result = new JSONArray();
        raw.forEach(object -> {
            String id = ((JSONObject) object).getString("_id");
            String methodName = id.split("@")[0];
            String address = id.split("@")[1];
            int count = ((JSONObject) object).getInt("count");

            result.put(new JSONObject()
                    .put("method", methodName)
                    .put("address", address)
                    .put("count", count)
            );

        });
        return result;
    }
}
