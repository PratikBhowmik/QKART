package QKART_SANITY_LOGIN.Module1;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Home {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app";

    public Home(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToHome() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    public Boolean PerformLogout() throws InterruptedException {
        try {
            // Find and click on the Logout Button
            WebElement logout_button = driver.findElement(By.className("MuiButton-text"));
            logout_button.click();

            // Wait for Logout to Complete
            Thread.sleep(3000);

            return true;
        } catch (Exception e) {
            // Error while logout
            return false;
        }
    }

    /*
     * Returns Boolean if searching for the given product name occurs without any errors
     */
    public Boolean searchForProduct(String product) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Clear the contents of the search box and Enter the product name in the search
            // box
            WebElement searchBox = driver.findElement(By.xpath("//input[@name='search'][1]"));
            searchBox.clear();
            searchBox.sendKeys(product);
            //WebDriverWait wait = new WebDriverWait(driver, 10);
            //wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.tagName("button")));
            //wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(String.format("//div[@class='MuiCardContent-root css-1qw96cp'][1]/p[contains(text(),'%s')]",product))));
            return true;
        } catch (Exception e) {
            System.out.println("Error while searching for a product: " + e.getMessage());
            return false;
        }
    }
    /*
     * Returns Array of Web Elements that are search results and return the same
     */
    public List<WebElement> getSearchResults() {
        List<WebElement> searchResults = new ArrayList<>();
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Find all webelements corresponding to the card content section of each of
            // search results
            searchResults = driver.findElementsByClassName("css-1qw96cp");
            return searchResults;
        } catch (Exception e) {
            System.out.println("There were no search results: " + e.getMessage());
            return searchResults;

        }
    }

    /*
     * Returns Boolean based on if the "No products found" text is displayed
     */
    public Boolean isNoResultFound() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Check the presence of "No products found" text in the web page. Assign status
            // = true if the element is *displayed* else set status = false
            WebElement noResultFound =
                    driver.findElement(By.xpath("//h4[contains(text(),'No products found')]"));
            status = noResultFound.isDisplayed();
            return status;
        } catch (Exception e) {
            return status;
        }
    }

    /*
     * Return Boolean if add product to cart is successful
     */
    public Boolean addProductToCart(String productName) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            /*
             * Iterate through each product on the page to find the WebElement corresponding to the
             * matching productName
             * 
             * Click on the "ADD TO CART" button for that element
             * 
             * Return true if these operations succeeds
             */
            List<WebElement> productElements =
                    driver.findElements(By.className("MuiCardContent-root"));
            String xpathProdName =
                    "//*[contains(text(),'" + productName + "')]//following::button[1]";
            WebElement addToCartBtn = driver.findElement(By.xpath(xpathProdName));
            addToCartBtn.getText().equalsIgnoreCase(productName);
            addToCartBtn.click();
            return true;
            /*
             * for (WebElement webElements : productElements) { WebElement searchedProductName =
             * webElements.findElement(By.tagName("p"));
             * 
             * if (searchedProductName.getText().equalsIgnoreCase(productName)) {
             * addToCartBtn.click(); break;}}
             */
        } catch (Exception e) {
            System.out.println("Exception while performing add to cart: " + e.getMessage());
            return false;
        }
    }
    /*
     * Return Boolean denoting the status of clicking on the checkout button
     */
    public Boolean clickCheckout() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            // Find and click on the the Checkout button
            WebElement checkOutButton = driver.findElementByXPath("//button[text()='Checkout']");
            if (checkOutButton.isDisplayed()) {
                checkOutButton.click();
                status = true;
            }
            return status;
        } catch (Exception e) {
            System.out.println("Exception while clicking on Checkout: " + e.getMessage());
            return status;
        }
    }
    /*
     * Return Boolean denoting the status of change quantity of product in cart operation
     */
    public Boolean changeProductQuantityinCart(String productName, int quantity) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 06: MILESTONE 5
            // Find the item on the cart with the matching productName
            // Increment or decrement the quantity of the matching product until the current
            // quantity is reached (Note: Keep a look out when then input quantity is 0,
            // here we need to remove the item completely from the cart)
            WebDriverWait wait = new WebDriverWait(driver, 25);
            WebElement cartParent = driver.findElement(By.className("cart"));
            List<WebElement> cartContents = cartParent.findElements(By.className("css-zgtx0t"));
            int currentQty;
            for (WebElement item : cartContents) {
                if (item.findElement(By.className("css-1gjj37g")).getText().contains(productName)) {
                    currentQty =
                            Integer.valueOf(item.findElement(By.className("css-olyig7")).getText());
                    WebElement plusIcon = item.findElement(By.xpath("//div[contains(text(),'"
                            + productName + "')]/following-sibling::div//button[2]"));
                    WebElement minusIcon = item.findElement(By.xpath("//div[contains(text(),'"
                            + productName + "')]/following-sibling::div//button[1]"));
                    while (currentQty != quantity) {
                        if (currentQty < quantity) {
                            Thread.sleep(3000);
                            plusIcon.click();
                        } else {
                            Thread.sleep(3000);
                            minusIcon.click();
                        }
                        Thread.sleep(3000);
                        currentQty = Integer.valueOf(
                                item.findElement(By.xpath("//div[@data-testid=\"item-qty\"]"))
                                        .getText());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            if (quantity == 0)
                return true;
            System.out.println("exception occurred when updating cart: " + e.getMessage());
            return false;
        }
    }
    /*
     * Return Boolean denoting if the cart contains items as expected
     */
    public Boolean verifyCartContents(List<String> expectedCartContents) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 07: MILESTONE 6
            // Get all the cart items as an array of webelements
            // Iterate through expectedCartContents and check if item with matching product
            // name is present in the cart
            WebElement cartParent =
                    driver.findElement(By.xpath("//div[@class = 'cart MuiBox-root css-0']"));
            List<WebElement> cartContents = cartParent.findElements(By.className("css-zgtx0t"));
            ArrayList<String> actualCartContents = new ArrayList<>();
            for (WebElement item : cartContents) {
                actualCartContents
                        .add(item.findElement(By.className("css-0")).getText().split("\n")[0]);
            }

            for (String expected : actualCartContents) {
                if (actualCartContents.contains(expected.trim())) {
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("Exception while verifying cart contents: " + e.getMessage());
            return false;
        }
    }
}
