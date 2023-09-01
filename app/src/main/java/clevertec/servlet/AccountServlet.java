package clevertec.servlet;

import static clevertec.servlet.RequestTemplates.get;
import static clevertec.servlet.RequestTemplates.post;
import static clevertec.servlet.RequestTemplates.put;
import static clevertec.servlet.RequestTemplates.delete;
import static clevertec.util.RequestUtil.getBody;

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
import clevertec.util.Pair;
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
        post(request,
            response,
            (req, resp) -> {
                String body = RequestUtil.getBody(req);
                Account account = fromJson(body);
                accountService.create(account);
            });
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            get(request,
                response,
                Integer::parseInt,
                (id, pair_) -> {
                    Pair<HttpServletRequest, HttpServletResponse> pair = pair_.get();
                    HttpServletResponse resp = pair.second();

                    Account account = accountService.read(id);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    try {
                        resp.getWriter().write(objectMapper.writeValueAsString(account));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void doPut(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        put(request,
            response,
            Integer::parseInt,
            (id, pair_) -> {
                Pair<HttpServletRequest, HttpServletResponse> pair = pair_.get();
                HttpServletRequest req = pair.first();

                String body = getBody(req);
                Account account = fromJson(body);

                account.setId(id);
                accountService.update(account);
            });
    }

    public void doDelete(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        delete(
            request,
            response,
            Integer::parseInt,
            (id, pair_) -> {
                accountService.delete(id);
            });
    }

    private Account fromJson(String json) {
        Account account;
        try {
            account = new Account();
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
