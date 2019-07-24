package com.ruoyi.web.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * websocket链接
 * 注：本websocket服务断无需建立websocket链接
 * 服务端无需接收客户端消息
 * 用户后台代码指定画面刷新
 */
@ServerEndpoint(value = "/socketServer/{code}")
@Component
@Slf4j
public class SocketServer {

    private Session session;
    private String code;
    /**
     * 使用CopyOnWriteArraySet线程安全
     */
    private static ConcurrentMap<String, CopyOnWriteArraySet<SocketServer>> map = new ConcurrentHashMap<>();

    /**
     * 创建链接-触发
     *
     * @param code
     * @param session
     */
    @OnOpen
    public void open(@PathParam("code") String code, Session session) {
        this.code = code;
        this.session = session;

        if (map.containsKey(code)) {
            map.get(code).add(this);
        } else {
            CopyOnWriteArraySet<SocketServer> sets = new CopyOnWriteArraySet<>();
            sets.add(this);
            map.put(code, sets);
        }
    }

    /**
     * 收到信息时触发
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("接收到客户端发送消息为:{}" , message);
    }

    /**
     * 连接关闭触发
     */
    @OnClose
    public void onClose() {
        if (map.containsKey(code)) {
            map.get(code).remove(this);
            if (map.get(code) == null || map.get(code).size() == 0) {
                map.remove(code);
            }
        }
    }

    /**
     * 发生错误时触发
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        log.error("websocket发生错误!sessionid:{}； message:{}" , session.getId(), error.getMessage());
    }

    /**
     * 信息发送的方法
     *
     * @param message 发送的消息
     * @param code  消息接收页面的连接code
     */
    public synchronized static void sendMessage(String message, String code) {
        try {
            CopyOnWriteArraySet<SocketServer> set = map.get(code);
            if (set != null) {
                for (SocketServer socketServer : map.get(code)) {
                    socketServer.session.getBasicRemote().sendText(message);
                }
            }
        } catch (IOException e) {
            System.out.println("发送消息出错:" + e.getMessage());
            log.error("发送消息出错:{}" , e.getMessage());
        }
    }

    /**
     * 获取当前连接数
     * 一个code可以链接多个
     *
     * @return
     */
    public synchronized static int getOnlineNum() {
        //keySet获取map集合key的集合  然后在遍历key即可
        int count = 0;
        for (String key : map.keySet()) {
            CopyOnWriteArraySet<SocketServer> arraySet = map.get(key);
            if (arraySet != null) {
                count += arraySet.size();
            }
        }
        return count;
    }

    /**
     * 获取在线连接code以逗号隔开
     *
     * @return
     */
    public synchronized static String getOnlineUsers() {
        StringBuffer codes = new StringBuffer();
        for (String key : map.keySet()) {
            codes.append(key + ",");
        }
        return codes.toString();
    }

    /**
     * 信息群发
     *
     * @param msg
     */
    public synchronized static void sendAll(String msg) {
        for (String code : map.keySet()) {
            sendMessage(msg, code);
        }
    }

    /**
     * 批量发送消息
     *
     * @param msg
     * @param codes
     */

    public synchronized static void SendMany(String msg, String[] codes) {
        for (String code : codes) {
            sendMessage(msg, code);
        }
    }
}