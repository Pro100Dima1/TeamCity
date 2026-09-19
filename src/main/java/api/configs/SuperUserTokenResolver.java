package api.configs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Super User token меняется при каждом старте TeamCity.
 * Читается из лог-файла внутри Docker-контейнера (не через полный {@code docker logs}).
 */
public final class SuperUserTokenResolver {

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "Super user authentication token:\\s*(\\d+)",
            Pattern.CASE_INSENSITIVE
    );

    private static String cachedToken;

    private SuperUserTokenResolver() {
    }

    public static String resolve() {
        String configured = Config.getProperty("superuser.token");
        if (configured != null && !configured.isBlank() && !"auto".equalsIgnoreCase(configured.trim())) {
            return configured.trim();
        }

        if (cachedToken != null) {
            return cachedToken;
        }

        String container = Config.getProperty("superuser.docker.container");
        if (container == null || container.isBlank()) {
            container = "teamcity-server";
        }

        cachedToken = readFromContainerLogFile(container.trim());
        return cachedToken;
    }

    public static void clearCache() {
        cachedToken = null;
    }

    private static String readFromContainerLogFile(String container) {
        List<String> commands = List.of(
                "grep -i 'Super user authentication token' /opt/teamcity/logs/teamcity-server.log | tail -1",
                "grep -i 'Super user authentication token' /opt/teamcity/logs/teamcity-startup.log | tail -1"
        );

        List<String> errors = new ArrayList<>();
        for (String command : commands) {
            try {
                String token = runDockerExec(container, command);
                if (token != null) {
                    return token;
                }
            } catch (Exception e) {
                errors.add(e.getMessage());
            }
        }

        try {
            String token = runDockerLogsTail(container);
            if (token != null) {
                return token;
            }
        } catch (Exception e) {
            errors.add(e.getMessage());
        }

        throw new IllegalStateException(
                "Super user token not found for container '" + container + "'. "
                        + "Set superuser.token manually or check superuser.docker.container. "
                        + "Details: " + String.join("; ", errors)
        );
    }

    private static String runDockerExec(String container, String shellCommand) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec", container, "sh", "-c", shellCommand
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = readProcessOutput(process, 15);
        int exit = process.exitValue();
        if (exit != 0 && (output == null || output.isBlank())) {
            return null;
        }
        return extractLastToken(output);
    }

    private static String runDockerLogsTail(String container) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "logs", "--tail", "5000", container
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = readProcessOutput(process, 20);
        return extractLastToken(output);
    }

    private static String readProcessOutput(Process process, int timeoutSeconds) throws Exception {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        }
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("Timeout after " + timeoutSeconds + "s");
        }
        return output.toString();
    }

    private static String extractLastToken(String output) {
        if (output == null || output.isBlank()) {
            return null;
        }
        String lastToken = null;
        Matcher matcher = TOKEN_PATTERN.matcher(output);
        while (matcher.find()) {
            lastToken = matcher.group(1);
        }
        return lastToken;
    }
}
