package vn.mentora.mentora_api.domain.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateMentorDTO {
    private String bio;
    private String headline;
    private List<String> languages;
    private List<Long> skillIds;
    private String country;
    private String timezone;
    private String cvUrl;
}
