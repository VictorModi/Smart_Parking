package mba.vm.smart.parking.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mba.vm.smart.parking.StringContact.API_ROOT
import mba.vm.smart.parking.UserType
import mba.vm.smart.parking.data.DataRequest
import mba.vm.smart.parking.data.DataRequest.Companion.handlerMap
import mba.vm.smart.parking.data.JSONResponse.setJSONResponse
import mba.vm.smart.parking.tool.RequestTool.getBodyObject
import mba.vm.smart.parking.tool.UserLoginTool.getUserByRequest
import java.sql.SQLException

/**
 * SmartParking - mba.vm.smart.parking.servlet
 * @description https://github.com/VictorModi/Smart_Parking
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/5 上午12:24
 */
@WebServlet(API_ROOT + "data")
class DataServlet : HttpServlet() {
    override fun doPost(request: HttpServletRequest?, response: HttpServletResponse?) {
        request ?: run {
            setJSONResponse(response, 403, "Access Denied")
            return
        }
        val userInfo = getUserByRequest(request) ?: run {
            setJSONResponse(response, 403, "Access Denied")
            return
        }

        val dataRequest = request.getBodyObject<Map<String, Any>>()?.let { requestBody ->
            val result = DataRequest.fromMap(requestBody)
            if (result.isFailure) {
                setJSONResponse(response, 403, result.exceptionOrNull()?.message ?: "Unknown error")
                return
            }
            result.getOrNull()
        } ?: run {
            setJSONResponse(response, 401, "dataRequest is Null, is request body a valid JSON?")
            return
        }

        try {
            val result = handlerMap[dataRequest.dataType]?.handle(
                UserType.getUserTypeByInt(userInfo.first.permission_level),
                dataRequest.actionType,
                dataRequest.data
            )
            if (result == null) {
                setJSONResponse(response, 401, "Result is Null")
                return
            }
            setJSONResponse(
                response,
                if (result.success) 200 else 400,
                if (result.success) "" else result.message,
                result.toMap()
            )
            return
        } catch (e: SQLException) {
            setJSONResponse(response, 400, e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            setJSONResponse(response, 400, "Unknown Error")
        }
    }

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        setJSONResponse(resp, 405, "Method Not Allowed")
    }

}


