package be.ida.jetpack.patchsystem.servlets;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
import be.ida.jetpack.patchsystem.services.PatchSystemJobService;
import be.ida.jetpack.patchsystem.servlets.responsemodels.TriggerResponse;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Trigger all new or modified patches.
 */
@Component(
        service = { Servlet.class },
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/services/patches/trigger",
                Constants.SERVICE_DESCRIPTION + "=Trigger all new or modified patches",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,
        })
public class TriggerNewPatchesServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TriggerNewPatchesServlet.class);

    @Reference
    private PatchSystemJobService patchSystemJobService;

    @Reference
    private OnDeployScriptSystemService onDeployScriptSystemService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            if (isValid(request)) {
                response.setContentType(JetpackConstants.APPLICATION_JSON);
                process(response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error during TriggerNewPatchesServlet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValid(SlingHttpServletRequest request) {
        return JetpackConstants.APPLICATION_JSON.equals(request.getContentType());
    }

    private void process(SlingHttpServletResponse response) throws JSONException, IOException {
        List<String> patches = patchSystemJobService.executeNewPatches();

        TriggerResponse triggerResponse = new TriggerResponse();
        if (patches == null) {
            triggerResponse.setMessage("Could not trigger patches.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else if (CollectionUtils.isEmpty(patches)) {
            triggerResponse.setMessage("No patches found to trigger.");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            triggerResponse.setMessage("Success.");
            triggerResponse.setPatches(patches);
            response.setStatus(HttpServletResponse.SC_OK);
        }

        if (onDeployScriptSystemService.isPatchSystemReady()) {
            String message = triggerResponse.getMessage();
            triggerResponse.setMessage(message
                    + " On Deploy Scripts cannot be triggered using the Patch System.");
        }

        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(triggerResponse));
    }
}