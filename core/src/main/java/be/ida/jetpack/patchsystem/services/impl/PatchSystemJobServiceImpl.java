package be.ida.jetpack.patchsystem.services.impl;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.executors.PatchJobExecutor;
import be.ida.jetpack.patchsystem.executors.JobResult;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.models.PatchFile;
import be.ida.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
import be.ida.jetpack.patchsystem.services.PatchSystemJobService;
import be.ida.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
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
    public boolean executePatch(String patchPath, String type, boolean runnable) {
        if (runnable) {
            return executePatches(Collections.singletonList(patchPath));
        }
        return false;
    }

    @Override
    public boolean executePatches(List<String> patchPaths) {
        if (CollectionUtils.isNotEmpty(patchPaths)) {
            Map<String, Object> properties = Collections.singletonMap(JetpackConstants.PATCH_PATHS, patchPaths);
            Job job =  jobManager.addJob(PatchJobExecutor.TOPIC, properties);
            return job != null;
        }
        return false;
    }

    @Override
    public List<String> executeNewPatches() {
        List<String> patchesToRun = getAllPatchesToExecute();

        if (!CollectionUtils.isEmpty(patchesToRun)) {
            boolean success = executePatches(patchesToRun);
            if (success) {
                return patchesToRun;
            } else {
                return null;
            }
        }

        return patchesToRun;
    }

    @Override
    public List<String> getAllPatchesToExecute() {
        List<PatchFile> patchFiles = groovyPatchSystemService.getPatchesToExecute();

        return patchFiles
                .stream()
                .map(PatchFile::getPath)
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