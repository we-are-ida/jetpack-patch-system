package be.ida.jetpack.patchsystem.models;

/**
 * @author michael
 * @since 2019-06-12
 */
public interface PatchFile {

    String getProjectName();

    String getScriptName();

    String getResultPath();

    String getPath();

    boolean isRunnable();

    String getType();

}
