package com.eliassen.crucible.web.tests;

import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import org.openqa.selenium.WebDriver;

public class SeleniumGridConnectionTests extends CrucibleWebdriver
{
    private WebDriver driver;
    private String baseUrl = "http://127.0.0.1";
    private StringBuffer verificationErrors = new StringBuffer();

//    @Before
//    public void setup() throws Exception
//    {
//        driver = new RemoteWebDriver(new URL("http://192.168.56.101:4444/wd/hub"), getChromeOptions());
//        System.out.println("Chrome Instantiated");
//    }
//
//    @After
//    public void TearDown()
//    {
//        driver.quit();
//    }
//
//    @Test()
//    public void test_GoToGoogle() throws MalformedURLException
//    {
//        driver.navigate().to("http://www.msn.com");
//    }
}
