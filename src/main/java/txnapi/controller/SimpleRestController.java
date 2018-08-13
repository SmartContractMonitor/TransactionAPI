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
    public String getTopMethodsForDate(
            @Param(value = "date") String date,
            @Param(value = "endDate") String endDate,
            @Param(value = "num") String num) {

        return getTopStatsForDate(date, endDate, num, "method");
    }

    @RequestMapping("/contracts")
    public String getTopContractsForDate(
            @Param(value = "date") String date,
            @Param(value = "endDate") String endDate,
            @Param(value = "num") String num) {

        return getTopStatsForDate(date, endDate, num, "to");
    }

    /**
     * Gets list of top stats for a range of dates and wraps it in JSON with result type.
     * @param date start date or just day
     * @param endDate end date (optional)
     * @param num how many top stats to get
     * @param stat which stat to get (either "method" or "to")
     * @return JSON with query result
     */
    private String getTopStatsForDate(String date, String endDate, String num, String stat) {
        String stats = dbHelper.topStatForDate(date, endDate, num, stat);

        JSONObject response = new JSONObject();
        if (stats.startsWith("Invalid")) {
            response.put("error", stats);
            response.put("status", "ERROR");
        } else {
            response.put("result",
                    stat.equals("to")
                            ? convertContractsToJSON(stats)
                            : convertMethodsToJSON(stats)
            );

            response.put("status", "OK");
        }
        return response.toString();
    }

    /**
     * Converts database query response to JSON server response.
     * @param methods database methods array
     * @return response as JSONArray
     */
    private JSONArray convertMethodsToJSON(String methods) {
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

    /**
     * Converts database query response to JSON server response.
     * @param contracts database methods array
     * @return response as JSONArray
     */
    private JSONArray convertContractsToJSON(String contracts) {
        JSONArray raw = new JSONArray(contracts);
        JSONArray result = new JSONArray();
        raw.forEach(object -> {
            String id = ((JSONObject) object).getString("_id");
            int count = ((JSONObject) object).getInt("count");

            result.put(new JSONObject()
                    .put("address", id)
                    .put("count", count)
            );

        });
        return result;
    }
}
