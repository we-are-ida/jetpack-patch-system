package be.ida.jetpack.patchsystem.repositories.impl;

import be.ida.jetpack.carve.manager.ModelManager;
import be.ida.jetpack.carve.manager.exception.ModelManagerException;
import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.models.PatchFile;
import be.ida.jetpack.patchsystem.models.PatchResult;
import be.ida.jetpack.patchsystem.repositories.PatchResultRepository;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

@Component(
        name = "Jetpack - Patch Result Repository",
        service = PatchResultRepository.class,
        property = {
                Constants.SERVICE_DESCRIPTION + ":String=Repository for Patch Results (CRUD).",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,
        })
public class PatchResultRepositoryImpl implements PatchResultRepository {
    private final static Logger LOG = LoggerFactory.getLogger(PatchResultRepository.class);

    private static final String RUNNING = "RUNNING";

    @Reference
    private ModelManager modelManager; //Carve

    @Override
    public PatchResult getResult(PatchFile patchFile) {
        PatchResult patchResult = null;

        try {
            patchResult = modelManager.retrieve(PatchResult.class, patchFile.getResultPath());
        } catch (ModelManagerException e) {
            LOG.error("Couldn't get PatchResult", e);
        }

        return patchResult;
    }

    @Override
    public PatchResult createResult(PatchFile patchFile) {
        PatchResult patchResult = new PatchResult(patchFile.getResultPath(), RUNNING, Calendar.getInstance());
        patchResult.setMd5(patchFile.getMd5());

        try {
            modelManager.persist(patchResult);
        } catch (ModelManagerException e) {
            LOG.error("Couldn't persist PatchResult", e);
        }
        return patchResult;
    }

    @Override
    public void updateResult(PatchResult patchResult) {
        try {
            patchResult.setEndDate(Calendar.getInstance());
            modelManager.persist(patchResult);
        } catch (ModelManagerException e) {
            LOG.error("Couldn't persist PatchResult", e);
        }
    }
}