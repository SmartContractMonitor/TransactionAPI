package txnapi.controller;

import com.mongodb.DBObject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;
import txnapi.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;

@Component
public class DBHelper {

    @Produce
    private ProducerTemplate producerTemplate;

    private static final long SECONDS_IN_DAY = 24 * 60 * 60;

    @SuppressWarnings("unchecked")
    public String topMethodForDate(String date, String endDate, String num) {
        int topNumber;

        try {
            topNumber = Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return "Invalid number format";
        }

        Long minTimestamp = DateUtils.timestampForDate(date);

        if (minTimestamp == null)
            return "Invalid date format";

        Long endTimestamp;
        if (endDate == null) {
            endTimestamp = minTimestamp + SECONDS_IN_DAY;
        } else {
            endTimestamp = DateUtils.timestampForDate(endDate);
            if (endDate == null)
                return "Invalid end date format";
        }

        String aggregationTemplate = "[  " +
                "{$match: {decTimestamp: {$gte:%d, $lt:%d}, method:{\"$ne\":null}}}," +
                "{$project: {method: 1} }," +
                "{$group: { _id: \"$method\", count: {$sum:1}}}," +
                "{$sort: { count: -1}}," +
                "{$limit: %d}" +
                "]";

        String aggregation = String.format(
                aggregationTemplate,
                minTimestamp,
                endTimestamp,
                topNumber);

        ArrayList<DBObject> response = (ArrayList<DBObject>) producerTemplate.requestBody(
                "direct:dbAggregate",
                aggregation
        );

        return response.toString();
    }

}
