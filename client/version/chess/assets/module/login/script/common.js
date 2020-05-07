var beiMiCommon = require("BeiMiCommon");
cc.Class({
    extends: beiMiCommon,

    // use this for initialization
    onLoad: function () {
        function GetRequest() {//这个函数，其实就是js的方法，在我的博客中能找到出处的
            var url = location.search; //获取url中"?"符后的字串
            var theRequest = new Object();
            if (url.indexOf("?") != -1) {
              var str = url.substr(1);
              var strs = str.split("&");
              for(var i = 0; i < strs.length; i ++) {
                theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
              }
            }
            return theRequest;
          }
        var Request = new Object();//使用方法也和纯js一样的，不信可以打印看看
        Request = GetRequest();
        var code=Request["code"]; 
        if(code){
            this.io = require("IOUtils");
            this.loadding();
            var xhr = cc.beimi.http.httpGet("/weixin?"+code, this.sucess , this.error , this);
            console.log("code:" + code);
        }
        
    },

    login:function(){
        this.io = require("IOUtils");
        this.loadding();
        if(this.io.get("userinfo") == null){
            //发送游客注册请求
            var xhr = cc.beimi.http.httpGet("/api/guest", this.sucess , this.error , this);
        }else{
            //通过ID获取 玩家信息
            var data = JSON.parse(this.io.get("userinfo")) ;
            if(data.token != null){     //获取用户登录信息
                var xhr = cc.beimi.http.httpGet("/api/guest?token="+data.token.id, this.sucess , this.error , this);
            }
        }
    },
    
    weixinLogin:function(){
        window.location = "https://open.weixin.qq.com/connect/oauth2/authorize?"
            +"appid=wxfc17d017b8180ca5"
            +"&redirect_uri=" + encodeURIComponent("http://lzxplay.top")
            +"&response_type=code"
            +"&scope=snsapi_userinfo"
            +"&state=STATE#wechat_redirect";
    },

    sucess:function(result , object){
        var data = JSON.parse(result) ;
        if(data!=null && data.token!=null && data.data!=null){
            //放在全局变量
            object.reset(data , result);
            cc.beimi.gamestatus = data.data.gamestatus ;
            /**
             * 登录成功后即创建Socket链接
             */
            object.connect();
            //预加载场景
            if(cc.beimi.gametype!=null && cc.beimi.gametype != ""){//只定义了单一游戏类型 ，否则 进入游戏大厅
                object.scene(cc.beimi.gametype , object) ;
            }else{
                /**
                 * 暂未实现功能
                 */
            }
        }
    },
    error:function(object){
        object.closeloadding(object.loaddingDialog);
        object.alert("网络异常，服务访问失败");
    }

    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // },
});
