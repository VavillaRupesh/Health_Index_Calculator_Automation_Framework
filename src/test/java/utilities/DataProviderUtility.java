package utilities;

import org.testng.annotations.DataProvider;

public class DataProviderUtility {

    // Static variables to track row numbers for each sheet
    private static int scoreDataRowCounter = 1;
    private static int overallScoreRowCounter = 1;
    private static int invalidDataRowCounter = 1;
    private static int validDataRowCounter = 1;

    // Static column numbers for Test Status (0-based in Apache POI)
    // FormFieldTestData.xlsx: 0=Name, 1=Age, 2=Pulse, 3=SysBP, 4=DiaBP, 5=Field, 6=Expected result, 7=Test status, 8=Test type
    private static int formFieldTestStatusColumn = 7;  // Test status column for FormFieldTestData

    // ScoreValidationData.xlsx: 0=TestCaseID, 1=Name, 2=Age, 3=Pulse, 4=SysBP, 5=DiaBP, 6=Field, 7=Expected Score, 8=Test Status, 9=Test Type
    private static int scoreValidationTestStatusColumn = 8;  // Test status column for ScoreValidationData

    @DataProvider(name="validTestData")
    public Object[][] supplyValidData() throws Exception{
        String filePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\FormFieldTestData.xlsx";
        String sheetName = "ValidData";
        return ExcelUtility.getData(filePath, sheetName);
    }

    @DataProvider(name="invalidTestData")
    public Object[][] supplyInvalidData() throws Exception{
        String filePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\FormFieldTestData.xlsx";
        String sheetName = "InvalidData";
        return ExcelUtility.getData(filePath, sheetName);
    }

    @DataProvider(name="ScoreData")
    public Object[][] supplyScoreValidationData() throws Exception{
        String filePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\ScoreValidationData.xlsx";
        String sheetName = "ScoreValidation";
        return ExcelUtility.getData(filePath, sheetName);
    }

    @DataProvider(name="OverallScoreData")
    public Object[][] supplyOverScoreData() throws Exception{
        String filePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\ScoreValidationData.xlsx";
        String sheetName = "OverScoreValidation";
        return ExcelUtility.getData(filePath, sheetName);
    }

    public void writeScoreData(String result) throws Exception{
        // Initialize variables to track row number and column number
        int scoreDataRowNum = scoreDataRowCounter;
        String scoreDataFilePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\ScoreValidationData.xlsx";
        String scoreDataSheetName = "ScoreValidation";

        // Pass row number and column number to called method
        ExcelUtility.writeFile(scoreDataFilePath, scoreDataSheetName, scoreDataRowNum, result);

        // Increment row number for next test
        scoreDataRowCounter++;
    }

    public void writeOverallScoreData(String result) throws Exception{
        // Initialize variables to track row number and column number
        int overallScoreRowNum = overallScoreRowCounter;
        String overallScoreFilePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\ScoreValidationData.xlsx";
        String overallScoreSheetName = "OverScoreValidation";

        // Pass row number and column number to called method
        ExcelUtility.writeFile(overallScoreFilePath, overallScoreSheetName, overallScoreRowNum, result);

        // Increment row number for next test
        overallScoreRowCounter++;
    }

    public void writeInvalidTestData(String result) throws Exception{
        // Initialize variables to track row number and column number
        int invalidDataRowNum = invalidDataRowCounter; // Column 7 = Test Status for FormFieldTestData
        String invalidDataFilePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\FormFieldTestData.xlsx";
        String invalidDataSheetName = "InvalidData";

        // Pass row number and column number to called method
        ExcelUtility.writeFile(invalidDataFilePath, invalidDataSheetName, invalidDataRowNum, result);

        // Increment row number for next test
        invalidDataRowCounter++;
    }

    public void writeValidTestData(String result) throws Exception{
        // Initialize variables to track row number and column number
        int validDataRowNum = validDataRowCounter;
        String validDataFilePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\FormFieldTestData.xlsx";
        String validDataSheetName = "ValidData";

        // Pass row number and column number to called method
        ExcelUtility.writeFile(validDataFilePath, validDataSheetName, validDataRowNum, result);

        // Increment row number for next test
        validDataRowCounter++;
    }

    // Method to reset counters (useful for test resets or multiple test suites)
    public static void resetRowCounters() {
        scoreDataRowCounter = 1;
        overallScoreRowCounter = 1;
        invalidDataRowCounter = 1;
        validDataRowCounter = 1;
    }
}
