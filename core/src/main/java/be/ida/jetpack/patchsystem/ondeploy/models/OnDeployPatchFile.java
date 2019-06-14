package be.ida.jetpack.patchsystem.ondeploy.models;

import be.ida.jetpack.patchsystem.models.PatchFile;
import com.adobe.acs.commons.ondeploy.OnDeployScriptProvider;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScript;

public class OnDeployPatchFile implements PatchFile {

    private String scriptName;
    private String projectName;

    //TODO private String scriptTitle;

    public OnDeployPatchFile(OnDeployScript onDeployScript, OnDeployScriptProvider onDeployScriptProvider) {
        this.scriptName = onDeployScript.getClass().getName();
        this.projectName = onDeployScriptProvider.getClass().getSimpleName();
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public String getScriptName() {
        return scriptName;
    }

    @Override
    public String getResultPath() {
        return this.scriptName;
    }

    @Override
    public String getPath() {
        return getScriptName();
    }
}
