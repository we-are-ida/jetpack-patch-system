package be.ida.jetpack.patchsystem.servlets;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.services.PatchSystemJobService;
import be.ida.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import be.ida.jetpack.patchsystem.servlets.responsemodels.PatchesListResponse;
import com.google.gson.Gson;
import org.apache.http.entity.ContentType;
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
import java.util.List;

/**
 * Check whether patches are still running or not.
 */
@Component(
        service = { Servlet.class },
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/services/patches/list",
                Constants.SERVICE_DESCRIPTION + "=Check if new patches are available",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,
        })
public class HasPatchesToExecuteServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(HasPatchesToExecuteServlet.class);

    @Reference
    private PatchSystemJobService patchSystemJobService;

    @Reference
    private GroovyPatchSystemService patchSystemService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        try {
            process(response);
        } catch (Exception e) {
            LOG.error("Error during HasPatchesToExecuteServlet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void process(SlingHttpServletResponse response) throws IOException {
        List<String> patches = patchSystemJobService.getAllPatchesToExecute();

        PatchesListResponse output = new PatchesListResponse(patches);

        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(output));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}