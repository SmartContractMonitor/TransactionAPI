package txnapi.controller;

import com.mongodb.DBObject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;
import txnapi.utils.DateUtils;

import java.util.ArrayList;

/**
 * Class for working with MongoDB database.
 */
@Component
public class DBHelper {

    @Produce
    private ProducerTemplate producerTemplate;

    private static final long SECONDS_IN_DAY = 24 * 60 * 60;

    /**
     * Queries top N called contract method in given time period.
     * @param date start date
     * @param endDate end date
     * @param num how many top methods to get
     * @return JSON with result
     */
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
            if (endTimestamp == null)
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
