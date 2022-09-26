package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import org.testng.Assert;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class QKART_Tests {
    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }
    /*
     * Testcase01: Verify a new user can successfully register
     */
    @Test(priority = 1, groups = {"Sanity"})
    @Parameters({"username", "password"})
    public void TestCase01(@Optional("testUser") String username,
            @Optional("abc@123") String password) throws InterruptedException {
        Boolean status;
        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(username, password, true);
        Assert.assertTrue(status, "Failed to register new user");
        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;
        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, password);
        Assert.assertTrue(status, "Failed to login with registered user");
        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();
    }

    @Test(priority = 2, groups = {"Sanity"})
    @Parameters({"username", "password"})
    public void TestCase02(@Optional("testUser") String username,
            @Optional("abc@123") String password) throws InterruptedException {
        Boolean status;
        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(username, password, true);
        Assert.assertTrue(status, "Failed to register");
        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;
        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, password, false);
        Assert.assertTrue(!status, "Registered with existing user");
        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
    }

    @Test(priority = 3, groups = {"Sanity"})
    @Parameters({"validProductName","invalidProductName"})
    public void TestCase03(String validProductName, String invalidProductName) throws InterruptedException {
        boolean status = false;
        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        // Search for the "yonex" product
        status = homePage.searchForProduct(validProductName);
        SoftAssert s_assert = new SoftAssert();
        s_assert.assertTrue(status, "Failed to search for product");
        List<WebElement> searchResults = homePage.getSearchResults();
        s_assert.assertTrue(searchResults.size() != 0, "Failed as search result is 0");
        // Verify the search results are available
        // if (searchResults.size() == 0) {
        // logStatus("TestCase 3",
        // "Test Case Failure. There were no results for the given search string", "FAIL");
        // return false;
        // }
        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);
            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            s_assert.assertTrue(elementText.toUpperCase().contains("YONEX"),
                    "Failed because product yonex wasn't found");

            // if (!elementText.toUpperCase().contains("YONEX")) {
            // logStatus("TestCase 3",
            // "Test Case Failure. Test Results contains un-expected values: "
            // + elementText,
            // "FAIL");
            // return false;
            // }
        }
        // logStatus("Step Success", "Successfully validated the search results ", "PASS");
        // Search for product
        status = homePage.searchForProduct(invalidProductName);
        s_assert.assertTrue(status, "Failed to search gesundheit");
        // if (status) {
        // logStatus("TestCase 3", "Test Case Failure. Invalid keyword returned results", "FAIL");
        // return false;
        // }
        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        s_assert.assertTrue(searchResults.size() == 0, "No product found message is not displayed");
    }

    // if (searchResults.size() == 0) {
    // if (homePage.isNoResultFound()) {
    // logStatus("Step Success",
    // "Successfully validated that no products found message is displayed",
    // "PASS");
    // }
    // logStatus("TestCase 3",
    // "Test Case PASS. Verified that no search results were found for the given text",
    // "PASS");
    // } else {
    // logStatus("TestCase 3",
    // "Test Case Fail. Expected: no results , actual: Results were available",
    // "FAIL");
    // return false;
    // }
    // return true;
    // }
    @Test(priority = 4, groups = {"Regression"})
    @Parameters({"productname"})
    public void TestCase04(@Optional("Roadster") String productname) throws InterruptedException {
        boolean status = false;
        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();
        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));
        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);
            // Verify if the size chart exists for the search result
            SoftAssert s_assrt = new SoftAssert();
            s_assrt.assertTrue(result.verifySizeChartExists(), "Size chart link doesn't exist");
            s_assrt.assertTrue(result.verifyExistenceofSizeDropdown(driver),
                    "Error while validating size selection dropdown");
            s_assrt.assertTrue(result.openSizechart(), "Error in opening size chart");
            s_assrt.assertTrue(result.validateSizeChartContents(expectedTableHeaders,
                    expectedTableBody, driver), "Error in size chart contents");
            s_assrt.assertTrue(result.closeSizeChart(driver), "Error in closing size chart link");

            // if (result.verifySizeChartExists()) {
            // logStatus("Step Success", "Successfully validated presence of Size Chart Link",
            // "PASS");
            // // Verify if size dropdown exists
            // status = result.verifyExistenceofSizeDropdown(driver);
            // logStatus("Step Success", "Validated presence of drop down", status ? "PASS" :
            // "FAIL");
            // // Open the size chart
            // if (result.openSizechart()) {
            // // Verify if the size chart contents matches the expected values
            // if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,
            // driver)) {
            // logStatus("Step Success", "Successfully validated contents of Size Chart Link",
            // "PASS");
            // } else {
            // logStatus("Step Failure", "Failure while validating contents of Size Chart Link",
            // "FAIL");
            // status = false;
            // }
            // // Close the size chart modal
            // status = result.closeSizeChart(driver);
            // } else {
            // logStatus("TestCase 4", "Test Case Fail. Failure to open Size Chart", "FAIL");
            // return false;
            // }

            // } else {
            // logStatus("TestCase 4", "Test Case Fail. Size Chart Link does not exist", "FAIL");
            // return false;
            // }
        }
        // logStatus("TestCase 4", "End Test Case: Validated Size Chart Details", status ? "PASS" :
        // "FAIL");
    }
    @Test(priority = 5, groups = {"Sanity"})
    @Parameters({"productone", "producttwo"})
    public void TestCase05() throws InterruptedException {
        Boolean status = false;
        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;
        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();
        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        status = homePage.searchForProduct("Tan");
        homePage.addProductToCart("Tan Leatherette Weekender Duffle");
        // Click on the checkout button
        homePage.clickCheckout();
        SoftAssert as_hert = new SoftAssert();
        as_hert.assertTrue(homePage.clickCheckout(),
                "Error in navigating to checkout page a bug is there");
        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        // Place the order
        status = checkoutPage.placeOrder();

        Assert.assertTrue(status, "Error in ordering product it's a bug");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        // Thread.sleep(3000);
        // Assert.assertTrue(status,
        // "Error in ordering product it's a bug");
        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");
        as_hert.assertTrue(status, "Order successful message isn't displayed");
        // Go to the home page
        homePage.navigateToHome();
        // Log out the user
        homePage.PerformLogout();
    }

    @Test(priority = 6, groups = {"Regression"})
    @Parameters({"productone", "producttwo"})
    public void TestCase06() throws InterruptedException {
        Boolean status;
        List<String> expectedResults = Arrays.asList("Xtend Smart Watch");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        homePage.navigateToHome();
        status = homePage.searchForProduct("Xtend");
        homePage.addProductToCart("Xtend Smart Watch");
        status = homePage.searchForProduct("Yarine");
        homePage.addProductToCart("Yarine Floor Lamp");
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 2);
        homePage.changeProductQuantityinCart("Yarine Floor Lamp", 0);
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 1);
        homePage.clickCheckout();
        Checkout checkoutPage = new Checkout(driver);
        SoftAssert assherrt = new SoftAssert();
        assherrt.assertTrue(checkoutPage.verifyCartContents(expectedResults),
                "Bug because after checkout expected cart contents are not matching!");
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.placeOrder();
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(
                    ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
        }
        status = driver.getCurrentUrl().endsWith("/thanks");
        homePage.navigateToHome();
        homePage.PerformLogout();
    }

    @Test(priority = 7, groups = {"Regression"})
    @Parameters({"productfour", "productfive"})
    public void TestCase07(String productfour, String productfive) throws InterruptedException {
        Boolean status = false;
        List<String> expectedResult = Arrays.asList(productfour, productfive);
        SoftAssert asserrrtt = new SoftAssert();
        Register registration = new Register(driver);
        Login login = new Login(driver);
        Home homePage = new Home(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        homePage.navigateToHome();
        status = homePage.searchForProduct(productfour);
        homePage.addProductToCart(productfour);
        status = homePage.searchForProduct(productfive);
        homePage.addProductToCart(productfive);
        WebElement checkoutBtn = driver.findElement(
                By.xpath("//div[@class = 'cart-footer MuiBox-root css-1bvc4cc']/button"));
        asserrrtt.assertTrue(checkoutBtn.isDisplayed(),
                "Bug in cart checkout button issue in cart");
        homePage.PerformLogout();
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        status = homePage.verifyCartContents(expectedResult);
        asserrrtt.assertTrue(status,
                "Cart content's don't match error!!!!");
        homePage.PerformLogout();
    }

    @Test(priority = 8, groups = {"Sanity"})
    @Parameters({"productfour", "address", "quantity"})
    public void TestCase08(String productfour, String address, int quantity) throws InterruptedException {
        Boolean status;
        SoftAssert as = new SoftAssert();
        String acterrorMessage = "You do not have enough balance in your wallet for this purchase";
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(productfour);
        homePage.addProductToCart(productfour);
        homePage.changeProductQuantityinCart(productfour, quantity);
        homePage.clickCheckout();
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);
        checkoutPage.placeOrder();
        as.assertEquals(acterrorMessage,
                "You do not have enough balance in your wallet for this purchase",
                "Error message doesn't match");
        Thread.sleep(3000);
        status = checkoutPage.verifyInsufficientBalanceMessage();

    }

    @Test(dependsOnMethods = {"TestCase10"}, groups = {"Regression"})
    public void TestCase09() throws InterruptedException {
        Boolean status = false;
        SoftAssert ashert = new SoftAssert();
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        String currentURL = driver.getCurrentUrl();
        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        driver.get(currentURL);
        Thread.sleep(2000);
        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);
        ashert.assertTrue(homePage.verifyCartContents(expectedResult),
                "Issue in cart contents after opening in new tab");
        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
    }

    @Test(groups = {"Regression"})
    public void TestCase10() throws InterruptedException {
        Boolean status = false;
        SoftAssert assertt = new SoftAssert();
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        String basePageURL = driver.getCurrentUrl();
        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        assertt.assertTrue(status, "Privacy policy link wasn't clickable");
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();
        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        assertt.assertTrue(status, "Terms of service link wasn't clickable");
        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
    }

    @Test(priority = 11, groups = {"Regression"})
    @Parameters({"contactUsUsername", "contactUsEmail", "queryContent"})
    public void TestCase11(String contactUsUsername, String contactUsEmail, String queryContent)
            throws InterruptedException {
        Home homePage = new Home(driver);
        SoftAssert saaartt = new SoftAssert();
        homePage.navigateToHome();
        driver.findElement(By.xpath("//*[text()='Contact us']")).click();
        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(contactUsUsername);
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(contactUsEmail);
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(queryContent);
        WebElement contactUs = driver.findElement(By.xpath(
                "/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));
        contactUs.click();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));
    }

    @Test(priority = 12, groups = {"Sanity"})
    @Parameters({"productone", "address"})
    public void TestCase12(String productname, String address) throws InterruptedException {
        Boolean status = false;
        SoftAssert soft_assert = new SoftAssert();
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(productname);
        homePage.addProductToCart(productname);
        homePage.changeProductQuantityinCart(productname, 1);
        homePage.clickCheckout();
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);
        checkoutPage.placeOrder();
        Thread.sleep(3000);
        String currentURL = driver.getCurrentUrl();
        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));
        status = Advertisements.size() == 3;
        WebElement Advertisement1 =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();
        status = !driver.getCurrentUrl().equals(currentURL);
        driver.get(currentURL);
        Thread.sleep(3000);
        WebElement Advertisement2 =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();
        status = !driver.getCurrentUrl().equals(currentURL);
    }

    @AfterSuite
    public void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public void logStatus(String type, String message, String status) {
        System.out.println(String.format("%s |  %s  |  %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    public void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType,
                    description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

