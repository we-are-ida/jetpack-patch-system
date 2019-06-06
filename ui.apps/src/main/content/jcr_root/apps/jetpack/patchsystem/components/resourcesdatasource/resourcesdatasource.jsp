<%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"
          import="be.ida.jetpack.patchsystem.services.PatchSystemDataSourceService"
          import="com.adobe.granite.ui.components.ds.DataSource"%>
<%
    PatchSystemDataSourceService service = (PatchSystemDataSourceService)sling.getService(PatchSystemDataSourceService.class);
    DataSource dataSource = service.getDataSource(request, cmp, resource);
    request.setAttribute(DataSource.class.getName(), dataSource);
%>