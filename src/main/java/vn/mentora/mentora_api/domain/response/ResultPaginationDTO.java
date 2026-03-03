package vn.mentora.mentora_api.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int page; // Trang hiện tại đang xem
        private int pageSize; // Số phần tử trên mỗi trang
        private int pages; // Tổng số trang
        private long total; // Tổng số bản ghi trong database
    }
}
