package mba.vm.smart.parking.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mba.vm.smart.parking.StringContact.API_ROOT
import mba.vm.smart.parking.data.JSONResponse.setJSONResponse
import mba.vm.smart.parking.tool.UserLoginTool.getUserByRequest

/**
 * SmartParking - mba.vm.smart.parking.servlet
 * @description https://github.com/VictorModi/Smart_Parking
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/5 上午12:12
 */
@WebServlet(API_ROOT + "info")
class InfoServlet : HttpServlet() {
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val user = getUserByRequest(request)
        if (user == null) {
            setJSONResponse(response, 403, "尚未登录")
            return
        }
        setJSONResponse(response, 200, "", user.first)
    }
}
