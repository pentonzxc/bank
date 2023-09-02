package clevertec.servlet;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import clevertec.util.BiSupplier;
import clevertec.util.Pair;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestTemplates {
    private RequestTemplates() {
    }

    public static void post(HttpServletRequest req, HttpServletResponse resp,
            BiConsumer<HttpServletRequest, HttpServletResponse> f) throws IOException {
        if (req.getPathInfo() != null) {
            resp.sendError(404);
        } else {
            f.accept(req, resp);
        }
    }

    public static <ID> void get(
            HttpServletRequest req,
            HttpServletResponse resp,
            Function<String, ID> fId,
            BiConsumer<ID, BiSupplier<HttpServletRequest, HttpServletResponse>> f) throws IOException {
        ID id = null;
        boolean error = false;
        try {
            id = fId.apply(req.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            resp.sendError(404);
        } else {
            f.accept(id, () -> Pair.of(req, resp));
        }
    }

    public static <ID> void put(
            HttpServletRequest req,
            HttpServletResponse resp,
            Function<String, ID> fId,
            BiConsumer<ID, BiSupplier<HttpServletRequest, HttpServletResponse>> f) throws IOException {
        ID id = null;
        boolean error = false;
        try {
            id = fId.apply(req.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            resp.sendError(404);
        } else {
            f.accept(id, () -> Pair.of(req, resp));
        }
    }

    public static <ID> void delete(HttpServletRequest req,
            HttpServletResponse resp,
            Function<String, ID> fId,
            BiConsumer<ID, BiSupplier<HttpServletRequest, HttpServletResponse>> f) throws IOException {
        ID id = null;
        boolean error = false;
        try {
            id = fId.apply(req.getPathInfo().substring(1));
        } catch (Exception ex) {
            error = true;
        }
        if (error) {
            resp.sendError(404);
        } else {
            f.accept(id, () -> Pair.of(req, resp));
        }
    }

}
