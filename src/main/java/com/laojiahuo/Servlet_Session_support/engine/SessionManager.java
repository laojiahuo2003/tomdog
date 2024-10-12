package com.laojiahuo.Servlet_Session_support.engine;


import com.laojiahuo.Servlet_Session_support.utils.DateUtils;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器，用于管理所有的 HttpSession 对象
 */
public class SessionManager implements Runnable {

    final Logger logger = LoggerFactory.getLogger(getClass());

    // 与当前会话管理器相关联的 Servlet 上下文对象
    final ServletContextImpl servletContext;

    // 使用线程安全的 ConcurrentHashMap 存储会话对象，以确保在多线程环境下能够安全地访问和修改会话数据
    final Map<String, HttpSessionImpl> sessions = new ConcurrentHashMap<>();

    // 会话的失效时间，单位为秒。当会话超过这个时间没有活动时，它将被认为是失效的
    final int inactiveInterval;

    // 构造方法，传入 ServletContext 和失效间隔时间
    public SessionManager(ServletContextImpl servletContext, int interval) {
        this.servletContext = servletContext;
        this.inactiveInterval = interval;

        // 创建一个守护线程，用于定期检查并清除过期的会话
        Thread t = new Thread(this, "Session-Cleanup-Thread");
        t.setDaemon(true);  // 设置为守护线程，确保 JVM 退出时不会阻塞
        t.start();  // 启动清理线程
    }

    // 获取指定会话 ID 的 HttpSession。如果该会话不存在，则创建一个新的会话
    public HttpSession getSession(String sessionId) {
        // 从会话映射中获取会话对象
        HttpSessionImpl session = sessions.get(sessionId);

        // 如果会话不存在，则创建一个新的会话
        if (session == null) {
            session = new HttpSessionImpl(this.servletContext, sessionId, inactiveInterval);
            sessions.put(sessionId, session);  // 将新会话放入映射中
        } else {
            // 如果会话存在，则更新最后访问时间为当前时间
            session.lastAccessedTime = System.currentTimeMillis();
        }

        // 返回会话对象
        return session;
    }

    // 移除指定的会话对象
    public void remove(HttpSession session) {
        this.sessions.remove(session.getId());  // 根据会话 ID 从映射中移除
    }

    // 清理线程的运行逻辑，用于定期检查并移除过期会话
    @Override
    public void run() {
        for (;;) {  // 无限循环，定期执行清理任务
            try {
                // 每 60 秒（1 分钟）执行一次清理
                Thread.sleep(60_000L);
            } catch (InterruptedException e) {
                // 如果线程被中断，退出循环
                break;
            }

            // 获取当前系统时间
            long now = System.currentTimeMillis();

            // 遍历所有会话，检查哪些会话已经过期
            for (String sessionId : sessions.keySet()) {
                HttpSession session = sessions.get(sessionId);

                // 检查会话是否过期：如果最后访问时间加上失效间隔已经超过当前时间，则会话过期
                if (session.getLastAccessedTime() + session.getMaxInactiveInterval() * 1000L < now) {
                    // 记录日志，表示会话已过期并被移除
                    logger.warn("remove expired session: {}, last access time: {}", sessionId,
                            DateUtils.formatDateTimeGMT(session.getLastAccessedTime()));

                    // 使该会话无效（即标记为已过期，并移除）
                    session.invalidate();
                }
            }
        }
    }
}
