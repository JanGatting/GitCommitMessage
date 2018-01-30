package de.gatting.scm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.zmlx.hg4idea.util.HgUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitMessage {

    private CommitMessage() {
    }

    public static String extractBranchName(Project project) {
        String branch = "";
        ProjectLevelVcsManager instance = ProjectLevelVcsManagerImpl.getInstance(project);
        if (instance.checkVcsIsActive("Git")) {
            GitLocalBranch currentBranch = GitBranchUtil.getCurrentRepository(project).getCurrentBranch();

            if (currentBranch != null) {
                // Branch name  matches Ticket Name
                branch = currentBranch.getName().trim();
            }
        } else if (instance.checkVcsIsActive("Mercurial")) {
            branch = HgUtil.getCurrentRepository(project).getCurrentBranch().trim();
        }

        return branch;
    }

    public static String parseBranchNameByRegex(String branchName, String variable, String templateString) {
        String regExFromTemplateVariable = getRegExFromTemplateVariable(variable, templateString);
        if (StringUtils.isNotEmpty(regExFromTemplateVariable)) {
            Pattern p = Pattern.compile(regExFromTemplateVariable);
            Matcher m = p.matcher(branchName);
            StringBuilder sb = new StringBuilder();
            while (m.find()) {
                sb.append(m.group() + " ");
            }
            if (sb.length() > 0) {
                return sb.toString().trim();
            }
        }
        return branchName;
    }

    private static String getRegExFromTemplateVariable(String variable, String templateString) {
        // The optional regEx {ticket:"regEx"}
        String result = StringUtils.substringAfterLast(templateString, variable + ":\"");
        return StringUtils.substringBefore(result, "\"}");
    }

    public static String replaceVariableWithinTemplate(String templateString, String variable, String variableValue) {
        if (isRegExForVariableInTemplateDefined(templateString, variable)) {
            return templateString.replace(getVariableWithRegex(variable, templateString), variableValue);
        }
        return StringUtils.remove(templateString, "${" + variable + "}");
    }

    public static boolean isRegExForVariableInTemplateDefined(String templateString, String variable) {

        if (StringUtils.isNotEmpty(getRegExFromTemplateVariable(variable, templateString))) {
            return true;
        }
        return false;
    }

    @NotNull
    public static String getVariableWithRegex(String variable, String templateString) {
        return "${" + variable + ":\"" + getRegExFromTemplateVariable(variable, templateString) + "\"}";
    }
}