package de.gatting.scm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;

public class Panel {
    private JPanel mainPanel;
    private JTextField ticket;
    private JTextField shortDescription;
    private JTextArea longDescription;
    private JButton changeTemplateButton;

    Panel(Project project) {

        String branch = CommitMessage.extractBranchName(project);
        if (branch != null) {
            // Branch name  matches Ticket Name
            setTextFieldsBasedOnBranch(branch, project);
        }

        changeTemplateButton.addActionListener(e -> {
            DialogWrapper dialog = createTemplateDialog(project);
            if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
                dialog.getExitCode();
            }

        });
    }

    private void setTextFieldsBasedOnBranch(String branchName, Project project) {

        String templateString = TemplateFileHandler.loadFile(project);
        // Ticket
        String parsedTicket = CommitMessage.parseBranchNameByRegex(branchName, Consts.TICKET, templateString);
        if (CommitMessage.isRegExForVariableInTemplateDefined(templateString, Consts.TICKET)) {
            ticket.setText(parsedTicket);
        }

        // ShortDescription
        String parsedShortDescription = CommitMessage.parseBranchNameByRegex(branchName, Consts.SHORT_DESCRIPTION, templateString);
        if (CommitMessage.isRegExForVariableInTemplateDefined(templateString, Consts.SHORT_DESCRIPTION)) {
            shortDescription.setText(parsedShortDescription);
        }

        // LongDescription
        String parsedLongDescription = CommitMessage.parseBranchNameByRegex(branchName, Consts.LONG_DESCRIPTION, templateString);
        if (CommitMessage.isRegExForVariableInTemplateDefined(templateString, Consts.LONG_DESCRIPTION)) {
            longDescription.setText(parsedLongDescription);
        }
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
        builder.setTitle("Git / Hg Mercurial Commit Message Template.");
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