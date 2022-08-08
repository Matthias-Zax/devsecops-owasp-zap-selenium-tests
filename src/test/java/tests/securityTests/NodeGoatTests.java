package tests.securityTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import tests.BaseTest;

public class NodeGoatTests extends BaseTest {

    @BeforeMethod
    public void setup() {
        String proxyServerUrl = BaseTest.ZAP_PROXY_ADDRESS + ":" + BaseTest.ZAP_PROXY_PORT;

        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyServerUrl);
        proxy.setSslProxy(proxyServerUrl);

        ChromeOptions co = new ChromeOptions();
        co.setAcceptInsecureCerts(true);
        co.setProxy(proxy);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(co);
        api = new ClientApi(BaseTest.ZAP_PROXY_ADDRESS, BaseTest.ZAP_PROXY_PORT, BaseTest.ZAP_API_KEY);
    }

    @Test
    public void T1_LOGIN_withValidCredentials_dashboardIsDisplayed() {
        driver.get(BaseTest.SUT_NODEGOAT_URL);
        myExplicitWait(500);
        performLogin("wzhmaza", "123qwe");

        String dashboard = driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div[1]/div/ol")).getText();
        myExplicitWait(2000);
        Assert.assertTrue(dashboard.contains("Dashboard"));
    }

    public void performLogin(String email, String password) {
        driver.findElement(By.id("userName")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"loginform\"]/div[3]/div[3]/button")).click();
        myExplicitWait(2000);
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

            String description = "This is my OWASP ZAP test report";
            String reportFilename = "NodeGoat-shop-zap-report.html";
            String targetFolder = "C:\\temp";
            try {
                ApiResponse response = api.reports.generate(title, template, null, description, null, null, null,
                        null, null, reportFilename, null, targetFolder, null);

                System.out.println("ZAP report generated at this location: " + response.toString());

            } catch (ClientApiException e) {
                e.printStackTrace();
            }
        }
    }
}

