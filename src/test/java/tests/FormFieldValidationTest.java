package tests;

import base.BaseTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utilities.DataProviderUtility;
import utilities.Reporter;

public class FormFieldValidationTest extends BaseTest {

    @Test( dataProvider = "validTestData", dataProviderClass = DataProviderUtility.class, priority = 1,groups = {"functional","smoke","sanity","regression"})
    public void testInputFieldsWithValidData(String field, String name, String age, String pulse, String sysBP, String diaBP) throws Exception {
        resultPage = indexPage.submitHealthIndexForm(name, age, pulse, sysBP, diaBP);
        reporter.info("Input Form Submitted with valid data");
        new DataProviderUtility().writeValidTestData(resultPage.isResultCardDisplayed()?"Pass":"Fail");
        Assert.assertTrue(resultPage.isResultCardDisplayed());
        resultPage.clickResetBtn();
        reporter.info("Reset Button Clicked");

    }

    @Test( dataProvider = "invalidTestData", dataProviderClass = DataProviderUtility.class, priority = 2,groups = {"functional","regression"} )
    public void testInputFieldsWithInvalidData(String field, String name, String age, String pulse, String sysBP, String diaBP, String expectedResult) throws Exception {;
        resultPage=indexPage.submitHealthIndexForm(name, age, pulse, sysBP, diaBP);
        reporter.info("Input Form submitted with invalid data");
        String actualResult = null;
        if(field.toLowerCase().contains("name field")){

            if(resultPage.isResultCardDisplayed()){
                failAndReset("Name Field");
            }else{
                actualResult = indexPage.getNameErrorMessage();
                validateError(actualResult, expectedResult, "Name Field");
            }
        }
        else if(field.toLowerCase().contains("age field")){
            if(resultPage.isResultCardDisplayed()){
                failAndReset("Age Field");
            }else{
                actualResult = indexPage.getAgeErrorMessage();
                validateError(actualResult, expectedResult, "Age Field");
            }

        }
        else if(field.toLowerCase().contains("pulse field")){
            if(resultPage.isResultCardDisplayed()){
                failAndReset("Pulse Field");
            }else{
                actualResult = indexPage.getPulse();
                validateError(actualResult, expectedResult, "Pulse Field");
            }
        }
        else if(field.toLowerCase().contains("systolic bp field")){
            if(resultPage.isResultCardDisplayed()){
                failAndReset("Systolic BP Field");
            }else{
                actualResult = indexPage.getSystolicBpErrorMessage();
                validateError(actualResult, expectedResult, "Systolic BP Field");
            }
        }
        else if(field.toLowerCase().contains("diastolic bp field")){
            if(resultPage.isResultCardDisplayed()){
                failAndReset("Diastolic BP Field");
            }else{
                actualResult = indexPage.getDiastolicBpErrorMessage();
                validateError(actualResult, expectedResult, "Diastolic BP Field");
            }
        }
    }
    private void failAndReset(String fieldName) throws Exception {
        resultPage.clickResetBtn();
        reporter.info("Reset Button Clicked");
        new DataProviderUtility().writeInvalidTestData("Fail");
        Assert.fail("Unexpected result card displayed for Invalid" + fieldName);
    }

    private void validateError(String actualResult, String expectedResult, String fieldName) throws Exception {
        try {
            Assert.assertEquals(actualResult, expectedResult, "Validation failed for " + fieldName);
            new DataProviderUtility().writeInvalidTestData("Pass");
            reporter.info(fieldName + " validation passed.");
        } catch (AssertionError | Exception e) {
            new DataProviderUtility().writeInvalidTestData("Fail");
            reporter.fail(fieldName + " validation failed. " + "Expected: [" + expectedResult + "] | Actual: [" + actualResult + "]");
            throw e;
        }

        }

}
