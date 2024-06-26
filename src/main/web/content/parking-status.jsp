<%@ page import="mba.vm.smart.parking.data.DataRequest" %>
<%@ page import="java.io.File" %>
<%@ page import="mba.vm.smart.parking.frontend.DataPageBuilder" %>
<%@ page import="mba.vm.smart.parking.frontend.ui.HTMLElement" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    HashMap<String, String> keyDisplayNameMap = new HashMap<>();
    keyDisplayNameMap.put("location", "位置");
    keyDisplayNameMap.put("current_car_id", "当前停放车辆");

    DataPageBuilder builder = new DataPageBuilder(
            request,
            new File(request.getRequestURI()).getName().split("\\.")[0],
            "车辆停放状态管理",
            DataRequest.DataType.PARKING_STATUS,
            keyDisplayNameMap,
            true,
            null,
            null
    );
%>
<%=builder.toHTML()%>
<!--<%=builder%>-->
