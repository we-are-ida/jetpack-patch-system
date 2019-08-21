package be.ida_mediafoundry.jetpack.patchsystem.executors;

import be.ida_mediafoundry.jetpack.patchsystem.JetpackConstants;
import be.ida_mediafoundry.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import be.ida_mediafoundry.jetpack.patchsystem.models.PatchResult;
import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
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
            List<String> types = job.getProperty(JetpackConstants.TYPES, List.class);

            if (CollectionUtils.isNotEmpty(patchPaths)) {
                executePatches(patchPaths, types, context);
            }
        } catch (Exception e) {
            result = context.result().message(e.getMessage()).failed();
            LOG.error("Error during PatchJobExecutor", e);
        }

        return result;
    }

    private void executePatches(List<String> patchPaths, List<String> types, JobExecutionContext context) {
        int progressCounter = 1;
        context.initProgress(patchPaths.size(), ETA);

        for (int i = 0; i < patchPaths.size(); i++) {
            String patchPath = patchPaths.get(i);
            String type = types.get(i);

            context.log("Executing patch '{0}' of type '{1}'", patchPath, types);

            PatchResult patchResult = null;
            if ("groovy".equals(type)) {
                patchResult = groovyPatchSystemService.runPatch(patchPath);
            } else if ("onDeployScript".equals(type)) {
                patchResult = onDeployScriptSystemService.runPatch(patchPath);
            }

            context.incrementProgressCount(progressCounter++);

            if (patchResult != null) {
                context.log("Executed patch '{0}' - RESULT '{1}' - RUNNING TIME '{2}'", patchPath, patchResult.getStatus(), patchResult.getRunningTime());
            } else {
                context.log("Not Executed patch '{0}' - No runner found for type '{1}'", patchPath, type);
            }
        }
    }
}