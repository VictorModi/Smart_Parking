<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="mba.vm.smart.parking.frontend.ui.Navigation" %><%@ page import="static mba.vm.smart.parking.StringContact.TITLE_TEXT" %><%@ page import="static mba.vm.smart.parking.StringContact.API_ROOT" %><%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");%><%@ page import="mba.vm.smart.parking.tool.UserLoginTool" %><%@ page import="mba.vm.smart.parking.GetUserByID" %><%@ page import="kotlin.Pair" %><%@ page import="mba.vm.smart.parking.UserType" %><%@ page import="org.owasp.encoder.Encode" %><%@ page import="jakarta.servlet.http.Cookie" %>
<%@ page import="mba.vm.smart.parking.frontend.ui.HTMLElement" %><!doctype html>
<html lang="cn" class="mdui-theme-auto" xmlns="http://www.w3.org/1999/html" xmlns="">
<head>
    <meta charset="UTF-8">
    <title><%=TITLE_TEXT%></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0,,maximum-scale=1,user-scalable=no">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, shrink-to-fit=no"/>
    <meta name="renderer" content="webkit"/>
    <!-- On Internet -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Round" rel="stylesheet">
    <!-- On Local -->
<%--    <link href="${pageContext.request.contextPath}/static/css/Material-Icons-Round.css" rel="stylesheet">--%>
</head>
<body>
<noscript>
    <h1>您的浏览器不支持 JavaScript，请启用 JavaScript 以获得正常的体验。</h1>
</noscript>
<%
    Pair<GetUserByID, Cookie> USER;
    try {
        USER = UserLoginTool.INSTANCE.getUserByRequest(request);
    } catch (Throwable ex) {
        out.print("无法获取用户信息，这很可能是由于数据库连接失败导致的，请联系系统管理员!!</body>");
        StringBuilder stackTraceBuilder = new StringBuilder();
        stackTraceBuilder.append("\n<!--\n").append(ex.fillInStackTrace()).append("\n\tStackTrace: Ciallo～(∠・ω< )⌒☆\n");
        for (StackTraceElement element : ex.getStackTrace()) {
            stackTraceBuilder.append("\t\t").append(element.toString()).append("\n");
        }
        stackTraceBuilder.append("-->");
        out.print(stackTraceBuilder.toString());
        return;
    }
    final Boolean isLogin = USER != null;
    final String DISPLAY_NAME = USER != null ? Encode.forHtml(USER.getFirst().getDisplay_name()) : "游客";
%>
<div class="progress-bar-container">
    <mdui-linear-progress class="loaded-complete-progress-bar progress-bar"></mdui-linear-progress>
    <mdui-linear-progress class="always-loading-progress-bar progress-bar"></mdui-linear-progress>
</div>
<div class="main">
    <mdui-dialog
            close-on-esc
            close-on-overlay-click
            class="logout-dialog"
            headline="你确定要登出吗?"
            description="未保存的工作可能丢失。"
    >
        <mdui-button slot="action" variant="text" class="logout-confirm">登出</mdui-button>
        <mdui-button slot="action" variant="filled" class="logout-cancel">取消</mdui-button>
    </mdui-dialog>
    <mdui-layout full-height>
        <mdui-top-app-bar>
            <mdui-button-icon class="menu-toggle">
                <mdui-icon-menu--rounded class="menu-is-close" style="display: none;"></mdui-icon-menu--rounded>
                <mdui-icon-menu-open--rounded class="menu-is-open"></mdui-icon-menu-open--rounded>
            </mdui-button-icon>
            <mdui-top-app-bar-title>
                <a href="#" class="title-link"><%=TITLE_TEXT%></a>
            </mdui-top-app-bar-title>
            <div style="flex-grow: 1"></div>
            <div class="app-bar-item">
                <!-- 明明是个 switch 为什么写作 button ? -->
                <mdui-button-icon icon="logout--rounded" class="logout-btn"></mdui-button-icon>
                <theme-button class="theme-switch" value="light" size="1.2"></theme-button>
            </div>
        </mdui-top-app-bar>

        <mdui-navigation-drawer close-on-esc close-on-overlay-click open class="navigation-drawer">
            <mdui-list>
                <mdui-collapse accordion class="collapse">
                <%
                    StringBuilder navigationALL = new StringBuilder();
                    int userType = isLogin ? USER.getFirst().getPermission_level() : -1;
                    Navigation[] navigations = mba.vm.smart.parking.NavigationConstant.getNavigationsByUserType(UserType.getUserTypeByInt(userType));
                    for (Navigation navigation : navigations) {
                        navigationALL.append(navigation.toHTML());
                    }
                %><%=navigationALL.toString()%>
                </mdui-collapse>
            </mdui-list>
        </mdui-navigation-drawer>

        <mdui-layout-main class="layout-main">
            <div class="content-container">
                <main class="content default-page">
                    <%
                        java.util.Calendar now = java.util.Calendar.getInstance();
                        int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                        String greeting = "夜深了, 注意休息!";

                        switch (hour / 6) {
                            case 0:
                                greeting = "凌晨了, 注意休息!";
                                break;
                            case 1:
                                greeting = "早上好!";
                                break;
                            case 2:
                                if (hour < 14) {
                                    greeting = "中午好, 午间注意休息。";
                                } else {
                                    greeting = "下午好, 继续工作吧!";
                                }
                                break;
                            case 3:
                                greeting = "晚上好!";
                                break;
                            default:
                                break;
                        }
                    %>
                    <h1><%=greeting%> <%=DISPLAY_NAME%>！</h1>
                    <p>欢迎进入<%=TITLE_TEXT%>，您可以通过右边的抽屉导航栏查看关于信息<%=!isLogin ? "，更多功能请 <a href=\"#user-login\">登录</a>" : ""%>。</p>
                        <%="http".equalsIgnoreCase(request.getScheme()) ? "<p><strong>强烈建议使用 HTTPS！</strong> 您当前正在使用不安全的 HTTP 协议，建议切换到 HTTPS 以确保数据传输的安全性。<p>" : ""%>
                </main>
                <main class="content content-page">
                    <h1>正在加载...</h1>
                    请稍等...
                </main>
            </div>
        </mdui-layout-main>
    </mdui-layout>
</div>
<jsp:include page="WEB-INF/includes/import.jsp" />
<script>
    alert = undefined;
    prompt = undefined;
    confirm = undefined;
    // console = undefined;
    document.write = undefined;
    document.writeln = undefined;
    eval = function (n) {
        console.log("I don't know what is " + n + ". Can you tell me something about " + n + "?");
    };
    // env?
    window.contact.CONTENT_PATH = "${pageContext.request.contextPath}";
    const API_ROOT = "<%=API_ROOT%>";
    const $ = window.mdui.$;
    const snakeBar = window.mdui.snackbar;
    const sendRequest = window.sendRequest;

    const isLogin = <%=isLogin%>;
    if (!isLogin && window.contact.饼干_我最爱饼干("token")) {
        window.contact.饼干_我最讨厌饼干("token");
        snakeBar({message: "发现登录令牌已过期，即将刷新..."});
        setTimeout(function () {
            window.location.reload();
        }, 1200);
    }

    if (window.contact.disabledNavigation === undefined) window.contact.disabledNavigation = [];
    if (isLogin) window.contact.disabledNavigation.push($(".nav-user-login"))

    const logoutDialog = $('.logout-dialog');
    $(".logout-btn").on("click", function() {
        logoutDialog.each(function (){
            this.open = true;
        })
    });

    $(".logout-cancel").on("click", function() {
        logoutDialog.each(function (){
            this.open = false;
        })
    })

    $(".logout-confirm").on("click", function() {
        if (!isLogin) {
            snakeBar({
                message: "你仍未登录...无法登出..."
            });
            return
        }
        sendRequest('GET', API_ROOT + 'logout', undefined, function () {
            window.contact.饼干_我最讨厌饼干("token");
            snakeBar({message: "登出成功。"});
            setTimeout(function (){
                window.location = "#";
                window.location.reload();
            }, 1200)
        }, function (res) {
            try {
                const data = JSON.parse(res.xhr.response);
                snakeBar({message: "登出失败。是否重复退登? 原因： " + data.message});
            } catch (e) {
                snakeBar({message: "登出时出现未知错误。"})
            }
        }, function () {
            logoutDialog.each(function (){
                this.open = false;
            })
        });
    });

    window.contact.disabledNavigation.forEach(element => {
        element.attr("disabled", '');
    });
</script>
<%if (USER != null) out.print(new HTMLElement("script").setAttribute("src", request.getContextPath() + "/static/js/dataPage.js"));%>
<script src="${pageContext.request.contextPath}/static/js/day-night-toggle-switch/script.js"></script>
</body>
</html>
