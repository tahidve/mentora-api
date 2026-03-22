package vn.mentora.mentora_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Discount;
import vn.mentora.mentora_api.repository.DiscountRepository;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public List<Discount> findAllActive() {
        return this.discountRepository.findByActiveTrue();
    }

    public Discount findById(Long id) throws IdInvalidException {
        Discount result = this.discountRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Discount với id: " + id + " không tồn tại"));
        return result;
    }

    public BigDecimal validateAndApplyDiscount(Long discountId, BigDecimal originalAmount) throws IdInvalidException {
        Discount discount = this.findById(discountId);
        if (discount.isActive()) {
            LocalDate today = LocalDate.now();
            // isStarted lưu thông tin đã bắt đầu chưa, so sánh xem nếu không nằm trước ngày
            // bắt đầu thì đã bắt đầu
            boolean isStarted = !today.isBefore(discount.getStartDate());
            // isNotExpired lưu thông tin đã hét hạn chưa, so sánh xem nếu không nằm sau
            // ngày hết hạn thì chưa hết hạn
            boolean isNotExpired = !today.isAfter(discount.getEndDate());
            if (isStarted && isNotExpired) {
                if (discount.getUsageLimit() == 0 || discount.getUsedCount() < discount.getUsageLimit()) {
                    // Trả về giá đã giảm
                    BigDecimal amountTotal;
                    switch (discount.getDiscountType()) {
                        case PERCENTAGE:
                            BigDecimal reduction = originalAmount
                                    .multiply(discount.getDiscountValue())
                                    .divide(BigDecimal.valueOf(100));
                            amountTotal = originalAmount.subtract(reduction);
                            break;
                        case FIXED_AMOUNT:
                            amountTotal = originalAmount.subtract(discount.getDiscountValue());
                            break;
                        default:
                            amountTotal = originalAmount;
                    }
                    // Đảm bảo không âm
                    if (amountTotal.compareTo(BigDecimal.ZERO) < 0) {
                        amountTotal = BigDecimal.ZERO;
                    }
                    return amountTotal;
                } else {
                    throw new IdInvalidException("Mã giảm giá: " + discount.getDiscountName() + " đã hết lượt sử dụng");
                }
            } else {
                throw new IdInvalidException("Mã giảm giá: " + discount.getDiscountName() + " đã hết hạn");
            }
        } else {
            throw new IdInvalidException("Mã giảm giá: " + discount.getDiscountName() + " không còn được áp dụng");
        }
    }

}
