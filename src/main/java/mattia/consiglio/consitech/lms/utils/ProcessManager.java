package mattia.consiglio.consitech.lms.utils;

import lombok.extern.slf4j.Slf4j;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProcessManager {
    private static final String OUTPUT = "output";
    private static final String ERROR = "error";

    private ProcessManager() {
        throw new AssertionError("This class cannot be instantiated");
    }

    public static Map<String, String> run(String[] command, LineProcessor lineProcessor) {
        Map<String, String> output = new HashMap<>();
        output.put(OUTPUT, "");
        output.put(ERROR, "");
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                //execute a method to process the line here
                lineProcessor.process(line);
                output.put(OUTPUT, output.get(OUTPUT) + line + "\n");
            }

            // Log error output
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                log.error(errorLine);
                output.put(ERROR, output.get(ERROR) + errorLine + "\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new BadRequestException("Error during running the process , exit code: " + exitCode);
            }
            log.info("Process complete");
        } catch (IOException | InterruptedException e) {
            log.error("Error during running the process: " + e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            Thread.currentThread().interrupt();
        }
        return output;
    }

    public static Map<String, String> run(String[] command) {
        LineProcessor lineProcessor = (String line) -> {
        };
        return run(command, lineProcessor);
    }
}
