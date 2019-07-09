package be.ida.jetpack.patchsystem.servlets;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.services.PatchSystemJobService;
import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.commands.WCMCommand;
import com.day.cq.wcm.api.commands.WCMCommandContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HtmlResponse;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Execute 1 single job.
 * Used by the Touch UI interface to trigger 1 job.
 * This endpoint will be called multiple times in case multiple scripts were selected.
 */
@Component(service= WCMCommand.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Run Groovy Script WcmCommand",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,

        })
public class TriggerSinglePatchWcmCommand implements WCMCommand {

    @Reference
    private PatchSystemJobService patchSystemJobService;

    @Override
    public String getCommandName() {
        return "triggerSinglePatch";
    }

    @Override
    public HtmlResponse performCommand(WCMCommandContext wcmCommandContext,
                                       SlingHttpServletRequest slingHttpServletRequest,
                                       SlingHttpServletResponse slingHttpServletResponse,
                                       PageManager pageManager) {

        RequestParameter path = slingHttpServletRequest.getRequestParameter(PATH_PARAM);
        RequestParameter type = slingHttpServletRequest.getRequestParameter("type");
        RequestParameter runnable = slingHttpServletRequest.getRequestParameter("runnable");

        boolean runEnabled = false;
        if (runnable != null && "yes".equals(runnable.getString())) {
            runEnabled = true;
        }

        HtmlResponse resp = null;
        try {
            boolean success = patchSystemJobService.executePatch(path.getString(), type.getString(), runEnabled);

            resp = HtmlStatusResponseHelper.createStatusResponse(success, "executed",
                    path.getString());
        } catch (Exception e) {
            resp = HtmlStatusResponseHelper.createStatusResponse(false, e.getMessage());
        }

        return resp;
    }
}
