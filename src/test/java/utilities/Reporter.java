package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aventstack.extentreports.Status;
import io.qameta.allure.Allure;

public class Reporter {
    private final Logger log;

    public Reporter(Class clazz) {
        this.log = LogManager.getLogger(clazz);
    }

    public void info(String message) {
        log.info(message);
        ExtentReportUtility.getTest().log(Status.INFO, message);
    }

    public void pass(String message) {
        log.info("Test Passed: " + message);
        ExtentReportUtility.getTest().log(Status.PASS, message);
    }

    public void fail(String message) {
        log.error("Test Failed: " + message);
        ExtentReportUtility.getTest().log(Status.FAIL, message);
    }

    public void skip(String message) {
        log.warn("Test Skipped: " + message);
        ExtentReportUtility.getTest().log(Status.SKIP, message);
    }

    // Manual screenshot logging
    public void screenshot(String testName) {
        String path = ExtentReportUtility.takeScreenshot(testName);
        try {
            ExtentReportUtility.getTest().addScreenCaptureFromPath(path);
            log.info("Screenshot attached: " + path);
        } catch (Exception e) {
            log.error("Screenshot attach failed: " + e.getMessage());
            ExtentReportUtility.getTest().log(Status.WARNING, "Screenshot attach failed: " + e.getMessage());
        }
    }

    /**
     * Manually trigger AI analysis for a specific error
     * Useful when you want to analyze an error within a test method
     *
     * @param testName     Name of the test or context
     * @param errorMessage The error message to analyze
     * @param stackTrace   The stack trace (can be null)
     */
    public void analyzeWithAI(String testName, String errorMessage, String stackTrace) {
        try {
            AIFailureAnalyzer analyzer = AIFailureAnalyzer.getInstance();

            if (!analyzer.isEnabled()) {
                log.debug("AI Failure Analysis is disabled");
                return;
            }

            log.info("Performing manual AI analysis for: " + testName);
            String analysis = analyzer.analyzeFailure(testName, errorMessage, stackTrace);

            if (analysis != null && !analysis.isEmpty()) {
                // Add to Extent Report
                String extentFormatted = analyzer.formatForExtentReport(analysis);
                if (extentFormatted != null) {
                    ExtentReportUtility.getTest().log(Status.INFO, extentFormatted);
                }

                // Add to Allure Report
                String allureFormatted = analyzer.formatForAllureReport(analysis);
                if (allureFormatted != null) {
                    Allure.addAttachment("AI Analysis - " + testName, "text/plain", allureFormatted);
                }

                log.info("Manual AI analysis completed for: " + testName);
            }
        } catch (Exception e) {
            log.error("Manual AI analysis failed: " + e.getMessage());
        }
    }
}
