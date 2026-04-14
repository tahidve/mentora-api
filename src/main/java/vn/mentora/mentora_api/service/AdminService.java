package vn.mentora.mentora_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.response.ResAdminMenteeDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.repository.BookingRepository;
import vn.mentora.mentora_api.repository.MenteeRepository;

@Service
public class AdminService {

    private final MenteeRepository menteeRepository;
    private final BookingRepository bookingRepository;

    public AdminService(
            MenteeRepository menteeRepository,
            BookingRepository bookingRepository) {
        this.menteeRepository = menteeRepository;
        this.bookingRepository = bookingRepository;
    }

    public ResultPaginationDTO getAllMentees(Pageable pageable) {
        Page<Mentee> page = this.menteeRepository.findAll(pageable);

        List<ResAdminMenteeDTO> dtoList = new ArrayList<>();
        for (Mentee mentee : page.getContent()) {
            dtoList.add(convertToMenteeDTO(mentee));
        }

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(dtoList);

        return result;
    }

    private ResAdminMenteeDTO convertToMenteeDTO(Mentee mentee) {
        ResAdminMenteeDTO dto = new ResAdminMenteeDTO();
        dto.setMenteeId(mentee.getMenteeId());
        dto.setFirstName(mentee.getUser().getFirstName());
        dto.setLastName(mentee.getUser().getLastName());
        dto.setEmail(mentee.getUser().getEmail());
        dto.setPhone(mentee.getUser().getPhone());
        dto.setCountry(mentee.getUser().getCountry());
        dto.setGender(mentee.getUser().getGender());
        dto.setStatus(mentee.getUser().getStatus());
        dto.setGoal(mentee.getGoal());
        dto.setCreatedAt(mentee.getUser().getCreatedAt());

        // N+1 querry, tạm chấp nhận
        long totalBookings = this.bookingRepository.countByMenteeMenteeId(mentee.getMenteeId());
        dto.setTotalBookings(totalBookings);

        return dto;
    }
}
