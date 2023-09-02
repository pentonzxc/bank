package clevertec.servlet;

import static clevertec.servlet.RequestTemplates.delete;
import static clevertec.servlet.RequestTemplates.get;
import static clevertec.servlet.RequestTemplates.post;
import static clevertec.servlet.RequestTemplates.put;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import clevertec.service.UserService;
import clevertec.user.User;
import clevertec.util.ObjectMapperUtil;
import clevertec.util.Pair;
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

        post(
                request,
                response,
                (req, resp) -> {
                    String body = RequestUtil.getBody(req);
                    User user;
                    try {
                        user = objectMapper.readValue(body, User.class);
                        resp.getWriter().write(userService.create(user));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        get(
                request,
                response,
                Integer::parseInt,
                (id, pair_) -> {
                    Pair<HttpServletRequest, HttpServletResponse> pair = pair_.get();
                    HttpServletResponse resp = pair.second();

                    User user = userService.read(id);

                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    try {
                        resp.getWriter().write(objectMapper.writeValueAsString(user));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        put(
                request,
                response,
                Integer::parseInt,
                (id, pair_) -> {
                    Pair<HttpServletRequest, HttpServletResponse> pair = pair_.get();
                    HttpServletRequest req = pair.first();

                    String body = RequestUtil.getBody(req);
                    User user;
                    try {
                        user = objectMapper.readValue(body, User.class);
                        user.setId(id);
                        userService.update(user);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void doDelete(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        delete(
                request,
                response,
                Integer::parseInt,
                (id, pair_) -> {
                    userService.delete(id);
                });
    }
}
