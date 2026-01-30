package pages.pageobjects;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.base.BasePage;

public class IndexPage extends BasePage {
    @FindBy(xpath="//div[@class='header']")
    private WebElement headerTitle;
    @FindBy(tagName="h2")
    private WebElement enterDetailsHeading;
    @FindBy(className="card")
    private WebElement inputCard;
    @FindBy(xpath="//label[@for='name']")
    private WebElement nameLabel;
    @FindBy(xpath="//label[@for='age']")
    private WebElement ageLabel;
    @FindBy(xpath="//label[@for='pulse']")
    private WebElement pulseLabel;
    @FindBy(xpath="//label[@for='bp']")
    private WebElement bpLabel;
    @FindBy(id="name")
    private WebElement nameField;
    @FindBy(id="age")
    private WebElement ageField;
    @FindBy(id="pulse")
    private WebElement pulseDropDown;
    @FindBy(id="bp-systolic")
    private WebElement systolicBpField;
    @FindBy(id="bp-diastolic")
    private WebElement diastolicBpField;
    @FindBy(xpath="//button[text()='Calculate']")
    private WebElement calculateBtn;
    public IndexPage(WebDriver driver) {
        super(driver);
    }
    public void setName(String name){
        set(nameField,name);
    }
    public void setAge(String age){
        set(ageField,age);
    }
    public void setPulse(String pulse){
        select(pulseDropDown,pulse);
    }
    public void setSystolicBp(String systolicBp){
        set(systolicBpField,systolicBp);
    }
    public void setDiastolicBp(String diastolicBp){
        set(diastolicBpField,diastolicBp);
    }
    public void clickCalculateBtn(){
        click(calculateBtn);
    }
    public String getName(){
        return getAttributeValue(nameField,"value");
    }
    public String getAge(){
        return getAttributeValue(ageField,"value");
    }
    public String getSystolicBp(){
        return getAttributeValue(systolicBpField,"value");
    }
    public String getDiastolicBp(){
        return getAttributeValue(diastolicBpField,"value");
    }
    public String getPulse(){
        return getSelected(pulseDropDown);
    }
    public String getInputTitleColor(){
        return getCssAtrributeValue(enterDetailsHeading,"color");
    }
    public String getAgeErrorMessage(){
        return getAttributeValue(ageField,"validationMessage");
    }
    public String getSystolicBpErrorMessage(){
        return getAttributeValue(systolicBpField,"validationMessage");
    }
    public String getDiastolicBpErrorMessage(){
        return getAttributeValue(diastolicBpField,"validationMessage");
    }
    public String getNameErrorMessage(){
        return getAttributeValue(nameField,"validationMessage");
    }
    public String getHeadingText(){
        return get(headerTitle);
    }
    public String getHeadingBackgroundColor(){
        return getCssAtrributeValue(headerTitle,"background-color");
    }
    public String getHeadingColor(){
        return getCssAtrributeValue(headerTitle,"color");
    }
    public String getInputCardTitleText(){
        return get(enterDetailsHeading);
    }
    public String getCalculateBtnText(){
        return get(calculateBtn);
    }
    public String getCalculateBtnBackgroundColor(){
        return getCssAtrributeValue(calculateBtn,"background-color");
    }
    public String getCalculateBtnColor(){
        return getCssAtrributeValue(calculateBtn,"color");
    }
    public boolean isNameFieldDisplayed(){
        return isElementDisplayed(nameField);
    }
    public boolean isAgeFieldDisplayed(){
        return isElementDisplayed(ageField);
    }
    public boolean isPulseDropDownDisplayed(){
        return isElementDisplayed(pulseDropDown);
    }
    public boolean isSystolicBpFieldDisplayed(){
        return isElementDisplayed(systolicBpField);
    }
    public boolean isDiastolicBpFieldDisplayed(){
        return isElementDisplayed(diastolicBpField);
    }
    public boolean isNameLabelDisplayed(){
        return isElementDisplayed(nameLabel);
    }
    public boolean isAgeLabelDisplayed(){
        return isElementDisplayed(ageLabel);
    }
    public boolean isPulseLabelDisplayed(){
        return isElementDisplayed(pulseLabel);
    }
    public boolean isBpLabelDisplayed(){
        return isElementDisplayed(bpLabel);
    }
    public boolean isInputCardDisplayed(){
        return isElementDisplayed(inputCard);
    }
    public boolean isCalculateBtnDisplayed(){
        return isElementDisplayed(calculateBtn);
    }
    public ResultPage submitHealthIndexForm(String name, String age, String pulse, String systolicBp, String diastolicBp){
        setName(name);
        setAge(age);
        setPulse(pulse);
        setSystolicBp(systolicBp);
        setDiastolicBp(diastolicBp);
        clickCalculateBtn();
        return new ResultPage(driver);
    }
    public String acceptAlertAndGetText() {
        Alert alert = driver.switchTo().alert();
        String text = alert.getText();
        alert.accept();
        return text; }
}
