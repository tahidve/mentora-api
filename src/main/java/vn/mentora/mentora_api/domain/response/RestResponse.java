package vn.mentora.mentora_api.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message; // có thể là chuỗi hoặc mảng chuỗi lỗi
    private T data;
}
