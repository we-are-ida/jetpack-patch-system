package be.ida.jetpack.patchsystem.groovy.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class)
public class GroovyPatchFolder {

    @Self
    private Resource resource;

    @ValueMapValue(name = "jcr:title", optional = true)
    private String projectName;

    private String folderName;

    private GroovyPatchFolder parent;

    @PostConstruct
    private void init() {
        if (StringUtils.isBlank(projectName)) {
            this.projectName = resource.getName();
        }

        this.folderName = resource.getName();
    }

    public String getProjectName() {
        if (parent != null) {
            return parent.getProjectName() + " > " + projectName;
        }
        return projectName;
    }

    public void setParent(GroovyPatchFolder parent) {
        this.parent = parent;
    }

    public String getResultPath() {
        StringBuilder sb = new StringBuilder();
        if (parent != null) {
            sb.append(parent.getResultPath()).append("/");
        }
        sb.append(this.folderName);
        return sb.toString();
    }
}
