package de.gatting.scm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class Panel {
    private JPanel mainPanel;
    private JTextField ticket;
    private JTextField shortDescription;
    private JTextArea longDescription;
    private JButton changeTemplateButton;

    Panel(Project project) {
        GitLocalBranch currentBranch = GitBranchUtil.getCurrentRepository(project).getCurrentBranch();
        if (currentBranch != null) {
            // Branch name  matches Ticket Name
            String branch = currentBranch.getName().trim();
            // If e.g feature branch feature/JiraId-1234
            if (branch.contains("/")) {
                ticket.setText(StringUtils.substringAfterLast(branch, "/"));
            } else {
                ticket.setText(currentBranch.getName());
            }
        }

        changeTemplateButton.addActionListener(e -> {
            DialogWrapper dialog = createTemplateDialog(project);
            if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
                dialog.getExitCode();
            }

        });
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    public String getTicket() {
        return this.ticket.getText();
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
        builder.setTitle("Git Commit Message Template.");
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