package clevertec.servlet;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import clevertec.Bank;
import clevertec.User;
import clevertec.service.UserService;
import clevertec.util.ObjectMapperUtil;
import clevertec.util.RequestUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "user_servlet", value = "/user/*")
public class UserServlet extends HttpServlet {
    ObjectMapper objectMapper = ObjectMapperUtil.get();

    UserService userService = new UserService();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() != null) {
            response.sendError(404);
        } else {
            String body = RequestUtil.getBody(request);
            User user = objectMapper.readValue(body, User.class);
            userService.create(user);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = null;
        boolean error = false;
        try {
            id = Integer.parseInt(request.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            response.sendError(404);
        } else {
            User user = userService.read(id);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(user));
        }
    }

    public void doPut(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Integer id = null;
        boolean error = false;
        try {
            id = Integer.parseInt(request.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            response.sendError(404);
        } else {
            String body = RequestUtil.getBody(request);
            User user = objectMapper.readValue(body, User.class);

            user.setId(id);
            userService.update(user);
        }
    }

    public void doDelete(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Integer id = null;
        boolean error = false;
        try {
            id = Integer.parseInt(request.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            response.sendError(404);
        } else {
            userService.delete(id);
        }
    }
}
