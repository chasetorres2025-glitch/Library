CREATE DATABASE IF NOT EXISTS library_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_system;

DROP TABLE IF EXISTS sys_log;
DROP TABLE IF EXISTS book_borrow;
DROP TABLE IF EXISTS book_stock;
DROP TABLE IF EXISTS book_info;
DROP TABLE IF EXISTS book_category;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;

CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_name VARCHAR(20) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(20) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(100) COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密后）',
    real_name VARCHAR(20) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(11) UNIQUE COMMENT '手机号',
    email VARCHAR(50) COMMENT '邮箱',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE book_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    category_name VARCHAR(30) NOT NULL UNIQUE COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort INT DEFAULT 0 COMMENT '排序权重',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

CREATE TABLE book_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    book_isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN编号',
    book_name VARCHAR(100) NOT NULL COMMENT '图书名称',
    author VARCHAR(50) NOT NULL COMMENT '作者',
    publisher VARCHAR(50) NOT NULL COMMENT '出版社',
    publish_time DATE NOT NULL COMMENT '出版时间',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '图书定价',
    cover_url VARCHAR(255) COMMENT '封面图片URL',
    description TEXT COMMENT '图书简介',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES book_category(id),
    INDEX idx_book_isbn (book_isbn),
    INDEX idx_book_name (book_name),
    INDEX idx_author (author),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书信息表';

CREATE TABLE book_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    book_id BIGINT NOT NULL UNIQUE COMMENT '图书ID',
    total_num INT NOT NULL DEFAULT 0 COMMENT '总库存数量',
    borrow_num INT NOT NULL DEFAULT 0 COMMENT '已借出数量',
    available_num INT DEFAULT 0 COMMENT '可借数量',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (book_id) REFERENCES book_info(id),
    INDEX idx_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书库存表';

CREATE TABLE book_borrow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    user_id BIGINT NOT NULL COMMENT '借阅用户ID',
    borrow_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    due_time DATETIME NOT NULL COMMENT '应归还时间',
    return_time DATETIME COMMENT '实际归还时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-未还，1-已还，2-逾期）',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    FOREIGN KEY (book_id) REFERENCES book_info(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_due_time (due_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书借阅表';

CREATE TABLE sys_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '操作人ID',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',
    content VARCHAR(255) COMMENT '操作内容',
    ip VARCHAR(20) COMMENT '操作IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

INSERT INTO sys_role (role_name, role_code, description) VALUES 
('管理员', 'ADMIN', '系统管理员，拥有所有权限'),
('读者', 'READER', '普通读者，可借阅图书');

INSERT INTO sys_user (username, password, real_name, phone, email, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 'admin@library.com', 1, 1);

-- V2.0 新增表：需要先删除有外键依赖的表
DROP TABLE IF EXISTS book_tag_relation;
DROP TABLE IF EXISTS book_tag;
DROP TABLE IF EXISTS ai_conversation;
DROP TABLE IF EXISTS user_behavior;
DROP TABLE IF EXISTS user_profile;

-- 扩展现有 book_info 表
ALTER TABLE book_info ADD COLUMN difficulty_level VARCHAR(20) COMMENT '难度等级：beginner/intermediate/advanced';
ALTER TABLE book_info ADD COLUMN ai_tags JSON COMMENT 'AI生成的标签建议';
ALTER TABLE book_info ADD COLUMN embedding_vector JSON COMMENT '书籍向量化表示';

-- 用户画像表
CREATE TABLE user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    reading_level VARCHAR(20) DEFAULT 'beginner' COMMENT '阅读水平：beginner/intermediate/advanced',
    preferred_categories JSON COMMENT '偏好分类：["计算机", "文学"]',
    skill_tags JSON COMMENT '技能标签：["Python", "数据分析"]',
    reading_goals JSON COMMENT '阅读目标：{"monthly_target": 4, "focus_areas": ["技术提升"]}',
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户画像表';

-- 用户行为记录表
CREATE TABLE user_behavior (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT COMMENT '图书ID',
    behavior_type VARCHAR(30) NOT NULL COMMENT '行为类型：view/borrow/return/favorite/rate/search',
    duration INT COMMENT '浏览时长（秒）',
    rating TINYINT COMMENT '评分1-5',
    session_id VARCHAR(50) COMMENT '会话ID',
    metadata JSON COMMENT '额外元数据',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_time (user_id, created_at),
    INDEX idx_book_id (book_id),
    INDEX idx_user_book (user_id, book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为记录表';

-- AI 对话历史表
CREATE TABLE ai_conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id VARCHAR(50) NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色：user/assistant',
    content TEXT NOT NULL COMMENT '对话内容',
    intent_type VARCHAR(50) COMMENT '意图类型',
    intent_confidence DECIMAL(3,2) COMMENT '意图置信度',
    recommend_result JSON COMMENT '推荐结果',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_session (user_id, session_id),
    INDEX idx_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话历史表';

-- 图书标签表
CREATE TABLE book_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_type VARCHAR(30) NOT NULL COMMENT '标签类型：subject/difficulty/scene/emotion/style/target',
    parent_id BIGINT DEFAULT 0 COMMENT '父标签ID',
    sort INT DEFAULT 0 COMMENT '排序',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    description VARCHAR(200) COMMENT '标签描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_name_type (tag_name, tag_type),
    INDEX idx_type (tag_type),
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书标签表';

-- 图书-标签关联表
CREATE TABLE book_tag_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    weight DECIMAL(3,2) DEFAULT 1.00 COMMENT '标签权重 0.00-1.00',
    source VARCHAR(20) DEFAULT 'manual' COMMENT '来源：manual/ai/auto',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_book_tag (book_id, tag_id),
    INDEX idx_book_id (book_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书-标签关联表';

-- 初始化基础标签数据
INSERT INTO book_tag (tag_name, tag_type, description) VALUES
-- 主题标签
('Python', 'subject', 'Python编程语言'),
('Java', 'subject', 'Java编程语言'),
('数据分析', 'subject', '数据分析相关'),
('项目管理', 'subject', '项目管理方法'),
('Web开发', 'subject', 'Web应用开发'),
('机器学习', 'subject', '机器学习与人工智能'),
('文学', 'subject', '文学类书籍'),
('哲学', 'subject', '哲学思想'),
('管理学', 'subject', '管理学理论'),
-- 难度标签
('入门', 'difficulty', '适合初学者'),
('进阶', 'difficulty', '适合有一定基础的读者'),
('高级', 'difficulty', '适合专业人士'),
-- 场景标签
('技能学习', 'scene', '用于技能学习'),
('职业提升', 'scene', '用于职业能力提升'),
('兴趣阅读', 'scene', '用于兴趣爱好阅读'),
-- 情感标签
('励志', 'emotion', '激励人心'),
('治愈', 'emotion', '温暖治愈'),
('震撼', 'emotion', '令人震撼'),
-- 风格标签
('幽默', 'style', '写作风格幽默'),
('严谨', 'style', '学术严谨'),
('通俗', 'style', '通俗易懂'),
-- 目标读者
('新手', 'target', '适合新手'),
('专业人士', 'target', '适合专业人士'),
('青少年', 'target', '适合青少年');
