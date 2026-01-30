package tests;

import base.BaseTest;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utilities.DataProviderUtility;

import java.time.Duration;

public class ScoreValidationTest_2 extends BaseTest {

    @Test(dataProvider = "ScoreData", dataProviderClass = DataProviderUtility.class, priority = 1,groups = {"functional","smoke","regression"})
    public void ScoreValidation(String testcase, String name, String age, String pulse, String sysBP, String diaBP, String score) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        resultPage = indexPage.submitHealthIndexForm(name, age, pulse, sysBP, diaBP);
        reporter.info("Input form submitted");
        try {
            if (testcase.toLowerCase().contains("age score")) {
                String actualScore = resultPage.getAgeScore();
                softAssert.assertEquals(actualScore, score);
                resultPage.clickResetBtn();
                reporter.info("Reset Button Clicked");
            } else if (testcase.toLowerCase().contains("bp score")) {
                String actualScore = resultPage.getBpScore();
                softAssert.assertEquals(actualScore, score);
                resultPage.clickResetBtn();
                reporter.info("Reset Button Clicked");
            } else if (testcase.toLowerCase().contains("pulse score")) {
                String actualScore = resultPage.getPulseScore();
                softAssert.assertEquals(actualScore, score);
                resultPage.clickResetBtn();
                reporter.info("Reset Button Clicked");
            } else if (testcase.toLowerCase().contains("alert")) {
                String alertText = indexPage.acceptAlertAndGetText();
                softAssert.assertTrue(alertText.contains("Provided BP values are not valid"));

            }

            softAssert.assertAll();
        } catch(AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Test(dataProvider = "OverallScoreData", dataProviderClass = DataProviderUtility.class, priority = 2, groups = {"functional","smoke","regression"})
    public void OverallHealthScoreValidation(String testCase, String name, String age, String pulse, String sysBP, String diasBP, String expScore, String expRemark, String expColor) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        resultPage = indexPage.submitHealthIndexForm(name, age, pulse, sysBP, diasBP);
        reporter.info("Input form submitted");
        try {
            if (isAlertPresent()) {
                String alertText = indexPage.acceptAlertAndGetText();
                reporter.info("Unexpected Alert found: " + alertText);
                Assert.fail("Test Failed: Unexpected Alert [" + alertText + "] appeared for " + testCase);
            } else {
                String actualScore = resultPage.getOverAllScore();
                softAssert.assertEquals(actualScore, expScore, "Overall Score mismatch in " + testCase);

                String actualRemark = resultPage.getRemark();
                softAssert.assertEquals(actualRemark, expRemark, "Remark mismatch in " + testCase);

                String actualColor = resultPage.getResultCardBackgroundColor();
                softAssert.assertEquals(actualColor, expColor, "Color mismatch in " + testCase);

                resultPage.clickResetBtn();
                reporter.info("Reset Button Clicked");
            }

            softAssert.assertAll();
        } catch (AssertionError | Exception e) {
            throw e;
        }
    }


    public boolean isAlertPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

