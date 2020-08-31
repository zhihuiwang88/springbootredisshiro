package com.zhy.springbootshirorsa.shirotongshi;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * @author cwm
 * @date 2020/2/21
 * @desc shiro session 监听
 **/
public class ShiroSessionListener1 implements SessionListener {
    /**
     * juc 线程安全自增
     */
    private final  AtomicInteger sessionCount=new AtomicInteger(0);

    /**
     * 会话开始
     * @param session
     */
    @Override
    public void onStart(Session session) {
        //在线人数加一
        sessionCount.incrementAndGet();
    }

    /**
     * 会话结束
     * @param session
     */
    @Override
    public void onStop(Session session) {
        //在线人数减一
        sessionCount.decrementAndGet();
    }

    /**
     * 会话过期
     * @param session
     */
    @Override
    public void onExpiration(Session session) {
        //在线人数减一
        sessionCount.decrementAndGet();
    }

    /**
     * 获取在线人数
     * @return
     */
    public  AtomicInteger getSessionCount()
    {
        return sessionCount;
    }
}
