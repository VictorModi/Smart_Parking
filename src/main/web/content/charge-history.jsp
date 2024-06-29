<%@ page import="mba.vm.smart.parking.data.DataRequest" %>
<%@ page import="java.io.File" %>
<%@ page import="mba.vm.smart.parking.frontend.DataPageBuilder" %>
<%@ page import="mba.vm.smart.parking.frontend.ui.HTMLElement" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    HashMap<String, String> keyDisplayNameMap = new HashMap<>();
    keyDisplayNameMap.put("license_plate", "车牌");
    keyDisplayNameMap.put("owner_name", "车主姓名");
    keyDisplayNameMap.put("contact_number", "联系电话");
    keyDisplayNameMap.put("brand", "车辆品牌");
    keyDisplayNameMap.put("model", "车辆型号");
    keyDisplayNameMap.put("color", "车辆颜色");

    DataPageBuilder builder = new DataPageBuilder(
            request,
            new File(request.getRequestURI()).getName().split("\\.")[0],
            "车辆信息管理",
            DataRequest.DataType.CHARGE_HISTORY,
            keyDisplayNameMap,
            true,
            null,
            null
    );
%>
<%=builder.toHTML()%>
<!--<%=builder%>-->
