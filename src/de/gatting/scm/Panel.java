package de.gatting.scm;

import com.google.common.base.CharMatcher;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;

public class Panel {
    private JPanel mainPanel;
    private JTextField ticket;
    private JTextField shortDescription;
    private JTextArea longDescription;
    private JButton changeTemplateButton;

    private CharMatcher CLEANER = CharMatcher.anyOf("-_").precomputed();

    Panel(Project project) {
        GitLocalBranch currentBranch = GitBranchUtil.getCurrentRepository(project).getCurrentBranch();
        if (currentBranch != null) {
            // Branch name  matches Ticket Name
            setTextFieldsBasedOnBranch(currentBranch.getName().trim());
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

    private void setTextFieldsBasedOnBranch(String branch) {
        final String[] branchParts = branch.split("/");
        // If e.g feature branch feature/JiraId-1234
        switch (branchParts.length) {
            case 0: parseTicket(branch, ticket); break;
            case 1: parseTicket(branchParts[1], ticket); break;
            case 2: parseTicketAndShortDesc(branch, ticket, shortDescription); break;
            default: parseAllFields(branch, ticket, shortDescription, longDescription); break;
        }
    }

    private void parseTicket(String branchName, JTextField ticket) {
        ticket.setText(branchName.toUpperCase());
    }

    private void parseTicketAndShortDesc(String branchName, JTextField ticket, JTextField shortDescription) {
        String[] branchParts = branchName.split("/");

        parseTicket(branchParts[1], ticket);

        final String shortDesc = CLEANER.removeFrom(StringUtils.defaultString(branchParts[2], ""));
        shortDescription.setText(shortDesc);
    }

    private void parseAllFields(String branchName, JTextField ticket, JTextField shortDescription, JTextArea textArea) {
        final String[] branchParts = branchName.split("/");
        parseTicketAndShortDesc(branchName, ticket, shortDescription);
        final String longDesc = CLEANER.removeFrom(StringUtils.defaultString(branchParts[3], ""));
        textArea.setText(longDesc);
    }

}