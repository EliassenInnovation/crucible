package com.eliassen.crucible.demo.unitTests;

//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.remote.DesiredCapabilities;


public class CalculatorTests
{
//    private static WindowsDriver CalculatorSession = null;
//    private static WebElement CalculatorResult = null;
//
//    @BeforeAll
//    public static void setup() {
//        try {
//            DesiredCapabilities capabilities = new DesiredCapabilities();
//            capabilities.setCapability("app", "C:/Windows/System32/calc.exe");
//            CalculatorSession = new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);
//            CalculatorSession.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
//
//            CalculatorResult = CalculatorSession.findElement(By.name("CalculatorResults"));
//            assertNotNull(CalculatorResult);
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Before
//    public void Clear()
//    {
//        CalculatorSession.findElement(By.name("Clear")).click();
//        assertEquals("0", _GetCalculatorResultText());
//    }
//
//    @AfterAll
//    public static void TearDown()
//    {
//        CalculatorResult = null;
//        if (CalculatorSession != null) {
//            CalculatorSession.quit();
//        }
//        CalculatorSession = null;
//    }
//
//    @Test
//    public void Addition()
//    {
//        CalculatorSession.findElement(By.name("One")).click();
//        CalculatorSession.findElement(By.name("Plus")).click();
//        CalculatorSession.findElement(By.name("Seven")).click();
//        CalculatorSession.findElement(By.name("Equals")).click();
//        assertEquals("8", _GetCalculatorResultText());
//    }
//
//    protected String _GetCalculatorResultText()
//    {
//        // trim extra text and whitespace off of the display value
//        return CalculatorResult.getText().replace("Display is", "").trim();
//    }

}
