package de.gatting.scm;

import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TemplateFileHandler {

    private static final String SCM_COMMIT_TEMPLATE_FILE = "/GitCommitMessage.template";

    private static Path path;

    public static String loadFile(final Project project) {
        if (path == null) {
            path = Paths.get(project.getBasePath() + SCM_COMMIT_TEMPLATE_FILE);
        }
        try {
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path));
            }
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void storeFile(final Project project, String templateContent) {
        try {
            if (path == null) {
                path = Paths.get(project.getBasePath() + SCM_COMMIT_TEMPLATE_FILE);
            }
            Files.write(path, templateContent.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
