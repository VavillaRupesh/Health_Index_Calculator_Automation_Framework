package pages.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
public class BasePage {
    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver=driver;
        PageFactory.initElements(driver,this);
    }
    public void click(WebElement element){
        element.click();
    }
    public void set(WebElement element, String text){
        element.clear();
        element.sendKeys(text);
    }
    public void select(WebElement element, String text){

        try {
            new Select(element).selectByVisibleText(text);
        }catch (Exception e){}
    }
    public String get(WebElement element){
        return element.getText();
    }
    public String getAttributeValue(WebElement element, String attribute){
        return element.getAttribute(attribute);
    }
    public String getCssAtrributeValue(WebElement element, String attribute){
        return element.getCssValue(attribute);
    }
    public boolean isElementDisplayed(WebElement element){
        return element.isDisplayed();
    }
    public String getSelected(WebElement element){
        return new Select(element).getFirstSelectedOption().getText();
    }
}
