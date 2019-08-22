package be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models;

import be.ida_mediafoundry.jetpack.patchsystem.models.PatchFile;
import com.adobe.acs.commons.ondeploy.OnDeployScriptProvider;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScript;

public class OnDeployPatchFile implements PatchFile {

    private String projectName;
    private String scriptTitle;
    private String path;

    public OnDeployPatchFile(OnDeployScript onDeployScript, OnDeployScriptProvider onDeployScriptProvider) {
        if (onDeployScript != null) {
            this.path = onDeployScript.getClass().getName();
            this.projectName = onDeployScriptProvider.getClass().getSimpleName();

            this.scriptTitle = this.path;
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
        return true;
    }

    @Override
    public String getType() {
        return "onDeployScript";
    }
}
