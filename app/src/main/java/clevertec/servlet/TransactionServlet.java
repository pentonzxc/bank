package clevertec.servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import clevertec.Account;
import clevertec.service.AccountService;
import clevertec.service.TransactionService;
import clevertec.transaction.check.TransactionCheck;
import clevertec.transaction.check.TransactionDescription;
import clevertec.util.ObjectMapperUtil;
import clevertec.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TransactionServlet {
    ObjectMapper objectMapper = ObjectMapperUtil.get();

    AccountService accountService = new AccountService();

    TransactionService transactionService = new TransactionService(accountService);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() != null) {
            response.sendError(404);
        } else {
            String body = RequestUtil.getBody(request);
            TransactionCheck transactionCheck = fromJson(body);
            transactionService.create(transactionCheck);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UUID id = null;
        boolean error = false;
        try {
            id = UUID.fromString(request.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            response.sendError(404);
        } else {
            TransactionCheck transactionCheck = transactionService.read(id);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(transactionCheck));
        }
    }

    public void doPut(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        UUID id = null;
        boolean error = false;
        try {
            id = UUID.fromString(request.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            response.sendError(404);
        } else {
            String body = RequestUtil.getBody(request);
            TransactionCheck transactionCheck = fromJson(body);

            transactionCheck.setId(id);
            transactionService.update(transactionCheck);
        }
    }

    public void doDelete(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        UUID id = null;
        boolean error = false;
        try {
            id = UUID.fromString(request.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            response.sendError(404);
        } else {
            transactionService.delete(id);
        }
    }

    public TransactionCheck fromJson(String json) {
        TransactionCheck check = null;
        try {
            check = new TransactionCheck();
            JsonNode jsonNode = objectMapper.readTree(json);
            UUID id = Optional.ofNullable(jsonNode.get("id"))
                    .map(JsonNode::asText)
                    .map(UUID::fromString)
                    .orElse(null);
            LocalDateTime createdAt = Optional.ofNullable(jsonNode.get("created_at"))
                    .map(node -> objectMapper.convertValue(node, LocalDateTime.class))
                    .orElse(null);
            TransactionDescription description = Optional.ofNullable(
                    jsonNode.get("description"))
                    .map(JsonNode::asText)
                    .flatMap(TransactionDescription::fromDescription)
                    .orElse(null);
            Account origin = Optional.ofNullable(jsonNode.get("account_origin_id"))
                    .map(JsonNode::asInt)
                    .map(accountService::read)
                    .orElse(null);

            Account target = Optional.ofNullable(jsonNode.get("account_target_id"))
                    .map(JsonNode::asInt)
                    .map(accountService::read)
                    .orElse(null);
            Double transferAmount = Optional.ofNullable(jsonNode.get("transfer_amount"))
                    .map(JsonNode::asDouble)
                    .orElse(null);

            check.setId(id);
            check.setCreatedAt(createdAt);
            check.setDescription(description);
            check.setOrigin(origin);
            check.setTarget(target);
            check.setTransferAmount(transferAmount);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return check;
    }
}
