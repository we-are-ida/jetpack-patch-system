package be.ida.jetpack.patchsystem.models;

import com.day.cq.commons.date.RelativeTimeFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Calendar;

@Model(adaptables = SlingHttpServletRequest.class)
public class PatchFileWithResult {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    @ValueMapValue(name="projectName", optional = true)
    private String project;

    @ValueMapValue(name="scriptName")
    private String script;

    @ValueMapValue(name="type")
    private String type;

    @ValueMapValue(name="runnable")
    private boolean runnable;

    @ValueMapValue(name="status", optional = true)
    private String status;

    @ValueMapValue(name="status", optional = true)
    private PatchStatus status2;

    @ValueMapValue(name="startDate", optional = true)
    private Calendar startDate;

    @ValueMapValue(name="endDate", optional = true)
    private Calendar endDate;

    @ValueMapValue(name="output", optional = true)
    private String output;

    @ValueMapValue(name="runningTime", optional = true)
    private String runningTime;

    private String statusClass;
    private String dateExecuted;
    private String path;

    @PostConstruct
    private void init() {
        path = resource.getPath();

        if (StringUtils.isNotBlank(status)) {
            statusClass = "info";
            PatchStatus patchStatus = PatchStatus.getStatus(status);
            if (patchStatus != null) {
                statusClass = patchStatus.cssClass();
            }
        }

        dateExecuted = formatDateRDF(endDate, null);
    }

    public String getThumbnail() {
        return request.getContextPath() + getThumbnailUrl();
    }

    private String formatDateRDF(Calendar cal, String defaultValue) {
        if (cal == null) {
            return defaultValue;
        }
        RelativeTimeFormat rtf = new RelativeTimeFormat("r");
        return rtf.format(cal.getTimeInMillis(), true);
    }

    private String getThumbnailUrl() {
        if ("onDeployScript".equals(this.type)) {
            return "/apps/jetpack/patchsystem/components/thumb-ondeployscript.png"  ;
        }
        return "/apps/jetpack/patchsystem/components/thumb-groovy.png";
    }

    public String getProject() {
        return project;
    }

    public String getScript() {
        return script;
    }

    public String getStatus() {
        return status;
    }

    public String getDateExecuted() {
        return dateExecuted;
    }

    public String getPath() {
        return path;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public String getOutput() {
        return output;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public boolean isRunnable() {
        return runnable;
    }

    public String getType() {
        return type;
    }
}
