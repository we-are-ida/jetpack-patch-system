package be.ida.jetpack.patchsystem.repositories;

import be.ida.jetpack.patchsystem.models.PatchFile;
import be.ida.jetpack.patchsystem.models.PatchResult;

/**
 * Interface to get patch results from the repository, but also update or create patch results.
 */
public interface PatchResultRepository {

    PatchResult getResult(PatchFile patchFile);

    PatchResult createResult(PatchFile patchFile);

    void updateResult(PatchResult patchResult);
}
