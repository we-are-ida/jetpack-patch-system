package be.ida_mediafoundry.jetpack.patchsystem.servlets.responsemodels;

import be.ida.jetpack.patchsystem.models.SimplePatchFile;

import java.util.List;

/**
 * @author michael
 * @since 2019-06-06
 */
public class TriggerResponse {

    private String message;
    private List<SimplePatchFile> patches;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SimplePatchFile> getPatches() {
        return patches;
    }

    public void setPatches(List<SimplePatchFile> patches) {
        this.patches = patches;
    }
}
