package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.pageobjects.ResultPage;
import utilities.DataProviderUtility;

import java.util.List;

public class FormUIValidationTest extends BaseTest {

    @Test(priority = 1,groups = {"ui","smoke","sanity","regression"})
    public void testHeadingAppearance() {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(indexPage.isInputCardDisplayed(), "Input card not displayed!");
        softAssert.assertEquals(indexPage.getInputCardTitleText(), "Enter Details", "Card title mismatch!");
        softAssert.assertEquals(indexPage.getHeadingText(), "Health Index Calculator", "Heading text mismatch!");
        softAssert.assertEquals(indexPage.getHeadingColor(), "rgba(255, 255, 255, 1)", "Heading color mismatch!");
        softAssert.assertEquals(indexPage.getHeadingBackgroundColor(), "rgba(106, 76, 147, 1)", "Heading background mismatch!");
        softAssert.assertAll();
    }


    @Test(priority = 2,groups = {"ui","smoke","regression"})
    public void testInputFieldsPresence() {
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(indexPage.isNameLabelDisplayed(), "Name label not displayed!");
        softAssert.assertTrue(indexPage.isNameFieldDisplayed(), "Name field not displayed!");
        softAssert.assertTrue(indexPage.isAgeLabelDisplayed(), "Age label not displayed!");
        softAssert.assertTrue(indexPage.isAgeFieldDisplayed(), "Age field not displayed!");
        softAssert.assertTrue(indexPage.isPulseLabelDisplayed(), "Pulse label not displayed!");
        softAssert.assertTrue(indexPage.isPulseDropDownDisplayed(), "Pulse dropdown not displayed!");
        softAssert.assertTrue(indexPage.isBpLabelDisplayed(), "BP label not displayed!");
        softAssert.assertTrue(indexPage.isSystolicBpFieldDisplayed(), "Systolic BP field not displayed!");
        softAssert.assertTrue(indexPage.isDiastolicBpFieldDisplayed(), "Diastolic BP field not displayed!");

        String defaultOption = indexPage.getPulse();
        softAssert.assertEquals(defaultOption, "Select Pulse Range", "Default option mismatch!");

        String[] expectedOptions = {
                "Select Pulse Range",
                "Below 40",
                "40-49 BPM",
                "50-59 BPM",
                "60-80 BPM",
                "81-90 BPM",
                "91-100 BPM",
                "Above 100"
        };

        List<WebElement> actualOptions = driver.findElements(By.xpath("//select[@id='pulse']/option"));
        for (int i = 0; i < expectedOptions.length; i++) {
            softAssert.assertEquals(actualOptions.get(i).getText(), expectedOptions[i],
                    "Pulse option mismatch at index " + i);
        }
        softAssert.assertTrue(indexPage.isCalculateBtnDisplayed(), "Calculate button not displayed!");
        softAssert.assertEquals(indexPage.getCalculateBtnText(), "Calculate", "Button text mismatch!");
        softAssert.assertEquals(indexPage.getCalculateBtnColor(), "rgba(255, 255, 255, 1)", "Button text color mismatch!");
        softAssert.assertEquals(indexPage.getCalculateBtnBackgroundColor(), "rgba(106, 76, 147, 1)", "Button background mismatch!");

        softAssert.assertAll();
    }


    @Test(priority = 3,groups = {"functional","smoke","sanity","regression"})
    public void testFormSubmissionResult() {
        ResultPage resultPage = indexPage.submitHealthIndexForm("Sairam", "21", "60-80 BPM", "120", "80");
        Assert.assertTrue(resultPage.isResultCardDisplayed(), "Result not displayed after form submission!");
    }

}
