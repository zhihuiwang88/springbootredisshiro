package com.zhy.springbootshirorsa.redis.tongshi;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @autor cwm
 * @date 2020/2/21
 * @desc redis session
 **/
public class RedisSessionDao1 extends AbstractSessionDAO {
    @Autowired
    RedisUtil1 redisUtil;

    @Value("${expire}")
    private Integer expire;

    private final String SHIRO_SESSION_PREFIX = "shiro-session:";

    private String getkey(String key) {
        return SHIRO_SESSION_PREFIX + key;
    }

    /**
     * 保存session
     */
    private void saveSession(Session session) {
        if (session != null && session.getId() != null) {
            redisUtil.set(getkey(session.getId().toString()), session, expire);
//            redisUtil.set(getkey(session.getId().toString()),session,600);
        }
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable serializable) {
        System.out.println("sessioin request");
        if (serializable == null) {
            return null;
        }
        String key = getkey(serializable.toString());
        Object o = null;
        try {
            o = redisUtil.get(key);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return (Session) o;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        redisUtil.del(getkey(session.getId().toString()));
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<String> objects = redisUtil.keys(SHIRO_SESSION_PREFIX + "*");
        Set<Session> sessions = new HashSet<>();
        if (CollectionUtils.isEmpty(objects)) {
            return sessions;
        }
        for (String str : objects) {
            Object o = redisUtil.get(str);
            sessions.add((Session) o);
        }
        return sessions;
    }
}
