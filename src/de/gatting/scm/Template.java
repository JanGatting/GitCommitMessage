package de.gatting.scm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Template {

    private JPanel mainPanel;
    private JTextArea templateContent;

    public Template(Project project) {
        templateContent.setText(TemplateFileHandler.loadFile(project));
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    String getTemplateContent() {
        return templateContent.getText().trim();
    }

}