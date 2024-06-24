package mba.vm.smart.parking.tool

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import jakarta.servlet.http.HttpServletRequest
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * SmartParking - mba.vm.smart.parking.tool
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/20 下午7:34
 */

/**
 * RequestTool对象，提供从HttpServletRequest中读取并解析请求体的方法。
 */
object RequestTool {

    /**
     * 从HttpServletRequest中读取请求体并将其解析为指定类型的对象。
     * @return 如果解析成功，则返回指定类型的对象，否则返回null。
     */
    inline fun <reified T> HttpServletRequest.getBodyObject(): T? {
        val stringBuilder = StringBuilder()
        var bufferedReader: BufferedReader? = null

        return try {
            val inputStream = this.inputStream
            if (inputStream != null) {
                bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val charBuffer = CharArray(128)
                var bytesRead: Int
                while (bufferedReader.read(charBuffer).also { bytesRead = it } != -1) {
                    stringBuilder.appendRange(charBuffer, 0, bytesRead)
                }
            }
            val body = stringBuilder.toString()
            try {
                Gson().fromJson(body, T::class.java)
            } catch (ex: JsonSyntaxException) {
                null
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        } finally {
            try {
                bufferedReader?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
