package com.example.explorer.explorer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/explorer/source")
public class ExplorerController {

    public record SourceResponse(String file, String content, int totalLines, Integer startLine, Integer endLine) {}

    @GetMapping
    public SourceResponse getSource(
            @RequestParam String file,
            @RequestParam(required = false) Integer startLine,
            @RequestParam(required = false) Integer endLine) {

        try {
            Path targetPath = resolveSourceFile(file);

            if (targetPath == null || !Files.exists(targetPath) || !Files.isRegularFile(targetPath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Source file not found: " + file);
            }

            List<String> lines = Files.readAllLines(targetPath);
            int totalLines = lines.size();

            int actualStart = (startLine != null && startLine > 0) ? startLine : 1;
            int actualEnd = (endLine != null && endLine <= totalLines) ? endLine : totalLines;

            if (actualStart > actualEnd || actualStart > totalLines) {
                actualStart = 1;
                actualEnd = totalLines;
            }

            List<String> selectedLines = lines.subList(actualStart - 1, actualEnd);
            String content = String.join("\n", selectedLines);

            return new SourceResponse(file, content, totalLines, actualStart, actualEnd);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading file", e);
        }
    }

    private Path resolveSourceFile(String requestedFile) {
        String cleanPath = requestedFile.replace("\\", "/").trim();
        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        // Possible candidate root directories
        String userDir = System.getProperty("user.dir");
        Path[] candidateRoots = new Path[] {
            Paths.get(userDir),
            Paths.get(userDir, "backend"),
            Paths.get(userDir, "src/main/java"),
            Paths.get(userDir, "backend/src/main/java"),
            Paths.get(userDir, "src/main/resources"),
            Paths.get(userDir, "backend/src/main/resources")
        };

        // Try direct resolution or prefixed resolution
        for (Path root : candidateRoots) {
            Path direct = root.resolve(cleanPath).normalize();
            if (Files.exists(direct) && Files.isRegularFile(direct)) {
                return direct;
            }

            Path inJava = root.resolve("src/main/java/" + cleanPath).normalize();
            if (Files.exists(inJava) && Files.isRegularFile(inJava)) {
                return inJava;
            }

            Path inResources = root.resolve("src/main/resources/" + cleanPath).normalize();
            if (Files.exists(inResources) && Files.isRegularFile(inResources)) {
                return inResources;
            }
        }

        return null;
    }
}
