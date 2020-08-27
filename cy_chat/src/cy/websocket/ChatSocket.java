package cy.websocket;

import com.alibaba.fastjson.JSON;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import cy.util.MessageUtil;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value = "/websocket", configurator = GetHttpSessionConfigurator.class)
public class ChatSocket  {

    private static Session session;
    private static HttpSession httpSession;
    //保存当前系统中登录的用户的httpsession信息，及对应的endpoint实例信息
    private static Map<HttpSession, ChatSocket> onlineUsers = new HashMap<HttpSession, ChatSocket>();
    private static int onlineCount = 0;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        //1、记录websocket的会话信息对象session
        this.session = session;

        //2、获取当前登录用户httpsession信息
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;

        //3、记录当前登录用户信息，及对应的endpoint实例
        if (null != httpSession.getAttribute("username") ){
            onlineUsers.put(httpSession, this);
        }

        System.out.println("users online status: " + httpSession.getAttribute("username") + ", endpoint: " + hashCode());

        //4、获取当前所有登录用户
        String names = getNames();

        //5、组装消息
        String message = MessageUtil.getContent(MessageUtil.TYPE_USER, "", "", names);

        //6、通过广播的形式发送消息
        broadcastAllUsers(message);

        //7、记录当前用户登录数
        increaseCount();


    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        System.out.println("on msg is be luanched:" + msg);
        
        //1、获取客户端的消息内容，并解析
        Map<String, String> map = JSON.parseObject(msg, Map.class);
        String fromName = map.get("fromName");
        String toName = map.get("toName");
        String content = map.get("content");

        //2、判定当前有没有接收人
        if(toName == null || toName.isEmpty()){
            return;
        }
        String msgContent = MessageUtil.getContent(MessageUtil.TYPE_MESSAGE, fromName, toName, content);
        System.out.println("server send msg to client, msg content is: " + msgContent);
        //3、如果接收人是广播（all），则说明发送广播消息
        if("all".equals(toName)){
            //3.1组装消息内容

            broadcastAllUsers(msgContent);
        }
        //4、如果不是all，给指定的用户推送消息
        else {
            singlePushMessage(msgContent, fromName, toName);

        }
    }
    //给指定用户推送消息
    private void singlePushMessage(String content, String fromName, String toName) {
        boolean isOnline = false;
        //1、判定当前接收人是否在线
        for (HttpSession httpSession1 : onlineUsers.keySet()) {
            if(toName.equals(httpSession1.getAttribute("username"))){
                isOnline = true;
            }
        }
        //2、如果存在，发送消息
        if(isOnline){
            for (HttpSession hsession : onlineUsers.keySet()) {
                if(hsession.getAttribute("username").equals(fromName) ||
                        hsession.getAttribute("username").equals(toName)){
                    try {
                        onlineUsers.get(hsession).session.getBasicRemote().sendText(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void broadcastAllUsers(String message) {
        for(HttpSession httpSession : onlineUsers.keySet()){
            try {
                onlineUsers.get(httpSession).session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getNames(){
        String names = "";
        if(onlineUsers.size() > 0){
            for(HttpSession session : onlineUsers.keySet()){
                String username = (String) session.getAttribute("username");
                names += username + ", ";
            }
        }
        return names.substring(0, names.length() - 1);
    }




    public int getOnlineCount(){
        return onlineCount;
    }

    public synchronized void increaseCount(){
        onlineCount ++;
    }

    public synchronized void decreateCount(){
        onlineCount --;
    }
}
