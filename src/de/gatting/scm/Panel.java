package de.gatting.scm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import org.apache.commons.lang3.StringUtils;
import org.zmlx.hg4idea.branch.HgBranchUtil;
import org.zmlx.hg4idea.repo.HgRepository;
import org.zmlx.hg4idea.util.HgUtil;

import javax.swing.*;

public class Panel {
    private JPanel mainPanel;
    private JTextField ticket;
    private JTextField shortDescription;
    private JTextArea longDescription;
    private JButton changeTemplateButton;

    Panel(Project project) {

        String branch = "";
        ProjectLevelVcsManager instance = ProjectLevelVcsManagerImpl.getInstance(project);
        if (instance.checkVcsIsActive("Git")) {
            GitLocalBranch currentBranch = GitBranchUtil.getCurrentRepository(project).getCurrentBranch();

            if (currentBranch != null) {
                // Branch name  matches Ticket Name
                branch = currentBranch.getName().trim();
            }
        } else if (instance.checkVcsIsActive("Mercurial")) {
            branch = HgUtil.getCurrentRepository(project).getCurrentBranch();
        }

        // If e.g feature branch feature/JiraId-1234
        if (branch != null && branch.contains("/")) {
            ticket.setText(StringUtils.substringAfterLast(branch, "/"));
        } else {
            ticket.setText(branch);
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