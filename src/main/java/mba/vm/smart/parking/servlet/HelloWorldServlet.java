package mba.vm.smart.parking.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * SmartParking - mba.vm.smart.parking.servlet
 *
 * @author VictorModi
 * @description https://github.com/VictorModi/Smart_Parking
 * @email victormodi@outlook.com
 * @date 2024/5/31 上午9:08
 */
@WebServlet(name = "HelloWorld", value = {"/helloworld"})
public class HelloWorldServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Hello World!!</h1>");
    }
}
