package woopaca.jspcafe.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import woopaca.jspcafe.resolver.RequestParametersResolver;
import woopaca.jspcafe.service.PostService;
import woopaca.jspcafe.servlet.dto.WritePostRequest;

import java.io.IOException;

@WebServlet("/posts")
public class PostsServlet extends HttpServlet {

    private PostService postService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        this.postService = (PostService) servletContext.getAttribute("postService");
    }

    /**
     * 게시글 작성 HTTP POST 요청을 처리
     * @param request an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WritePostRequest writePostRequest = RequestParametersResolver.resolve(request, WritePostRequest.class);
        String userId = (String) request.getSession()
                .getAttribute("userId");
        postService.writePost(writePostRequest, userId);
        response.sendRedirect("/");
    }
}