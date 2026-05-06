package vn.mentora.mentora_api.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
 * DataInitializer — chạy một lần khi startup, seed dữ liệu nền.
 * Guard: existsByEmail("admin@mentora.com") → đã có → SKIP.
 * Password tất cả accounts: Abc@12345
 *
 * Chỉ seed dữ liệu nền (accounts, skills, plans, slots).
 * Core business (booking, invoice, meeting, feedback) = TRỐNG → thao tác qua
 * UI.
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
        private final SlotRepository slotRepository;
        private final DiscountRepository discountRepository;
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
                        SlotRepository slotRepository,
                        DiscountRepository discountRepository,
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
                this.slotRepository = slotRepository;
                this.discountRepository = discountRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                try {
                if (userRepository.existsByEmail("admin@mentora.com")) {
                        System.out.println(">>> [DataInitializer] Data exists → SKIP.");
                        return;
                }

                System.out.println(">>> [DataInitializer] Seeding initial data...");
                String pw = passwordEncoder.encode("Abc@12345");

                // ════════════════════════════════════════════════════════════════
                // 1. CATEGORIES
                // ════════════════════════════════════════════════════════════════
                Category cTech = cat("Technology", null);
                Category cBiz = cat("Business", null);
                Category cDesign = cat("Design", null);
                Category cData = cat("Data & AI", null);
                Category cCloud = cat("DevOps & Cloud", null);
                categoryRepository.saveAll(List.of(cTech, cBiz, cDesign, cData, cCloud));

                Category cFE = cat("Frontend Development", cTech);
                Category cBE = cat("Backend Development", cTech);
                Category cMobile = cat("Mobile Development", cTech);
                Category cMarketing = cat("Marketing", cBiz);
                Category cStartup = cat("Startup", cBiz);
                Category cUIUX = cat("UI/UX Design", cDesign);
                Category cML = cat("Machine Learning", cData);
                Category cInfra = cat("Cloud Infrastructure", cCloud);
                categoryRepository.saveAll(List.of(cFE, cBE, cMobile, cMarketing, cStartup, cUIUX, cML, cInfra));

                // ════════════════════════════════════════════════════════════════
                // 2. SKILLS
                // ════════════════════════════════════════════════════════════════
                Skill sJava = skill("Java");
                Skill sSpring = skill("Spring Boot");
                Skill sReact = skill("React");
                Skill sTS = skill("TypeScript");
                Skill sNode = skill("Node.js");
                Skill sPython = skill("Python");
                Skill sDocker = skill("Docker");
                Skill sK8s = skill("Kubernetes");
                Skill sMySQL = skill("MySQL");
                Skill sPostgres = skill("PostgreSQL");
                Skill sAWS = skill("AWS");
                Skill sGit = skill("Git");
                Skill sREST = skill("REST API");
                Skill sGraphQL = skill("GraphQL");
                Skill sFlutter = skill("Flutter");
                Skill sSwift = skill("Swift");
                Skill sFigma = skill("Figma");
                Skill sPM = skill("Product Management");
                Skill sDataAn = skill("Data Analysis");
                Skill sMLSkill = skill("Machine Learning");
                skillRepository.saveAll(List.of(
                                sJava, sSpring, sReact, sTS, sNode, sPython, sDocker, sK8s,
                                sMySQL, sPostgres, sAWS, sGit, sREST, sGraphQL, sFlutter,
                                sSwift, sFigma, sPM, sDataAn, sMLSkill));

                // Category ↔ Skill
                cFE.setSkills(new ArrayList<>(List.of(sReact, sTS)));
                cBE.setSkills(new ArrayList<>(List.of(sJava, sSpring, sNode, sMySQL, sPostgres, sREST, sGraphQL)));
                cMobile.setSkills(new ArrayList<>(List.of(sFlutter, sSwift)));
                cMarketing.setSkills(new ArrayList<>(List.of(sPM)));
                cStartup.setSkills(new ArrayList<>(List.of(sPM)));
                cUIUX.setSkills(new ArrayList<>(List.of(sFigma)));
                cML.setSkills(new ArrayList<>(List.of(sPython, sMLSkill)));
                cInfra.setSkills(new ArrayList<>(List.of(sDocker, sK8s, sAWS)));
                categoryRepository.saveAll(new ArrayList<>(List.of(cFE, cBE, cMobile, cMarketing, cStartup, cUIUX, cML, cInfra)));

                // ════════════════════════════════════════════════════════════════
                // 3. COMPANIES & JOB TITLES
                // ════════════════════════════════════════════════════════════════
                Company coGoogle = company("Google");
                Company coAWS = company("Amazon Web Services");
                Company coMeta = company("Meta");
                Company coStripe = company("Stripe");
                Company coSpotify = company("Spotify");
                Company coShopify = company("Shopify");
                Company coGitHub = company("GitHub");
                Company coDatadog = company("Datadog");
                Company coGrab = company("Grab");
                companyRepository.saveAll(
                                List.of(coGoogle, coAWS, coMeta, coStripe, coSpotify, coShopify, coGitHub, coDatadog, coGrab));

                JobTitle jtSE = jobTitle("Software Engineer");
                JobTitle jtSSE = jobTitle("Senior Software Engineer");
                JobTitle jtTL = jobTitle("Tech Lead");
                JobTitle jtEM = jobTitle("Engineering Manager");
                JobTitle jtDevOps = jobTitle("DevOps Engineer");
                JobTitle jtPM = jobTitle("Product Manager");
                JobTitle jtUX = jobTitle("UX Designer");
                JobTitle jtDS = jobTitle("Data Scientist");
                jobTitleRepository.saveAll(List.of(jtSE, jtSSE, jtTL, jtEM, jtDevOps, jtPM, jtUX, jtDS));

                // ════════════════════════════════════════════════════════════════
                // 4. USERS
                // ════════════════════════════════════════════════════════════════
                // Admin
                User uAdmin = user("Admin", "Mentora", "admin@mentora.com", pw,
                                "United States", "America/New_York", GenderEnum.MALE, RoleEnum.ADMIN);
                userRepository.save(uAdmin);

                // Mentors
                User uAlex = user("Alex", "Chen", "alex.chen@mentora.com", pw,
                                "United States", "America/Los_Angeles", GenderEnum.MALE, RoleEnum.MENTOR);
                User uSophia = user("Sophia", "Mueller", "sophia.mueller@mentora.com", pw,
                                "Germany", "Europe/Berlin", GenderEnum.FEMALE, RoleEnum.MENTOR);
                User uRaj = user("Raj", "Patel", "raj.patel@mentora.com", pw,
                                "India", "Asia/Kolkata", GenderEnum.MALE, RoleEnum.MENTOR);
                User uEmma = user("Emma", "Larsson", "emma.larsson@mentora.com", pw,
                                "Sweden", "Europe/Stockholm", GenderEnum.FEMALE, RoleEnum.MENTOR);
                userRepository.saveAll(List.of(uAlex, uSophia, uRaj, uEmma));

                // Mentees
                User uJames = user("James", "Wilson", "james.wilson@mentora.com", pw,
                                "United Kingdom", "Europe/London", GenderEnum.MALE, RoleEnum.MENTEE);
                User uYuki = user("Yuki", "Tanaka", "yuki.tanaka@mentora.com", pw,
                                "Japan", "Asia/Tokyo", GenderEnum.FEMALE, RoleEnum.MENTEE);
                User uLucas = user("Lucas", "Silva", "lucas.silva@mentora.com", pw,
                                "Brazil", "America/Sao_Paulo", GenderEnum.MALE, RoleEnum.MENTEE);
                userRepository.saveAll(List.of(uJames, uYuki, uLucas));

                // ════════════════════════════════════════════════════════════════
                // 5. MENTORS
                // ════════════════════════════════════════════════════════════════
                Mentor mAlex = mentor(uAlex,
                                "Staff Engineer at Google with 10+ years building distributed systems. " +
                                                "Passionate about clean architecture, microservices, and mentoring the next generation of engineers.",
                                "Java/Spring Boot Expert | Staff Engineer @ Google",
                                ResponseTimeEnum.WITHIN_1_DAY,
                                List.of(sJava, sSpring, sMySQL, sREST, sDocker, sGit),
                                List.of("English", "Mandarin"));

                Mentor mSophia = mentor(uSophia,
                                "Senior Frontend Engineer specializing in React ecosystem. " +
                                                "Built design systems used by millions. Open-source contributor and conference speaker.",
                                "React & TypeScript Specialist | Senior FE @ Spotify",
                                ResponseTimeEnum.WITHIN_12_HOURS,
                                List.of(sReact, sTS, sNode, sGraphQL, sGit, sFigma),
                                List.of("English", "German", "French"));

                Mentor mRaj = mentor(uRaj,
                                "Principal DevOps Engineer with deep expertise in cloud-native architecture. " +
                                                "AWS certified Solutions Architect. Helped startups scale from 0 to millions of users.",
                                "Cloud Architect & DevOps Lead | Principal Engineer @ AWS",
                                ResponseTimeEnum.WITHIN_1_DAY,
                                List.of(sDocker, sK8s, sAWS, sPython, sGit),
                                List.of("English", "Hindi"));

                Mentor mEmma = mentor(uEmma,
                                "Lead Data Scientist experienced in NLP, recommendation systems, and MLOps. " +
                                                "Published researcher with a passion for making AI accessible.",
                                "Data Scientist & ML Engineer | Lead DS @ Shopify",
                                ResponseTimeEnum.MORE_THAN_2_DAYS,
                                List.of(sPython, sMLSkill, sDataAn, sPostgres),
                                List.of("English", "Swedish"));

                mentorRepository.saveAll(List.of(mAlex, mSophia, mRaj, mEmma));

                // ════════════════════════════════════════════════════════════════
                // 6. MENTEES
                // ════════════════════════════════════════════════════════════════
                Mentee meJames = mentee(uJames,
                                "Learning Java and Spring Boot to land my first backend developer role");
                Mentee meYuki = mentee(uYuki, "Transitioning from QA to frontend development with React");
                Mentee meLucas = mentee(uLucas, "Building cloud computing and DevOps skills for career growth");
                menteeRepository.saveAll(List.of(meJames, meYuki, meLucas));

                // ════════════════════════════════════════════════════════════════
                // 7. WORK EXPERIENCE
                // ════════════════════════════════════════════════════════════════
                workForRepository.saveAll(List.of(
                                // Alex: Stripe SE → Google Tech Lead (current)
                                workFor(mAlex, coStripe, jtSE, "2015-03-01", "2019-12-31"),
                                workForCurrent(mAlex, coGoogle, jtTL, "2023-07-01"),
                                // Sophia: GitHub SE → Spotify Senior SE (current)
                                workFor(mSophia, coGitHub, jtSE, "2017-09-01", "2021-05-31"),
                                workForCurrent(mSophia, coSpotify, jtSSE, "2021-06-01"),
                                // Raj: Datadog DevOps → AWS DevOps (current)
                                workFor(mRaj, coDatadog, jtDevOps, "2016-01-01", "2020-08-31"),
                                workForCurrent(mRaj, coAWS, jtDevOps, "2020-09-01"),
                                // Emma: Meta DS → Shopify Lead DS (current)
                                workFor(mEmma, coMeta, jtDS, "2018-06-01", "2022-12-31"),
                                workForCurrent(mEmma, coShopify, jtDS, "2023-01-01")));

                // ════════════════════════════════════════════════════════════════
                // 8. PLANS (Session only — USD pricing)
                // ════════════════════════════════════════════════════════════════
                planSessionRepository.saveAll(List.of(
                                // Alex
                                session(mAlex, "Java Quick Review",
                                                "30-min code review: architecture patterns, debugging, and best practices.",
                                                25, 30),
                                session(mAlex, "Spring Boot Deep Dive",
                                                "60-min deep mentoring on Spring Boot security, JPA, and microservices.",
                                                45, 60),
                                // Sophia
                                session(mSophia, "React Code Review",
                                                "30-min session to optimize your React components and state management.",
                                                20, 30),
                                session(mSophia, "Frontend Career Coaching",
                                                "60-min career guidance: portfolio review, interview prep, salary negotiation.",
                                                40, 60),
                                // Raj
                                session(mRaj, "Docker & K8s Troubleshoot",
                                                "30-min hands-on troubleshooting for container and orchestration issues.",
                                                30, 30),
                                session(mRaj, "AWS Architecture Review",
                                                "60-min cloud architecture review with cost optimization recommendations.",
                                                55, 60),
                                // Emma
                                session(mEmma, "ML Model Review",
                                                "30-min review of your ML approach: model selection, evaluation, and tuning.",
                                                35, 30),
                                session(mEmma, "Data Pipeline Design",
                                                "60-min session on data engineering, ETL pipelines, and Python best practices.",
                                                60, 60)));

                // ════════════════════════════════════════════════════════════════
                // 9. SLOTS (ALL AVAILABLE — future dates)
                // ════════════════════════════════════════════════════════════════
                LocalDate today = LocalDate.now();
                slotRepository.saveAll(List.of(
                                // Alex (PST) — Mon/Wed/Fri
                                slot(mAlex, today.plusDays(1), "09:00", "09:30", SlotStatusEnum.AVAILABLE),
                                slot(mAlex, today.plusDays(1), "10:00", "11:00", SlotStatusEnum.AVAILABLE),
                                slot(mAlex, today.plusDays(1), "14:00", "14:30", SlotStatusEnum.AVAILABLE),
                                slot(mAlex, today.plusDays(3), "09:00", "09:30", SlotStatusEnum.AVAILABLE),
                                slot(mAlex, today.plusDays(3), "10:00", "11:00", SlotStatusEnum.AVAILABLE),
                                slot(mAlex, today.plusDays(5), "14:00", "15:00", SlotStatusEnum.AVAILABLE),
                                slot(mAlex, today.plusDays(7), "09:00", "10:00", SlotStatusEnum.AVAILABLE),
                                slot(mAlex, today.plusDays(8), "10:00", "11:00", SlotStatusEnum.AVAILABLE),
                                // Sophia (CET) — Tue/Thu/Sat
                                slot(mSophia, today.plusDays(2), "13:00", "13:30", SlotStatusEnum.AVAILABLE),
                                slot(mSophia, today.plusDays(2), "15:00", "16:00", SlotStatusEnum.AVAILABLE),
                                slot(mSophia, today.plusDays(4), "13:00", "13:30", SlotStatusEnum.AVAILABLE),
                                slot(mSophia, today.plusDays(4), "15:00", "16:00", SlotStatusEnum.AVAILABLE),
                                slot(mSophia, today.plusDays(6), "10:00", "10:30", SlotStatusEnum.AVAILABLE),
                                slot(mSophia, today.plusDays(9), "13:00", "14:00", SlotStatusEnum.AVAILABLE),
                                // Raj (IST) — Mon/Wed
                                slot(mRaj, today.plusDays(1), "16:00", "16:30", SlotStatusEnum.AVAILABLE),
                                slot(mRaj, today.plusDays(1), "17:00", "18:00", SlotStatusEnum.AVAILABLE),
                                slot(mRaj, today.plusDays(3), "16:00", "16:30", SlotStatusEnum.AVAILABLE),
                                slot(mRaj, today.plusDays(3), "17:00", "18:00", SlotStatusEnum.AVAILABLE),
                                slot(mRaj, today.plusDays(8), "16:00", "17:00", SlotStatusEnum.AVAILABLE),
                                // Emma (CET) — Fri/Sat
                                slot(mEmma, today.plusDays(5), "09:00", "09:30", SlotStatusEnum.AVAILABLE),
                                slot(mEmma, today.plusDays(5), "10:00", "11:00", SlotStatusEnum.AVAILABLE),
                                slot(mEmma, today.plusDays(6), "09:00", "09:30", SlotStatusEnum.AVAILABLE),
                                slot(mEmma, today.plusDays(6), "10:00", "11:00", SlotStatusEnum.AVAILABLE),
                                slot(mEmma, today.plusDays(12), "09:00", "10:00", SlotStatusEnum.AVAILABLE)));

                // ════════════════════════════════════════════════════════════════
                // ── BATCH 2: 4 thêm mentors ──────────────────────────────────
                // ════════════════════════════════════════════════════════════════

                // New companies & job titles
                Company coApple = company("Apple");
                Company coUber = company("Uber");
                Company coMicrosoft = company("Microsoft");
                Company coAdobe = company("Adobe");
                Company coNotion = company("Notion");
                Company coAirbnb = company("Airbnb");
                Company coNetflix = company("Netflix");
                Company coLinkedIn = company("LinkedIn");
                companyRepository.saveAll(
                                List.of(coApple, coUber, coMicrosoft, coAdobe, coNotion, coAirbnb, coNetflix,
                                                coLinkedIn));

                JobTitle jtPrincipal = jobTitle("Principal Engineer");
                JobTitle jtStaff = jobTitle("Staff Engineer");
                JobTitle jtVPE = jobTitle("VP of Engineering");
                JobTitle jtHeadDesign = jobTitle("Head of Design");
                jobTitleRepository.saveAll(List.of(jtPrincipal, jtStaff, jtVPE, jtHeadDesign));

                // New skills
                Skill sVue = skill("Vue.js");
                Skill sNextJS = skill("Next.js");
                Skill sGo = skill("Go");
                Skill sAzure = skill("Azure");
                Skill sGCP = skill("GCP");
                Skill sTerraform = skill("Terraform");
                Skill sKafka = skill("Kafka");
                Skill sRedis = skill("Redis");
                skillRepository.saveAll(List.of(sVue, sNextJS, sGo, sAzure, sGCP, sTerraform, sKafka, sRedis));

                // New sub-categories
                Category cFullstack = cat("Full-stack Development", cTech);
                Category cFintech = cat("Fintech", cBiz);
                categoryRepository.saveAll(List.of(cFullstack, cFintech));
                cFullstack.setSkills(new ArrayList<>(List.of(sNode, sReact, sNextJS, sVue, sPostgres, sRedis)));
                cFintech.setSkills(new ArrayList<>(List.of(sPM, sGo)));
                categoryRepository.saveAll(List.of(cFullstack, cFintech));
                cInfra.setSkills(new ArrayList<>(List.of(sDocker, sK8s, sAWS, sAzure, sGCP, sTerraform)));
                categoryRepository.save(cInfra);

                // Users
                User uMarcus = user("Marcus", "Johnson", "marcus.johnson@mentora.com", pw,
                                "United States", "America/New_York", GenderEnum.MALE, RoleEnum.MENTOR);
                User uPriya = user("Priya", "Sharma", "priya.sharma@mentora.com", pw,
                                "Singapore", "Asia/Singapore", GenderEnum.FEMALE, RoleEnum.MENTOR);
                User uLeo = user("Leo", "Wang", "leo.wang@mentora.com", pw,
                                "Canada", "America/Toronto", GenderEnum.MALE, RoleEnum.MENTOR);
                User uIsabel = user("Isabel", "Costa", "isabel.costa@mentora.com", pw,
                                "Portugal", "Europe/Lisbon", GenderEnum.FEMALE, RoleEnum.MENTOR);
                userRepository.saveAll(List.of(uMarcus, uPriya, uLeo, uIsabel));

                // New mentees
                User uAnna = user("Anna", "Schmidt", "anna.schmidt@mentora.com", pw,
                                "Germany", "Europe/Berlin", GenderEnum.FEMALE, RoleEnum.MENTEE);
                User uMohammed = user("Mohammed", "Al-Hassan", "m.alhassan@mentora.com", pw,
                                "United Arab Emirates", "Asia/Dubai", GenderEnum.MALE, RoleEnum.MENTEE);
                User uSeo = user("Seo-yeon", "Park", "seoyeon.park@mentora.com", pw,
                                "South Korea", "Asia/Seoul", GenderEnum.FEMALE, RoleEnum.MENTEE);
                userRepository.saveAll(List.of(uAnna, uMohammed, uSeo));

                // Mentor objects
                Mentor mMarcus = mentor(uMarcus,
                                "Senior iOS/Flutter engineer with 9 years building apps used by 50M+ users. " +
                                                "Former Apple engineer. Expert in SwiftUI, Flutter performance optimization, and app architecture.",
                                "Mobile Engineering Expert | Senior iOS @ Uber (ex-Apple)",
                                ResponseTimeEnum.WITHIN_1_DAY,
                                List.of(sSwift, sFlutter, sGit, sReact),
                                List.of("English"));

                Mentor mPriya = mentor(uPriya,
                                "Product leader with 8 years in Southeast Asia's tech scene. " +
                                                "Shipped products at Grab and Airbnb used by millions across APAC. " +
                                                "Specialist in 0-to-1 product development and growth strategies.",
                                "Senior Product Manager | PM @ Airbnb (ex-Grab)",
                                ResponseTimeEnum.WITHIN_12_HOURS,
                                List.of(sPM, sDataAn, sFigma),
                                List.of("English", "Hindi", "Bahasa Indonesia"));

                Mentor mLeo = mentor(uLeo,
                                "Principal Engineer at Microsoft with deep expertise in full-stack development. " +
                                                "Built scalable Node.js microservices handling 100k+ req/s. " +
                                                "Open-source contributor and technical blogger.",
                                "Principal Full-stack Engineer | Node.js & React Expert @ Microsoft",
                                ResponseTimeEnum.WITHIN_1_DAY,
                                List.of(sNode, sReact, sTS, sNextJS, sPostgres, sRedis, sKafka, sGit),
                                List.of("English", "Mandarin"));

                Mentor mIsabel = mentor(uIsabel,
                                "Head of Design at Notion with 10 years of UX/UI experience. " +
                                                "Previously design lead at Adobe. " +
                                                "Passionate about design systems, accessibility, and the intersection of design and engineering.",
                                "Head of Design | UX Expert & Design Systems @ Notion (ex-Adobe)",
                                ResponseTimeEnum.MORE_THAN_2_DAYS,
                                List.of(sFigma, sPM, sTS),
                                List.of("English", "Portuguese", "Spanish"));

                mentorRepository.saveAll(List.of(mMarcus, mPriya, mLeo, mIsabel));

                // New mentees
                Mentee meAnna = mentee(uAnna, "Improving React skills and transitioning from Angular to build modern SPAs");
                Mentee meMohammed = mentee(uMohammed, "Breaking into product management from a software engineering background");
                Mentee meSeo = mentee(uSeo, "Learning mobile development with Flutter to build my startup idea");
                menteeRepository.saveAll(List.of(meAnna, meMohammed, meSeo));

                // Work experience (batch 2)
                workForRepository.saveAll(List.of(
                                // Marcus: Apple → Uber (current)
                                workFor(mMarcus, coApple, jtSSE, "2015-08-01", "2022-01-31"),
                                workForCurrent(mMarcus, coUber, jtSSE, "2022-02-01"),
                                // Priya: Grab PM → Airbnb PM (current)
                                workFor(mPriya, coGrab, jtPM, "2016-05-01", "2021-11-30"),
                                workForCurrent(mPriya, coAirbnb, jtPM, "2022-01-01"),
                                // Leo: Netflix SE → Microsoft Principal (current)
                                workFor(mLeo, coNetflix, jtSE, "2017-03-01", "2020-09-30"),
                                workForCurrent(mLeo, coMicrosoft, jtPrincipal, "2023-02-01"),
                                // Isabel: Adobe → Notion Head of Design (current)
                                workFor(mIsabel, coAdobe, jtUX, "2014-06-01", "2022-06-30"),
                                workForCurrent(mIsabel, coNotion, jtHeadDesign, "2022-07-01")));

                // Plans (batch 2)
                planSessionRepository.saveAll(List.of(
                                // Marcus
                                session(mMarcus, "Swift Code Review",
                                                "30-min review of your Swift/SwiftUI code: architecture, performance, and App Store best practices.",
                                                25, 30),
                                session(mMarcus, "Flutter App Architecture",
                                                "60-min session on Flutter architecture patterns, state management, and performance tuning.",
                                                45, 60),
                                // Priya
                                session(mPriya, "PM Career Switch Coaching",
                                                "30-min coaching for engineers transitioning to product management.", 30, 30),
                                session(mPriya, "Product Strategy Workshop",
                                                "60-min deep dive into product roadmapping, prioritization frameworks, and stakeholder management.",
                                                55, 60),
                                // Leo
                                session(mLeo, "Node.js Performance Review",
                                                "30-min profiling and optimization session for your Node.js application.", 25, 30),
                                session(mLeo, "Full-stack System Design",
                                                "60-min system design session: from database schema to API design to frontend architecture.",
                                                50, 60),
                                // Isabel
                                session(mIsabel, "UX Portfolio Review",
                                                "30-min critique of your UX portfolio with actionable feedback to land your next role.", 30, 30),
                                session(mIsabel, "Design System Workshop",
                                                "60-min hands-on session building or improving your Figma design system.", 55, 60)));

                // Slots (batch 2)
                slotRepository.saveAll(List.of(
                                // Marcus (EST) — Tue/Thu
                                slot(mMarcus, today.plusDays(2), "10:00", "10:30", SlotStatusEnum.AVAILABLE),
                                slot(mMarcus, today.plusDays(2), "11:00", "12:00", SlotStatusEnum.AVAILABLE),
                                slot(mMarcus, today.plusDays(4), "10:00", "10:30", SlotStatusEnum.AVAILABLE),
                                slot(mMarcus, today.plusDays(4), "11:00", "12:00", SlotStatusEnum.AVAILABLE),
                                slot(mMarcus, today.plusDays(7), "14:00", "14:30", SlotStatusEnum.AVAILABLE),
                                slot(mMarcus, today.plusDays(11), "10:00", "11:00", SlotStatusEnum.AVAILABLE),
                                // Priya (SGT) — Mon/Wed/Fri
                                slot(mPriya, today.plusDays(1), "10:00", "10:30", SlotStatusEnum.AVAILABLE),
                                slot(mPriya, today.plusDays(1), "11:00", "12:00", SlotStatusEnum.AVAILABLE),
                                slot(mPriya, today.plusDays(3), "10:00", "10:30", SlotStatusEnum.AVAILABLE),
                                slot(mPriya, today.plusDays(3), "11:00", "12:00", SlotStatusEnum.AVAILABLE),
                                slot(mPriya, today.plusDays(5), "10:00", "10:30", SlotStatusEnum.AVAILABLE),
                                slot(mPriya, today.plusDays(10), "11:00", "12:00", SlotStatusEnum.AVAILABLE),
                                // Leo (EST) — Mon/Thu
                                slot(mLeo, today.plusDays(1), "18:00", "18:30", SlotStatusEnum.AVAILABLE),
                                slot(mLeo, today.plusDays(1), "19:00", "20:00", SlotStatusEnum.AVAILABLE),
                                slot(mLeo, today.plusDays(4), "18:00", "18:30", SlotStatusEnum.AVAILABLE),
                                slot(mLeo, today.plusDays(4), "19:00", "20:00", SlotStatusEnum.AVAILABLE),
                                slot(mLeo, today.plusDays(8), "18:00", "19:00", SlotStatusEnum.AVAILABLE),
                                slot(mLeo, today.plusDays(11), "18:00", "18:30", SlotStatusEnum.AVAILABLE),
                                // Isabel (WET) — Wed/Fri/Sat
                                slot(mIsabel, today.plusDays(3), "14:00", "14:30", SlotStatusEnum.AVAILABLE),
                                slot(mIsabel, today.plusDays(3), "15:00", "16:00", SlotStatusEnum.AVAILABLE),
                                slot(mIsabel, today.plusDays(5), "14:00", "14:30", SlotStatusEnum.AVAILABLE),
                                slot(mIsabel, today.plusDays(5), "15:00", "16:00", SlotStatusEnum.AVAILABLE),
                                slot(mIsabel, today.plusDays(6), "10:00", "10:30", SlotStatusEnum.AVAILABLE),
                                slot(mIsabel, today.plusDays(13), "14:00", "15:00", SlotStatusEnum.AVAILABLE)));

                // ════════════════════════════════════════════════════════════════
                // 10. DISCOUNTS
                // ════════════════════════════════════════════════════════════════
                Discount dWelcome = new Discount();
                dWelcome.setDiscountName("WELCOME10");
                dWelcome.setDiscountType(DiscountTypeEnum.PERCENTAGE);
                dWelcome.setDiscountValue(BigDecimal.valueOf(10));
                dWelcome.setStartDate(LocalDate.now());
                dWelcome.setEndDate(LocalDate.now().plusDays(90));
                dWelcome.setActive(true);
                dWelcome.setUsageLimit(100);
                dWelcome.setUsedCount(0);

                Discount dSummer = new Discount();
                dSummer.setDiscountName("SUMMER25");
                dSummer.setDiscountType(DiscountTypeEnum.PERCENTAGE);
                dSummer.setDiscountValue(BigDecimal.valueOf(25));
                dSummer.setStartDate(LocalDate.of(2026, 6, 1));
                dSummer.setEndDate(LocalDate.of(2026, 8, 31));
                dSummer.setActive(true);
                dSummer.setUsageLimit(50);
                dSummer.setUsedCount(0);

                Discount dFlat = new Discount();
                dFlat.setDiscountName("FLAT10");
                dFlat.setDiscountType(DiscountTypeEnum.FIXED_AMOUNT);
                dFlat.setDiscountValue(BigDecimal.valueOf(10));
                dFlat.setStartDate(LocalDate.now());
                dFlat.setEndDate(LocalDate.now().plusDays(60));
                dFlat.setActive(true);
                dFlat.setUsageLimit(200);
                dFlat.setUsedCount(0);

                discountRepository.saveAll(List.of(dWelcome, dSummer, dFlat));

                System.out.println(
                                ">>> [DataInitializer] ✅ Done! 8 mentors, 6 mentees, 16 plans, 49 slots, 3 discounts seeded.");
                } catch (Exception e) {
                        System.err.println(">>> [DataInitializer] ❌ FAILED! Root cause: " + e.getMessage());
                        Throwable cause = e.getCause();
                        while (cause != null) {
                                System.err.println(">>> [DataInitializer]   Caused by: " + cause.getMessage());
                                cause = cause.getCause();
                        }
                        throw e;
                }
        }


        // ══════════════════════════════════════════════════════════════════════
        // HELPER METHODS
        // ══════════════════════════════════════════════════════════════════════

        private User user(String first, String last, String email, String pw,
                        String country, String timezone, GenderEnum gender, RoleEnum role) {
                User u = new User();
                u.setFirstName(first);
                u.setLastName(last);
                u.setEmail(email);
                u.setPassword(pw);
                u.setCountry(country);
                u.setTimezone(timezone);
                u.setGender(gender);
                u.setRole(role);
                u.setStatus(UserStatusEnum.ACTIVE);
                u.setEmailVerified(true);
                return u;
        }

        private Mentor mentor(User user, String bio, String headline,
                        ResponseTimeEnum responseTime, List<Skill> skills, List<String> languages) {
                Mentor m = new Mentor();
                m.setUser(user);
                m.setBio(bio);
                m.setHeadline(headline);
                m.setResponseTime(responseTime);
                m.setTotalMeetings(0);
                m.setTotalStars(0);
                m.setTotalReviews(0);
                m.setStripeOnboarded(false);
                m.setSkills(skills);
                m.setLanguages(languages);
                return m;
        }

        private Mentee mentee(User user, String goal) {
                Mentee m = new Mentee();
                m.setUser(user);
                m.setGoal(goal);
                return m;
        }

        private Category cat(String name, Category parent) {
                Category c = new Category();
                c.setCategoryName(name);
                c.setParent(parent);
                return c;
        }

        private Skill skill(String name) {
                Skill s = new Skill();
                s.setSkillName(name);
                return s;
        }

        private Company company(String name) {
                Company c = new Company();
                c.setCompanyName(name);
                return c;
        }

        private JobTitle jobTitle(String name) {
                JobTitle j = new JobTitle();
                j.setJobTitleName(name);
                return j;
        }

        private WorkFor workFor(Mentor mentor, Company company, JobTitle jt, String start, String end) {
                WorkFor w = new WorkFor();
                w.setId(new WorkForId(mentor.getMentorId(), company.getCompanyId()));
                w.setMentor(mentor);
                w.setCompany(company);
                w.setJobTitle(jt);
                w.setStartDate(LocalDate.parse(start));
                w.setEndDate(LocalDate.parse(end));
                w.setCurrent(false);
                return w;
        }

        private WorkFor workForCurrent(Mentor mentor, Company company, JobTitle jt, String start) {
                WorkFor w = new WorkFor();
                w.setId(new WorkForId(mentor.getMentorId(), company.getCompanyId()));
                w.setMentor(mentor);
                w.setCompany(company);
                w.setJobTitle(jt);
                w.setStartDate(LocalDate.parse(start));
                w.setEndDate(null);
                w.setCurrent(true);
                return w;
        }

        private PlanSession session(Mentor mentor, String name, String desc, double charge, int duration) {
                PlanSession p = new PlanSession();
                p.setMentor(mentor);
                p.setSessionName(name);
                p.setDescription(desc);
                p.setCharge(BigDecimal.valueOf(charge));
                p.setStatus(PlanStatusEnum.ACTIVE);
                p.setSessionsDuration(duration);
                return p;
        }

        private Slot slot(Mentor mentor, LocalDate date, String start, String end, SlotStatusEnum status) {
                Slot s = new Slot();
                s.setMentor(mentor);
                s.setDate(date);
                s.setStartTime(LocalTime.parse(start));
                s.setEndTime(LocalTime.parse(end));
                s.setStatus(status);
                return s;
        }
}
