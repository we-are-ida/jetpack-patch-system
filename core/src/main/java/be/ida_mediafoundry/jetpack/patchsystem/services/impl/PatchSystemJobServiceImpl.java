package be.ida_mediafoundry.jetpack.patchsystem.services.impl;

import be.ida_mediafoundry.jetpack.patchsystem.JetpackConstants;
import be.ida_mediafoundry.jetpack.patchsystem.executors.JobResult;
import be.ida_mediafoundry.jetpack.patchsystem.executors.PatchJobExecutor;
import be.ida_mediafoundry.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import be.ida_mediafoundry.jetpack.patchsystem.models.PatchFile;
import be.ida_mediafoundry.jetpack.patchsystem.models.SimplePatchFile;
import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
import be.ida_mediafoundry.jetpack.patchsystem.services.PatchSystemJobService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Component(
        name = "Jetpack - Patch System Job Service",
        service = { PatchSystemJobService.class },
        property={
                Constants.SERVICE_DESCRIPTION + "=Service for triggering patches",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,

        })
public class PatchSystemJobServiceImpl implements PatchSystemJobService {

    private static final Logger LOG = LoggerFactory.getLogger(PatchSystemJobServiceImpl.class);

    @Reference
    private JobManager jobManager;

    @Reference
    private GroovyPatchSystemService groovyPatchSystemService;

    @Reference
    private OnDeployScriptSystemService onDeployScriptSystemService;

    @Override
    public boolean executePatch(String patchPath, String type) {
        return executePatches(Collections.singletonList(new SimplePatchFile(type, patchPath)));
    }

    @Override
    public boolean executePatches(List<SimplePatchFile> patchFiles) {
        if (CollectionUtils.isNotEmpty(patchFiles)) {

            List<String> patchPaths = patchFiles
                    .stream()
                    .map(SimplePatchFile::getPatchFile)
                    .collect(Collectors.toList());

            List<String> types = patchFiles
                    .stream()
                    .map(SimplePatchFile::getType)
                    .collect(Collectors.toList());

            Map<String, Object> properties = new HashMap<>();
            properties.put(JetpackConstants.PATCH_PATHS, patchPaths);
            properties.put(JetpackConstants.TYPES, types);

            Job job =  jobManager.addJob(PatchJobExecutor.TOPIC, properties);
            return job != null;
        }
        return false;
    }

    @Override
    public List<SimplePatchFile> executeNewPatches() {
        List<SimplePatchFile> patchesToRun = getAllPatchesToExecute();

        if (!CollectionUtils.isEmpty(patchesToRun)) {
            boolean success = executePatches(patchesToRun);
            if (success) {
                return patchesToRun;
            } else {
                return new ArrayList<>();
            }
        }

        return patchesToRun;
    }

    @Override
    public List<SimplePatchFile> getAllPatchesToExecute() {
        List<PatchFile> patchFiles = groovyPatchSystemService.getPatchesToExecute();

        if (onDeployScriptSystemService.isPatchSystemReady()) {
            patchFiles.addAll(onDeployScriptSystemService.getPatchesToExecute());
        }

        return patchFiles
                .stream()
                .map(patchFile -> new SimplePatchFile(patchFile.getType(), patchFile.getPath()))
                .collect(Collectors.toList());
    }

    @Override
    public JobResult getPatchSystemStatus() {
        Collection<Job> jobs = jobManager.findJobs(JobManager.QueryType.ALL, PatchJobExecutor.TOPIC, 1, null);

        if (jobs != null && !jobs.isEmpty()) {
            Job job = jobs.iterator().next();

            JobResult jobResult = new JobResult(Job.JobState.ACTIVE.equals(job.getJobState()));
            jobResult.setProgress(job.getFinishedProgressStep() / job.getProgressStepCount() * 100);
            jobResult.setNumberOfPatches(job.getProgressStepCount());
            jobResult.setLogs(StringUtils.join(job.getProgressLog(), ", "));
            return jobResult;
        }

        return new JobResult(false);
    }
}