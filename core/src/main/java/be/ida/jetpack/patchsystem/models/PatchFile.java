package be.ida.jetpack.patchsystem.models;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class)
public class PatchFile {

    @Self
    private Resource resource;

    private String path;

    private String scriptName;
    private String fileContent;
    private String md5;

    private PatchFolder parentFolder;

    @PostConstruct
    private void init() {
        this.path = resource.getPath();

        this.scriptName = resource.getName();

        Resource scriptResource = resource.getChild("jcr:content");
        this.fileContent = scriptResource.getValueMap().get("jcr:data", String.class);

        this.md5 = DigestUtils.md5Hex(this.fileContent);
    }

    public String getProjectName() {
        if (parentFolder != null) {
            return parentFolder.getProjectName();
        }
        return null;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public String getMd5() {
        return md5;
    }

    public String getPath() {
        return path;
    }

    public void setParentFolder(PatchFolder parentFolder) {
        this.parentFolder = parentFolder;
    }

    public String getResultPath() {
        StringBuilder sb = new StringBuilder();
        if (parentFolder != null) {
            sb.append(parentFolder.getResultPath()).append("/");
        }
        sb.append(this.scriptName);
        return sb.toString();
    }
}
