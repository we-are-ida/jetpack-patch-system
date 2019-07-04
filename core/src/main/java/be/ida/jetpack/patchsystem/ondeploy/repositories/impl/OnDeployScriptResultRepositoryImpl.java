package be.ida.jetpack.patchsystem.ondeploy.repositories.impl;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.ondeploy.models.OnDeployPatchResult;
import be.ida.jetpack.patchsystem.ondeploy.repositories.OnDeployScriptsResultRepository;
import be.ida_mediafoundry.jetpack.carve.manager.ModelManager;
import be.ida_mediafoundry.jetpack.carve.manager.exception.ModelManagerException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        name = "Jetpack - OnDeployScripts - Patch Result Repository",
        service = OnDeployScriptsResultRepository.class,
        property = {
                Constants.SERVICE_DESCRIPTION + ":String=Repository for Patch Results (CRUD).",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,
        })
public class OnDeployScriptResultRepositoryImpl implements OnDeployScriptsResultRepository {
    private static final Logger LOG = LoggerFactory.getLogger(OnDeployScriptResultRepositoryImpl.class);

    @Reference
    private ModelManager modelManager; //Carve

    @Override
    public OnDeployPatchResult getResult(String patchFile) {
        OnDeployPatchResult patchResult = null;

        try {
            patchResult = modelManager.retrieve(OnDeployPatchResult.class, patchFile);
        } catch (ModelManagerException e) {
            LOG.error("Couldn't get GroovyPatchResult", e);
        }

        return patchResult;
    }
}