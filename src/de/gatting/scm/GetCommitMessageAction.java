package de.gatting.scm;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.openapi.vcs.ui.Refreshable;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jetbrains.annotations.Nullable;
import org.zmlx.hg4idea.util.HgUtil;

import java.util.HashMap;
import java.util.Map;


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

        Map<String, String> valuesMap = new HashMap<>();
        String branchName = CommitMessage.extractBranchName(project);
        String[] branchParts = CommitMessage.splitBranchName(branchName);
        if (branchParts.length > 1) {
            valuesMap.put(Consts.TICKET, branchParts[1]);
        }
        valuesMap.put(Consts.SHORT_DESCRIPTION, "");
        valuesMap.put(Consts.LONG_DESCRIPTION, "");
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(templateString);
    }

}