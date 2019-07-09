package be.ida.jetpack.patchsystem.groovy.repositories;

import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;

import java.util.List;

/**
 * Interface to get patches from the repository.
 */
public interface GroovyPatchFileRepository {

    /**
     * Get all patches.
     *
     * @return list of all patches
     */
    List<GroovyPatchFile> getPatches();

    /**
     * Get 1 specific patch file.
     *
     * @param path path of groovy script
     * @return GroovyPatchFile
     */
    GroovyPatchFile getPatch(String path);

}
