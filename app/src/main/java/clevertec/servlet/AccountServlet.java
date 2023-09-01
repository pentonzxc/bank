package clevertec.servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import clevertec.account.Account;
import clevertec.bank.Bank;
import clevertec.service.AccountService;
import clevertec.service.BankService;
import clevertec.service.UserService;
import clevertec.user.User;
import clevertec.util.ObjectMapperUtil;
import clevertec.util.RequestUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "account_servlet", value = "/account/*")
public class AccountServlet extends HttpServlet {
    ObjectMapper objectMapper = ObjectMapperUtil.get();

    BankService bankService = new BankService();

    UserService userService = new UserService();

    AccountService accountService = new AccountService(bankService, userService);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() != null) {
            response.sendError(404);
        } else {
            String body = RequestUtil.getBody(request);
            Account account = fromJson(body);
            accountService.create(account);
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
            Account account = accountService.read(id);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(account));
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
            Account account = fromJson(body);

            account.setId(id);
            accountService.update(account);
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
            accountService.delete(id);
        }
    }

    private Account fromJson(String json) {
        Account account;
        try {
            account      = new Account();
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer id = Optional.ofNullable(jsonNode.get("id"))
                    .map(JsonNode::asInt)
                    .orElse(null);
            String accountNumber = Optional.ofNullable(jsonNode.get("account_number"))
                    .map(JsonNode::asText)
                    .orElse(null);
            String currency = Optional.ofNullable(jsonNode.get("currency"))
                    .map(JsonNode::asText)
                    .orElse(null);
            Double balance = Optional.ofNullable(jsonNode.get("balance"))
                    .map(JsonNode::asDouble)
                    .orElse(null);
            LocalDateTime openingDate = Optional.ofNullable(jsonNode.get("opening_date"))
                    .map(node -> objectMapper.convertValue(node, LocalDateTime.class))
                    .orElse(null);
            Bank bank = Optional.ofNullable(jsonNode.get("bank_id"))
                    .map(JsonNode::asInt)
                    .map(bankService::read)
                    .orElse(new Bank());
            User user = Optional.ofNullable(jsonNode.get("user_id"))
                    .map(JsonNode::asInt)
                    .map(userService::read)
                    .orElse(new User());

            account.setAccountNumber(accountNumber);
            account.setId(id);
            account.setBalance(balance);
            account.setOpeningDate(openingDate);
            account.setCurrency(currency);
            account.setBank(bank);
            account.setUser(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return account;
    }
}
