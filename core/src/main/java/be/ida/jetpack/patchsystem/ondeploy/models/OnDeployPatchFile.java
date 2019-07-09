package be.ida.jetpack.patchsystem.ondeploy.models;

import be.ida.jetpack.patchsystem.models.PatchFile;
import com.adobe.acs.commons.ondeploy.OnDeployScriptProvider;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScript;
import org.apache.commons.lang3.StringUtils;

public class OnDeployPatchFile implements PatchFile {

    private String projectName;
    private String scriptTitle;
    private String path;

    public OnDeployPatchFile(OnDeployScript onDeployScript, OnDeployScriptProvider onDeployScriptProvider) {
        if (onDeployScript != null) {
            this.path = onDeployScript.getClass().getName();
            this.projectName = onDeployScriptProvider.getClass().getSimpleName();

            if (onDeployScript instanceof OnDeployScriptPatch) {
                scriptTitle = ((OnDeployScriptPatch) onDeployScript).name();
            }
        }

        if (StringUtils.isBlank(scriptTitle)) {
            this.scriptTitle = this.path;
        } else {
            this.scriptTitle += " (" + this.path + ")";
        }
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public String getScriptName() {
        return scriptTitle;
    }

    @Override
    public String getResultPath() {
        return this.path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public boolean isRunnable() {
        return false;
    }

    @Override
    public String getType() {
        return "onDeployScript";
    }
}
