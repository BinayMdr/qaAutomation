package com.task.pageobjects;

import org.openqa.selenium.WebDriver;

import com.task.AbstractComponents.AbstractComponents;

public class LandingPage extends AbstractComponents
{
    WebDriver driver;
    
    public LandingPage(WebDriver driver)
    {
        super(driver);
        this.driver = driver;
    }

    public void goTo()
    {
        driver.get("https://www.flipkart.com/");
    }
}
