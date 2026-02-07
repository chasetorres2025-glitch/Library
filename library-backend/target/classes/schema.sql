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
