package io.aicode.project.ctrl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.TextMessage;

/**
 * Created by lixin on 2018/9/30.
 */

@Controller
@RequestMapping(value = "/ws")
@Slf4j
public class WSTestCtrl {



    //http://localhost:8080/TestSpring4/HelloWorld/testReturnJSON.do
    @RequestMapping(value = "/testReturnJSON")
    @ResponseBody
    public Object testReturnJSON(@RequestParam("data") String data) {
        log.debug("我的测试<<" + data);

        TextMessage toWSMsg = new TextMessage(data);


        //把收到的消息群发到WS客户端
//        try {
////            Iterator<WebSocketSession> it = wsClientManager.setWSS.iterator();
////            while (it.hasNext()) {
////                WebSocketSession client = it.next();
////                client.sendMessage(toWSMsg);
////            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        return "{\"data\":\"" + data + "\"}";
    }

    @RequestMapping(value = "/returnModelView")
    public ModelAndView returnModelView() {
        //如果用户访问的时候不带userID参数，就不会映射到这个handler中。在Web browser中会提示400错误。
        ModelAndView mav = new ModelAndView();
        mav.setViewName("returnModelView");
        //mav.addObject("userID", userID);
        return mav;
    }
}
