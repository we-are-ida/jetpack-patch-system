package be.ida.jetpack.patchsystem.groovy.repositories.impl;

import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFolder;
import be.ida.jetpack.patchsystem.groovy.repositories.GroovyPatchFileRepository;
import com.day.crx.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(
        name = "Jetpack - Groovy Patch File Repository",
        service = GroovyPatchFileRepository.class
)
public class GroovyPatchFileRepositoryImpl implements GroovyPatchFileRepository {
    private final static Logger LOG = LoggerFactory.getLogger(GroovyPatchFileRepositoryImpl.class);

    private static final String ROOT = "/apps/patches";

    private static final String DEFAULT_USER = "jetpack-patch-system";
    private static final String DEFAULT_SERVICE = "be.ida.jetpack.patch-system.core";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public GroovyPatchFile getPatch(String path)  {
        GroovyPatchFile patchFile = null;

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(getCredentials())) {
            Resource resource = resourceResolver.getResource(path);
            if (resource != null) {
                patchFile = resource.adaptTo(GroovyPatchFile.class);
                if (patchFile != null) {
                    GroovyPatchFolder patchFolder = getPatchFolder(resource.getParent());
                    if (patchFolder != null) {
                        patchFile.setParentFolder(patchFolder);
                    }
                }
            }
        } catch (LoginException e) {
            LOG.error("Couldn't login to get GroovyPatchFile", e);
        }

        return patchFile;
    }

    private GroovyPatchFolder getPatchFolder(Resource resource) {
        GroovyPatchFolder patchFolder = resource.adaptTo(GroovyPatchFolder.class);

        if (resource.getPath().equals(ROOT)) {
            return null;
        }

        if (resource.getParent() != null && !resource.getParent().getPath().equals(ROOT)) {
            GroovyPatchFolder parentPatchFolder = getPatchFolder(resource.getParent());
            if (parentPatchFolder != null) {
                patchFolder.setParent(parentPatchFolder);
            }
        }
        return patchFolder;
    }

    @Override
    public List<GroovyPatchFile> getPatches() {
        List<GroovyPatchFile> patches = new ArrayList<>();

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(getCredentials())) {
            Resource root = resourceResolver.getResource(ROOT);
            if (root != null) {
                patches = scanFolderForPatches(root, null);
            }
        } catch (LoginException e) {
            LOG.error("Couldn't login to get GroovyPatchFile", e);
        }

        return patches;
    }

    private List<GroovyPatchFile> scanFolderForPatches(Resource resource, GroovyPatchFolder parent) {
        List<GroovyPatchFile> patchFiles = new ArrayList<>();

        Iterable<Resource> subResources = resource.getChildren();
        for (Resource subResource : subResources) {
            if (subResource.getResourceType().equals(JcrConstants.NT_FOLDER)
                    || subResource.getResourceType().equals("sling:Folder")
                    || subResource.getResourceType().equals("sling:OrderedFolder")) {
                GroovyPatchFolder patchFolder = subResource.adaptTo(GroovyPatchFolder.class);
                if (patchFolder != null) {
                    patchFolder.setParent(parent);
                    patchFiles.addAll(scanFolderForPatches(subResource, patchFolder));
                }
            } else if (subResource.getResourceType().equals(JcrConstants.NT_FILE)
                    && subResource.getName().endsWith(".groovy")) {
                GroovyPatchFile patchFile = subResource.adaptTo(GroovyPatchFile.class);
                if (patchFile != null) {
                    patchFile.setParentFolder(parent);
                    patchFiles.add(patchFile);
                }
            }
        }

        return patchFiles;
    }

    private Map<String, Object> getCredentials() {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put(ResourceResolverFactory.USER, DEFAULT_USER);
        credentials.put(ResourceResolverFactory.SUBSERVICE, DEFAULT_SERVICE);
        return credentials;
    }
}
