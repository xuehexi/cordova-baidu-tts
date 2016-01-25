# cordova-baidu-tts 百度离线语音合成(android)
# 缘起
一些cordova做的应用需要具有离线语音朗读能力
# 说明
不是专业做安卓开发，只是实现了js调用百度sdk，具体tts功能建议查看百度官方文档。
# 安装
1. 远程
控制台命令
$ cordova plugin add https://github.com/xuehexi/cordova-baidu-tts.git
2. 本地
下载源码后，控制台命令
$ cordova plugin add  /目录/cordova-baidu-tts
＃ 使用
1. 初始化
```
var options_init={
                appId:"你在百度申请的id", 
                apiKey:"你的apiKey",
                secretKey:"你的secretKey",
                speed:"5", //朗读语速，取值范围[0, 9]，数值越大，语速越快
                pitch:"5"  //音调，取值范围[0, 9]，数值越大，音量越高
            	};
baidu_tts.init(options_init);
```
2. 播放
```
var options = {txt:"新年好"};
baidu_tts.speak(
        function(ret){}, //success
        function(e){},   //error
        options);
```
3. 停止
```
baidu_tts.stop();
```
# 其他
时间关系，只是完成基本功能，抛砖引玉，更多功能请自行完善。