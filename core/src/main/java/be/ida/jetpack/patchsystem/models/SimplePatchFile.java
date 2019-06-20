package be.ida.jetpack.patchsystem.models;

/**
 * @author michael
 * @since 2019-06-20
 */
public class SimplePatchFile {

    private String type;
    private String patchFile;

    public SimplePatchFile(String type, String patchFile) {
        this.type = type;
        this.patchFile = patchFile;
    }

    public String getType() {
        return type;
    }

    public String getPatchFile() {
        return patchFile;
    }
}
