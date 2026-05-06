#!/bin/bash
# ============================================================
# Library Management System - Service Management Script
# Usage: ./start.sh [start|stop|restart|health]
# ============================================================

set -e

# ---------- 基础配置 ----------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/library-backend"
FRONTEND_DIR="$SCRIPT_DIR/library-frontend"

BACKEND_PORT=8080
FRONTEND_PORT=5173       # vite dev 默认端口

BACKEND_JAR="$BACKEND_DIR/target/library-backend-1.0.0.jar"
BACKEND_LOG="$SCRIPT_DIR/logs/backend.log"
FRONTEND_LOG="$SCRIPT_DIR/logs/frontend.log"
BACKEND_PID_FILE="$SCRIPT_DIR/logs/backend.pid"
FRONTEND_PID_FILE="$SCRIPT_DIR/logs/frontend.pid"

# ---------- 颜色输出 ----------
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info()    { echo -e "${GREEN}[INFO]${NC}  $*"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $*"; }
log_section() { echo -e "\n${BLUE}=== $* ===${NC}"; }

# ---------- 工具函数 ----------
ensure_log_dir() {
    mkdir -p "$SCRIPT_DIR/logs"
}

is_port_in_use() {
    local port=$1
    lsof -i :"$port" -t >/dev/null 2>&1
}

get_pid_by_port() {
    local port=$1
    lsof -i :"$port" -t 2>/dev/null | head -1
}

read_pid_file() {
    local pid_file=$1
    if [ -f "$pid_file" ]; then
        local pid
        pid=$(cat "$pid_file")
        if kill -0 "$pid" 2>/dev/null; then
            echo "$pid"
            return 0
        fi
    fi
    return 1
}

# ---------- 后端操作 ----------
build_backend() {
    log_section "构建后端"
    if [ ! -f "$BACKEND_JAR" ]; then
        log_info "未检测到 JAR 包，开始 Maven 构建..."
        cd "$BACKEND_DIR"
        mvn clean package -DskipTests -q
        cd "$SCRIPT_DIR"
        log_info "后端构建完成: $BACKEND_JAR"
    else
        log_info "检测到已有 JAR 包，跳过构建（如需重新构建请删除 target 目录）"
    fi
}

start_backend() {
    log_section "启动后端服务"

    if is_port_in_use "$BACKEND_PORT"; then
        log_warn "端口 $BACKEND_PORT 已被占用，后端服务可能已在运行（PID: $(get_pid_by_port "$BACKEND_PORT")）"
        return 0
    fi

    build_backend

    log_info "启动后端 Spring Boot 服务，端口: $BACKEND_PORT"
    nohup java -jar "$BACKEND_JAR" \
        --spring.profiles.active=default \
        > "$BACKEND_LOG" 2>&1 &
    local pid=$!
    echo "$pid" > "$BACKEND_PID_FILE"

    # 等待启动（最多 30 秒）
    local timeout=30
    local count=0
    while ! is_port_in_use "$BACKEND_PORT"; do
        sleep 1
        count=$((count + 1))
        if [ $count -ge $timeout ]; then
            log_error "后端启动超时（${timeout}s），请查看日志: $BACKEND_LOG"
            return 1
        fi
        echo -n "."
    done
    echo ""
    log_info "后端服务启动成功 (PID: $pid)"
}

stop_backend() {
    log_section "停止后端服务"

    local pid
    pid=$(read_pid_file "$BACKEND_PID_FILE") || pid=$(get_pid_by_port "$BACKEND_PORT")

    if [ -z "$pid" ]; then
        log_warn "后端服务未在运行"
        return 0
    fi

    log_info "停止后端服务 (PID: $pid)..."
    kill "$pid" 2>/dev/null

    # 等待进程退出（最多 15 秒）
    local count=0
    while kill -0 "$pid" 2>/dev/null; do
        sleep 1
        count=$((count + 1))
        if [ $count -ge 15 ]; then
            log_warn "进程未响应，强制终止..."
            kill -9 "$pid" 2>/dev/null
            break
        fi
    done

    rm -f "$BACKEND_PID_FILE"
    log_info "后端服务已停止"
}

# ---------- 前端操作 ----------
start_frontend() {
    log_section "启动前端服务"

    if is_port_in_use "$FRONTEND_PORT"; then
        log_warn "端口 $FRONTEND_PORT 已被占用，前端服务可能已在运行（PID: $(get_pid_by_port "$FRONTEND_PORT")）"
        return 0
    fi

    if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
        log_info "未检测到 node_modules，执行 npm install..."
        cd "$FRONTEND_DIR"
        npm install --silent
        cd "$SCRIPT_DIR"
    fi

    log_info "启动前端 Vite 开发服务器，端口: $FRONTEND_PORT"
    cd "$FRONTEND_DIR"
    nohup npm run dev -- --port "$FRONTEND_PORT" --host \
        > "$FRONTEND_LOG" 2>&1 &
    local pid=$!
    echo "$pid" > "$FRONTEND_PID_FILE"
    cd "$SCRIPT_DIR"

    # 等待启动（最多 20 秒）
    local timeout=20
    local count=0
    while ! is_port_in_use "$FRONTEND_PORT"; do
        sleep 1
        count=$((count + 1))
        if [ $count -ge $timeout ]; then
            log_error "前端启动超时（${timeout}s），请查看日志: $FRONTEND_LOG"
            return 1
        fi
        echo -n "."
    done
    echo ""
    log_info "前端服务启动成功 (PID: $pid)"
}

stop_frontend() {
    log_section "停止前端服务"

    local pid
    pid=$(read_pid_file "$FRONTEND_PID_FILE") || pid=$(get_pid_by_port "$FRONTEND_PORT")

    if [ -z "$pid" ]; then
        log_warn "前端服务未在运行"
        return 0
    fi

    log_info "停止前端服务 (PID: $pid)..."
    kill "$pid" 2>/dev/null

    local count=0
    while kill -0 "$pid" 2>/dev/null; do
        sleep 1
        count=$((count + 1))
        if [ $count -ge 10 ]; then
            kill -9 "$pid" 2>/dev/null
            break
        fi
    done

    rm -f "$FRONTEND_PID_FILE"
    log_info "前端服务已停止"
}

# ---------- 主命令 ----------
cmd_start() {
    ensure_log_dir
    log_section "启动所有服务"
    start_backend
    start_frontend
    echo ""
    log_info "所有服务已启动"
    log_info "  后端接口: http://localhost:${BACKEND_PORT}"
    log_info "  前端页面: http://localhost:${FRONTEND_PORT}"
    log_info "  后端日志: $BACKEND_LOG"
    log_info "  前端日志: $FRONTEND_LOG"
}

cmd_stop() {
    log_section "停止所有服务"
    stop_frontend
    stop_backend
    echo ""
    log_info "所有服务已停止"
}

cmd_restart() {
    log_section "重启所有服务"
    cmd_stop
    sleep 2
    cmd_start
}

cmd_health() {
    log_section "健康检查"

    # 后端健康检查
    echo -n "  后端服务 (port $BACKEND_PORT): "
    if is_port_in_use "$BACKEND_PORT"; then
        local backend_pid
        backend_pid=$(get_pid_by_port "$BACKEND_PORT")
        # 尝试调用 Spring Boot 健康端点
        local http_code
        http_code=$(curl -s -o /dev/null -w "%{http_code}" \
            --connect-timeout 3 \
            "http://localhost:${BACKEND_PORT}/actuator/health" 2>/dev/null || echo "000")
        if [ "$http_code" = "200" ] || [ "$http_code" = "000" ]; then
            # actuator 可能未开启，端口通就认为正常
            echo -e "${GREEN}RUNNING${NC} (PID: $backend_pid)"
        else
            echo -e "${YELLOW}DEGRADED${NC} (port open, HTTP: $http_code, PID: $backend_pid)"
        fi
    else
        echo -e "${RED}STOPPED${NC}"
    fi

    # 前端健康检查
    echo -n "  前端服务 (port $FRONTEND_PORT): "
    if is_port_in_use "$FRONTEND_PORT"; then
        local frontend_pid
        frontend_pid=$(get_pid_by_port "$FRONTEND_PORT")
        echo -e "${GREEN}RUNNING${NC} (PID: $frontend_pid)"
    else
        echo -e "${RED}STOPPED${NC}"
    fi

    # MySQL 连通性检查（可选）
    echo -n "  MySQL     (port 3306):          "
    if is_port_in_use 3306; then
        echo -e "${GREEN}RUNNING${NC}"
    else
        echo -e "${RED}STOPPED${NC} (请确认 MySQL 已启动)"
    fi

    echo ""
}

# ---------- 入口 ----------
case "$1" in
    start)
        cmd_start
        ;;
    stop)
        cmd_stop
        ;;
    restart)
        cmd_restart
        ;;
    health)
        cmd_health
        ;;
    *)
        echo "用法: $0 {start|stop|restart|health}"
        echo ""
        echo "  start    启动后端（Spring Boot）和前端（Vite）服务"
        echo "  stop     停止所有服务"
        echo "  restart  重启所有服务"
        echo "  health   查看各服务运行状态"
        exit 1
        ;;
esac
