package be.ida.jetpack.patchsystem.models;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.Map;

public class PatchFileWithResultResource extends SyntheticResource {

    private PatchFile patchFile;
    private PatchResult patchResult;
    private boolean modified;

    public PatchFileWithResultResource(ResourceResolver resourceResolver, PatchFile patchFile, PatchResult patchResult, boolean modified) {
        super(resourceResolver, patchFile.getPath(), null);

        this.patchFile = patchFile;
        this.patchResult = patchResult;
        this.modified = modified;
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if (type.getName().equals(ValueMap.class.getName())) {
            return (AdapterType) createValueMap();
        }
        return super.adaptTo(type);
    }

    private ValueMap createValueMap() {
        final Map<String, Object> valueMap = new HashMap<>();
        final Map<String, Object> originalValueMap = super.adaptTo(ValueMap.class);
        if (originalValueMap != null) {
            valueMap.putAll(originalValueMap);
        }
        Map<String, Object> propertiesMap = createPropertiesMap();
        if (propertiesMap != null) {
            valueMap.putAll(propertiesMap);
        }
        return new ValueMapDecorator(valueMap);
    }

    private Map<String, Object> createPropertiesMap() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("projectName", patchFile.getProjectName());
        properties.put("scriptName", patchFile.getScriptName());

        if (patchResult != null) {
            properties.put("status", patchResult.getStatus());
            properties.put("startDate", patchResult.getStartDate());
            properties.put("endDate", patchResult.getEndDate());
            properties.put("output", patchResult.getOutput());
            properties.put("runningTime", patchResult.getRunningTime());

            if (modified) {
                properties.put("status", "RE-RUN");
            }
        } else {
            properties.put("status", "NEW");
        }

        return properties;
    }
}
