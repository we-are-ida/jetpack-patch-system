package be.ida.jetpack.patchsystem.services;

import com.adobe.granite.ui.components.ds.DataSource;
import org.apache.sling.api.resource.Resource;

import javax.servlet.http.HttpServletRequest;

public interface PatchSystemDataSourceService {

    DataSource getDataSource(HttpServletRequest request, Object ex, Resource resource);
}
