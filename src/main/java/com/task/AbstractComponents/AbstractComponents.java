package com.task.AbstractComponents;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AbstractComponents {
    
    WebDriver driver;

    public AbstractComponents(WebDriver driver)
    {
        this.driver = driver;
    }

    public void closePopUpByClassName(String name)
    {
        driver.findElement(By.cssSelector("button._2KpZ6l._2doB4z")).click();
    }

    public String getPropertiesValue(String key) throws IOException {
        Properties pro = new Properties();
        FileInputStream fis = new FileInputStream("/home/binay/Downloads/demo/src/main/java/com/task/resources/GlobalData.properties");
        pro.load(fis);
        return pro.getProperty(key);
    }
}
