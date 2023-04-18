package com.task.Tests;

import com.task.BaseTest;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.JavascriptExecutor;

import static java.lang.Double.parseDouble;

public class ValidateTest extends BaseTest{

    HashMap<String,String> selectedProduct = new HashMap<>();
    int count = 0;
    @Test(groups="ValidatePrice")
    public void validatePrice() throws IOException {
        // Go to the flipkart landing page
        landingPage.goTo();

        // Close the login popup
        landingPage.closePopUpByClassName("button._2KpZ6l._2doB4z");

        // Get the search item name from properties
        String searchParameter = landingPage.getPropertiesValue("searchParameter");

        // Finding the search textbox and typing the search parameter
        WebElement searchBox = driver.findElement(By.className("_3704LK"));
        searchBox.sendKeys(searchParameter);

        // Clicking on the Search button
        WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
        searchButton.click();

        // Get the sorting method for products from properties
        String sortParameter = landingPage.getPropertiesValue("filterParameter");
        WebElement sortParameterOption = driver.findElement(By.xpath("//div[contains(text()," + sortParameter + ")]"));
        sortParameterOption.click();

        // Retrieving the page limit no. from a data properties
        int pageLimit = Integer.valueOf(landingPage.getPropertiesValue("pageLimit"));

        // Validating the prices for all the products displayed till Page (2) are in ascending order
        boolean isPriceAscending = true;
        for (int i = 1; i <= pageLimit; i++) {
            // Finding all the products and their prices on the page
            List<WebElement> products = driver.findElements(By.xpath("//div[contains(@class,'_2kHMtA')]"));
            List<WebElement> prices = driver.findElements(By.xpath("//div[contains(@class,'_1xHGtK _373qXS')]//div[contains(@class,'_30jeq3')]"));

            // Validating the prices are in ascending order
            for (int j = 0; j < prices.size() - 1; j++) {
                String price1 = prices.get(j).getText().replaceAll("[^\\d.]", "");
                String price2 = prices.get(j + 1).getText().replaceAll("[^\\d.]", "");
                if (parseDouble(price1) > parseDouble(price2)) {
                    isPriceAscending = false;
                    break;
                }
            }

            // Clicking on the next page button
            WebElement nextPageButton = driver.findElement(By.xpath("//span[contains(text(),'Next')]//parent::a"));
            nextPageButton.click();
        }

        // Validating the result
        if (isPriceAscending) {
            System.out.println("Prices are in ascending order");
        } else {
            System.out.println("Prices are not in ascending order");
        }
    }

    @Test(groups="ValidateCartItem")
    public void validateCartItem() throws IOException, InterruptedException {
        // Go to the flipkart landing page
        landingPage.goTo();

        // Close the login popup
        landingPage.closePopUpByClassName("button._2KpZ6l._2doB4z");

        // Get the search item name from properties
        String searchParameter = landingPage.getPropertiesValue("searchParameter");

        // Finding the search textbox and typing the search parameter
        WebElement searchBox = driver.findElement(By.className("_3704LK"));
        searchBox.sendKeys(searchParameter);

        // Clicking on the Search button
        WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
        searchButton.click();

        // Get the sorting method for products
        String sortParameter = landingPage.getPropertiesValue("filterParameter");
        WebElement sortParameterOption = driver.findElement(By.xpath("//div[contains(text()," + sortParameter + ")]"));
        sortParameterOption.click();

        Thread.sleep(1000);
        addToCart("2");

        ArrayList<String> wid = new ArrayList<String>(driver.getWindowHandles());
        // Switch to active main tab
        driver.switchTo().window(wid.get(0));

        Thread.sleep(1000);
        addToCart("3");

        Thread.sleep(1000);
        // Get all items from cart
        List<WebElement> cartItems = driver.findElements(By.xpath("//div[@class='_2nQDXZ']"));
        double totalPrice = 0.0;

        for (WebElement cartItem : cartItems) {
            WebElement cartItemTitle = cartItem.findElement(By.xpath(".//a[contains(@class,'_2Kn22P')]"));
            String cartItemTitleText = cartItemTitle.getText();
            WebElement cartItemPrice = cartItem.findElement(By.xpath(".//span[contains(@class,'_1WpvJ7')]"));
            String cartItemPriceText = cartItemPrice.getText().replaceAll("[^\\d.]", "");
            double cartItemPriceValue = parseDouble(cartItemPriceText);
            totalPrice += cartItemPriceValue;
            // Check if product title exist as key in hashmap and price matches
            if ( selectedProduct.containsKey(cartItemTitleText)) {
                if (cartItemPriceValue != parseDouble(selectedProduct.get(cartItemTitleText))) {
                    System.out.println("Incorrect price for "+cartItemTitleText);
                }
            }
            else {
                System.out.println("Unknown product in the cart");
            }
        }

        // Validate the total amount of cart items
        Double totalCartAmount = Double.parseDouble(driver.findElement(By.xpath("//div[@class='z4Ha90']//span")).getText().replaceAll("[^\\d.]", ""));
        Assert.assertEquals(totalCartAmount,totalPrice);
    }

    public void addToCart(String productNumber) throws InterruptedException {
        // Find the product from the list and redirect to the product detail page
        WebElement product = driver.findElement(By.xpath("(//a[contains(@class,'_2UzuFa')])["+productNumber+"]"));
        product.click();

        ArrayList<String> wid = new ArrayList<String>(driver.getWindowHandles());
        // Switch to active tab
        driver.switchTo().window(wid.get(1));

        // Scroll down to make "Add to cart" visible in screen
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 500)");

        WebElement productAddToCart = driver.findElement(By.xpath("//button[@class='_2KpZ6l _2U9uOA _3v1-ww']"));
        String productName= driver.findElement(By.className("B_NuCI")).getText();
        String productPrice = driver.findElement(By.xpath("//div[@class='_30jeq3 _16Jk6d']")).getText().replace("₹", "");

        String currentUrl = driver.getCurrentUrl();

        // Check if the page is redirected to cart page after "Add to cart" button is clicked
        while(!currentUrl.contains("viewcart"))
        {
            Thread.sleep(500);
            currentUrl = driver.getCurrentUrl();
            if(!currentUrl.contains("viewcart")) productAddToCart.click();
        }

        // Remove the text which are within the bracket in product name
        String removeText = productName.substring(productName.indexOf("(")+1, productName.indexOf(")"));
        productName = productName.replaceAll("\\("+removeText+"\\)","").trim();

        // Store product name and product price in hashmap
        selectedProduct.put(productName,productPrice);

        // Update the count by 1
        int previousCount = count;
        count = count + 1;

        // If this function is run for the first time then window should be closed
        if(previousCount == 0) driver.close();
    }
}
