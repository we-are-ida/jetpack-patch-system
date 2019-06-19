package be.ida.jetpack.patchsystem.ondeploy.services.impl;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.models.PatchFileWithResultResource;
import be.ida.jetpack.patchsystem.models.*;
import be.ida.jetpack.patchsystem.ondeploy.models.OnDeployPatchFile;
import be.ida.jetpack.patchsystem.ondeploy.models.OnDeployPatchResult;
import be.ida.jetpack.patchsystem.ondeploy.repositories.OnDeployScriptsResultRepository;
import be.ida.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
import com.adobe.acs.commons.ondeploy.OnDeployScriptProvider;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component(
        immediate = true,
        name = "Jetpack - OnDeployScripts - Patch System Service",
        configurationPolicy = ConfigurationPolicy.IGNORE,
        service = { OnDeployScriptSystemService.class },
        property={
                Constants.SERVICE_DESCRIPTION + "=Service for accessing patches, results and run.",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,

        })
public class OnDeployScriptSystemServiceImpl implements OnDeployScriptSystemService {

    private static final Logger LOG = LoggerFactory.getLogger(OnDeployScriptSystemServiceImpl.class);

    @Reference
    private OnDeployScriptsResultRepository patchResultRepository;

    @Reference(cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY)
    private volatile List<OnDeployScriptProvider> onDeployScriptProvider = new CopyOnWriteArrayList<>();

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    public List<PatchFileWithResultResource> getPatches(ResourceResolver resourceResolver) {

        List<PatchFileWithResultResource> patchFiles = new ArrayList<>();

        onDeployScriptProvider
            .forEach(provider -> {
                patchFiles.addAll(provider.getScripts()
                   .stream()
                   .map(item -> new OnDeployPatchFile(item, provider))
                   .map(patchFile -> {
                       OnDeployPatchResult patchResult = getMatchingPatchResult(patchFile);
                       return new PatchFileWithResultResource(resourceResolver, patchFile, patchResult, false);
                   })
                   .collect(Collectors.toList()));
            });

        return patchFiles;
    }

    @Override
    public boolean isPatchSystemReady() {
        return !onDeployScriptProvider.isEmpty();
    }

    /**
     * Return the GroovyPatchResult for the provided Patch File.
     * The GroovyPatchResult contains the actual run state of the patch.
     *
     * @param patchFile patch file to get the result from/
     * @return result or null
     */
    private OnDeployPatchResult getMatchingPatchResult(OnDeployPatchFile patchFile) {
        return patchResultRepository.getResult(patchFile.getResultPath());
    }

    /**
     * Will check if the patch is executable.
     * Patches are only executable if no result is found
     * OR when the source is different from the saved result (=modified scripts).
     *
     * @param patchFile patch file to check
     * @return true in case it's a new or modified script.
     */
    private boolean isExecutable(OnDeployPatchFile patchFile) {
        PatchResult patchResult = getMatchingPatchResult(patchFile);
        return patchResult == null || patchResult.isError();
    }

    protected void unbindOnDeployScriptProvider(OnDeployScriptProvider scriptProvider) {
        this.onDeployScriptProvider.remove(scriptProvider);
    }
}