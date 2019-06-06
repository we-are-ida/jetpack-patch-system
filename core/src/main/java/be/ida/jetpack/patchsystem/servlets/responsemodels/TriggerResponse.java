package be.ida.jetpack.patchsystem.servlets.responsemodels;

import java.util.List;

/**
 * @author michael
 * @since 2019-06-06
 */
public class TriggerResponse {

    private String message;
    private List<String> patches;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getPatches() {
        return patches;
    }

    public void setPatches(List<String> patches) {
        this.patches = patches;
    }
}
