package clevertec.servlet;

import static clevertec.servlet.RequestTemplates.delete;
import static clevertec.servlet.RequestTemplates.get;
import static clevertec.servlet.RequestTemplates.post;
import static clevertec.servlet.RequestTemplates.put;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import clevertec.bank.Bank;
import clevertec.service.BankService;
import clevertec.util.ObjectMapperUtil;
import clevertec.util.Pair;
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
        post(
            request,
            response,
            (req, resp) -> {
                String body = RequestUtil.getBody(req);
                Bank bank;
                try {
                    bank = objectMapper.readValue(body, Bank.class);
                    resp.getWriter().write(bankService.create(bank));
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

                Bank bank = bankService.read(id);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try {
                    resp.getWriter().write(objectMapper.writeValueAsString(bank));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public void doPut(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        put(
            request,
            response,
            Integer::parseInt,
            (id, pair_) -> {
                Pair<HttpServletRequest, HttpServletResponse> pair = pair_.get();
                HttpServletRequest req = pair.first();

                String body = RequestUtil.getBody(req);
                Bank bank;
                try {
                    bank = objectMapper.readValue(body, Bank.class);
                    bank.setId(id);
                    bankService.update(bank);
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
                    bankService.delete(id);
                });
    }
}
