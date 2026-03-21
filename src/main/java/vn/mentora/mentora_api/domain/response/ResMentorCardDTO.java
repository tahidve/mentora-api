package vn.mentora.mentora_api.domain.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResMentorCardDTO {
    private Long id;
    private String name;
    private String avatarUrl;
    private String headline;
    private String company;    // Tên công ty hiện tại (isCurrent = true), null nếu chưa có
    private double rating;     // Rating trung bình
    private int totalStars;
    private int totalReviews;
    private List<String> skills; // top 3
    private List<String> badges;
}
