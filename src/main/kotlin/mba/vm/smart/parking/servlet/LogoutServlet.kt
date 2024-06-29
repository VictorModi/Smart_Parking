package mba.vm.smart.parking.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mba.vm.smart.parking.DataSourceProvider.getDatabase
import mba.vm.smart.parking.StringContact.API_ROOT
import mba.vm.smart.parking.data.JSONResponse.setJSONResponse
import mba.vm.smart.parking.tool.UserLoginTool.getUserByRequest

// 想法 cookie与ip生成避免乱七八糟
/**
 * SmartParking - mba.vm.smart.parking.servlet
 * @description https://github.com/VictorModi/Smart_Parking
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/5 上午12:23
 */
@WebServlet(API_ROOT + "logout")
class LogoutServlet : HttpServlet() {
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val user = getUserByRequest(request) ?: run {
            setJSONResponse(response, 418, "I'm a teapot, Because you haven't logged in yet. What can I do for you sir?")
            return
        }
        val cookieQueries = getDatabase().cookieQueries
        cookieQueries.setInactiveByToken(user.second.value)
        clearCookieToken(request, response)
        setJSONResponse(response, 200, "Never Gonna Say Goodbye~")
    }

    private fun clearCookieToken(request: HttpServletRequest, response: HttpServletResponse) {
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name.equals("token", ignoreCase = true)) {
                    cookie.value = ""
                    cookie.path = request.contextPath
                    cookie.maxAge = 0
                    response.addCookie(cookie)
                    break
                }
            }
        }
    }
}
