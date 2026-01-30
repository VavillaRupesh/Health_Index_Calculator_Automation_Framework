package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utilities.DataProviderUtility;

public class ResultUIValidationTest extends BaseTest {

    @Test(priority = 1,groups = {"ui","smoke","regression"})
    private void resultCardUiValidation()
    {
        resultPage = indexPage.submitHealthIndexForm("Bhaskar","22","60-80 BPM","120","80");
        SoftAssert softAssert=new SoftAssert();
        softAssert.assertTrue(resultPage.isResultCardDisplayed() );
        softAssert.assertTrue(resultPage.isResultHeadingDisplayed());
        softAssert.assertEquals(resultPage.getResultHeader(),"BHASKAR"+"'S RESULT");
        softAssert.assertTrue(resultPage.isRemarksDisplayed());
        String color= resultPage.getResultCardBackgroundColor();
        softAssert.assertTrue(color.matches("^rgba\\(\\d{1,3},\\d{1,3},\\d{1,3},(0|1|0?\\.\\d+)\\)$\n"));
    }
    @Test(priority = 2,groups = {"ui","regression"})
    private void summaryCardUIValidation()
    {
        SoftAssert softAssert=new SoftAssert();
        softAssert.assertTrue(resultPage.isSummaryCardDisplayed());
        softAssert.assertTrue(resultPage.isSummaryHeadingDisplayed());
        softAssert.assertTrue(resultPage.isOverAllDisplayed());
        softAssert.assertTrue(resultPage.isAgeScoreLabelDisplayed());
        softAssert.assertTrue(resultPage.isPulseScoreLabelDisplayed());
        softAssert.assertTrue(resultPage.isBpScoreLabelDisplayed());
        softAssert.assertTrue(resultPage.isResetBtnDisplayed());
        softAssert.assertAll();
    }
}
