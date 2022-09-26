package QKART_TESTNG;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass extends QKART_Tests implements ITestListener{
    public void onStart(ITestContext context) {
        takeScreenshot(driver, "on start SS", "Taken SS");
    }

    public void onFinish(ITestContext context) {
        takeScreenshot(driver, "on finish SS", "Taken SS");
    }

    public void onTestFailure(ITestResult result) {
        takeScreenshot(driver, "on failure SS", "Taken SS");
    }
}
