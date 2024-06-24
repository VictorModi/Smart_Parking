package mba.vm.smart.parking.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mba.vm.smart.parking.StringContact.API_ROOT
import mba.vm.smart.parking.data.JSONResponse.setJSONResponse
import mba.vm.smart.parking.tool.UserLoginTool.getUserByRequest
import mba.vm.smart.parking.tool.UserLoginTool.loginByEmail
import mba.vm.smart.parking.tool.UserLoginTool.loginByUsername
import java.util.*


/**
 * SmartParking - mba.vm.smart.parking.servlet
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/2 上午9:39
 */

@WebServlet(API_ROOT + "login")
class LoginServlet : HttpServlet() {
    override fun doPost(request: HttpServletRequest?, response: HttpServletResponse?) {
        if (request == null || response == null) {
            setJSONResponse(response, 401, "什么毛蛋")
            return
        }

        getUserByRequest(request)?.let {
            setJSONResponse(response, 425, "为什么你一定要重复登陆呢。")
            return
        }

        val username = request.getParameter("username")
        val email = request.getParameter("email")
        val password = request.getParameter("password")

        if (password.isNullOrEmpty() || (username.isNullOrEmpty() && email.isNullOrEmpty()) || (!username.isNullOrEmpty() && !email.isNullOrEmpty())) {
            setJSONResponse(response, 400, "你在干什么?")
            return
        }

        val decodedPassword = String(Base64.getDecoder().decode(password))
        if (!decodedPassword.startsWith("\$argon2id$")) {
            setJSONResponse(response, 403, "你传来的根本不是密码，你要干什么。")
            return
        }

        val cookieValue = when {
            username.isNullOrEmpty() -> loginByEmail(email, decodedPassword)
            email.isNullOrEmpty() -> loginByUsername(username, decodedPassword)
            else -> {
                setJSONResponse(response, 400, "你是怎么走到这一步的")
                return
            }
        }

        if (cookieValue.isNotEmpty()) {
            val cookie = Cookie("token", cookieValue).apply {
                path = request.contextPath
                maxAge = 60 * 60 * 24 * 3
            }
            response.addCookie(cookie)
            setJSONResponse(response, 200, "登陆成功!")
            return
        }

        setJSONResponse(response, 403, "登录失败! 请检查用户名或邮箱和密码是否正确!")
    }

    override fun doGet(request: HttpServletRequest?, response: HttpServletResponse?) {
        setJSONResponse(response, 405, "Method Not Allowed")
    }
}
