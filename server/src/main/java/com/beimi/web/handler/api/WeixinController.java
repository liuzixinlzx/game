package com.beimi.web.handler.api;

import com.alibaba.fastjson.JSONObject;
import com.beimi.web.handler.api.rest.user.GuestPlayerController;
import com.beimi.web.model.PlayUser;
import com.beimi.web.model.ResultData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/weixin")
public class WeixinController {
    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.appsecret}")
    private String appsecret;

    @Autowired
    private GuestPlayerController guestPlayerController;

    @RequestMapping
    public ResponseEntity<ResultData> weixinLogin(HttpServletRequest request, @RequestParam String code){
        RestTemplate restTemplate = new RestTemplate();
        String access_tokenURL = "http://api.weixin.qq.com/sns/oauth2/access_token?"
                +"appid=" + appid
                +"&secret=SECRET" + appsecret
                +"&code=" + code
                +"&grant_type=authorization_code";
        String access_tokenStr = restTemplate.getForObject(access_tokenURL , String.class);
        JSONObject  access_tokenObj =  JSONObject.parseObject(access_tokenStr);
        if(access_tokenObj.get("errmsg") != null){
            String errmsg = access_tokenObj.get("errmsg").toString();
            System.out.println("ERROR1:" + errmsg);
            return null;
        }
        String access_token =  access_tokenObj.get("access_token").toString();
        String openid = access_tokenObj.get("openid").toString();

        String userinfoUrl = "https://api.weixin.qq.com/sns/userinfo?"
                +"access_token="+access_token
                +"&openid="+openid
                +"&lang=zh_CN";

        String userinfoStr = restTemplate.getForObject(userinfoUrl , String.class);
        JSONObject  userinfoObj =  JSONObject.parseObject(userinfoStr);
        if(userinfoObj.get("errmsg") != null){
            String errmsg = userinfoObj.get("errmsg").toString();
            System.out.println("ERROR2:" + errmsg);
            return null;
        }

        String nickname = access_tokenObj.get("nickname").toString();
        String sex = access_tokenObj.get("sex").toString();
        String headimgurl = access_tokenObj.get("headimgurl").toString();
        PlayUser playUser = new PlayUser();
        playUser.setUsername(openid);
        playUser.setNickname(nickname);
        playUser.setGender(sex);

        ResponseEntity<ResultData>  resultData = guestPlayerController.guestPlayer(request, playUser);
        return resultData;
    }
}
