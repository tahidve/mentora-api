package vn.mentora.mentora_api.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.mentora.mentora_api.domain.response.RestResponse;
import vn.mentora.mentora_api.util.annotation.ApiMessage;

// Đăng ký class vào Spring container
// Advice -> báo cho Spring, class này sẽ can thiệp toàn cục vào tất cả Controller
@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    /*
     * Hàm dùng để trả lời cho câu hỏi:
     * - Có muốn xử lý response này hay không?
     */
    @Override
    public boolean supports(MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, // Controller trả về Object gì
            MethodParameter returnType, // metadata của method đang được gọi
            MediaType selectedContentType, // Kiểu content gì (json, xml, ...)
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, // thông tin http request gửi đến
            ServerHttpResponse response) { // thông tin http response gửi đi

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        // Skip: String response (StringHttpMessageConverter sẽ lỗi nếu wrap)
        if (body instanceof String) {
            return body;
        }

        // Skip: File download
        // if (body instanceof Resource) {
        // return body;
        // }

        // Skip: Response đã là RestResponse (từ GlobalException)
        if (body instanceof RestResponse) {
            return body;
        }

        // 4xx, 5xx → đã được GlobalException xử lý, trả nguyên
        if (status >= 400) {
            return body;
        }

        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(status);

        String message = "Call API success";
        ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);

        if (apiMessage != null) {
            message = apiMessage.value();
        }

        restResponse.setMessage(message);
        restResponse.setData(body);
        return restResponse;
    }

}
