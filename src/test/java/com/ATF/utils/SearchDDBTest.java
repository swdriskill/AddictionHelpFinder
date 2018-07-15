package test.java.com.ATF.utils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import main.java.com.ATF.storage.AddictionCenters;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SearchDDBTest {
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

    public static void main(String[] args) throws Exception {
        try {

            DynamoDBMapper mapper = new DynamoDBMapper(client);

            // Get a book - Id=101
//            getAddictionCenterByState(mapper, "CA");

            getAddictionCenterByStateAndCity(mapper, "CA", "Los Angeles");

            System.out.println("Example complete!");

        }
        catch (Throwable t) {
            System.err.println("Error running the SearchDDBTest: " + t);
            t.printStackTrace();
        }
    }

    private static void getAddictionCenterByStateAndCity(DynamoDBMapper mapper, String state, String city) throws Exception {
        System.out.println("getAddictionCenterByState: Scan SkillData.");

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":State", new AttributeValue().withS(state));
        eav.put(":City", new AttributeValue().withS(city));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.withExpressionAttributeValues(eav);

        List<AddictionCenters> scanResult = mapper.scan(AddictionCenters.class, scanExpression);

        for (AddictionCenters addicionData : scanResult) {
            System.out.println(addicionData.toString());
        }
    }

}
