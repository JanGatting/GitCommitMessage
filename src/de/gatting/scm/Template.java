package de.gatting.scm;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class Template {

    private JPanel mainPanel;
    private JTextArea templateContent;
    private JTextPane helpPane;

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