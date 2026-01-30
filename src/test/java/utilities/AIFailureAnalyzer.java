//package utilities;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URI;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.Properties;
//
///**
// * AI-powered test failure analyzer that uses GitHub Models API (OpenAI)
// * to analyze test failures and provide actionable suggestions.
// */
//public class AIFailureAnalyzer {
//
//    private static final Logger log = LogManager.getLogger(AIFailureAnalyzer.class);
//    private static final String GITHUB_MODELS_API_URL = "https://models.inference.ai.azure.com/chat/completions";
//
//    private final String githubToken;
//    private final String aiModel;
//    private final double temperature;
//    private final boolean enabled;
//
//    private static AIFailureAnalyzer instance;
//
//    private AIFailureAnalyzer() {
//        Properties props = loadConfig();
//        this.enabled = Boolean.parseBoolean(props.getProperty("ai.failure.analysis", "false"));
//        this.githubToken = props.getProperty("github.token", "");
//        this.aiModel = props.getProperty("ai.model", "gpt-4o");
//        this.temperature = Double.parseDouble(props.getProperty("ai.temperature", "0.2"));
//
//        if (enabled && (githubToken == null || githubToken.isEmpty())) {
//            log.warn("AI Failure Analysis is enabled but GitHub token is not configured");
//        }
//    }
//
//    public static synchronized AIFailureAnalyzer getInstance() {
//        if (instance == null) {
//            instance = new AIFailureAnalyzer();
//        }
//        return instance;
//    }
//
//    private Properties loadConfig() {
//        Properties props = new Properties();
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
//            if (input != null) {
//                props.load(input);
//            }
//        } catch (Exception e) {
//            log.error("Failed to load config for AI Failure Analyzer: " + e.getMessage());
//        }
//        return props;
//    }
//
//    /**
//     * Analyzes a test failure and returns AI-generated suggestions.
//     *
//     * @param testName      The name of the failed test
//     * @param errorMessage  The error message from the failure
//     * @param stackTrace    The full stack trace
//     * @return AI analysis and suggestions, or null if analysis is disabled/failed
//     */
//    public String analyzeFailure(String testName, String errorMessage, String stackTrace) {
//        return analyzeFailure(testName, errorMessage, stackTrace, null);
//    }
//
//    /**
//     * Analyzes a test failure with test parameters and returns AI-generated suggestions.
//     *
//     * @param testName       The name of the failed test
//     * @param errorMessage   The error message from the failure
//     * @param stackTrace     The full stack trace
//     * @param testParameters The test method parameters/arguments (can be null)
//     * @return AI analysis and suggestions, or null if analysis is disabled/failed
//     */
//    public String analyzeFailure(String testName, String errorMessage, String stackTrace, Object[] testParameters) {
//        if (!enabled) {
//            log.debug("AI Failure Analysis is disabled");
//            return null;
//        }
//
//        if (githubToken == null || githubToken.isEmpty()) {
//            log.warn("Cannot perform AI analysis: GitHub token not configured");
//            return null;
//        }
//
//        try {
//            String prompt = buildAnalysisPrompt(testName, errorMessage, stackTrace, testParameters);
//            return callAIApi(prompt);
//        } catch (Exception e) {
//            log.error("AI Failure Analysis failed: " + e.getMessage(), e);
//            return null;
//        }
//    }
//
//    private String buildAnalysisPrompt(String testName, String errorMessage, String stackTrace, Object[] testParameters) {
//        StringBuilder promptBuilder = new StringBuilder();
//        promptBuilder.append("You are a test automation expert. Analyze this failed Selenium/TestNG test.\n\n");
//        promptBuilder.append("**Test Name:** ").append(testName).append("\n\n");
//
//        // Add test parameters if present
//        if (testParameters != null && testParameters.length > 0) {
//            promptBuilder.append("**Test Parameters/Arguments:**\n");
//            for (int i = 0; i < testParameters.length; i++) {
//                Object param = testParameters[i];
//                String paramValue = param != null ? param.toString() : "null";
//                String paramType = param != null ? param.getClass().getSimpleName() : "null";
//                promptBuilder.append("  - Param ").append(i + 1).append(" (").append(paramType).append("): ").append(paramValue).append("\n");
//            }
//            promptBuilder.append("\n");
//        }
//
//        promptBuilder.append("**Error Message:**\n").append(errorMessage).append("\n\n");
//        promptBuilder.append("**Stack Trace:**\n").append(truncateStackTrace(stackTrace, 1500)).append("\n\n");
//
//        promptBuilder.append("""
//            Provide a CONCISE analysis in exactly 10-12 lines covering:
//            1. ROOT CAUSE: What caused this failure (2-3 lines)
//            2. SOLUTION: How to fix it (3-4 lines)
//            3. PREVENTION: How to avoid this in future (2-3 lines)
//
//            If test parameters are provided, analyze if the input data might be causing the issue.
//            Be specific and actionable. No lengthy explanations.""");
//
//        return promptBuilder.toString();
//    }
//
//    private String truncateStackTrace(String stackTrace, int maxLength) {
//        if (stackTrace == null) return "No stack trace available";
//        if (stackTrace.length() <= maxLength) return stackTrace;
//        return stackTrace.substring(0, maxLength) + "\n... [truncated]";
//    }
//
//    private String callAIApi(String prompt) throws Exception {
//        URL url = URI.create(GITHUB_MODELS_API_URL).toURL();
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//        try {
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("Authorization", "Bearer " + githubToken);
//            conn.setDoOutput(true);
//            conn.setConnectTimeout(30000);
//            conn.setReadTimeout(60000);
//
//            // Build JSON request body
//            String requestBody = buildRequestBody(prompt);
//
//            try (OutputStream os = conn.getOutputStream()) {
//                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
//            }
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                try (InputStream is = conn.getInputStream()) {
//                    String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
//                    return parseAIResponse(response);
//                }
//            } else {
//                try (InputStream es = conn.getErrorStream()) {
//                    String error = es != null ? new String(es.readAllBytes(), StandardCharsets.UTF_8) : "Unknown error";
//                    log.error("AI API returned error code " + responseCode + ": " + error);
//                    return null;
//                }
//            }
//        } finally {
//            conn.disconnect();
//        }
//    }
//
//    private String buildRequestBody(String prompt) {
//        // Escape special characters for JSON
//        String escapedPrompt = prompt
//                .replace("\\", "\\\\")
//                .replace("\"", "\\\"")
//                .replace("\n", "\\n")
//                .replace("\r", "\\r")
//                .replace("\t", "\\t");
//
//        return String.format("""
//            {
//                "model": "%s",
//                "messages": [
//                    {
//                        "role": "system",
//                        "content": "You are a helpful test automation expert who analyzes test failures and provides clear, actionable insights."
//                    },
//                    {
//                        "role": "user",
//                        "content": "%s"
//                    }
//                ],
//                "temperature": %s,
//                "max_tokens": 1000
//            }
//            """, aiModel, escapedPrompt, temperature);
//    }
//
//    private String parseAIResponse(String jsonResponse) {
//        try {
//            // Simple JSON parsing without external library
//            // Look for "content" field in the response
//            int contentStart = jsonResponse.indexOf("\"content\":");
//            if (contentStart == -1) {
//                log.warn("Could not find content in AI response");
//                return null;
//            }
//
//            // Find the start of the content value
//            int valueStart = jsonResponse.indexOf("\"", contentStart + 10) + 1;
//            if (valueStart == 0) return null;
//
//            // Find the end of the content value (look for unescaped quote)
//            int valueEnd = valueStart;
//            while (valueEnd < jsonResponse.length()) {
//                int nextQuote = jsonResponse.indexOf("\"", valueEnd);
//                if (nextQuote == -1) break;
//
//                // Check if this quote is escaped
//                int backslashCount = 0;
//                int checkPos = nextQuote - 1;
//                while (checkPos >= valueStart && jsonResponse.charAt(checkPos) == '\\') {
//                    backslashCount++;
//                    checkPos--;
//                }
//
//                if (backslashCount % 2 == 0) {
//                    // Quote is not escaped
//                    valueEnd = nextQuote;
//                    break;
//                }
//                valueEnd = nextQuote + 1;
//            }
//
//            String content = jsonResponse.substring(valueStart, valueEnd);
//
//            // Unescape JSON string
//            return content
//                    .replace("\\n", "\n")
//                    .replace("\\r", "\r")
//                    .replace("\\t", "\t")
//                    .replace("\\\"", "\"")
//                    .replace("\\\\", "\\");
//
//        } catch (Exception e) {
//            log.error("Failed to parse AI response: " + e.getMessage());
//            return null;
//        }
//    }
//
//    /**
//     * Formats the AI analysis for HTML display in Extent Report
//     */
//    public String formatForExtentReport(String analysis) {
//        if (analysis == null || analysis.isEmpty()) {
//            return null;
//        }
//
//        // Convert markdown-style formatting to HTML
//        String html = analysis
//                .replace("**", "<b>").replace("**", "</b>")
//                .replace("\n", "<br/>")
//                .replace("1.", "<br/>1.")
//                .replace("2.", "<br/>2.")
//                .replace("3.", "<br/>3.");
//
//        return "<div style='background-color:#1a1a2e; padding:15px; border-left:4px solid #4da6ff; margin:10px 0;'>" +
//                "<b style='color:#4da6ff;'>🤖 AI Failure Analysis</b><br/><br/>" +
//                html +
//                "</div>";
//    }
//
//    /**
//     * Formats the AI analysis for Allure Report
//     */
//    public String formatForAllureReport(String analysis) {
//        if (analysis == null || analysis.isEmpty()) {
//            return null;
//        }
//        return "🤖 AI FAILURE ANALYSIS\n" +
//                "========================\n\n" +
//                analysis;
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//}
package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * AI-powered test failure analyzer that uses GitHub Models API (OpenAI)
 * to analyze test failures and provide actionable suggestions.
 *
 * Config resolution order:
 * 1) Java System properties (-Dgithub.token=...)
 * 2) Environment variables (GITHUB_TOKEN, AI_FAILURE_ANALYSIS, etc.)
 * 3) config.properties (if present on classpath)
 */
public class AIFailureAnalyzer {

    private static final Logger log = LogManager.getLogger(AIFailureAnalyzer.class);
    private static final String GITHUB_MODELS_API_URL = "https://models.inference.ai.azure.com/chat/completions";

    // Property keys (config.properties / -D)
    private static final String KEY_ENABLED = "ai.failure.analysis";
    private static final String KEY_TOKEN = "github.token";
    private static final String KEY_MODEL = "ai.model";
    private static final String KEY_TEMP = "ai.temperature";

    // Env var names (Jenkins injects secrets here)
    private static final String ENV_ENABLED = "AI_FAILURE_ANALYSIS";
    private static final String ENV_TOKEN = "GITHUB_TOKEN";
    private static final String ENV_MODEL = "AI_MODEL";
    private static final String ENV_TEMP = "AI_TEMPERATURE";

    private final String githubToken;
    private final String aiModel;
    private final double temperature;
    private final boolean enabled;

    private static AIFailureAnalyzer instance;

    private AIFailureAnalyzer() {
        Properties props = loadConfig(); // may be empty if config.properties doesn't exist

        this.enabled = Boolean.parseBoolean(getConfig(props, KEY_ENABLED, ENV_ENABLED, "false"));
        this.githubToken = getConfig(props, KEY_TOKEN, ENV_TOKEN, "");
        this.aiModel = getConfig(props, KEY_MODEL, ENV_MODEL, "gpt-4o");
        this.temperature = parseDoubleSafe(getConfig(props, KEY_TEMP, ENV_TEMP, "0.2"), 0.2);

        if (enabled && (githubToken == null || githubToken.isBlank())) {
            log.warn("AI Failure Analysis is enabled but GitHub token is not configured (config.properties / env / -D)");
        }
    }

    public static synchronized AIFailureAnalyzer getInstance() {
        if (instance == null) {
            instance = new AIFailureAnalyzer();
        }
        return instance;
    }

    /**
     * Loads config.properties if present. If not present, returns empty props.
     */
    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                log.info("Loaded AI Failure Analyzer config from config.properties");
            } else {
                log.info("config.properties not found. Will use -D system properties or environment variables (Jenkins).");
            }
        } catch (Exception e) {
            log.error("Failed to load config for AI Failure Analyzer: " + e.getMessage(), e);
        }
        return props;
    }

    /**
     * Reads config in this order:
     * 1) System property (-Dkey=value)
     * 2) Environment variable (envKey)
     * 3) config.properties (props)
     * 4) defaultValue
     */
    private String getConfig(Properties props, String key, String envKey, String defaultValue) {
        try {
            String sys = System.getProperty(key);
            if (sys != null && !sys.isBlank()) return sys.trim();

            String env = System.getenv(envKey);
            if (env != null && !env.isBlank()) return env.trim();

            String file = props.getProperty(key);
            if (file != null && !file.isBlank()) return file.trim();

        } catch (Exception e) {
            log.warn("Config read failed for key=" + key + " envKey=" + envKey + ": " + e.getMessage());
        }
        return defaultValue;
    }

    private double parseDoubleSafe(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            log.warn("Invalid temperature value '" + value + "'. Using fallback " + fallback);
            return fallback;
        }
    }

    /**
     * Analyzes a test failure and returns AI-generated suggestions.
     */
    public String analyzeFailure(String testName, String errorMessage, String stackTrace) {
        return analyzeFailure(testName, errorMessage, stackTrace, null);
    }

    /**
     * Analyzes a test failure with test parameters and returns AI-generated suggestions.
     */
    public String analyzeFailure(String testName, String errorMessage, String stackTrace, Object[] testParameters) {
        if (!enabled) {
            log.debug("AI Failure Analysis is disabled");
            return null;
        }

        if (githubToken == null || githubToken.isBlank()) {
            log.warn("Cannot perform AI analysis: GitHub token not configured");
            return null;
        }

        try {
            String prompt = buildAnalysisPrompt(testName, errorMessage, stackTrace, testParameters);
            return callAIApi(prompt);
        } catch (Exception e) {
            log.error("AI Failure Analysis failed: " + e.getMessage(), e);
            return null;
        }
    }

    private String buildAnalysisPrompt(String testName, String errorMessage, String stackTrace, Object[] testParameters) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are a test automation expert. Analyze this failed Selenium/TestNG test.\n\n");
        promptBuilder.append("**Test Name:** ").append(testName).append("\n\n");

        if (testParameters != null && testParameters.length > 0) {
            promptBuilder.append("**Test Parameters/Arguments:**\n");
            for (int i = 0; i < testParameters.length; i++) {
                Object param = testParameters[i];
                String paramValue = param != null ? param.toString() : "null";
                String paramType = param != null ? param.getClass().getSimpleName() : "null";
                promptBuilder.append("  - Param ").append(i + 1).append(" (").append(paramType).append("): ").append(paramValue).append("\n");
            }
            promptBuilder.append("\n");
        }

        promptBuilder.append("**Error Message:**\n").append(errorMessage).append("\n\n");
        promptBuilder.append("**Stack Trace:**\n").append(truncateStackTrace(stackTrace, 1500)).append("\n\n");

        promptBuilder.append("""
            Provide a CONCISE analysis in exactly 10-12 lines covering:
            1. ROOT CAUSE: What caused this failure (2-3 lines)
            2. SOLUTION: How to fix it (3-4 lines)
            3. PREVENTION: How to avoid this in future (2-3 lines)
            
            If test parameters are provided, analyze if the input data might be causing the issue.
            Be specific and actionable. No lengthy explanations.""");

        return promptBuilder.toString();
    }

    private String truncateStackTrace(String stackTrace, int maxLength) {
        if (stackTrace == null) return "No stack trace available";
        if (stackTrace.length() <= maxLength) return stackTrace;
        return stackTrace.substring(0, maxLength) + "\n... [truncated]";
    }

    private String callAIApi(String prompt) throws Exception {
        URL url = URI.create(GITHUB_MODELS_API_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + githubToken);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

            String requestBody = buildRequestBody(prompt);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = conn.getInputStream()) {
                    String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    return parseAIResponse(response);
                }
            } else {
                try (InputStream es = conn.getErrorStream()) {
                    String error = es != null ? new String(es.readAllBytes(), StandardCharsets.UTF_8) : "Unknown error";
                    log.error("AI API returned error code " + responseCode + ": " + error);
                    return null;
                }
            }
        } finally {
            conn.disconnect();
        }
    }

    private String buildRequestBody(String prompt) {
        String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        return String.format("""
            {
                "model": "%s",
                "messages": [
                    {
                        "role": "system",
                        "content": "You are a helpful test automation expert who analyzes test failures and provides clear, actionable insights."
                    },
                    {
                        "role": "user",
                        "content": "%s"
                    }
                ],
                "temperature": %s,
                "max_tokens": 1000
            }
            """, aiModel, escapedPrompt, temperature);
    }

    private String parseAIResponse(String jsonResponse) {
        try {
            int contentStart = jsonResponse.indexOf("\"content\":");
            if (contentStart == -1) {
                log.warn("Could not find content in AI response");
                return null;
            }

            int valueStart = jsonResponse.indexOf("\"", contentStart + 10) + 1;
            if (valueStart == 0) return null;

            int valueEnd = valueStart;
            while (valueEnd < jsonResponse.length()) {
                int nextQuote = jsonResponse.indexOf("\"", valueEnd);
                if (nextQuote == -1) break;

                int backslashCount = 0;
                int checkPos = nextQuote - 1;
                while (checkPos >= valueStart && jsonResponse.charAt(checkPos) == '\\') {
                    backslashCount++;
                    checkPos--;
                }

                if (backslashCount % 2 == 0) {
                    valueEnd = nextQuote;
                    break;
                }
                valueEnd = nextQuote + 1;
            }

            String content = jsonResponse.substring(valueStart, valueEnd);

            return content
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

        } catch (Exception e) {
            log.error("Failed to parse AI response: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Formats the AI analysis for HTML display in Extent Report
     */
    public String formatForExtentReport(String analysis) {
        if (analysis == null || analysis.isEmpty()) {
            return null;
        }

        // Proper bold conversion: **text** -> <b>text</b>
        String html = analysis
                .replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>")
                .replace("\n", "<br/>")
                .replace("1.", "<br/>1.")
                .replace("2.", "<br/>2.")
                .replace("3.", "<br/>3.");

        return "<div style='background-color:#1a1a2e; padding:15px; border-left:4px solid #4da6ff; margin:10px 0;'>" +
                "<b style='color:#4da6ff;'>🤖 AI Failure Analysis</b><br/><br/>" +
                html +
                "</div>";
    }

    /**
     * Formats the AI analysis for Allure Report
     */
    public String formatForAllureReport(String analysis) {
        if (analysis == null || analysis.isEmpty()) {
            return null;
        }
        return "🤖 AI FAILURE ANALYSIS\n" +
                "========================\n\n" +
                analysis;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
