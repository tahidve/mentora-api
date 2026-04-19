package vn.mentora.mentora_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class EmailService {

        private static final Logger log = LoggerFactory.getLogger(EmailService.class);

        private final JavaMailSender mailSender;

        @Value("${spring.mail.username}")
        private String fromAddress;

        public EmailService(JavaMailSender mailSender) {
                this.mailSender = mailSender;
        }

        private void doSend(String to, String subject, String body) {
                try {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setFrom(fromAddress); // Ai gửi
                        message.setTo(to); // Gửi cho ai
                        message.setSubject(subject); // Tiêu đề
                        message.setText(body); // Nội dung plain text
                        mailSender.send(message); // Gọi SMTP thực sự
                        log.info("[EMAIL] Sent '{}' → {}", subject, to);
                } catch (Exception e) {
                        log.error("[EMAIL] Failed to send '{}' → {}: {}", subject, to, e.getMessage());
                }
        }

        @Async("emailExecutor")
        public void sendOtpEmail(String to, String name, String otp) {
                String subject = "Mentoria — Mã xác thực OTP của bạn";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Mã OTP xác thực tài khoản Mentoria của bạn là:\n\n" +
                                                "   %s\n\n" +
                                                "Mã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này.\n\n" +
                                                "Nếu bạn không đăng ký, hãy bỏ qua email này.\n\n" +
                                                "Trân trọng,\nMentoria Team",
                                name, otp);
                doSend(to, subject, body);
        }

        @Async("emailExecutor")
        public void sendBookingConfirmToMentor(String mentorEmail, String mentorName,
                        String menteeName, String planName,
                        LocalDate slotDate, LocalTime startTime) {
                String subject = "Mentoria — Bạn có một booking mới!";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Mentee %s vừa đặt lịch buổi học với bạn.\n\n" +
                                                "Chi tiết:\n" +
                                                "  - Gói:      %s\n" +
                                                "  - Ngày:     %s\n" +
                                                "  - Giờ bắt đầu: %s\n\n" +
                                                "Vui lòng vào hệ thống để cập nhật link họp cho buổi học này.\n\n" +
                                                "Trân trọng,\nMentoria Team",
                                mentorName, menteeName, planName, slotDate, startTime);
                doSend(mentorEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendBookingConfirmToMentee(String menteeEmail, String menteeName,
                        String mentorName, String planName,
                        LocalDate slotDate, LocalTime startTime,
                        BigDecimal amountTotal) {
                String subject = "Mentoria — Đặt lịch thành công!";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Bạn đã đặt lịch học thành công!\n\n" +
                                                "Chi tiết booking:\n" +
                                                "  - Mentor:   %s\n" +
                                                "  - Gói:      %s\n" +
                                                "  - Ngày:     %s\n" +
                                                "  - Giờ bắt đầu: %s\n" +
                                                "  - Số tiền:  %s VNĐ\n\n" +
                                                "Mentor sẽ gửi link họp cho bạn sớm. Hẹn gặp bạn trong buổi học!\n\n" +
                                                "Trân trọng,\nMentoria Team",
                                menteeName, mentorName, planName, slotDate, startTime, amountTotal);
                doSend(menteeEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendBookingCancelledToMentor(String mentorEmail, String mentorName,
                        String menteeName, String planName,
                        LocalDate slotDate) {
                String subject = "Mentoria — Mentee đã hủy lịch học";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Mentee %s đã hủy booking cho buổi học.\n\n" +
                                                "Chi tiết:\n" +
                                                "  - Gói:  %s\n" +
                                                "  - Ngày: %s\n\n" +
                                                "Slot đó đã được trả về trạng thái AVAILABLE — bạn có thể dùng lại.\n\n"
                                                +
                                                "Trân trọng,\nMentoria Team",
                                mentorName, menteeName, planName, slotDate);
                doSend(mentorEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendMeetingLocationToMentee(String menteeEmail, String menteeName,
                        String mentorName, String location,
                        LocalDate date, LocalTime startTime) {
                String subject = "Mentoria — Link buổi học của bạn đã sẵn sàng!";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Mentor %s đã cập nhật link họp cho buổi học sắp tới.\n\n" +
                                                "  - Ngày:     %s\n" +
                                                "  - Giờ:      %s\n" +
                                                "  - Link/Địa điểm: %s\n\n" +
                                                "Nhớ vào đúng giờ nhé! Chúc bạn có buổi học hiệu quả.\n\n" +
                                                "Trân trọng,\nMentoria Team",
                                menteeName, mentorName, date, startTime, location);
                doSend(menteeEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendMeetingCancelledToMentee(String menteeEmail, String menteeName,
                        String mentorName, LocalDate date,
                        LocalTime startTime) {
                String subject = "Mentoria — Buổi học của bạn đã bị hủy";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Rất tiếc, Mentor %s đã hủy buổi học vào:\n\n" +
                                                "  - Ngày: %s\n" +
                                                "  - Giờ:  %s\n\n" +
                                                "Bạn có thể chọn slot khác trên hệ thống. Chúng tôi xin lỗi vì sự bất tiện này.\n\n"
                                                +
                                                "Trân trọng,\nMentoria Team",
                                menteeName, mentorName, date, startTime);
                doSend(menteeEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendMeetingCompletedToMentee(String menteeEmail, String menteeName,
                        String mentorName, LocalDate date) {
                String subject = "Mentoria — Buổi học đã hoàn thành. Hãy để lại đánh giá!";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Buổi học với Mentor %s vào ngày %s đã hoàn thành.\n\n" +
                                                "Cảm ơn bạn đã tin tưởng Mentoria!\n\n" +
                                                "Hãy dành 1 phút để đánh giá buổi học — điều này giúp Mentor " +
                                                "cải thiện chất lượng và giúp các Mentee khác đưa ra lựa chọn tốt hơn.\n\n"
                                                +
                                                "Trân trọng,\nMentoria Team",
                                menteeName, mentorName, date);
                doSend(menteeEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendFeedbackToMentor(String mentorEmail, String mentorName,
                        String menteeName, int stars, String content) {
                String subject = "Mentoria — Bạn nhận được đánh giá mới (" + stars + " sao)";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Mentee %s vừa để lại đánh giá cho bạn:\n\n" +
                                                "  Số sao: %s/5\n" +
                                                "  Nhận xét: \"%s\"\n\n" +
                                                "Cảm ơn bạn đã dành thời gian chia sẻ kiến thức!\n\n" +
                                                "Trân trọng,\nMentoria Team",
                                mentorName, menteeName, "★".repeat(stars), content);
                doSend(mentorEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendMentorApproved(String mentorEmail, String mentorName) {
                String subject = "Mentoria — Hồ sơ của bạn đã được duyệt!";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Chúc mừng! Hồ sơ Mentor của bạn trên Mentoria đã được Admin xét duyệt thành công.\n\n"
                                                +
                                                "Tài khoản của bạn hiện đã ACTIVE. Bạn có thể:\n" +
                                                "  - Tạo các Plan (Session / Mentorship)\n" +
                                                "  - Mở Slot lịch rảnh\n" +
                                                "  - Nhận booking từ Mentee\n\n" +
                                                "Chào mừng bạn đến với cộng đồng Mentor Mentoria!\n\n" +
                                                "Trân trọng,\nMentoria Team",
                                mentorName);
                doSend(mentorEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendMentorRejected(String mentorEmail, String mentorName, String reason) {
                String subject = "Mentoria — Hồ sơ của bạn cần được cập nhật";
                String body = String.format(
                                "Xin chào %s,\n\n" +
                                                "Hồ sơ Mentor của bạn chưa được chấp thuận ở lần xét duyệt này.\n\n" +
                                                "Lý do: %s\n\n" +
                                                "Bạn có thể chỉnh sửa hồ sơ và bấm 'Gửi lại' để được xét duyệt lần tiếp theo.\n\n"
                                                +
                                                "Nếu có thắc mắc, vui lòng liên hệ support@mentoria.com.\n\n" +
                                                "Trân trọng,\nMentoria Team",
                                mentorName, reason);
                doSend(mentorEmail, subject, body);
        }

        @Async("emailExecutor")
        public void sendMentorResubmitToAdmin(String adminEmail, String mentorName,
                        String mentorEmail) {
                String subject = "Mentoria Admin — Mentor gửi lại hồ sơ chờ duyệt";
                String body = String.format(
                                "Kính gửi Admin,\n\n" +
                                                "Mentor %s (%s) vừa cập nhật và gửi lại hồ sơ sau khi bị từ chối.\n\n" +
                                                "Vui lòng vào trang Admin để xem xét và duyệt/từ chối hồ sơ này.\n\n" +
                                                "Trân trọng,\nHệ thống Mentoria",
                                mentorName, mentorEmail);
                doSend(adminEmail, subject, body);
        }
}
