<%@ page import="mba.vm.smart.parking.DataSourceProvider" %>
<%@ page import="static mba.vm.smart.parking.util.ContentFetch.genRedirect" %><%@ page import="java.io.File" %>
<%@ page import="mba.vm.smart.parking.Database" %>
<%@ page import="mba.vm.smart.parking.Car_informationQueries" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="mba.vm.smart.parking.frontend.ui.HTMLElement" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%=genRedirect(new File(request.getRequestURI()).getName().split("\\.")[0])%><style>

    .car--info-modify-dialog-main > * {
        margin-bottom: 15px;
    }
    .car--info-modify-dialog-main:last-child {
        margin-bottom: 0;
    }
</style>
<%
    HashMap<String, String> keyDisplayNameMap = new HashMap<>();
    keyDisplayNameMap.put("license_plate", "车牌");
    keyDisplayNameMap.put("owner_name", "车主姓名");
    keyDisplayNameMap.put("contact_number", "联系电话");
    keyDisplayNameMap.put("brand", "车辆品牌");
    keyDisplayNameMap.put("model", "车辆型号");
    keyDisplayNameMap.put("color", "车辆颜色");
%>
<mdui-dialog
        close-on-esc
        close-on-overlay-click
        class="car--info-modify-dialog"
>
    <div class="car--info-modify-dialog-main">

        <mdui-text-field label="Text Field"></mdui-text-field>
        <mdui-text-field label="Text Field"></mdui-text-field>
        <mdui-text-field label="Text Field"></mdui-text-field>
        <mdui-text-field label="Text Field"></mdui-text-field>
        <mdui-text-field label="Text Field"></mdui-text-field>
    </div>
    <mdui-button slot="action" variant="text">取消</mdui-button>
    <mdui-button slot="action" variant="tonal">保存</mdui-button>
</mdui-dialog>
<div>
    <h1>车辆信息管理</h1>
    <div class="mdui-table">
        <table>
            <thead class="car--info-thead">
            <tr></tr>
            </thead>
            <tbody class="car--info-tbody">
            </tbody>
        </table>
    </div>
</div>
<script
