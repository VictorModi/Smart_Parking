package mba.vm.smart.parking.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mba.vm.smart.parking.data.JSONResponse.setJSONResponse
import mba.vm.smart.parking.tool.RequestTool.getBodyObject


/**
 * SmartParking - mba.vm.smart.parking.servlet
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/6 下午8:40
 */

@WebServlet("/error")
class ErrorServlet : HttpServlet() {

    override fun service(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if (req == null || resp == null || resp.status == 200) {
            setJSONResponse(resp, 418, "I'm a teapot")
            return
        }
        setJSONResponse(resp, resp.status, "¯\\_(ツ)_/¯", req.getBodyObject<Map<String, Any>>())
    }
}
