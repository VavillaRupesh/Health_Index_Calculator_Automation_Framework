package pages.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.base.BasePage;

public class ResultPage extends BasePage {
    @FindBy(id="score-card-1")
    private WebElement resultCard;
    @FindBy(id="result-text")
    private WebElement resultHeading;
    @FindBy(id="overall_score")
    private WebElement overallScore;
    @FindBy(xpath="//span[text()='Pulse Score']")
    private WebElement pulseScoreLabel;
    @FindBy(xpath="//span[text()='BP Score']")
    private WebElement bpScoreLabel;
    @FindBy(xpath="//span[text()='Age Score']")
    private WebElement ageScoreLabel;
    @FindBy(id="age_score")
    private WebElement ageScore;
    @FindBy(id="pulse_score")
    private WebElement pulseScore;
    @FindBy(id="bp_score")
    private WebElement bpScore;
    @FindBy(xpath="//div[text()='Summary']")
    private WebElement summaryHeading;
    @FindBy(id="overall_score_remarks")
    private WebElement remarks;
    @FindBy(className="btn-reset")
    private WebElement resetBtn;
    @FindBy (className="results-summary-container__options")
    private WebElement summaryCard;
    public ResultPage(WebDriver driver){
        super(driver);
    }
    public boolean isResultCardDisplayed(){
        return isElementDisplayed(resultCard);
    }
    public boolean isResultHeadingDisplayed(){
        return isElementDisplayed(resultHeading);
    }
    public boolean isOverAllDisplayed(){
        return isElementDisplayed(overallScore);
    }
    public boolean isRemarksDisplayed(){
        return isElementDisplayed(remarks);
    }
    public boolean isAgeScoreLabelDisplayed(){
        return isElementDisplayed(ageScoreLabel);
    }
    public boolean isBpScoreLabelDisplayed(){
        return isElementDisplayed(bpScoreLabel);
    }
    public boolean isPulseScoreLabelDisplayed(){
        return isElementDisplayed(pulseScoreLabel);
    }
    public boolean isResetBtnDisplayed() {
        return isElementDisplayed(resetBtn);
    }
    public void clickResetBtn(){ resetBtn.click(); }
    public String getResultHeader(){
        return get(resultHeading);
    }
    public String getAgeScore(){
        return get(ageScore);
    }
    public String getBpScore(){
        return get(bpScore);
    }
    public String getPulseScore(){
        return get(pulseScore);
    }
    public String getRemark(){
        return get(remarks);
    }
    public String getOverAllScore(){
        return get(overallScore);
    }
    public String getResetBtnText(){
        return get(resetBtn);
    }
    public String getResultCardBackgroundColor(){
        return getCssAtrributeValue(resultCard,"background-color");
    }
    public boolean isSummaryCardDisplayed(){
        return isElementDisplayed(summaryCard);
    }
    public boolean isSummaryHeadingDisplayed(){
        return isElementDisplayed(summaryHeading);
    }
    public String getSummaryHeading(){
        return get(summaryHeading);
    }
}
