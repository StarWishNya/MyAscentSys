# MyAscentSys Frontend Dockerfile
FROM node:16-alpine

# 设置工作目录
WORKDIR /app

# 创建非root用户
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nextjs -u 1001

# 复制package文件
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production && npm cache clean --force

# 复制应用文件
COPY --chown=nextjs:nodejs . .

# 切换到非root用户
USER nextjs

# 暴露端口
EXPOSE 3000

# 设置默认环境变量
ENV PORT=3000
ENV BACKEND_HOST=localhost
ENV BACKEND_PORT=8083

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD node -e "require('http').get('http://localhost:3000/health', (res) => { process.exit(res.statusCode === 200 ? 0 : 1) })"

# 启动应用
CMD ["node", "proxy-server.js"]
