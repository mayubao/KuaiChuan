# KuaiChuan(仿茄子快传)

仿茄子快传的一款文件传输应用， 涉及到Socket通信，包括TCP，UDP通信。（喜欢的给一个star, 有帮助的给一个folk， 欢迎Star和Folk ^_^）

##缩略图
### 主页 ###
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/home.gif)

### 文件发送端 ###
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fs_1.gif)

![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fs_2.gif)

![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fs_3.gif)

### 文件接收端 ###
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fr_1.gif)

![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fr_2.gif)

##原理

快传是模仿 茄子快传来实现的,主要是是通过设备间发送文件。 文件传输在文件发送端或者是文件接收端通过自定义协议的Socket通信来实现。
由于文件接收方和文件发送方都是文件的缩略图
这里采用了header + body的自定义协议, header部分包括了文件的信息（长度，大小，缩略图）， body部分就是文件

##测试

在Android测试机 分别是 魅蓝2 与  华为 SCL-TL00， Vivo xs1 运行正常

##感谢

google: <http://www.google.com>

stackoverflow  <http://stackoverflow.com/>



## 版本

### v1.0 ###
完成了Android端到Android端的文件传输



##issue
QQ:345269374

Emial:345269374@qq.com
