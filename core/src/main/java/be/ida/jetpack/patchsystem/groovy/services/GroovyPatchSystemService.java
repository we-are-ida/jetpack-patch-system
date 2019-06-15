package be.ida.jetpack.patchsystem.groovy.services;

import be.ida.jetpack.patchsystem.models.PatchFile;
import be.ida.jetpack.patchsystem.models.PatchFileWithResultResource;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchResult;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface GroovyPatchSystemService {

    boolean isPatchSystemReady();

    List<PatchFile> getPatchesToExecute();

    List<PatchFileWithResultResource> getPatches(ResourceResolver resourceResolver);

    GroovyPatchResult runPatch(String patchPath);
}