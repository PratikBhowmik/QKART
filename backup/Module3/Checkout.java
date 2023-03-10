package QKART_SANITY_LOGIN.Module1;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    public Checkout(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /*
     * Return Boolean denoting the status of adding a new address
     */
    public Boolean addNewAddress(String addresString) {
        try {
            /*
             * Click on the "Add new address" button, enter the addressString in the address text
             * box and click on the "ADD" button to save the address
             */
            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement addNewAddressButton =
                    driver.findElementByXPath("//button[text()='Add new address']");
            if (addNewAddressButton.isDisplayed()) {
                addNewAddressButton.click();
                WebElement addressField = driver.findElementByXPath(
                        "//textarea[@placeholder = 'Enter your complete address']");
                addressField.sendKeys(addresString);
                WebElement addBtn = driver.findElement(By.xpath("//button[text()='Add']"));
                addBtn.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                        "//div[contains(@class,'address-item not-selected MuiBox-root css-0')]/div/following::div[1]/button")));
            }
            return true;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting the status of selecting an available address
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            /*
             * Iterate through all the address boxes to find the address box with matching text,
             * addressToSelect and click on it
             */
            WebElement radioButtonForAddress = driver.findElementByXPath("//input[@type='radio']");
            if (radioButtonForAddress.isEnabled()) {
                radioButtonForAddress.click();
                return true;
            }
            System.out.println("Unable to find the given address");
            return false;
        } catch (Exception e) {
            System.out.println(
                    "Exception Occurred while selecting the given address: " + e.getMessage());
            return false;
        }

    }

    /*
     * Return Boolean denoting the status of place order action
     */
    public Boolean placeOrder() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            // Find the "PLACE ORDER" button and click on it
            WebElement placeOrderBtn =
                    driver.findElement(By.xpath("//button[text() = 'PLACE ORDER']"));
            if (placeOrderBtn.isDisplayed()) {
                placeOrderBtn.click();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the insufficient balance message is displayed
     */
    public Boolean verifyInsufficientBalanceMessage() {
        // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 08: MILESTONE 7

        boolean status = false;
        try {
            WebElement element = driver.findElement(By.id("notistack-snackbar"));
            if (element.isDisplayed()) {
                if (element.getText().trim().equalsIgnoreCase(
                        "You do not have enough balance in your wallet for this purchase"))
                    ;
                status = true;
            }
            return status;
        } catch (Exception e) {
            System.out.println(
                    "Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    }
}
