package vn.mentora.mentora_api.config;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.mentora.mentora_api.domain.*;
import vn.mentora.mentora_api.repository.*;
import vn.mentora.mentora_api.util.constant.*;

/**
 * DataInitializer — chạy một lần khi startup, seed toàn bộ dữ liệu mẫu.
 * Guard: kiểm tra existsByEmail("admin@mentoria.vn") → nếu đã có → SKIP toàn
 * bộ.
 * Password tất cả accounts: Abc@12345
 */
@Component
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final MentorRepository mentorRepository;
        private final MenteeRepository menteeRepository;
        private final SkillRepository skillRepository;
        private final CategoryRepository categoryRepository;
        private final CompanyRepository companyRepository;
        private final JobTitleRepository jobTitleRepository;
        private final WorkForRepository workForRepository;
        private final PlanSessionRepository planSessionRepository;
        private final PlanMentorshipRepository planMentorshipRepository;
        private final SlotRepository slotRepository;
        private final DiscountRepository discountRepository;
        private final PlanRegistrationRepository planRegistrationRepository;
        private final BookingRepository bookingRepository;
        private final InvoiceRepository invoiceRepository;
        private final MeetingRepository meetingRepository;
        private final FeedbackRepository feedbackRepository;
        private final PasswordEncoder passwordEncoder;

        public DataInitializer(
                        UserRepository userRepository,
                        MentorRepository mentorRepository,
                        MenteeRepository menteeRepository,
                        SkillRepository skillRepository,
                        CategoryRepository categoryRepository,
                        CompanyRepository companyRepository,
                        JobTitleRepository jobTitleRepository,
                        WorkForRepository workForRepository,
                        PlanSessionRepository planSessionRepository,
                        PlanMentorshipRepository planMentorshipRepository,
                        SlotRepository slotRepository,
                        DiscountRepository discountRepository,
                        PlanRegistrationRepository planRegistrationRepository,
                        BookingRepository bookingRepository,
                        InvoiceRepository invoiceRepository,
                        MeetingRepository meetingRepository,
                        FeedbackRepository feedbackRepository,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.mentorRepository = mentorRepository;
                this.menteeRepository = menteeRepository;
                this.skillRepository = skillRepository;
                this.categoryRepository = categoryRepository;
                this.companyRepository = companyRepository;
                this.jobTitleRepository = jobTitleRepository;
                this.workForRepository = workForRepository;
                this.planSessionRepository = planSessionRepository;
                this.planMentorshipRepository = planMentorshipRepository;
                this.slotRepository = slotRepository;
                this.discountRepository = discountRepository;
                this.planRegistrationRepository = planRegistrationRepository;
                this.bookingRepository = bookingRepository;
                this.invoiceRepository = invoiceRepository;
                this.meetingRepository = meetingRepository;
                this.feedbackRepository = feedbackRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                // ── Guard: nếu đã có admin thì bỏ qua toàn bộ ──────────────────────
                if (userRepository.existsByEmail("admin@mentoria.vn")) {
                        System.out.println(">>> [DataInitializer] Dữ liệu đã tồn tại → SKIP.");
                        return;
                }

                System.out.println(">>> [DataInitializer] Bắt đầu khởi tạo dữ liệu mẫu...");
                String encodedPw = passwordEncoder.encode("Abc@12345");

                // ════════════════════════════════════════════════════════════════════
                // 1. USERS
                // ════════════════════════════════════════════════════════════════════
                User admin = makeUser("Admin", "Mentoria", "admin@mentoria.vn", encodedPw, RoleEnum.ADMIN, "Vietnam",
                                "Asia/Ho_Chi_Minh", GenderEnum.MALE, UserStatusEnum.ACTIVE);
                User uDuc = makeUser("Nguyen", "Minh Duc", "duc.nguyen@mentor.vn", encodedPw, RoleEnum.MENTOR,
                                "Vietnam",
                                "Asia/Ho_Chi_Minh", GenderEnum.MALE, UserStatusEnum.ACTIVE);
                User uHoa = makeUser("Tran", "Minh Hoa", "hoa.tran@mentor.vn", encodedPw, RoleEnum.MENTOR, "Vietnam",
                                "Asia/Ho_Chi_Minh", GenderEnum.FEMALE, UserStatusEnum.ACTIVE);
                User uThanh = makeUser("Le", "Van Thanh", "thanh.le@mentor.vn", encodedPw, RoleEnum.MENTOR, "Singapore",
                                "Asia/Singapore", GenderEnum.MALE, UserStatusEnum.ACTIVE);
                User uLinh = makeUser("Pham", "Ngoc Linh", "linh.pham@mentor.vn", encodedPw, RoleEnum.MENTOR,
                                "United States",
                                "America/New_York", GenderEnum.FEMALE, UserStatusEnum.ACTIVE);
                User uSon = makeUser("Vo", "Thanh Son", "son.vo@mentor.vn", encodedPw, RoleEnum.MENTOR, "Japan",
                                "Asia/Tokyo",
                                GenderEnum.MALE, UserStatusEnum.ACTIVE);
                User uLan = makeUser("Bui", "Thi Lan", "lan.bui@mentee.vn", encodedPw, RoleEnum.MENTEE, "Vietnam",
                                "Asia/Ho_Chi_Minh", GenderEnum.FEMALE, UserStatusEnum.ACTIVE);
                User uNam = makeUser("Hoang", "Van Nam", "nam.hoang@mentee.vn", encodedPw, RoleEnum.MENTEE, "Vietnam",
                                "Asia/Ho_Chi_Minh", GenderEnum.MALE, UserStatusEnum.ACTIVE);
                User uHien = makeUser("Nguyen", "Thu Hien", "hien.nguyen@mentee.vn", encodedPw, RoleEnum.MENTEE,
                                "Vietnam",
                                "Asia/Ho_Chi_Minh", GenderEnum.FEMALE, UserStatusEnum.ACTIVE);
                User uHung = makeUser("Do", "Manh Hung", "hung.do@mentee.vn", encodedPw, RoleEnum.MENTEE, "Vietnam",
                                "Asia/Ho_Chi_Minh", GenderEnum.MALE, UserStatusEnum.ACTIVE);
                User uMai = makeUser("Dang", "Thi Mai", "mai.dang@mentee.vn", encodedPw, RoleEnum.MENTEE, "Vietnam",
                                "Asia/Ho_Chi_Minh", GenderEnum.FEMALE, UserStatusEnum.ACTIVE);
                userRepository.saveAll(List.of(admin, uDuc, uHoa, uThanh, uLinh, uSon, uLan, uNam, uHien, uHung, uMai));

                // ════════════════════════════════════════════════════════════════════
                // 2. SKILLS
                // ════════════════════════════════════════════════════════════════════
                Skill sJava = skill("Java");
                Skill sSpring = skill("Spring Boot");
                Skill sMicro = skill("Microservices");
                Skill sPython = skill("Python");
                Skill sML = skill("Machine Learning");
                Skill sDL = skill("Deep Learning");
                Skill sReact = skill("React");
                Skill sNode = skill("Node.js");
                Skill sTS = skill("TypeScript");
                Skill sAWS = skill("AWS");
                Skill sDocker = skill("Docker");
                Skill sK8s = skill("Kubernetes");
                Skill sSD = skill("System Design");
                Skill sGolang = skill("Golang");
                Skill sPG = skill("PostgreSQL");
                Skill sMongo = skill("MongoDB");
                Skill sRedis = skill("Redis");
                Skill sKafka = skill("Kafka");
                Skill sCICD = skill("CI/CD");
                Skill sDSA = skill("Data Structures & Algorithms");
                Skill sNext = skill("Next.js");
                Skill sTF = skill("TensorFlow");
                Skill sPT = skill("PyTorch");
                Skill sSQL = skill("SQL");
                Skill sGit = skill("Git");
                Skill sAgile = skill("Agile/Scrum");
                Skill sLead = skill("Leadership");
                Skill sCoach = skill("Career Coaching");
                Skill sEnglish = skill("English for IT");
                Skill sCloud = skill("Cloud Native");
                skillRepository.saveAll(List.of(
                                sJava, sSpring, sMicro, sPython, sML, sDL, sReact, sNode, sTS, sAWS,
                                sDocker, sK8s, sSD, sGolang, sPG, sMongo, sRedis, sKafka, sCICD, sDSA,
                                sNext, sTF, sPT, sSQL, sGit, sAgile, sLead, sCoach, sEnglish, sCloud));

                // ════════════════════════════════════════════════════════════════════
                // 3. CATEGORIES (parent → child)
                // ════════════════════════════════════════════════════════════════════
                Category cSE = cat("Software Engineering", null, List.of(sJava, sSpring, sMicro, sGolang, sPG));
                Category cData = cat("Data & AI", null, List.of(sPython, sML, sDL, sTF, sPT, sSQL));
                Category cCloud = cat("Cloud & DevOps", null, List.of(sAWS, sDocker, sK8s, sCICD, sCloud));
                Category cCareer = cat("Career Development", null, List.of(sSD, sDSA, sCoach, sEnglish));
                Category cMobile = cat("Mobile Development", null, List.of(sReact, sTS));
                categoryRepository.saveAll(List.of(cSE, cData, cCloud, cCareer, cMobile));

                categoryRepository.saveAll(List.of(
                                cat("Backend Development", cSE, List.of(sJava, sSpring, sMicro, sPG, sRedis, sKafka)),
                                cat("Frontend Development", cSE, List.of(sReact, sTS, sNext)),
                                cat("Full-Stack", cSE, List.of(sReact, sNode, sTS, sAWS)),
                                cat("Machine Learning", cData, List.of(sML, sDL, sTF, sPT, sPython)),
                                cat("Data Engineering", cData, List.of(sPython, sPG, sMongo, sSQL)),
                                cat("AI/LLM", cData, List.of(sPython, sTF, sPT)),
                                cat("Kubernetes & Docker", cCloud, List.of(sDocker, sK8s)),
                                cat("CI/CD & Automation", cCloud, List.of(sCICD, sDocker)),
                                cat("Interview Prep", cCareer, List.of(sSD, sDSA, sCoach)),
                                cat("Resume & Portfolio", cCareer, List.of(sCoach, sEnglish)),
                                cat("Working Abroad", cCareer, List.of(sEnglish, sLead))));

                // ════════════════════════════════════════════════════════════════════
                // 4. MENTORS
                // ════════════════════════════════════════════════════════════════════
                Mentor mDuc = new Mentor();
                mDuc.setUser(uDuc);
                mDuc.setBio(
                                "Senior Software Engineer với 8+ năm kinh nghiệm Java/Spring Boot và Microservices. Từng làm tại VNG, Tiki. Đam mê lên level cho fresher/junior developer.");
                mDuc.setHeadline("Senior Java Engineer @ Tiki | Spring Boot | Microservices | 8+ YOE");
                mDuc.setResponseTime(ResponseTimeEnum.WITHIN_1_HOUR);
                mDuc.setTotalMeetings(24);
                mDuc.setTotalStars(108);
                mDuc.setTotalReviews(23);
                mDuc.setLanguages(List.of("Vietnamese", "English"));
                mDuc.setSkills(List.of(sJava, sSpring, sMicro, sDocker, sPG, sRedis, sKafka, sGit, sAgile));
                mentorRepository.save(mDuc);

                Mentor mHoa = new Mentor();
                mHoa.setUser(uHoa);
                mHoa.setBio(
                                "Data Scientist và AI Engineer từ Bách Khoa. Chuyên ML/DL production. Hiện work tại AI startup series B.");
                mHoa.setHeadline("Data Scientist | ML Engineer | TensorFlow & PyTorch | Ex-VinAI");
                mHoa.setResponseTime(ResponseTimeEnum.WITHIN_2_HOURS);
                mHoa.setTotalMeetings(18);
                mHoa.setTotalStars(68);
                mHoa.setTotalReviews(18);
                mHoa.setLanguages(List.of("Vietnamese", "English"));
                mHoa.setSkills(List.of(sPython, sML, sDL, sTF, sPT, sPG, sMongo, sSQL));
                mentorRepository.save(mHoa);

                Mentor mThanh = new Mentor();
                mThanh.setUser(uThanh);
                mThanh.setBio(
                                "Full-Stack Developer và Tech Lead 6 năm tại Singapore. Đã mentor 50+ junior developer. Chuyên React, Node.js, AWS.");
                mThanh.setHeadline("Tech Lead @ Singapore Startup | React | Node.js | AWS | Career Coach");
                mThanh.setResponseTime(ResponseTimeEnum.WITHIN_1_DAY);
                mThanh.setTotalMeetings(31);
                mThanh.setTotalStars(139);
                mThanh.setTotalReviews(29);
                mThanh.setLanguages(List.of("Vietnamese", "English"));
                mThanh.setSkills(List.of(sReact, sNode, sTS, sAWS, sDocker, sNext, sGit, sLead));
                mentorRepository.save(mThanh);

                Mentor mLinh = new Mentor();
                mLinh.setUser(uLinh);
                mLinh.setBio(
                                "Software Engineer tại Big Tech (Seattle). Chuyên System Design, Distributed Systems, FAANG interview. Đã coach 30+ candidates pass Google/Meta/Amazon.");
                mLinh.setHeadline("Software Engineer @ Big Tech | System Design | FAANG Interview Coach");
                mLinh.setResponseTime(ResponseTimeEnum.WITHIN_1_HOUR);
                mLinh.setTotalMeetings(45);
                mLinh.setTotalStars(206);
                mLinh.setTotalReviews(43);
                mLinh.setLanguages(List.of("Vietnamese", "English"));
                mLinh.setSkills(List.of(sSD, sDSA, sJava, sCoach, sEnglish, sAWS, sK8s));
                mentorRepository.save(mLinh);

                Mentor mSon = new Mentor();
                mSon.setUser(uSon);
                mSon.setBio(
                                "Backend Developer và Cloud Architect 5 năm tại Nhật Bản. Chuyên Golang, Kubernetes, Cloud Native. Giúp bạn hiểu IT culture Nhật và chuẩn bị làm việc ở nước ngoài.");
                mSon.setHeadline("Cloud Architect @ Japan | Golang | K8s | Working Abroad Advisor");
                mSon.setResponseTime(ResponseTimeEnum.WITHIN_2_HOURS);
                mSon.setTotalMeetings(12);
                mSon.setTotalStars(46);
                mSon.setTotalReviews(10);
                mSon.setLanguages(List.of("Vietnamese", "English", "Japanese"));
                mSon.setSkills(List.of(sGolang, sDocker, sK8s, sAWS, sCloud, sCICD, sEnglish));
                mentorRepository.save(mSon);

                // ════════════════════════════════════════════════════════════════════
                // 5. MENTEES
                // ════════════════════════════════════════════════════════════════════
                Mentee menLan = mentee(uLan, "Chuyển từ fresher sang junior Java developer trong 6 tháng.");
                Mentee menNam = mentee(uNam, "Nâng cao System Design để phỏng vấn công ty lớn.");
                Mentee menHien = mentee(uHien, "Học Machine Learning để chuyển ngành từ backend sang Data Science.");
                Mentee menHung = mentee(uHung, "Chuẩn bị portfolio và kỹ năng phỏng vấn để xin việc tại Singapore.");
                Mentee menMai = mentee(uMai, "Nắm vững React + Next.js để build full-stack web app.");
                menteeRepository.saveAll(List.of(menLan, menNam, menHien, menHung, menMai));

                // ════════════════════════════════════════════════════════════════════
                // 6. COMPANIES & JOB TITLES
                // ════════════════════════════════════════════════════════════════════
                Company cTiki = company("Tiki");
                Company cVNG = company("VNG Corporation");
                Company cVinAI = company("VinAI Research");
                Company cGrab = company("Grab");
                Company cSea = company("Sea Group");
                Company cFPT = company("FPT Software");
                Company cShopee = company("Shopee");
                Company cGoogle = company("Google");
                Company cBosch = company("Bosch Vietnam");
                Company cNTT = company("NTT Data Japan");
                companyRepository.saveAll(
                                List.of(cTiki, cVNG, cVinAI, cGrab, cSea, cFPT, cShopee, cGoogle, cBosch, cNTT));

                JobTitle jtSE = jobTitle("Software Engineer");
                JobTitle jtSSE = jobTitle("Senior Software Engineer");
                JobTitle jtDS = jobTitle("Data Scientist");
                JobTitle jtMLE = jobTitle("Machine Learning Engineer");
                JobTitle jtFSD = jobTitle("Full-Stack Developer");
                JobTitle jtTL = jobTitle("Tech Lead");
                JobTitle jtBE = jobTitle("Backend Developer");
                JobTitle jtCA = jobTitle("Cloud Architect");
                jobTitleRepository.saveAll(List.of(jtSE, jtSSE, jtDS, jtMLE, jtFSD, jtTL, jtBE, jtCA));

                // ════════════════════════════════════════════════════════════════════
                // 7. WORK EXPERIENCE (work_for — composite key)
                // ════════════════════════════════════════════════════════════════════
                workForRepository.saveAll(List.of(
                                workFor(mDuc, cVNG, jtSE, "2016-06-01", "2019-08-31", false),
                                workFor(mDuc, cTiki, jtSSE, "2019-09-01", null, true),
                                workFor(mHoa, cVinAI, jtMLE, "2019-01-01", "2021-12-31", false),
                                workFor(mHoa, cGrab, jtDS, "2022-01-01", null, true),
                                workFor(mThanh, cFPT, jtFSD, "2018-03-01", "2020-06-30", false),
                                workFor(mThanh, cShopee, jtTL, "2020-07-01", null, true),
                                workFor(mLinh, cBosch, jtSE, "2015-08-01", "2018-05-31", false),
                                workFor(mLinh, cGoogle, jtSSE, "2018-06-01", null, true),
                                workFor(mSon, cFPT, jtBE, "2017-04-01", "2019-12-31", false),
                                workFor(mSon, cNTT, jtCA, "2020-01-01", null, true)));

                // ════════════════════════════════════════════════════════════════════
                // 8. PLANS — Session Plans
                // ════════════════════════════════════════════════════════════════════
                PlanSession ps1 = planSession(mDuc, "Java Core Foundations", 60, 150_000,
                                "Buổi 1-on-1 sâu về Java Core, OOP, Collections, Exception Handling. Phù hợp fresher.");
                PlanSession ps2 = planSession(mDuc, "Spring Boot Deep Dive", 90, 250_000,
                                "Deep dive vào Spring Boot: REST API, Security, JPA/Hibernate. Code review thực tế.");
                PlanSession ps3 = planSession(mHoa, "Python for Data Science", 60, 180_000,
                                "Python cho Data Science: Pandas, NumPy, Matplotlib. Bắt đầu từ basics.");
                PlanSession ps4 = planSession(mHoa, "ML Project Review", 90, 300_000,
                                "Review ML project của bạn: code review, model evaluation, deployment tips.");
                PlanSession ps5 = planSession(mThanh, "React & TypeScript Essentials", 60, 200_000,
                                "React + TypeScript: Hooks, State Management, Component patterns thực chiến.");
                PlanSession ps6 = planSession(mThanh, "AWS Architecture Consultation", 60, 280_000,
                                "AWS: EC2, S3, RDS, Lambda. Thiết kế hệ thống scalable trên cloud.");
                PlanSession ps7 = planSession(mLinh, "Mock Interview Session", 90, 350_000,
                                "1 Leetcode medium + System Design 30 phút. Feedback chi tiết sau phỏng vấn.");
                PlanSession ps8 = planSession(mLinh, "System Design Workshop", 90, 400_000,
                                "Deep dive System Design: thiết kế Twitter, Uber, YouTube. Scalability patterns.");
                PlanSession ps9 = planSession(mSon, "Golang Bootcamp Session", 60, 220_000,
                                "Golang từ A-Z: Goroutines, Channels, HTTP server cho backend developer.");
                PlanSession ps10 = planSession(mSon, "Kubernetes in Practice", 90, 290_000,
                                "Kubernetes thực chiến: Deploy app lên K8s cluster, Helm, Ingress, HPA.");
                planSessionRepository.saveAll(List.of(ps1, ps2, ps3, ps4, ps5, ps6, ps7, ps8, ps9, ps10));

                // ════════════════════════════════════════════════════════════════════
                // 9. PLANS — Mentorship Plans + Benefits
                // ════════════════════════════════════════════════════════════════════
                PlanMentorship pm1 = planMentorship(mDuc, "Java Backend Mastery", 4, 60, 800_000,
                                "Mentorship 3 tháng: Java Backend từ zero → junior ready. Daily check-in qua Slack.",
                                List.of("Weekly 1-on-1 code review session (60 phút)",
                                                "Unlimited Q&A qua Slack trong giờ hành chính",
                                                "Lộ trình học cá nhân hóa theo level hiện tại",
                                                "Mock interview cuối tháng với feedback chi tiết",
                                                "Resource list: sách, khóa học, blog chọn lọc"));

                PlanMentorship pm2 = planMentorship(mHoa, "ML Engineer Roadmap", 4, 60, 1_000_000,
                                "Mentorship 3 tháng: ML Engineer từ Python basics → production ML models.",
                                List.of("4 buổi 1-on-1 mỗi tháng (60 phút/buổi)",
                                                "Review notebook và code ML projects của bạn",
                                                "Hướng dẫn deploy model lên production (FastAPI + Docker)",
                                                "Portfolio review: Kaggle profile, GitHub, LinkedIn"));

                PlanMentorship pm3 = planMentorship(mThanh, "Full-Stack Builder", 6, 60, 900_000,
                                "Mentorship 3 tháng: React + Node.js + AWS. Build 2 real projects vào portfolio.",
                                List.of("6 buổi call mỗi tháng, linh hoạt đặt lịch",
                                                "Guide xây dựng 2 dự án thực tế vào portfolio",
                                                "Code review toàn bộ PR của bạn",
                                                "Career advice: resume, job search strategy",
                                                "Access Discord community của mentor network"));

                PlanMentorship pm4 = planMentorship(mLinh, "FAANG Ready Program", 8, 60, 1_500_000,
                                "Mentorship 4 tháng: weekly mock interview + lộ trình Leetcode cá nhân hóa.",
                                List.of("8 buổi mock interview mỗi tháng (technical + behavioral)",
                                                "Lộ trình Leetcode cá nhân hóa theo timeline công ty target",
                                                "System Design flashcards và cheat sheets độc quyền",
                                                "Resume review và LinkedIn optimization",
                                                "Referral support nếu mentor có connection tại target company"));

                planMentorshipRepository.saveAll(List.of(pm1, pm2, pm3, pm4));

                // ════════════════════════════════════════════════════════════════════
                // 10. DISCOUNTS
                // ════════════════════════════════════════════════════════════════════
                Discount dWelcome = discount("WELCOME10", DiscountTypeEnum.PERCENTAGE, 10, "2025-01-01", "2026-12-31",
                                true,
                                100, 23);
                Discount dSummer = discount("SUMMER25", DiscountTypeEnum.PERCENTAGE, 25, "2026-06-01", "2026-08-31",
                                true, 50,
                                8);
                Discount dFlat = discount("FLAT50K", DiscountTypeEnum.FIXED_AMOUNT, 50000, "2026-01-01", "2026-12-31",
                                true,
                                200, 41);
                Discount dNew = discount("NEWMEMBER15", DiscountTypeEnum.PERCENTAGE, 15, "2026-01-01", "2026-06-30",
                                true, 30,
                                15);
                Discount dBf = discount("BLACKFRIDAY30", DiscountTypeEnum.PERCENTAGE, 30, "2026-11-25", "2026-11-30",
                                false,
                                100, 0);
                discountRepository.saveAll(List.of(dWelcome, dSummer, dFlat, dNew, dBf));

                // ════════════════════════════════════════════════════════════════════
                // 11. SLOTS — đủ slots trong tương lai + một số BOOKED (quá khứ)
                // ════════════════════════════════════════════════════════════════════
                LocalDate today = LocalDate.now();
                List<Slot> slots = List.of(
                                // Duc (mDuc)
                                slot(mDuc, today.plusDays(1), "09:00", "10:00", SlotStatusEnum.AVAILABLE),
                                slot(mDuc, today.plusDays(1), "14:00", "15:30", SlotStatusEnum.AVAILABLE),
                                slot(mDuc, today.plusDays(3), "09:00", "10:00", SlotStatusEnum.AVAILABLE),
                                slot(mDuc, today.plusDays(3), "15:00", "16:30", SlotStatusEnum.AVAILABLE),
                                slot(mDuc, today.plusDays(5), "10:00", "11:30", SlotStatusEnum.AVAILABLE),
                                slot(mDuc, today.plusDays(7), "09:00", "10:00", SlotStatusEnum.AVAILABLE),
                                slot(mDuc, today.plusDays(9), "14:00", "15:30", SlotStatusEnum.AVAILABLE),
                                slot(mDuc, today.plusDays(12), "10:00", "11:00", SlotStatusEnum.AVAILABLE),
                                // Hoa (mHoa)
                                slot(mHoa, today.plusDays(2), "08:00", "09:00", SlotStatusEnum.AVAILABLE),
                                slot(mHoa, today.plusDays(2), "19:00", "20:00", SlotStatusEnum.AVAILABLE),
                                slot(mHoa, today.plusDays(4), "08:00", "09:30", SlotStatusEnum.AVAILABLE),
                                slot(mHoa, today.plusDays(6), "08:00", "09:00", SlotStatusEnum.AVAILABLE),
                                slot(mHoa, today.plusDays(9), "19:00", "20:00", SlotStatusEnum.AVAILABLE),
                                slot(mHoa, today.plusDays(11), "08:00", "09:30", SlotStatusEnum.AVAILABLE),
                                // Thanh (mThanh) — Singapore, buổi tối VN
                                slot(mThanh, today.plusDays(1), "20:00", "21:00", SlotStatusEnum.AVAILABLE),
                                slot(mThanh, today.plusDays(6), "20:00", "21:30", SlotStatusEnum.AVAILABLE),
                                slot(mThanh, today.plusDays(5), "19:30", "20:30", SlotStatusEnum.AVAILABLE),
                                slot(mThanh, today.plusDays(8), "20:00", "21:00", SlotStatusEnum.AVAILABLE),
                                slot(mThanh, today.plusDays(11), "20:00", "21:30", SlotStatusEnum.AVAILABLE),
                                // Linh (mLinh) — US, sáng sớm VN
                                slot(mLinh, today.plusDays(5), "07:00", "08:30", SlotStatusEnum.AVAILABLE),
                                slot(mLinh, today.plusDays(4), "07:00", "08:30", SlotStatusEnum.AVAILABLE),
                                slot(mLinh, today.plusDays(6), "06:30", "08:00", SlotStatusEnum.AVAILABLE),
                                slot(mLinh, today.plusDays(10), "07:00", "08:30", SlotStatusEnum.AVAILABLE),
                                slot(mLinh, today.plusDays(13), "07:00", "08:30", SlotStatusEnum.AVAILABLE),
                                // Son (mSon) — Japan
                                slot(mSon, today.plusDays(1), "21:00", "22:00", SlotStatusEnum.AVAILABLE),
                                slot(mSon, today.plusDays(4), "21:00", "22:30", SlotStatusEnum.AVAILABLE),
                                slot(mSon, today.plusDays(7), "21:00", "22:00", SlotStatusEnum.AVAILABLE),
                                slot(mSon, today.plusDays(11), "21:00", "22:30", SlotStatusEnum.AVAILABLE),
                                // BOOKED slots — dùng cho completed meetings (quá khứ)
                                slot(mDuc, today.minusDays(5), "09:00", "10:00", SlotStatusEnum.BOOKED),
                                slot(mHoa, today.minusDays(3), "08:00", "09:30", SlotStatusEnum.BOOKED),
                                // BOOKED slots — dùng cho upcoming meetings (tương lai)
                                slot(mLinh, today.plusDays(2), "07:00", "08:30", SlotStatusEnum.BOOKED),
                                slot(mThanh, today.plusDays(3), "20:00", "21:00", SlotStatusEnum.BOOKED));
                slotRepository.saveAll(slots);

                // Lấy các BOOKED slots (4 cuối) để tạo booking/meeting
                Slot slotDucPast = slots.get(28);
                Slot slotHoaPast = slots.get(29);
                Slot slotLinhFuture = slots.get(30);
                Slot slotThanhFuture = slots.get(31);

                // ════════════════════════════════════════════════════════════════════
                // 12. PLAN REGISTRATIONS → BOOKINGS → INVOICES → MEETINGS
                // Use case 1: Lan đã học Java với Duc (COMPLETED)
                // ════════════════════════════════════════════════════════════════════
                PlanRegistration reg1 = new PlanRegistration();
                reg1.setMessage("Tôi muốn nắm vững Java OOP. Hiện ở level fresher 6 tháng kinh nghiệm.");
                reg1.setDiscount(dWelcome);
                reg1 = planRegistrationRepository.save(reg1);

                Booking bk1 = new Booking();
                bk1.setMentee(menLan);
                bk1.setPlan(ps1);
                bk1.setPlanRegistration(reg1);
                bk1.setStatus(BookingStatusEnum.COMPLETED);
                bookingRepository.save(bk1);

                Invoice inv1 = new Invoice();
                inv1.setPlanRegistration(reg1);
                inv1.setMentee(menLan);
                inv1.setPaymentStatus(PaymentStatusEnum.PAID);
                inv1.setAmountSubtotal(new BigDecimal("150000"));
                inv1.setAmountTotal(new BigDecimal("135000")); // WELCOME10 -10%
                inv1.setPaidTime(Instant.now().minusSeconds(86400 * 4)); // 4 ngày trước
                inv1.setNote("Áp dụng WELCOME10 (-10%)");
                inv1 = invoiceRepository.save(inv1);

                Meeting mt1 = new Meeting();
                mt1.setInvoice(inv1);
                mt1.setSlot(slotDucPast);
                mt1.setStatus(MeetingStatusEnum.COMPLETED);
                mt1.setLocation("https://meet.google.com/abc-defg-hij");
                mt1.setDate(slotDucPast.getDate());
                mt1.setStartTime(slotDucPast.getStartTime());
                mt1.setEndTime(slotDucPast.getEndTime());
                meetingRepository.save(mt1);

                Feedback fb1 = new Feedback();
                fb1.setPlanRegistration(reg1);
                fb1.setStars(5);
                fb1.setContent(
                                "Anh Đức giải thích rất rõ ràng và kiên nhẫn. Sau buổi học tôi đã hiểu rõ hơn về Java Collections. Highly recommend!");
                feedbackRepository.save(fb1);

                // ── Use case 2: Hien đã học Python với Hoa (COMPLETED) ───────────
                PlanRegistration reg2 = new PlanRegistration();
                reg2.setMessage("Cần học Python từ đầu để chuyển sang Data Science.");
                reg2.setDiscount(dFlat);
                reg2 = planRegistrationRepository.save(reg2);

                Booking bk2 = new Booking();
                bk2.setMentee(menHien);
                bk2.setPlan(ps3);
                bk2.setPlanRegistration(reg2);
                bk2.setStatus(BookingStatusEnum.COMPLETED);
                bookingRepository.save(bk2);

                Invoice inv2 = new Invoice();
                inv2.setPlanRegistration(reg2);
                inv2.setMentee(menHien);
                inv2.setPaymentStatus(PaymentStatusEnum.PAID);
                inv2.setAmountSubtotal(new BigDecimal("180000"));
                inv2.setAmountTotal(new BigDecimal("130000")); // FLAT50K -50k
                inv2.setPaidTime(Instant.now().minusSeconds(86400 * 2));
                inv2.setNote("Áp dụng FLAT50K (-50,000 VND)");
                inv2 = invoiceRepository.save(inv2);

                Meeting mt2 = new Meeting();
                mt2.setInvoice(inv2);
                mt2.setSlot(slotHoaPast);
                mt2.setStatus(MeetingStatusEnum.COMPLETED);
                mt2.setLocation("https://meet.google.com/xyz-uvwx-yzp");
                mt2.setDate(slotHoaPast.getDate());
                mt2.setStartTime(slotHoaPast.getStartTime());
                mt2.setEndTime(slotHoaPast.getEndTime());
                meetingRepository.save(mt2);

                Feedback fb2 = new Feedback();
                fb2.setPlanRegistration(reg2);
                fb2.setStars(4);
                fb2.setContent(
                                "Chị Hoa hướng dẫn rất tận tình. Tôi đã biết cách dùng Pandas phân tích dữ liệu. Chỉ mong buổi học dài hơn một chút.");
                feedbackRepository.save(fb2);

                // ── Use case 3: Nam sắp học System Design với Linh (SCHEDULED) ────
                PlanRegistration reg3 = new PlanRegistration();
                reg3.setMessage("Chuẩn bị phỏng vấn Shopee/Sea Group trong 2 tháng tới.");
                reg3.setDiscount(null);
                reg3 = planRegistrationRepository.save(reg3);

                Booking bk3 = new Booking();
                bk3.setMentee(menNam);
                bk3.setPlan(ps7);
                bk3.setPlanRegistration(reg3);
                bk3.setStatus(BookingStatusEnum.CONFIRMED);
                bookingRepository.save(bk3);

                Invoice inv3 = new Invoice();
                inv3.setPlanRegistration(reg3);
                inv3.setMentee(menNam);
                inv3.setPaymentStatus(PaymentStatusEnum.PENDING);
                inv3.setAmountSubtotal(new BigDecimal("350000"));
                inv3.setAmountTotal(new BigDecimal("350000"));
                inv3 = invoiceRepository.save(inv3);

                Meeting mt3 = new Meeting();
                mt3.setInvoice(inv3);
                mt3.setSlot(slotLinhFuture);
                mt3.setStatus(MeetingStatusEnum.SCHEDULED);
                mt3.setLocation("https://zoom.us/j/123456789?pwd=mentoria2026");
                mt3.setDate(slotLinhFuture.getDate());
                mt3.setStartTime(slotLinhFuture.getStartTime());
                mt3.setEndTime(slotLinhFuture.getEndTime());
                meetingRepository.save(mt3);

                // ── Use case 4: Mai sắp học React với Thanh (PENDING — chờ link) ──
                PlanRegistration reg4 = new PlanRegistration();
                reg4.setMessage("Muốn deploy React app lên AWS cho dự án freelance.");
                reg4.setDiscount(null);
                reg4 = planRegistrationRepository.save(reg4);

                Booking bk4 = new Booking();
                bk4.setMentee(menMai);
                bk4.setPlan(ps5);
                bk4.setPlanRegistration(reg4);
                bk4.setStatus(BookingStatusEnum.CONFIRMED);
                bookingRepository.save(bk4);

                Invoice inv4 = new Invoice();
                inv4.setPlanRegistration(reg4);
                inv4.setMentee(menMai);
                inv4.setPaymentStatus(PaymentStatusEnum.PENDING);
                inv4.setAmountSubtotal(new BigDecimal("200000"));
                inv4.setAmountTotal(new BigDecimal("200000"));
                inv4 = invoiceRepository.save(inv4);

                Meeting mt4 = new Meeting();
                mt4.setInvoice(inv4);
                mt4.setSlot(slotThanhFuture);
                mt4.setStatus(MeetingStatusEnum.PENDING); // Thanh chưa add link
                mt4.setLocation(null);
                mt4.setDate(slotThanhFuture.getDate());
                mt4.setStartTime(slotThanhFuture.getStartTime());
                mt4.setEndTime(slotThanhFuture.getEndTime());
                meetingRepository.save(mt4);

                System.out.println(">>> [DataInitializer] ✅ Seed hoàn tất!");
                System.out.println(">>> Accounts (password: Abc@12345):");
                System.out.println(">>>   ADMIN  : admin@mentoria.vn");
                System.out.println(
                                ">>>   MENTOR  : duc.nguyen@mentor.vn | hoa.tran@mentor.vn | thanh.le@mentor.vn | linh.pham@mentor.vn | son.vo@mentor.vn");
                System.out.println(
                                ">>>   MENTEE  : lan.bui@mentee.vn | nam.hoang@mentee.vn | hien.nguyen@mentee.vn | hung.do@mentee.vn | mai.dang@mentee.vn");
        }

        // ════════════════════════════════════════════════════════════════════════
        // Helper methods
        // ════════════════════════════════════════════════════════════════════════

        private User makeUser(String first, String last, String email, String pw,
                        RoleEnum role, String country, String tz,
                        GenderEnum gender, UserStatusEnum status) {
                User u = new User();
                u.setFirstName(first);
                u.setLastName(last);
                u.setEmail(email);
                u.setPassword(pw);
                u.setRole(role);
                u.setCountry(country);
                u.setTimezone(tz);
                u.setGender(gender);
                u.setStatus(status);
                u.setEmailVerified(true);
                u.setCreatedAt(Instant.now());
                return u;
        }

        private Skill skill(String name) {
                Skill s = new Skill();
                s.setSkillName(name);
                return s;
        }

        private Category cat(String name, Category parent, List<Skill> skills) {
                Category c = new Category();
                c.setCategoryName(name);
                c.setParent(parent);
                c.setSkills(skills);
                return c;
        }

        private Mentee mentee(User user, String goal) {
                Mentee m = new Mentee();
                m.setUser(user);
                m.setGoal(goal);
                return m;
        }

        private Company company(String name) {
                Company c = new Company();
                c.setCompanyName(name);
                return c;
        }

        private JobTitle jobTitle(String name) {
                JobTitle jt = new JobTitle();
                jt.setJobTitleName(name);
                return jt;
        }

        private WorkFor workFor(Mentor mentor, Company company, JobTitle jobTitle,
                        String startDate, String endDate, boolean isCurrent) {
                WorkFor wf = new WorkFor();
                wf.setId(new WorkForId(mentor.getMentorId(), company.getCompanyId()));
                wf.setMentor(mentor);
                wf.setCompany(company);
                wf.setJobTitle(jobTitle);
                wf.setStartDate(startDate != null ? LocalDate.parse(startDate) : null);
                wf.setEndDate(endDate != null ? LocalDate.parse(endDate) : null);
                wf.setCurrent(isCurrent);
                return wf;
        }

        private PlanSession planSession(Mentor mentor, String name, int duration,
                        long chargeVnd, String desc) {
                PlanSession ps = new PlanSession();
                ps.setMentor(mentor);
                ps.setSessionName(name);
                ps.setSessionsDuration(duration);
                ps.setCharge(new BigDecimal(chargeVnd));
                ps.setDescription(desc);
                ps.setStatus(PlanStatusEnum.ACTIVE);
                return ps;
        }

        private PlanMentorship planMentorship(Mentor mentor, String name, int callsPerMonth,
                        int minutesPerCall, long chargeVnd,
                        String desc, List<String> benefits) {
                PlanMentorship pm = new PlanMentorship();
                pm.setMentor(mentor);
                pm.setPlanName(name);
                pm.setCallsPerMonth(callsPerMonth);
                pm.setMinutesPerCall(minutesPerCall);
                pm.setCharge(new BigDecimal(chargeVnd));
                pm.setDescription(desc);
                pm.setStatus(PlanStatusEnum.ACTIVE);

                for (int i = 0; i < benefits.size(); i++) {
                        MentorshipsBenefit b = new MentorshipsBenefit();
                        b.setId(new MentorshipsBenefitId(null, i + 1)); // planId sẽ được fill sau khi save
                        b.setPlanMentorship(pm);
                        b.setBenefitContent(benefits.get(i));
                        pm.getBenefits().add(b);
                }
                return pm;
        }

        private Discount discount(String name, DiscountTypeEnum type, long value,
                        String start, String end, boolean active,
                        int limit, int used) {
                Discount d = new Discount();
                d.setDiscountName(name);
                d.setDiscountType(type);
                d.setDiscountValue(new BigDecimal(value));
                d.setStartDate(LocalDate.parse(start));
                d.setEndDate(LocalDate.parse(end));
                d.setActive(active);
                d.setUsageLimit(limit);
                d.setUsedCount(used);
                return d;
        }

        private Slot slot(Mentor mentor, LocalDate date, String start, String end,
                        SlotStatusEnum status) {
                Slot s = new Slot();
                s.setMentor(mentor);
                s.setDate(date);
                s.setStartTime(LocalTime.parse(start));
                s.setEndTime(LocalTime.parse(end));
                s.setStatus(status);
                return s;
        }
}
