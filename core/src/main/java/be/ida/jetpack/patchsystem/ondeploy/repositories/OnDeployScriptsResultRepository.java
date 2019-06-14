package be.ida.jetpack.patchsystem.ondeploy.repositories;

import be.ida.jetpack.patchsystem.ondeploy.models.OnDeployPatchFile;
import be.ida.jetpack.patchsystem.ondeploy.models.OnDeployPatchResult;

/**
 * Interface to get patch results from the repository, but also update or create patch results.
 */
public interface OnDeployScriptsResultRepository {

    OnDeployPatchResult getResult(String patchFile);

}
