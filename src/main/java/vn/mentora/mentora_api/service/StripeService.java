package vn.mentora.mentora_api.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import vn.mentora.mentora_api.util.error.IdInvalidException;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
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
                    .setRefreshUrl("https://mentora.tahidve.live/mentor/stripe/refresh")
                    // Sau khi Mentor điền xong, Stripe redirect về đây
                    .setReturnUrl("https://mentora.tahidve.live/mentor/stripe/return")
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

    public String createCheckoutSession(String planName, long amountInCents, String currency,
            Long invoiceId, Long slotId, String mentorAccountId)
            throws IdInvalidException {
        try {
            long feeAmount = amountInCents * platformFeePercent / 100;

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT) // Dạng thanh toán 1 lần, không phải trả hàng tháng
                    .setSuccessUrl(this.successUrl) // link fe thanh toán thành công
                    .setCancelUrl(this.cancelUrl) // ... thất bại
                    .putMetadata("invoice_id", invoiceId.toString()) // để nhận thông tin trừ stripe từ webhook, update
                                                                     // db
                    .putMetadata("slot_id", slotId.toString())
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(currency)
                                    .setUnitAmount(amountInCents)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(planName) // tên plan hiện trên màn hình nhập thẻ của stripe
                                            .build())
                                    .build())
                            .build())
                    .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                            .setApplicationFeeAmount(feeAmount)
                            .setTransferData(SessionCreateParams.PaymentIntentData.TransferData.builder()
                                    .setDestination(mentorAccountId)
                                    .build())
                            .build())
                    .build();

            Session session = Session.create(params); // gomm toàn bộ và gửi lên api của stripe
            return session.getUrl(); // lấy link thanh toán
        } catch (StripeException e) {
            throw new IdInvalidException("Lỗi tạo Checkout: " + e.getMessage());
        }
    }

    public String getWebhookSecret() {
        return this.webhookSecret;
    }

}
