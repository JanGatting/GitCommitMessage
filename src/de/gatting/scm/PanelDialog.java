package de.gatting.scm;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class PanelDialog extends DialogWrapper {

    private final Panel panel;

    PanelDialog(@Nullable Project project) {
        super(project);
        panel = new Panel(project);
        IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(PluginId.getId("git-commit-message-plugin"));
        String version = "";
        if (pluginDescriptor != null) {
            version = pluginDescriptor.getVersion();
        }
        setTitle("Git / Hg Mercurial Commit Message Plugin. Version: " + version);
        setOKButtonText("OK");
        setSize(300, 200);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel.getMainPanel();
    }

    String getCommitMessage(Project project) {
        String templateString = TemplateFileHandler.loadFile(project);
        templateString = CommitMessage.replaceVariableWithinTemplate(templateString, Consts.TICKET, panel.getTicket());
        templateString = CommitMessage.replaceVariableWithinTemplate(templateString, Consts.SHORT_DESCRIPTION, panel.getShortDescription());
        templateString = CommitMessage.replaceVariableWithinTemplate(templateString, Consts.LONG_DESCRIPTION, getLongDescription(templateString));
        return templateString;
    }

    private String getLongDescription(String templateString) {
        String longDescription = panel.getLongDescription();
        // Get the empty Spaces in templates in new Line and the longDescription Variable
        String searchString = CommitMessage.getVariableWithRegex(Consts.LONG_DESCRIPTION, templateString);
        String emptySpaces =
                StringUtils.substringBetween(templateString, identifyLineDelimiter(templateString), searchString);
        String lineDelimiter = identifyLineDelimiter(longDescription);
        return StringUtils.replace(longDescription, lineDelimiter, lineDelimiter + emptySpaces).trim();
    }


    /**
     * <h1> Identify which line delimiter is used in a string </h1>
     * <p>
     * This is useful when processing files that were created on different operating systems.
     *
     * @param str - the string with the mystery line delimiter.
     * @return the line delimiter for windows, {@code \r\n}, <br>
     * unix/linux {@code \n} or legacy mac {@code \r} <br>
     * if none can be identified, it falls back to unix {@code \n}
     */
    public static String identifyLineDelimiter(String str) {
        if (str.matches("(?s).*(\\r\\n).*")) {     //Windows //$NON-NLS-1$
            return "\r\n"; //$NON-NLS-1$
        } else if (str.matches("(?s).*(\\n).*")) { //Unix/Linux //$NON-NLS-1$
            return "\n"; //$NON-NLS-1$
        } else if (str.matches("(?s).*(\\r).*")) { //Legacy mac os 9. Newer OS X use \n //$NON-NLS-1$
            return "\r"; //$NON-NLS-1$
        } else {
            return "\n";  //fallback onto '\n' if nothing matches. //$NON-NLS-1$
        }
    }

}