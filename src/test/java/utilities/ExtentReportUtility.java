package utilities;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.IOException;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.qameta.allure.Allure;

public class ExtentReportUtility implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    public static WebDriver driver;
    private static final Logger log = LogManager.getLogger(ExtentReportUtility.class);
    Reporter reporter = new Reporter(ExtentReportUtility.class);

    @Override
    public void onStart(ITestContext context) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String reportName = "Test-Report-" + timeStamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter("./reports/" + reportName);
        spark.config().setDocumentTitle("Automation Report");
        spark.config().setReportName("Functional Testing");
        spark.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));

        log.info("Extent Report initialized: " + reportName);
    }

    @Override
    public void onTestStart(ITestResult result) {
        Object[] params = result.getParameters();
        String testName = result.getMethod().getMethodName();

        if (params != null && params.length > 0) {
            String tcId = (String) params[0];     // first param = TC ID
            testName = testName + " - " + tcId;
        }

        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
        reporter.info(result.getName() + "Test Started");
    }


    @Override
    public void onTestSuccess(ITestResult result) {
        reporter.pass(result.getName() + " passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        reporter.fail("Test Failed: " + result.getName() + " - " + result.getThrowable());
        // Capture screenshot on failure
        String screenshotPath = takeScreenshot(result.getName());
        try {
            getTest().addScreenCaptureFromPath(screenshotPath);
            log.info("Screenshot attached for failed test: " + screenshotPath);
        } catch (Exception e) {
            log.error("Failed to attach screenshot: " + e.getMessage());
        }

        // AI Failure Analysis
        performAIFailureAnalysis(result);
    }

    /**
     * Performs AI-powered analysis of the test failure and adds results to reports
     */
    private void performAIFailureAnalysis(ITestResult result) {
        try {
            AIFailureAnalyzer analyzer = AIFailureAnalyzer.getInstance();

            if (!analyzer.isEnabled()) {
                log.debug("AI Failure Analysis is disabled");
                return;
            }

            String testName = result.getName();
            Throwable throwable = result.getThrowable();
            String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
            String stackTrace = getStackTraceAsString(throwable);

            // Get test method parameters/arguments
            Object[] testParameters = result.getParameters();

            log.info("Performing AI analysis for failed test: " + testName);

            // Get AI analysis with test parameters
            String analysis = analyzer.analyzeFailure(testName, errorMessage, stackTrace, testParameters);

            if (analysis != null && !analysis.isEmpty()) {
                // Add to Extent Report (HTML formatted)
                String extentFormatted = analyzer.formatForExtentReport(analysis);
                if (extentFormatted != null) {
                    getTest().log(Status.INFO, extentFormatted);
                    log.info("AI analysis added to Extent Report");
                }

                // Add to Allure Report
                String allureFormatted = analyzer.formatForAllureReport(analysis);
                if (allureFormatted != null) {
                    Allure.addAttachment("AI Failure Analysis", "text/plain", allureFormatted);
                    log.info("AI analysis added to Allure Report");
                }
            } else {
                log.warn("AI analysis returned empty result for test: " + testName);
            }
        } catch (Exception e) {
            log.error("Error during AI failure analysis: " + e.getMessage(), e);
            // Don't fail the test reporting if AI analysis fails
        }
    }

    /**
     * Converts a Throwable's stack trace to a String
     */
    private String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return "No stack trace available";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        // Include cause if present
        Throwable cause = throwable.getCause();
        if (cause != null) {
            sb.append("Caused by: ").append(cause.toString()).append("\n");
            for (StackTraceElement element : cause.getStackTrace()) {
                sb.append("\tat ").append(element.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        reporter.skip("Test Skipped: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        log.info("Extent Report flushed.");
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static String takeScreenshot(String testName) {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String screenshotDir = System.getProperty("user.dir")
                + File.separator + "reports"
                + File.separator + "screenshots";

        String fullPath = screenshotDir + File.separator + testName + "_" + timestamp + ".png";

        try {
            FileUtils.copyFile(src, new File(fullPath));
            log.info("Screenshot captured for test: " + testName);
            byte[] screenshotBytes = Files.readAllBytes(Paths.get(fullPath));
            Allure.addAttachment("Screenshot - " + testName, new ByteArrayInputStream(screenshotBytes));
        } catch (IOException e) {
            log.error("Failed to save screenshot: " + e.getMessage());
        }

        return "screenshots/" + testName + "_" + timestamp + ".png";
    }

}
 
