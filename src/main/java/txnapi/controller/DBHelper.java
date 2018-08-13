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

    private static final String AGGREGATION_TEMPLATE = "[" +
            "{$match: {decTimestamp: {$gte:%1$d, $lt:%2$d}, method:{\"$ne\":null}}}," +
            "{$project: {%3$s: 1} }," +
            "{$group: { _id: \"$%3$s\", count: {$sum:1}}}," +
            "{$sort: { count: -1}}," +
            "{$limit: %4$d}" +
            "]";

    /**
     * Queries top N called contract method in given time period.
     * @param date start date
     * @param endDate end date
     * @param num how many top methods to get
     * @return JSON with result
     */
    public String topMethodForDate(String date, String endDate, String num) {
        return topStatForDate(date, endDate, num, "method");
    }

    /**
     * Queries top N called contracts in given time period.
     * @param date start date
     * @param endDate end date
     * @param num how many top contracts to get
     * @return JSON with result
     */
    public String topContractForDate(String date, String endDate, String num) {
        return topStatForDate(date, endDate, num, "to");
    }

    /**
     * Queries top N called stat(either contract or method) in given time period.
     * @param date start date
     * @param endDate end date
     * @param num how many top stats to get
     * @param stat which stat to get (either "method" or "to")
     * @return JSON with result
     */
    @SuppressWarnings("unchecked")
    public String topStatForDate(String date, String endDate, String num, String stat) {
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

        String aggregation = String.format(
                AGGREGATION_TEMPLATE,
                minTimestamp,
                endTimestamp,
                stat,
                topNumber);

        ArrayList<DBObject> response = (ArrayList<DBObject>) producerTemplate.requestBody(
                "direct:dbAggregate",
                aggregation
        );

        return response.toString();
    }
}
