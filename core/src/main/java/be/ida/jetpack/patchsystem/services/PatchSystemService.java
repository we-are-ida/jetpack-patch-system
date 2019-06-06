package be.ida.jetpack.patchsystem.services;

import be.ida.jetpack.patchsystem.models.PatchFile;
import be.ida.jetpack.patchsystem.models.PatchFileWithResultResource;
import be.ida.jetpack.patchsystem.models.PatchResult;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface PatchSystemService {

    boolean isPatchSystemReady();

    List<PatchFile> getPatchesToExecute();

    List<PatchFileWithResultResource> getPatches(ResourceResolver resourceResolver);

    PatchResult runPatch(String patchPath);
}