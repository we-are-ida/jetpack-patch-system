package be.ida.jetpack.patchsystem.servlets;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.executors.JobResult;
import be.ida.jetpack.patchsystem.services.PatchSystemJobService;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Check whether patches are still running or not.
 */
@Component(
        service = { Servlet.class },
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/services/patches/check",
                Constants.SERVICE_DESCRIPTION + "=Check if patch system is still running or not",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,
        })
public class CheckPatchStatusServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CheckPatchStatusServlet.class);

    @Reference
    private PatchSystemJobService patchSystemJobService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setContentType(JetpackConstants.APPLICATION_JSON);

        try {
            process(response);
        } catch (Exception e) {
            LOG.error("Error during CheckPatchStatusServlet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void process(SlingHttpServletResponse response) throws IOException {
        JobResult jobResult = patchSystemJobService.getPatchSystemStatus();

        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(jobResult));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}