package vn.mentora.mentora_api.domain.response;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.domain.WorkFor;

@Getter
@Setter
public class ResWorkExperienceDTO {
    private String companyName;
    private String logoUrl;
    private String jobTitleName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;

    public ResWorkExperienceDTO(WorkFor currentWork) {
        this.companyName = currentWork.getCompany().getCompanyName();
        this.logoUrl = currentWork.getCompany().getLogoUrl();
        this.jobTitleName = currentWork.getJobTitle().getJobTitleName();
        this.startDate = currentWork.getStartDate();
        this.endDate = currentWork.getEndDate();
        this.isCurrent = currentWork.isCurrent();
    }
}
