package de.gatting.scm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;

import javax.swing.*;

public class Panel {
    private JPanel mainPanel;
    private JTextField branch;
    private JTextField shortDescription;
    private JTextArea longDescription;
    private JButton changeTemplateButton;

    Panel(Project project) {

        GitLocalBranch currentBranch = GitBranchUtil.getCurrentRepository(project).getCurrentBranch();
        branch.setText(currentBranch.getName());
        changeTemplateButton.addActionListener(e -> {
            DialogWrapper dialog = createTemplateDialog(project);
            dialog.show();
        });
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    public String getBranch() {
        return branch.getText().trim();
    }

    public String getShortDescription() {
        return shortDescription.getText().trim();
    }

    public String getLongDescription() {
        return longDescription.getText().trim();
    }


    public DialogWrapper createTemplateDialog(Project project) {
        Template template = new Template(project);
        DialogBuilder builder = new DialogBuilder(project);
        builder.setCenterPanel(template.getMainPanel());
        builder.removeAllActions();
        builder.addOkAction();
        builder.addCancelAction();

        boolean isOk = builder.show() == DialogWrapper.OK_EXIT_CODE;
        if (isOk) {
            TemplateFileHandler.storeFile(project, template.getTemplateContent());
        }
        return builder.getDialogWrapper();
    }

}