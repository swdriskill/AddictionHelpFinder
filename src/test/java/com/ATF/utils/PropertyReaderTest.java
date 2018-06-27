package test.java.com.ATF.utils;

import main.java.com.ATF.utils.PropertyReader;

public class PropertyReaderTest {
    public static void main(String[] args) {
        PropertyReader propertyReader = PropertyReader.getPropertyReader();

        System.out.println(propertyReader.getGoodBye());
    }

}
