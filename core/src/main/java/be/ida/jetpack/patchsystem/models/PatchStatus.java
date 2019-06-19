package be.ida.jetpack.patchsystem.models;

/**
 * @author michael
 * @since 2019-06-16
 */
public enum PatchStatus {

    ERROR("ERROR", "error"),
    SUCCESS("SUCCESS", "success"),
    RUNNING("RUNNING", "warning"),
    RERUN("RE-RUN", "info"),
    NEW("NEW", "new");

    private String name;
    private String cssClass;

    PatchStatus(String name, String cssClass) {
        this.name = name;
        this.cssClass = cssClass;
    }

    public String displayName() {
        return name;
    }

    public String cssClass() {
        return cssClass;
    }

    public boolean isOfStatus(PatchResult patchResult) {
        if (patchResult == null) {
            return false;
        }
        return displayName().equals(patchResult.getStatus());
    }

    public static PatchStatus getStatus(String status) {
        try {
            return PatchStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
