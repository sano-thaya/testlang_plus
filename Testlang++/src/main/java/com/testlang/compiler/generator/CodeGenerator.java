package com.testlang.compiler.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.testlang.compiler.model.AssertionSpec;
import com.testlang.compiler.model.Config;
import com.testlang.compiler.model.Program;
import com.testlang.compiler.model.RequestSpec;
import com.testlang.compiler.model.TestCase;
import com.testlang.compiler.model.Variable;

public final class CodeGenerator {
    private static final Pattern VAR_PATTERN = Pattern.compile("\\$([A-Za-z_][A-Za-z0-9_]*)");

    public void generate(Program program, Path outputFile) throws IOException {
        String source = generateSource(program);
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, source, StandardCharsets.UTF_8);
    }

    public String generateSource(Program program) {
        Map<String, String> variables = toVariableMap(program.getVariables());
        Config config = program.getConfig();
        String baseUrl = config.getBaseUrl() == null ? "" : substitute(config.getBaseUrl(), variables);

        StringBuilder out = new StringBuilder();
        out.append("import static org.junit.jupiter.api.Assertions.assertEquals;\n");
        out.append("import static org.junit.jupiter.api.Assertions.assertTrue;\n\n");
        out.append("import java.io.IOException;\n");
        out.append("import java.net.URI;\n");
        out.append("import java.net.http.HttpClient;\n");
        out.append("import java.net.http.HttpRequest;\n");
        out.append("import java.net.http.HttpResponse;\n");
        out.append("import java.time.Duration;\n");
        out.append("import java.util.LinkedHashMap;\n");
        out.append("import java.util.Map;\n\n");
        out.append("import org.junit.jupiter.api.BeforeAll;\n");
        out.append("import org.junit.jupiter.api.Test;\n\n");
        out.append("public class GeneratedTests {\n");
        out.append("    private static HttpClient client;\n");
        out.append("    private static final Map<String, String> DEFAULT_HEADERS = new LinkedHashMap<>();\n");
        out.append("    private static final String BASE_URL = \"")
            .append(escapeJava(baseUrl))
            .append("\";\n\n");

        out.append("    @BeforeAll\n");
        out.append("    static void setupClient() {\n");
        out.append("        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();\n");

        for (Map.Entry<String, String> entry : config.getDefaultHeaders().entrySet()) {
            String value = substitute(entry.getValue(), variables);
            out.append("        DEFAULT_HEADERS.put(\"")
                .append(escapeJava(entry.getKey()))
                .append("\", \"")
                .append(escapeJava(value))
                .append("\");\n");
        }
        out.append("    }\n\n");

        Map<String, Integer> methodNameCounts = new LinkedHashMap<>();
        for (TestCase testCase : program.getTests()) {
            emitTestMethod(out, testCase, variables, methodNameCounts);
        }

        out.append("    private static String resolveUrl(String path) {\n");
        out.append("        if (path.startsWith(\"/\")) {\n");
        out.append("            return BASE_URL + path;\n");
        out.append("        }\n");
        out.append("        return path;\n");
        out.append("    }\n");
        out.append("}\n");

        return out.toString();
    }

    private void emitTestMethod(StringBuilder out, TestCase testCase, Map<String, String> vars, Map<String, Integer> methodNameCounts) {
        String baseMethod = sanitizeMethodName(testCase.getName());
        int count = methodNameCounts.getOrDefault(baseMethod, 0) + 1;
        methodNameCounts.put(baseMethod, count);
        String methodName = count == 1 ? baseMethod : baseMethod + "_" + count;

        RequestSpec request = testCase.getRequest();
        String url = substitute(request.getPath(), vars);
        String body = request.getBody() == null ? null : substitute(request.getBody(), vars);

        out.append("    @Test\n");
        out.append("    void ").append(methodName).append("() throws IOException, InterruptedException {\n");
        out.append("        String resolvedUrl = resolveUrl(\"").append(escapeJava(url)).append("\");\n");
        out.append("        Map<String, String> headers = new LinkedHashMap<>(DEFAULT_HEADERS);\n");
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            String headerValue = substitute(entry.getValue(), vars);
            out.append("        headers.put(\"")
                .append(escapeJava(entry.getKey()))
                .append("\", \"")
                .append(escapeJava(headerValue))
                .append("\");\n");
        }
        out.append("\n");
        out.append("        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(resolvedUrl));\n");
        out.append("        for (Map.Entry<String, String> h : headers.entrySet()) {\n");
        out.append("            requestBuilder.header(h.getKey(), h.getValue());\n");
        out.append("        }\n");

        String method = request.getMethod();
        if ("GET".equals(method)) {
            out.append("        requestBuilder.GET();\n");
        } else if ("DELETE".equals(method)) {
            out.append("        requestBuilder.DELETE();\n");
        } else {
            String resolvedBody = body == null ? "" : body;
            out.append("        requestBuilder.method(\"")
                .append(method)
                .append("\", HttpRequest.BodyPublishers.ofString(\"")
                .append(escapeJava(resolvedBody))
                .append("\"));\n");
        }

        out.append("\n");
        out.append("        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());\n");

        for (AssertionSpec assertion : testCase.getAssertions()) {
            switch (assertion.getType()) {
                case STATUS_EQUALS:
                    out.append("        assertEquals(")
                        .append(assertion.getNumberValue())
                        .append(", response.statusCode());\n");
                    break;
                case HEADER_EQUALS:
                    out.append("        assertEquals(\"")
                        .append(escapeJava(substitute(assertion.getTextValue(), vars)))
                        .append("\", response.headers().firstValue(\"")
                        .append(escapeJava(assertion.getKey()))
                        .append("\").orElse(null));\n");
                    break;
                case HEADER_CONTAINS:
                    out.append("        assertTrue(response.headers().allValues(\"")
                        .append(escapeJava(assertion.getKey()))
                        .append("\").stream().anyMatch(v -> v.contains(\"")
                        .append(escapeJava(substitute(assertion.getTextValue(), vars)))
                        .append("\")));\n");
                    break;
                case BODY_CONTAINS:
                    out.append("        assertTrue(response.body().contains(\"")
                        .append(escapeJava(substitute(assertion.getTextValue(), vars)))
                        .append("\"));\n");
                    break;
                default:
                    throw new IllegalStateException("Unsupported assertion type: " + assertion.getType());
            }
        }

        out.append("    }\n\n");
    }

    private String sanitizeMethodName(String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        if (sb.length() == 0 || !Character.isJavaIdentifierStart(sb.charAt(0))) {
            sb.insert(0, "test_");
        }
        return sb.toString();
    }

    private Map<String, String> toVariableMap(List<Variable> variables) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Variable variable : variables) {
            if (result.containsKey(variable.getName())) {
                throw new RuntimeException("Duplicate variable: " + variable.getName());
            }
            result.put(variable.getName(), String.valueOf(variable.getValue()));
        }
        return result;
    }

    private String substitute(String input, Map<String, String> variables) {
        Matcher matcher = VAR_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String varName = matcher.group(1);
            String value = variables.get(varName);
            if (value == null) {
                throw new RuntimeException("Undefined variable: " + varName);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String escapeJava(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
