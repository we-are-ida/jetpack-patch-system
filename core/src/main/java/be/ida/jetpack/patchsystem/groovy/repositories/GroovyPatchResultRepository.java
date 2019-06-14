package be.ida.jetpack.patchsystem.groovy.repositories;

import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchResult;

/**
 * Interface to get patch results from the repository, but also update or create patch results.
 */
public interface GroovyPatchResultRepository {

    GroovyPatchResult getResult(GroovyPatchFile patchFile);

    GroovyPatchResult createResult(GroovyPatchFile patchFile);

    void updateResult(GroovyPatchResult patchResult);
}
