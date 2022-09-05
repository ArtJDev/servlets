package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private final String FULL_PATH = "/api/posts";
    private final String POST_PATH = "/api/posts/\\d+";
    private final String METHOD_GET = "GET";
    private final String METHOD_POST = "POST";
    private final String METHOD_DELETE = "DELETE";
    final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);
    final PostController controller = context.getBean(PostController.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            if (method.equals(METHOD_GET) && path.equals(FULL_PATH)) {
                controller.all(resp);
                return;
            }
            if (method.equals(METHOD_GET) && path.matches(POST_PATH)) {
                final long id = GetId(path);
                controller.getById(id, resp);
                return;
            }
            if (method.equals(METHOD_POST) && path.equals(FULL_PATH)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals(METHOD_DELETE) && path.matches(POST_PATH)) {
                final var id = GetId(path);
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected Long GetId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}