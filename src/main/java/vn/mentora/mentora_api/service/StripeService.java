package vn.mentora.mentora_api.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import vn.mentora.mentora_api.util.error.IdInvalidException;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;

import org.springframework.beans.factory.annotation.Value;

@Service
public class StripeService {

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.platform-fee-percent}")
    private int platformFeePercent;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = this.apiKey;
    }

    // tạo express account trên stripe cho mentor
    // stripe tự render UI thu thông tin bank.
    // trả về account id lưu vào db
    public String createConnectedAccount(String email) throws IdInvalidException {
        try {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setEmail(email)
                    .setCapabilities(AccountCreateParams.Capabilities.builder()
                            // cho phép tài khoản này nhận thanh toán thẻ
                            .setCardPayments(AccountCreateParams.Capabilities.CardPayments.builder()
                                    .setRequested(true).build())
                            // cho phép tài khoản này nhận tiền transfer từ Platform
                            .setTransfers(AccountCreateParams.Capabilities.Transfers.builder()
                                    .setRequested(true).build())
                            .build())
                    .build();
            Account account = Account.create(params);
            return account.getId(); // VD: "acct_1Q2w3E4r5T6u7I8o"
        } catch (StripeException e) {
            throw new IdInvalidException("Stripe error: " + e.getMessage());
        }
    }

    // tạo link onboarding, mentor bấm vào link sẽ được Stripe hỏi: thông tin tên,
    // địa chỉ, ...
    // link hết hạn sau vài phút, call api sẽ tạo 1 link mới
    public String createOnboardingLink(String accountId) throws IdInvalidException {
        try {
            AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                    .setAccount(accountId)
                    // Nếu link hết hạn, FE redirect về đây để Mentor lấy link mới
                    .setRefreshUrl("http://localhost:5173/mentor/stripe/refresh")
                    // Sau khi Mentor điền xong, Stripe redirect về đây
                    .setReturnUrl("http://localhost:5173/mentor/stripe/return")
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();
            AccountLink link = AccountLink.create(params);
            return link.getUrl();
        } catch (StripeException e) {
            throw new IdInvalidException("Stripe error: " + e.getMessage());
        }
    }

    // kiểm tra xem mentor đã điền đủ thông tin ngân hàng chưa.
    // chargesEnabled = true nghĩa là tài khoản dc kích hoạt, Stripe đã xác minh đủ
    // thông tin nhận tiền
    public boolean checkOnboardingStatus(String accountId) throws IdInvalidException {
        try {
            Account account = Account.retrieve(accountId);
            return account.getChargesEnabled(); // true = đã onboard xong
        } catch (StripeException e) {
            throw new IdInvalidException("Stripe error: " + e.getMessage());
        }
    }

}
