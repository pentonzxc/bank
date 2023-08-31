package clevertec.servlet;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import clevertec.Bank;
import clevertec.User;
import clevertec.service.BankService;
import clevertec.service.UserService;
import clevertec.util.ObjectMapperUtil;
import clevertec.util.RequestUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "bank_servlet", value = "/bank/*")
public class BankServlet extends HttpServlet {
    ObjectMapper objectMapper = ObjectMapperUtil.get();

    BankService bankService = new BankService();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() != null) {
            response.sendError(404);
        } else {
            String body = RequestUtil.getBody(request);
            Bank bank = objectMapper.readValue(body, Bank.class);
            bankService.create(bank);
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
            Bank bank = bankService.read(id);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(bank));
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
            Bank bank = objectMapper.readValue(body, Bank.class);

            bank.setId(id);
            bankService.update(bank);
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
            bankService.delete(id);
        }
    }
}
