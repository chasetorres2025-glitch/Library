# 图书管理系统

基于 Vue 3 + SpringBoot 2.7.x + MySQL 的图书管理系统

## 技术栈

### 后端
- SpringBoot 2.7.18
- MyBatis-Plus 3.5.5
- Spring Security
- MySQL 8.0
- Lombok
- JWT

### 前端
- Vue 3
- Element Plus
- Vue Router
- Pinia
- Axios
- Vite

## 项目结构

```
Library_demo/
├── library-backend/          # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/library/
│   │   │   │   ├── controller/   # 控制器层
│   │   │   │   ├── service/      # 服务层
│   │   │   │   ├── mapper/       # 数据访问层
│   │   │   │   ├── entity/       # 实体类
│   │   │   │   ├── dto/          # 数据传输对象
│   │   │   │   ├── vo/           # 视图对象
│   │   │   │   ├── config/       # 配置类
│   │   │   │   ├── exception/    # 异常处理
│   │   │   │   └── utils/        # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── schema.sql
│   │   └── test/
│   └── pom.xml
│
└── library-frontend/         # 前端项目
    ├── src/
    │   ├── api/               # API 接口封装
    │   ├── views/             # 页面组件
    │   ├── router/            # 路由配置
    │   ├── store/             # 状态管理
    │   ├── utils/             # 工具类
    │   ├── App.vue
    │   └── main.js
    ├── package.json
    └── vite.config.js
```

## 数据库配置

1. 确保已安装 MySQL 8.0
2. 创建数据库并执行 SQL 脚本：
   ```bash
   mysql -u root -p < library-backend/src/main/resources/schema.sql
   ```
3. 修改 [application.yml](file:///Users/torres/Documents/trae_projects/Library_demo/library-backend/src/main/resources/application.yml) 中的数据库密码

## 后端启动

1. 进入后端目录：
   ```bash
   cd library-backend
   ```

2. 使用 Maven 启动：
   ```bash
   mvn spring-boot:run
   ```

后端服务将在 http://localhost:8080 启动

## 前端启动

1. 进入前端目录：
   ```bash
   cd library-frontend
   ```

2. 安装依赖（如果尚未安装）：
   ```bash
   npm install
   ```

3. 启动开发服务器：
   ```bash
   npm run dev
   ```

前端服务将在 http://localhost:5173 启动

## 默认账号

- 管理员账号：`admin` / `123456`
- 角色：管理员（ADMIN）

## 功能模块

### 管理员功能
- 用户管理：新增、编辑、删除、重置密码
- 图书分类管理：新增、编辑、删除分类
- 图书信息管理：新增、编辑、删除图书
- 借阅管理：为用户办理借阅、归还图书
- 借阅记录查询：查看所有借阅记录

### 读者功能
- 图书浏览：查看图书列表和详情
- 我的借阅：查看个人借阅记录
- 图书借阅：通过管理员办理借阅

## API 接口

### 认证接口
- POST `/api/auth/login` - 用户登录

### 用户管理
- GET `/api/user/list` - 获取用户列表
- GET `/api/user/{id}` - 获取用户详情
- POST `/api/user` - 新增用户
- PUT `/api/user` - 更新用户
- DELETE `/api/user/{id}` - 删除用户
- PUT `/api/user/reset-password/{id}` - 重置密码

### 图书分类
- GET `/api/category/list` - 获取分类列表
- GET `/api/category/all` - 获取所有分类
- POST `/api/category` - 新增分类
- PUT `/api/category` - 更新分类
- DELETE `/api/category/{id}` - 删除分类

### 图书管理
- GET `/api/book/list` - 获取图书列表
- GET `/api/book/{id}` - 获取图书详情
- POST `/api/book` - 新增图书
- PUT `/api/book` - 更新图书
- DELETE `/api/book/{id}` - 删除图书

### 借阅管理
- GET `/api/borrow/list` - 获取借阅记录列表
- GET `/api/borrow/my-borrows` - 获取我的借阅记录
- POST `/api/borrow` - 借阅图书
- PUT `/api/borrow/return/{id}` - 归还图书

## 注意事项

1. 首次运行前请确保 MySQL 服务已启动
2. 修改数据库密码后需同步更新 [application.yml](file:///Users/torres/Documents/trae_projects/Library_demo/library-backend/src/main/resources/application.yml) 配置
3. 前端默认连接后端地址为 http://localhost:8080，如需修改请更新 [request.js](file:///Users/torres/Documents/trae_projects/Library_demo/library-frontend/src/utils/request.js)
