package be.ida.jetpack.patchsystem.groovy.models;

import be.ida.jetpack.patchsystem.models.PatchFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class)
public class GroovyPatchFile implements PatchFile {

    @Self
    private Resource resource;

    private String path;

    private String scriptName;
    private String fileContent;
    private String md5;

    private GroovyPatchFolder parentFolder;

    @PostConstruct
    private void init() {
        this.path = resource.getPath();

        this.scriptName = resource.getName();

        Resource scriptResource = resource.getChild("jcr:content");
        this.fileContent = scriptResource.getValueMap().get("jcr:data", String.class);

        this.md5 = DigestUtils.md5Hex(this.fileContent);
    }

    @Override
    public String getProjectName() {
        if (parentFolder != null) {
            return parentFolder.getProjectName();
        }
        return null;
    }

    @Override
    public String getScriptName() {
        return scriptName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public String getMd5() {
        return md5;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setParentFolder(GroovyPatchFolder parentFolder) {
        this.parentFolder = parentFolder;
    }

    @Override
    public String getResultPath() {
        StringBuilder sb = new StringBuilder();
        if (parentFolder != null) {
            sb.append(parentFolder.getResultPath()).append("/");
        }
        sb.append(this.scriptName);
        return sb.toString();
    }

    @Override
    public boolean isRunnable() {
        return true;
    }

    @Override
    public String getType() {
        return "groovy";
    }
}
