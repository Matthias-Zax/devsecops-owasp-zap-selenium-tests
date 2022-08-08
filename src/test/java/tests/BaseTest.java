package tests;

import org.openqa.selenium.WebDriver;
import org.zaproxy.clientapi.core.ClientApi;

public class BaseTest {
    protected static final String VALID_PASSWORD = "123qwe";

    protected static final String SUT_NODEGOAT_URL = "https://nodegoat.herokuapp.com/login";
    protected static final String ZAP_PROXY_ADDRESS = "localhost";
    protected static final int ZAP_PROXY_PORT = 7777;
    protected static final String ZAP_API_KEY = "dv5kvcvf68ejjq9q9g7uje9n77";

    protected WebDriver driver;
    protected ClientApi api;

    public void myExplicitWait(int milliSeconds) {
        synchronized (driver) {
            try {
                driver.wait(milliSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
