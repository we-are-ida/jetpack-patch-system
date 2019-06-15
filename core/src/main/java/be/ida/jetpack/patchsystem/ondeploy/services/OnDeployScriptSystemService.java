package be.ida.jetpack.patchsystem.ondeploy.services;

import be.ida.jetpack.patchsystem.models.PatchFileWithResultResource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface OnDeployScriptSystemService {

    boolean isPatchSystemReady();

    List<PatchFileWithResultResource> getPatches(ResourceResolver resourceResolver);
}