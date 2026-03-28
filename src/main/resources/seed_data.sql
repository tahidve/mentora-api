-- ============================================================
-- MENTORIA — INIT SEED DATA
-- Run sau khi backend đã tạo bảng (ddl-auto=update hoặc create)
-- Password BCrypt của tất cả accounts: Abc@12345
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. USERS (1 admin, 5 mentors, 5 mentees)
-- password = BCrypt('Abc@12345')
-- ============================================================
INSERT INTO users (user_id, first_name, last_name, email, password, phone, avatar_url, country, timezone, gender, role, status, email_verified, created_at, refresh_token) VALUES
-- Admin
(1,  'Admin',   'Mentoria', 'admin@mentoria.vn',           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000001', NULL,      'Vietnam',       'Asia/Ho_Chi_Minh',  'MALE',   'ADMIN',  'ACTIVE', true, NOW(), NULL),

-- Mentors
(2,  'Nguyen',  'Minh Duc', 'duc.nguyen@mentor.vn',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000002', NULL,      'Vietnam',       'Asia/Ho_Chi_Minh',  'MALE',   'MENTOR', 'ACTIVE', true, NOW(), NULL),
(3,  'Tran',    'Minh Hoa', 'hoa.tran@mentor.vn',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000003', NULL,      'Vietnam',       'Asia/Ho_Chi_Minh',  'FEMALE', 'MENTOR', 'ACTIVE', true, NOW(), NULL),
(4,  'Le',      'Van Thanh','thanh.le@mentor.vn',           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000004', NULL,      'Singapore',     'Asia/Singapore',    'MALE',   'MENTOR', 'ACTIVE', true, NOW(), NULL),
(5,  'Pham',    'Ngoc Linh','linh.pham@mentor.vn',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000005', NULL,      'United States', 'America/New_York',  'FEMALE', 'MENTOR', 'ACTIVE', true, NOW(), NULL),
(6,  'Vo',      'Thanh Son','son.vo@mentor.vn',             '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000006', NULL,      'Japan',         'Asia/Tokyo',        'MALE',   'MENTOR', 'ACTIVE', true, NOW(), NULL),

-- Mentees
(7,  'Bui',     'Thi Lan',  'lan.bui@mentee.vn',           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000007', NULL,      'Vietnam',       'Asia/Ho_Chi_Minh',  'FEMALE', 'MENTEE', 'ACTIVE', true, NOW(), NULL),
(8,  'Hoang',   'Van Nam',  'nam.hoang@mentee.vn',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000008', NULL,      'Vietnam',       'Asia/Ho_Chi_Minh',  'MALE',   'MENTEE', 'ACTIVE', true, NOW(), NULL),
(9,  'Nguyen',  'Thu Hien', 'hien.nguyen@mentee.vn',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000009', NULL,      'Vietnam',       'Asia/Ho_Chi_Minh',  'FEMALE', 'MENTEE', 'ACTIVE', true, NOW(), NULL),
(10, 'Do',      'Manh Hung','hung.do@mentee.vn',            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000010', NULL,      'Vietnam',       'Asia/Ho_Chi_Minh',  'MALE',   'MENTEE', 'ACTIVE', true, NOW(), NULL),
(11, 'Dang',    'Thi Mai',  'mai.dang@mentee.vn',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdubhiw36', '0900000011', NULL,      'Vietnam',       'Asia/Ho_Chi_Minh',  'FEMALE', 'MENTEE', 'ACTIVE', true, NOW(), NULL);

-- ============================================================
-- 2. MENTORS profile
-- ============================================================
INSERT INTO mentors (mentor_id, bio, headline, cv_url, avatar_url, response_time, total_meetings, total_stars, total_reviews) VALUES
(2,
 'Senior Software Engineer với 8+ năm kinh nghiệm trong lĩnh vực Java/Spring Boot và Microservices. Từng làm việc tại VNG, Tiki, và các startup fintech lớn tại Việt Nam. Đam mê chia sẻ kiến thức và giúp fresher/junior developer tăng tốc sự nghiệp.',
 'Senior Java Engineer @ Tiki | Spring Boot | Microservices | 8+ YOE',
 NULL, NULL, 'WITHIN_AN_HOUR', 24, 112, 22),

(3,
 'Data Scientist và AI Engineer với nền tảng vững từ đại học Bách Khoa. Chuyên về Machine Learning, Deep Learning và ứng dụng AI trong production. Hiện đang work tại một AI startup series B.',
 'Data Scientist | ML Engineer | TensorFlow & PyTorch | Ex-VinAI',
 NULL, NULL, 'WITHIN_A_FEW_HOURS', 18, 81, 17),

(4,
 'Full-Stack Developer và Technical Lead với 6 năm kinh nghiệm xây dựng sản phẩm B2B tại Singapore. Chuyên React, Node.js và AWS Cloud Architecture. Đã mentor 50+ junior developer.',
 'Tech Lead @ Singapore Startup | React | Node.js | AWS | Career Coach',
 NULL, NULL, 'WITHIN_A_DAY', 31, 143, 29),

(5,
 'Software Engineer tại Big Tech (Seattle). Chuyên về System Design, Distributed Systems, và phỏng vấn FAANG. Đã pass phỏng vấn tại Google, Meta, Amazon và coaching thành công 30+ candidates.',
 'Software Engineer @ Big Tech | System Design | FAANG Interview Coach',
 NULL, NULL, 'WITHIN_AN_HOUR', 45, 218, 43),

(6,
 'Backend Developer và Cloud Architect với 5 năm kinh nghiệm tại Nhật Bản. Chuyên về Golang, Kubernetes, và Cloud Native. Giúp bạn hiểu văn hóa làm việc IT tại Nhật và chuẩn bị cho cuộc sống IT ở nước ngoài.',
 'Cloud Architect @ Japan | Golang | K8s | Working Abroad Advisor',
 NULL, NULL, 'WITHIN_A_FEW_HOURS', 12, 52, 10);

-- ============================================================
-- 3. MENTEES profile
-- ============================================================
INSERT INTO mentees (mentee_id, goal) VALUES
(7,  'Muốn chuyển từ fresher sang junior Java developer trong 6 tháng tới.'),
(8,  'Cần nâng cao kiến thức về System Design để phỏng vấn các công ty lớn.'),
(9,  'Muốn học Machine Learning để chuyển ngành từ backend sang Data Science.'),
(10, 'Cần chuẩn bị portfolio và kỹ năng phỏng vấn để xin việc ở Singapore.'),
(11, 'Muốn nắm vững React và Next.js để build full-stack web app.');

-- ============================================================
-- 4. SKILLS
-- ============================================================
INSERT INTO skills (skill_id, skill_name) VALUES
(1,  'Java'),
(2,  'Spring Boot'),
(3,  'Microservices'),
(4,  'Python'),
(5,  'Machine Learning'),
(6,  'Deep Learning'),
(7,  'React'),
(8,  'Node.js'),
(9,  'TypeScript'),
(10, 'AWS'),
(11, 'Docker'),
(12, 'Kubernetes'),
(13, 'System Design'),
(14, 'Golang'),
(15, 'PostgreSQL'),
(16, 'MongoDB'),
(17, 'Redis'),
(18, 'Kafka'),
(19, 'CI/CD'),
(20, 'Data Structures & Algorithms'),
(21, 'Next.js'),
(22, 'TensorFlow'),
(23, 'PyTorch'),
(24, 'SQL'),
(25, 'Git'),
(26, 'Agile/Scrum'),
(27, 'Leadership'),
(28, 'Career Coaching'),
(29, 'English for IT'),
(30, 'Cloud Native');

-- ============================================================
-- 5. CATEGORIES (parent + children)
-- ============================================================
INSERT INTO categories (category_id, category_name, super_category_id) VALUES
-- Parents
(1,  'Software Engineering',    NULL),
(2,  'Data & AI',               NULL),
(3,  'Cloud & DevOps',          NULL),
(4,  'Career Development',      NULL),
(5,  'Mobile Development',      NULL),

-- Children of Software Engineering
(6,  'Backend Development',     1),
(7,  'Frontend Development',    1),
(8,  'Full-Stack',              1),

-- Children of Data & AI
(9,  'Machine Learning',        2),
(10, 'Data Engineering',        2),
(11, 'AI/LLM',                  2),

-- Children of Cloud & DevOps
(12, 'AWS',                     3),
(13, 'Kubernetes & Docker',     3),
(14, 'CI/CD & Automation',      3),

-- Children of Career Development
(15, 'Interview Prep',          4),
(16, 'Resume & Portfolio',      4),
(17, 'Working Abroad',          4);

-- ============================================================
-- 6. OWN_SKILL (Category → Skill)
-- ============================================================
INSERT INTO own_skill (category_id, skill_id) VALUES
-- Backend
(6, 1),(6, 2),(6, 3),(6, 14),(6, 15),(6, 17),(6, 18),
-- Frontend
(7, 7),(7, 9),(7, 21),
-- Full Stack
(8, 7),(8, 8),(8, 9),(8, 10),(8, 24),
-- Machine Learning
(9, 4),(9, 5),(9, 6),(9, 22),(9, 23),
-- Data Engineering
(10, 4),(10, 15),(10, 16),(10, 24),
-- Cloud & DevOps
(12, 10),(13, 11),(13, 12),(14, 19),
-- Cloud Native
(3, 30),
-- Interview & Career
(15, 13),(15, 20),(15, 28),
(16, 28),(17, 29);

-- ============================================================
-- 7. SET_SKILL (Mentor → Skill)
-- ============================================================
INSERT INTO set_skill (mentor_id, skill_id) VALUES
-- Duc (Java/Spring expert)
(2, 1),(2, 2),(2, 3),(2, 11),(2, 15),(2, 17),(2, 18),(2, 25),(2, 26),
-- Hoa (Data Scientist)
(3, 4),(3, 5),(3, 6),(3, 22),(3, 23),(3, 15),(3, 16),(3, 24),
-- Thanh (Full-Stack/AWS)
(4, 7),(4, 8),(4, 9),(4, 10),(4, 11),(4, 21),(4, 25),(4, 27),
-- Linh (System Design/FAANG)
(5, 13),(5, 20),(5, 1),(5, 28),(5, 29),(5, 10),(5, 12),
-- Son (Golang/K8s/Japan)
(6, 14),(6, 11),(6, 12),(6, 10),(6, 30),(6, 19),(6, 29);

-- ============================================================
-- 8. MENTOR_LANGUAGES
-- ============================================================
INSERT INTO mentor_languages (mentor_id, language) VALUES
(2, 'Vietnamese'),(2, 'English'),
(3, 'Vietnamese'),(3, 'English'),
(4, 'Vietnamese'),(4, 'English'),
(5, 'Vietnamese'),(5, 'English'),
(6, 'Vietnamese'),(6, 'English'),(6, 'Japanese');

-- ============================================================
-- 9. COMPANIES & JOB TITLES & WORK EXPERIENCE
-- ============================================================
INSERT INTO companies (company_id, company_name) VALUES
(1, 'Tiki'),
(2, 'VNG Corporation'),
(3, 'VinAI Research'),
(4, 'Grab'),
(5, 'Sea Group'),
(6, 'FPT Software'),
(7, 'Shopee'),
(8, 'Google'),
(9, 'Bosch Vietnam'),
(10, 'NTT Data Japan');

INSERT INTO job_titles (job_title_id, job_title_name) VALUES
(1,  'Software Engineer'),
(2,  'Senior Software Engineer'),
(3,  'Data Scientist'),
(4,  'Senior Data Scientist'),
(5,  'Full-Stack Developer'),
(6,  'Tech Lead'),
(7,  'Backend Developer'),
(8,  'Machine Learning Engineer'),
(9,  'Cloud Architect'),
(10, 'Software Engineering Manager');

-- work_for composite PK: (mentor_id, company_id)
INSERT INTO work_for (mentor_id, company_id, job_title_id, start_date, end_date, is_current) VALUES
-- Duc
(2, 2, 1,  '2016-06-01', '2019-08-31', false),  -- VNG SE
(2, 1, 2,  '2019-09-01', NULL,         true),   -- Tiki Senior SE (hiện tại)
-- Hoa
(3, 3, 8,  '2019-01-01', '2021-12-31', false),  -- VinAI ML Engineer
(3, 4, 3,  '2022-01-01', NULL,         true),   -- Grab Data Scientist (hiện tại)
-- Thanh
(4, 6, 5,  '2018-03-01', '2020-06-30', false),  -- FPT Full-Stack
(4, 7, 6,  '2020-07-01', NULL,         true),   -- Shopee Tech Lead (hiện tại)
-- Linh
(5, 9, 1,  '2015-08-01', '2018-05-31', false),  -- Bosch SE
(5, 8, 2,  '2018-06-01', NULL,         true),   -- Google Senior SE (hiện tại)
-- Son
(6, 6, 7,  '2017-04-01', '2019-12-31', false),  -- FPT Backend
(6, 10, 9, '2020-01-01', NULL,         true);   -- NTT Japan Cloud Architect (hiện tại)

-- ============================================================
-- 10. PLANS — parent table (JOINED inheritance)
-- plan_type: 'SESSION' | 'MENTORSHIP'
-- ============================================================
INSERT INTO plans (id, description, charge, status, plan_type, mentor_id) VALUES
-- Duc's plans
(1,  'Buổi 1-on-1 sâu về Java Core, OOP, Collections, và Exception Handling. Phù hợp fresher.',       150000.00, 'ACTIVE', 'SESSION',    2),
(2,  'Deep dive vào Spring Boot: REST API, Security, JPA/Hibernate. Review code thực tế của bạn.',     250000.00, 'ACTIVE', 'SESSION',    2),
(3,  'Mentorship 3 tháng toàn diện: Java Backend từ zero → junior ready. Daily check-in qua Slack.',   800000.00, 'ACTIVE', 'MENTORSHIP', 2),

-- Hoa's plans
(4,  '1-on-1 về Python cho Data Science: Pandas, NumPy, Matplotlib. Bắt đầu từ basics.',               180000.00, 'ACTIVE', 'SESSION',    3),
(5,  'Review và guidance cho ML project của bạn. Code review, model evaluation, deployment tips.',      300000.00, 'ACTIVE', 'SESSION',    3),
(6,  'Mentorship 3 tháng: ML Engineer roadmap từ Python basics đến production ML models.',             1000000.00, 'ACTIVE', 'MENTORSHIP', 3),

-- Thanh's plans
(7,  'React + TypeScript session: Hooks, State Management, Component patterns.',                        200000.00, 'ACTIVE', 'SESSION',    4),
(8,  'AWS Architecture session: EC2, S3, RDS, Lambda. Thiết kế hệ thống scalable.',                    280000.00, 'ACTIVE', 'SESSION',    4),
(9,  'Full-Stack mentorship: React + Node.js + AWS. Build 2 real projects trong 3 tháng.',              900000.00, 'ACTIVE', 'MENTORSHIP', 4),

-- Linh's plans (FAANG coach)
(10, 'Mock Interview: 1 Leetcode medium + System Design 30 phút. Feedback chi tiết.',                  350000.00, 'ACTIVE', 'SESSION',    5),
(11, 'System Design deep dive: Design Twitter, Uber, YouTube. Scalability patterns.',                   400000.00, 'ACTIVE', 'SESSION',    5),
(12, 'FAANG Interview Prep mentorship: 4 tháng, weekly mock + lộ trình Leetcode cá nhân hóa.',        1500000.00, 'ACTIVE', 'MENTORSHIP', 5),

-- Son's plans
(13, 'Golang từ A-Z: Goroutines, Channels, HTTP server. Cho veteran backend developer.',               220000.00, 'ACTIVE', 'SESSION',    6),
(14, 'Kubernetes thực chiến: Deploy app lên K8s cluster, Helm, Ingress, HPA.',                        290000.00, 'ACTIVE', 'SESSION',    6);

-- ============================================================
-- 11. PLAN_SESSIONS (child table cho SESSION plans)
-- ============================================================
INSERT INTO plan_sessions (sessions_id, session_name, sessions_duration) VALUES
(1,  'Java Core Foundations',         60),
(2,  'Spring Boot Deep Dive',         90),
(4,  'Python for Data Science',       60),
(5,  'ML Project Review',             90),
(7,  'React & TypeScript Essentials', 60),
(8,  'AWS Architecture Consultation', 60),
(10, 'Mock Interview Session',        90),
(11, 'System Design Workshop',        90),
(13, 'Golang Bootcamp Session',       60),
(14, 'Kubernetes in Practice',        90);

-- ============================================================
-- 12. PLAN_MENTORSHIPS (child table cho MENTORSHIP plans)
-- ============================================================
INSERT INTO plan_mentorships (mentorships_id, plan_name, calls_per_month, minutes_per_call) VALUES
(3,  'Java Backend Mastery',       4, 60),
(6,  'ML Engineer Roadmap',        4, 60),
(9,  'Full-Stack Builder',         6, 60),
(12, 'FAANG Ready Program',        8, 60);

-- ============================================================
-- 13. MENTORSHIPS_BENEFITS (composite PK: plan_id + sequence_no)
-- ============================================================
INSERT INTO mentorships_benefits (plan_id, sequence_no, benefit_content) VALUES
-- Java Backend Mastery (plan 3)
(3, 1, 'Weekly 1-on-1 code review session (60 phút)'),
(3, 2, 'Unlimited Q&A qua Slack trong giờ hành chính'),
(3, 3, 'Lộ trình học cá nhân hóa theo level hiện tại'),
(3, 4, 'Mock interview cuối tháng với feedback chi tiết'),
(3, 5, 'Resource list: sách, khóa học, blog chọn lọc'),

-- ML Engineer Roadmap (plan 6)
(6, 1, '4 buổi 1-on-1 mỗi tháng (60 phút/buổi)'),
(6, 2, 'Review notebook và code ML projects của bạn'),
(6, 3, 'Hướng dẫn deploy model lên production (FastAPI + Docker)'),
(6, 4, 'Portfolio review: Kaggle profile, GitHub, LinkedIn'),

-- Full-Stack Builder (plan 9)
(9, 1, '6 buổi call mỗi tháng, linh hoạt đặt lịch'),
(9, 2, 'Guide xây dựng 2 dự án thực tế vào portfolio'),
(9, 3, 'Code review toàn bộ PR của bạn'),
(9, 4, 'Career advice: resume, job search strategy'),
(9, 5, 'Access Discord community của mentor network'),

-- FAANG Ready Program (plan 12)
(12, 1, '8 buổi mock interview mỗi tháng (technical + behavioral)'),
(12, 2, 'Lộ trình Leetcode cá nhân hóa theo timeline công ty target'),
(12, 3, 'System Design flashcards và cheat sheets độc quyền'),
(12, 4, 'Resume review và LinkedIn optimization'),
(12, 5, 'Referral support nếu mentor có connection tại target company');

-- ============================================================
-- 14. DISCOUNTS
-- ============================================================
INSERT INTO discounts (discount_id, discount_name, discount_type, discount_value, start_date, end_date, active, usage_limit, used_count) VALUES
(1, 'WELCOME10',     'PERCENTAGE',    10.00, '2025-01-01', '2026-12-31', true,  100, 23),
(2, 'SUMMER25',      'PERCENTAGE',    25.00, '2026-06-01', '2026-08-31', true,  50,  8),
(3, 'FLAT50K',       'FIXED_AMOUNT',  50000, '2026-01-01', '2026-12-31', true,  200, 41),
(4, 'NEWMEMBER15',   'PERCENTAGE',    15.00, '2026-01-01', '2026-06-30', true,  30,  30),
(5, 'BLACKFRIDAY30', 'PERCENTAGE',    30.00, '2026-11-25', '2026-11-30', false, 100, 0);

-- ============================================================
-- 15. SLOTS — tạo nhiều slot trong tương lai
-- Mentors: 2=Duc, 3=Hoa, 4=Thanh, 5=Linh, 6=Son
-- ============================================================
INSERT INTO slots (slot_id, mentor_id, date, start_time, end_time, status) VALUES
-- Duc (id=2) — slots tuần này và tuần sau
(1,  2, DATE_ADD(CURDATE(), INTERVAL 1 DAY),  '09:00:00', '10:00:00', 'AVAILABLE'),
(2,  2, DATE_ADD(CURDATE(), INTERVAL 1 DAY),  '14:00:00', '15:30:00', 'AVAILABLE'),
(3,  2, DATE_ADD(CURDATE(), INTERVAL 3 DAY),  '09:00:00', '10:00:00', 'AVAILABLE'),
(4,  2, DATE_ADD(CURDATE(), INTERVAL 3 DAY),  '15:00:00', '16:30:00', 'AVAILABLE'),
(5,  2, DATE_ADD(CURDATE(), INTERVAL 5 DAY),  '10:00:00', '11:30:00', 'AVAILABLE'),
(6,  2, DATE_ADD(CURDATE(), INTERVAL 7 DAY),  '09:00:00', '10:00:00', 'AVAILABLE'),
(7,  2, DATE_ADD(CURDATE(), INTERVAL 8 DAY),  '14:00:00', '15:30:00', 'AVAILABLE'),
(8,  2, DATE_ADD(CURDATE(), INTERVAL 10 DAY), '10:00:00', '11:00:00', 'AVAILABLE'),

-- Hoa (id=3)
(9,  3, DATE_ADD(CURDATE(), INTERVAL 2 DAY),  '08:00:00', '09:00:00', 'AVAILABLE'),
(10, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY),  '19:00:00', '20:00:00', 'AVAILABLE'),
(11, 3, DATE_ADD(CURDATE(), INTERVAL 4 DAY),  '08:00:00', '09:30:00', 'AVAILABLE'),
(12, 3, DATE_ADD(CURDATE(), INTERVAL 6 DAY),  '08:00:00', '09:00:00', 'AVAILABLE'),
(13, 3, DATE_ADD(CURDATE(), INTERVAL 9 DAY),  '19:00:00', '20:00:00', 'AVAILABLE'),
(14, 3, DATE_ADD(CURDATE(), INTERVAL 11 DAY), '08:00:00', '09:30:00', 'AVAILABLE'),

-- Thanh (id=4) — Singapore timezone, buổi tối VN
(15, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY),  '20:00:00', '21:00:00', 'AVAILABLE'),
(16, 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY),  '20:00:00', '21:30:00', 'AVAILABLE'),
(17, 4, DATE_ADD(CURDATE(), INTERVAL 5 DAY),  '19:30:00', '20:30:00', 'AVAILABLE'),
(18, 4, DATE_ADD(CURDATE(), INTERVAL 8 DAY),  '20:00:00', '21:00:00', 'AVAILABLE'),
(19, 4, DATE_ADD(CURDATE(), INTERVAL 10 DAY), '20:00:00', '21:30:00', 'AVAILABLE'),

-- Linh (id=5) — US timezone, sáng sớm VN
(20, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY),  '07:00:00', '08:30:00', 'AVAILABLE'),
(21, 5, DATE_ADD(CURDATE(), INTERVAL 4 DAY),  '07:00:00', '08:30:00', 'AVAILABLE'),
(22, 5, DATE_ADD(CURDATE(), INTERVAL 6 DAY),  '06:30:00', '08:00:00', 'AVAILABLE'),
(23, 5, DATE_ADD(CURDATE(), INTERVAL 9 DAY),  '07:00:00', '08:30:00', 'AVAILABLE'),
(24, 5, DATE_ADD(CURDATE(), INTERVAL 12 DAY), '07:00:00', '08:30:00', 'AVAILABLE'),

-- Son (id=6) — Japan timezone
(25, 6, DATE_ADD(CURDATE(), INTERVAL 1 DAY),  '21:00:00', '22:00:00', 'AVAILABLE'),
(26, 6, DATE_ADD(CURDATE(), INTERVAL 4 DAY),  '21:00:00', '22:30:00', 'AVAILABLE'),
(27, 6, DATE_ADD(CURDATE(), INTERVAL 7 DAY),  '21:00:00', '22:00:00', 'AVAILABLE'),
(28, 6, DATE_ADD(CURDATE(), INTERVAL 11 DAY), '21:00:00', '22:30:00', 'AVAILABLE'),

-- BOOKED slots (sẽ dùng cho booking/meeting data)
(29, 2, DATE_ADD(CURDATE(), INTERVAL -5 DAY), '09:00:00', '10:00:00', 'BOOKED'),  -- completed booking
(30, 3, DATE_ADD(CURDATE(), INTERVAL -3 DAY), '08:00:00', '09:30:00', 'BOOKED'),  -- completed booking
(31, 5, DATE_ADD(CURDATE(), INTERVAL  2 DAY), '07:00:00', '08:30:00', 'BOOKED'),  -- upcoming booking
(32, 4, DATE_ADD(CURDATE(), INTERVAL  3 DAY), '20:00:00', '21:00:00', 'BOOKED');  -- upcoming scheduling

-- ============================================================
-- 16. PLAN_REGISTRATION
-- ============================================================
INSERT INTO plan_registration (registration_id, message, discount_id, created_at) VALUES
(1, 'Tôi muốn nắm vững Java OOP. Hiện đang ở level fresher 6 tháng kinh nghiệm.', 1, DATE_ADD(NOW(), INTERVAL -6 DAY)),
(2, 'Cần học Python từ đầu để chuyển sang Data Science.',                           3, DATE_ADD(NOW(), INTERVAL -4 DAY)),
(3, 'Chuẩn bị phỏng vấn Shopee/Sea Group trong 2 tháng tới.',                       NULL, DATE_ADD(NOW(), INTERVAL -1 DAY)),
(4, 'Muốn deploy React app lên AWS cho dự án freelance.',                           NULL, DATE_ADD(NOW(), INTERVAL  0 DAY));

-- ============================================================
-- 17. BOOKINGS
-- ============================================================
INSERT INTO bookings (booking_id, mentee_id, plan_id, plan_registration_id, status, created_at) VALUES
(1, 7, 1, 1, 'COMPLETED',  DATE_ADD(NOW(), INTERVAL -6 DAY)),  -- Lan đã học xong với Duc
(2, 9, 4, 2, 'COMPLETED',  DATE_ADD(NOW(), INTERVAL -4 DAY)),  -- Hien đã học xong với Hoa
(3, 8, 10, 3, 'CONFIRMED', DATE_ADD(NOW(), INTERVAL -1 DAY)),  -- Nam sắp học với Linh
(4, 11, 7, 4, 'CONFIRMED', DATE_ADD(NOW(), INTERVAL  0 DAY));  -- Mai sắp học với Thanh

-- ============================================================
-- 18. INVOICES
-- ============================================================
INSERT INTO invoices (invoice_id, registration_id, mentee_id, payment_status, currency, amount_subtotal, amount_total, paid_time, note, created_at, payment_method) VALUES
(1, 1, 7,  'PAID',    'VND', 150000.00, 135000.00, DATE_ADD(NOW(), INTERVAL -5 DAY), 'Áp dụng WELCOME10 (-10%)',  DATE_ADD(NOW(), INTERVAL -6 DAY), 'DIRECT'),
(2, 2, 9,  'PAID',    'VND', 180000.00, 130000.00, DATE_ADD(NOW(), INTERVAL -3 DAY), 'Áp dụng FLAT50K (-50,000)', DATE_ADD(NOW(), INTERVAL -4 DAY), 'DIRECT'),
(3, 3, 8,  'PENDING', 'VND', 350000.00, 350000.00, NULL,                             NULL,                         DATE_ADD(NOW(), INTERVAL -1 DAY), 'DIRECT'),
(4, 4, 11, 'PENDING', 'VND', 200000.00, 200000.00, NULL,                             NULL,                         DATE_ADD(NOW(), INTERVAL  0 DAY), 'DIRECT');

-- ============================================================
-- 19. MEETINGS
-- Snapshot: startTime/endTime/date từ slot
-- ============================================================
INSERT INTO meetings (meeting_id, invoice_id, slot_id, status, location, start_time, end_time, date) VALUES
-- Completed meetings (trong quá khứ)
(1, 1, 29, 'COMPLETED', 'https://meet.google.com/abc-defg-hij',
   '09:00:00', '10:00:00', DATE_ADD(CURDATE(), INTERVAL -5 DAY)),
(2, 2, 30, 'COMPLETED', 'https://meet.google.com/xyz-uvwx-yzp',
   '08:00:00', '09:30:00', DATE_ADD(CURDATE(), INTERVAL -3 DAY)),

-- Upcoming meetings
(3, 3, 31, 'SCHEDULED', 'https://zoom.us/j/123456789?pwd=abc',
   '07:00:00', '08:30:00', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(4, 4, 32, 'PENDING', NULL,
   '20:00:00', '21:00:00', DATE_ADD(CURDATE(), INTERVAL 3 DAY));

-- ============================================================
-- 20. FEEDBACKS (chỉ cho completed meetings)
-- ============================================================
INSERT INTO feedbacks (feedback_id, plan_registration_id, stars, content, sent_time) VALUES
(1, 1, 5, 'Anh Đức giải thích rất rõ ràng và kiên nhẫn. Sau buổi học tôi đã hiểu rõ hơn về Java Collections và cách tối ưu code. Highly recommend!', DATE_ADD(NOW(), INTERVAL -4 DAY)),
(2, 2, 4, 'Chị Hoa hướng dẫn rất tận tình. Tôi đã biết cách dùng Pandas để phân tích dữ liệu. Chỉ mong buổi học dài hơn một chút.', DATE_ADD(NOW(), INTERVAL -2 DAY));

-- Update mentor ratings
UPDATE mentors SET
  total_reviews = 23,
  total_stars = 115  -- avg 5.0 * 23 ≈ 115
WHERE mentor_id = 2;

UPDATE mentors SET
  total_reviews = 18,
  total_stars = 72   -- avg 4.0 * 18 = 72
WHERE mentor_id = 3;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- VERIFY — chạy sau khi seed để check
-- ============================================================
SELECT 'users'             AS tbl, COUNT(*) AS cnt FROM users
UNION ALL SELECT 'mentors',           COUNT(*) FROM mentors
UNION ALL SELECT 'mentees',           COUNT(*) FROM mentees
UNION ALL SELECT 'skills',            COUNT(*) FROM skills
UNION ALL SELECT 'categories',        COUNT(*) FROM categories
UNION ALL SELECT 'set_skill',         COUNT(*) FROM set_skill
UNION ALL SELECT 'mentor_languages',  COUNT(*) FROM mentor_languages
UNION ALL SELECT 'companies',         COUNT(*) FROM companies
UNION ALL SELECT 'job_titles',        COUNT(*) FROM job_titles
UNION ALL SELECT 'work_for',          COUNT(*) FROM work_for
UNION ALL SELECT 'plans',             COUNT(*) FROM plans
UNION ALL SELECT 'plan_sessions',     COUNT(*) FROM plan_sessions
UNION ALL SELECT 'plan_mentorships',  COUNT(*) FROM plan_mentorships
UNION ALL SELECT 'mentorships_benefits', COUNT(*) FROM mentorships_benefits
UNION ALL SELECT 'discounts',         COUNT(*) FROM discounts
UNION ALL SELECT 'slots',             COUNT(*) FROM slots
UNION ALL SELECT 'plan_registration', COUNT(*) FROM plan_registration
UNION ALL SELECT 'bookings',          COUNT(*) FROM bookings
UNION ALL SELECT 'invoices',          COUNT(*) FROM invoices
UNION ALL SELECT 'meetings',          COUNT(*) FROM meetings
UNION ALL SELECT 'feedbacks',         COUNT(*) FROM feedbacks;
