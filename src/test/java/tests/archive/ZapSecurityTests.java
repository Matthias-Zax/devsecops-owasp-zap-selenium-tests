package tests.archive;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import tests.BaseTest;


public class ZapSecurityTests extends BaseTest {
    protected static final String SUT_URL = "http://localhost:3000/#/";
    protected static final String SUT_URL_LOGIN = "http://localhost:3000/#/login";
    protected static final String SUT_URL_BASKET = "http://localhost:3000/#/basket";

    @BeforeSuite
    public void setupSuite() {
        api = new ClientApi(ZAP_PROXY_ADDRESS, ZAP_PROXY_PORT, ZAP_API_KEY);
    }

    @BeforeMethod
    public void setup() {
        String proxyServerUrl = ZAP_PROXY_ADDRESS + ":" + ZAP_PROXY_PORT;

        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyServerUrl);
        proxy.setSslProxy(proxyServerUrl);

        ChromeOptions co = new ChromeOptions();
        co.setAcceptInsecureCerts(true);
        co.setProxy(proxy);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(co);

    }

    @Test
    public void T1_TITLE_verifyTitleText_isOWASPJuiceShop() {
        openPage(SUT_URL);

        Assert.assertTrue(driver.getTitle().contains("OWASP Juice Shop"));
    }

    @Test
    public void T5_LOGIN_withInvalidCredentials_rightErrorMsgDisplayed() {
        openPage(SUT_URL_LOGIN);
        performLogin("invalid@mail.com", "invalidPassword");
        String errText = driver.findElement(By.cssSelector("div.error.ng-star-inserted")).getText();

        Assert.assertEquals(errText, "Invalid email or password.");
    }

    @Test
    public void T3_LOGIN_withSQLInjection_userIsLoggedIn() {
        openPage(SUT_URL_LOGIN);
        performLogin("valid@mail.com'--", "a");
        String errText = driver.findElement(By.cssSelector("div.error.ng-star-inserted")).getText();

        Assert.assertEquals(errText, "Invalid email or password.");
    }

    @Test
    public void T1_LOGIN_withValidCredentials_userIsLoggedIn() {
        openPage(SUT_URL_LOGIN);
        performLogin("valid@mail.com", VALID_PASSWORD);

        driver.findElement(By.xpath("//mat-grid-list/div/mat-grid-tile[1]//mat-card//button")).click();
        myExplicitWait(500);
        driver.get(SUT_URL_BASKET);
        myExplicitWait(500);
        String itemInBasket = driver.findElement(By.xpath("//app-basket/mat-card/app-purchase-basket/mat-table/mat-row/mat-cell[2]")).getText();

        Assert.assertTrue(itemInBasket.contains("Apple"));
    }

    public void performLogin(String email, String password) {
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("loginButton")).click();
        myExplicitWait(2000);
    }

    public void openPage(String url) {
        driver.get(SUT_URL_LOGIN);
        driver.findElement(By.xpath("//*[@id=\"mat-dialog-0\"]//button[2]")).click();
    }

    @AfterMethod
    public void afterMethod() {
        driver.quit();
    }

    @AfterSuite
    public void tearDown() {

        if (api != null) {
            String title = "My ZAP Security Report";
            String template = "traditional-html";

            String description = "This is my amazon zap test report";
            String reportFilename = "juice-shop-zap-report.html";
            String targetFolder = "C:\\temp";
            try {
                ApiResponse response = api.reports.generate(title, template, null, description, null, null, null,
                        null, null, reportFilename, null, targetFolder, null);

                System.out.println("ZAP report generatred at this location: " + response.toString());

            } catch (ClientApiException e) {
                System.out.println("ZAP");
                e.printStackTrace();
            }
        }
    }
}
