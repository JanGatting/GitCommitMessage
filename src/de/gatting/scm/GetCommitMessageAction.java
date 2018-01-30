package de.gatting.scm;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.Refreshable;
import org.jetbrains.annotations.Nullable;


public class GetCommitMessageAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {

        final CommitMessageI commitPanel = getCommitPanel(actionEvent);
        if (commitPanel == null) {
            return;
        }
        commitPanel.setCommitMessage(getCommitMessage(actionEvent.getProject()));
    }

    @Nullable
    private static CommitMessageI getCommitPanel(@Nullable AnActionEvent e) {
        if (e == null) {
            return null;
        }

        Refreshable data = Refreshable.PANEL_KEY.getData(e.getDataContext());
        if (data instanceof CommitMessageI) {
            return (CommitMessageI) data;
        }
        return VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext());
    }


    private String getCommitMessage(final Project project) {
        String templateString = TemplateFileHandler.loadFile(project);

        String branchName = CommitMessage.extractBranchName(project);

        // Ticket
        String parsedTicket = CommitMessage.parseBranchNameByRegex(branchName, Consts.TICKET, templateString);
        templateString = CommitMessage.replaceVariableWithinTemplate(templateString, Consts.TICKET, parsedTicket);

        // ShortDescription
        String parsedShortDescription = CommitMessage.parseBranchNameByRegex(branchName, Consts.SHORT_DESCRIPTION, templateString);
        templateString = CommitMessage.replaceVariableWithinTemplate(templateString, Consts.SHORT_DESCRIPTION, parsedShortDescription);

        // LongDescription
        String parsedLongDescription = CommitMessage.parseBranchNameByRegex(branchName, Consts.LONG_DESCRIPTION, templateString);
        templateString = CommitMessage.replaceVariableWithinTemplate(templateString, Consts.LONG_DESCRIPTION, parsedLongDescription);

        return templateString;
    }

}