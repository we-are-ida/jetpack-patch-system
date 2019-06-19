package be.ida.jetpack.patchsystem.groovy.services.impl;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchResult;
import be.ida.jetpack.patchsystem.models.PatchFileWithResultResource;
import be.ida.jetpack.patchsystem.groovy.utils.PatchUtils;
import be.ida.jetpack.patchsystem.models.*;
import be.ida.jetpack.patchsystem.groovy.repositories.GroovyPatchResultRepository;
import be.ida.jetpack.patchsystem.groovy.repositories.GroovyPatchFileRepository;
import be.ida.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import com.icfolson.aem.groovy.console.GroovyConsoleService;
import com.icfolson.aem.groovy.console.response.RunScriptResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component(
        immediate = true,
        name = "Jetpack - Patch System Service",
        configurationPolicy = ConfigurationPolicy.IGNORE,
        service = { GroovyPatchSystemService.class },
        property={
                Constants.SERVICE_DESCRIPTION + "=Service for accessing patches, results and run.",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,

        })
public class GroovyPatchSystemServiceImpl implements GroovyPatchSystemService {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyPatchSystemServiceImpl.class);

    private static final String DEFAULT_USER = "jetpack-patch-system";
    private static final String DEFAULT_SERVICE = "be.ida.jetpack.patch-system.core";

    @Reference
    private GroovyPatchResultRepository patchResultRepository;

    @Reference
    private GroovyPatchFileRepository patchFileRepository;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY)
    private volatile GroovyConsoleService groovyConsoleService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public GroovyPatchResult runPatch(String patchPath) {
        GroovyPatchFile patchFile = patchFileRepository.getPatch(patchPath);
        GroovyPatchResult patchResult = patchResultRepository.createResult(patchFile);

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(getCredentials())) {
            MockSlingHttpServletRequest mockRequest = new MockSlingHttpServletRequest(resourceResolver);
            MockSlingHttpServletResponse mockResponse = new MockSlingHttpServletResponse();

            if (isPatchSystemReady()) {
                //run script
                RunScriptResponse response = groovyConsoleService.runScript(mockRequest, mockResponse, patchFile.getPath());

                patchResult.setRunningTime(response.getRunningTime());

                //process response of script execution
                if (StringUtils.isBlank(response.getExceptionStackTrace())) {
                    patchResult.setStatus(PatchStatus.SUCCESS);
                    if (StringUtils.isNotBlank(response.getOutput())) {
                        patchResult.setOutput(response.getOutput());
                    }
                } else {
                    patchResult.setStatus(PatchStatus.ERROR);
                    patchResult.setOutput(response.getExceptionStackTrace());
                }
            } else {
                LOG.error("Groovy Console is not installed.");
                patchResult.setStatus(PatchStatus.ERROR);
                patchResult.setOutput("Groovy Console is not installed.");
            }
        } catch (Exception e) {
            LOG.error("Could not execute script", e);
            patchResult.setStatus(PatchStatus.ERROR);
            patchResult.setOutput("Script Execution error, check log files");
        }

        patchResultRepository.updateResult(patchResult);

        return patchResult;
    }

    public List<PatchFile> getPatchesToExecute() {
        return patchFileRepository.getPatches()
                                  .stream()
                                  .filter(this::isExecutable)
                                  .collect(Collectors.toList());
    }

    public List<PatchFileWithResultResource> getPatches(final ResourceResolver resourceResolver) {
        return patchFileRepository.getPatches()
                .stream()
                .map(patchFile -> {
                    GroovyPatchResult patchResult = getMatchingPatchResult(patchFile);
                    boolean diff = PatchUtils.isDiff(patchFile, patchResult);
                    return new PatchFileWithResultResource(resourceResolver, patchFile, patchResult, diff);
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPatchSystemReady() {
        return groovyConsoleService != null;
    }

    /**
     * Return the GroovyPatchResult for the provided Patch File.
     * The GroovyPatchResult contains the actual run state of the patch.
     *
     * @param patchFile patch file to get the result from/
     * @return result or null
     */
    private GroovyPatchResult getMatchingPatchResult(GroovyPatchFile patchFile) {
        return patchResultRepository.getResult(patchFile);
    }

    /**
     * Will check if the patch is executable.
     * Patches are only executable if no result is found
     * OR when the source is different from the saved result (=modified scripts).
     *
     * @param patchFile patch file to check
     * @return true in case it's a new or modified script.
     */
    private boolean isExecutable(GroovyPatchFile patchFile) {
        GroovyPatchResult patchResult = getMatchingPatchResult(patchFile);
        if (patchResult == null) {
            return true;
        } else {
            return PatchUtils.isDiff(patchFile, patchResult);
        }
    }

    private Map<String, Object> getCredentials() {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put(ResourceResolverFactory.USER, DEFAULT_USER);
        credentials.put(ResourceResolverFactory.SUBSERVICE, DEFAULT_SERVICE);
        return credentials;
    }

    protected void unbindGroovyConsole() {
        this.groovyConsoleService = null;
    }
}