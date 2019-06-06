package be.ida.jetpack.patchsystem.repositories;

import be.ida.jetpack.patchsystem.models.PatchFile;

import java.util.List;

/**
 * Interface to get patches from the repository.
 */
public interface PatchFileRepository {

    /**
     * Get all patches.
     *
     * @return list of all patches
     */
    List<PatchFile> getPatches();

    /**
     * Get 1 specific patch file.
     *
     * @param path path of groovy script
     * @return PatchFile
     */
    PatchFile getPatch(String path);

}
