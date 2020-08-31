package com.zhy.springbootshirorsa.shiro.session;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.stereotype.Component;

/**
 * 会话监听器用于监听会话创建、过期及停止事件
 * @author Dell
 *https://www.jianshu.com/p/5aa03c2d118e
 */
@Component
public class ShiroSessionListener implements SessionListener{

	private final AtomicInteger sessionCount = new AtomicInteger(0);
	
	/**
	 * 会话开始
	 */
	@Override
	public void onStart(Session session) {
		sessionCount.incrementAndGet();
		System.out.println("会话开始+1=:"+sessionCount.get());
	}

	/**
	 * 会话结束
	 */
	@Override
	public void onStop(Session session) {
		sessionCount.decrementAndGet();
		 System.out.println("会话结束-1==:"+sessionCount.get());
	}

	/**
	 * 会话过期
	 */
	@Override
	public void onExpiration(Session session) {
		sessionCount.decrementAndGet();
		System.out.println("会话过期-1==:"+sessionCount.get());
	}
	
	/**
     * 获取在线人数
     * @return
     */
    public  AtomicInteger getSessionCount() {
        return sessionCount;
    }
	
}
