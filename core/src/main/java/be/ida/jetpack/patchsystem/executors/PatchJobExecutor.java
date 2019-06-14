package be.ida.jetpack.patchsystem.executors;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchResult;
import be.ida.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import be.ida.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutionResult;
import org.apache.sling.event.jobs.consumer.JobExecutor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : maartentutak
 * @since : 09/11/2018
 */
@Component(
        service = JobExecutor.class,
        property = {JobExecutor.PROPERTY_TOPICS + "=" + PatchJobExecutor.TOPIC}
)
public class PatchJobExecutor implements JobExecutor {

    public static final String TOPIC = "be/ida/jetpack/patch";
    private static final Logger LOG = LoggerFactory.getLogger(PatchJobExecutor.class);
    private static final long ETA = -1L;

    @Reference
    private GroovyPatchSystemService groovyPatchSystemService;

    @Reference
    private OnDeployScriptSystemService onDeployScriptSystemService;

    @Override
    public JobExecutionResult process(Job job, JobExecutionContext context) {

        JobExecutionResult result = context.result().succeeded();

        try {
            List<String> patchPaths = job.getProperty(JetpackConstants.PATCH_PATHS, List.class);
            if (CollectionUtils.isNotEmpty(patchPaths)) {
                executePatches(patchPaths, context);
            }
        } catch (Exception e) {
            result = context.result().message(e.getMessage()).failed();
            LOG.error("Error during PatchJobExecutor", e);
        }

        return result;
    }

    private void executePatches(List<String> patchPaths, JobExecutionContext context) {
        int progressCounter = 1;
        context.initProgress(patchPaths.size(), ETA);

        for (String patchPath : patchPaths) {
            context.log("Executing patch '{0}'", patchPath);

            GroovyPatchResult patchResult = groovyPatchSystemService.runPatch(patchPath);

            context.incrementProgressCount(progressCounter++);
            context.log("Executed patch '{0}' - RESULT '{1}' - RUNNING TIME '{2}'", patchPath, patchResult.getStatus(), patchResult.getRunningTime());
        }
    }
}