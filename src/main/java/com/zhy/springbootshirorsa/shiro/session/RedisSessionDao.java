package com.zhy.springbootshirorsa.shiro.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import com.zhy.springbootshirorsa.redis.cache.RedisUtil;

/**
 * 自定义SessionDao继承AbstractSessionDAO重写方法将session持久化到redis等缓存中
 * https://www.imooc.com/video/16963
 * 用于会话的CRUD
 * https://www.jianshu.com/p/5aa03c2d118e
 * @author Dell
 *
 */
public class RedisSessionDao extends AbstractSessionDAO {

	@Autowired
	private RedisUtil redisUtil;

	private final String SHIRO_SESSIOM_PREFIX = "shiro-session:";
	// 设置过期时间，单位 妙
	private final int EXPIRE_TIME = 600;
	
	private String getKey(String key) {
		return (SHIRO_SESSIOM_PREFIX + key);
	}

	/**
	 * 更新会话；如更新会话最后访问时间/停止会话/设置超时时间/设置移除属性等会调用
	 */
	@Override
	public void update(Session session) throws UnknownSessionException {
		saveSession(session);
	}

	/**
	 * 删除会话；当会话过期/会话停止（如用户退出时）会调用
	 */
	@Override
	public void delete(Session session) {
		if (session == null || session.getId() == null) {
			return;
		}
		String key = getKey(session.getId().toString());
		redisUtil.del(key);
	}

	/**
	 * 获取当前所有活跃用户，如果用户量多此方法影响性能
	 */
	@Override
	public Collection<Session> getActiveSessions() {
		Set<String> keys = redisUtil.getKeys(SHIRO_SESSIOM_PREFIX + "*");
		Set<Session> sessions = new HashSet<>();
		if (CollectionUtils.isEmpty(keys)) {
			return sessions;
		}
		for (String key : keys) {
			Session session = (Session) SerializationUtils.deserialize(redisUtil.get(key));
			sessions.add(session);
		}
		return sessions;
	}

	/**
	 * 创建session 如DefaultSessionManager在创建完session后会调用该方法；
	 * 如保存到关系数据库/文件系统/NoSQL数据库；即可以实现会话的持久化；返回会话ID；
	 * 主要此处返回的ID.equals(session.getId())；
	 *
	 */
	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);
		saveSession(session);
		return sessionId;
	}

	/**
	 * 根据会话ID获取会话
	 */
	@Override
    protected Session doReadSession(Serializable serializable) {
        System.out.println("sessioin request");
        if (serializable == null) {
            return null;
        }
        String key = getKey(serializable.toString());
        byte[] value = null;
        try {
        	 value =  redisUtil.get(key);
        } catch (Exception e) {
            e.getMessage();
        }
        return (Session) SerializationUtils.deserialize(value);
    }
	
	/**
	 * 保存
	 * @param session
	 */
	private void saveSession(Session session) {
		// 判断session
		if (session != null && session.getId() != null) {
			String key = getKey(session.getId().toString());
			byte[] value = SerializationUtils.serialize(session);
			redisUtil.set(key, value);
			redisUtil.expire(key, EXPIRE_TIME);
		}
	}

}
