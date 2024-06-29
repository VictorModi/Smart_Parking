package mba.vm.smart.parking.data;

import com.google.common.base.CaseFormat;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import mba.vm.smart.parking.data.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * SmartParking - mba.vm.smart.parking.data
 *
 * @author VictorModi
 * @description https://github.com/VictorModi/Smart_Parking
 * @email victormodi@outlook.com
 * @date 2024/6/2 上午1:34
 */

/**
 * JSONResponse类，表示一个标准的JSON响应对象。
 */
@Data
@NoArgsConstructor
public class JSONResponse {
    private int statusCode;
    private String message;
    private Object data;

    /**
     * 带状态码和消息的构造函数。
     * @param statusCode 状态码。
     * @param message 消息。
     */
    public JSONResponse(int statusCode, String message) {
        this(statusCode, message, null);
    }

    /**
     * 带状态码、消息和数据的构造函数。
     * @param statusCode 状态码。
     * @param message 消息。
     * @param data 数据对象。
     */
    public JSONResponse(int statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data != null ? data : new Object();
    }

    /**
     * 将JSONResponse对象转换为JSON字符串。
     * @return JSON字符串。
     */
    public String toJSON() {
        return new GsonBuilder()
                .setFieldNamingStrategy(field ->
                        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName())
                )
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create()
                .toJson(this);
    }

    /**
     * 设置HTTP响应的JSON内容。
     * @param response HTTP响应对象。
     * @param statusCode 状态码。
     * @param message 消息。
     * @param data 数据对象。
     * @return 修改后的HTTP响应对象。
     */
    public static HttpServletResponse setJSONResponse(HttpServletResponse response, int statusCode, String message, Object data) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(statusCode);
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.print(new JSONResponse(statusCode, message, data).toJSON());
        out.close();
        return response;
    }

    /**
     * 设置HTTP响应的JSON内容（无数据）。
     * @param response HTTP响应对象。
     * @param statusCode 状态码。
     * @param message 消息。
     * @return 修改后的HTTP响应对象。
     */
    public static HttpServletResponse setJSONResponse(HttpServletResponse response, int statusCode, String message) {
        return setJSONResponse(response, statusCode, message, null);
    }

    /**
     * 创建JSON字符串。
     * @param statusCode 状态码。
     * @param message 消息。
     * @param data 数据对象。
     * @return JSON字符串。
     */
    public static String createJSON(int statusCode, String message, Object data) {
        return new JSONResponse(statusCode, message, data).toJSON();
    }

    /**
     * 创建JSON字符串（无数据）。
     * @param statusCode 状态码。
     * @param message 消息。
     * @return JSON字符串。
     */
    public static String createJSON(int statusCode, String message) {
        return createJSON(statusCode, message, null);
    }
}
