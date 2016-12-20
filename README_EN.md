# AnyShare
[中文](https://github.com/mayubao/KuaiChuan/blob/master/README.md)

This is an android application like AnyShare Application which can transfer files from device to the other device, and include socket communication(tcp, udp communication). If you like it, please give me a like, and if it is helpful to you, please fork it. Welcome your start and fork ^_^.

[Download](http://fir.im/6ntz) click the Download text to download the app.

## Preview

### Home ###
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/home.gif)

### File Sender ###
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fs_1.gif)
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fs_2.gif)
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fs_3.gif)
### File Receiver ###
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fr_1.gif)
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/fr_2.gif)

### Web Transfer(20161218 add) ###
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/w_1.gif)

![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/w_2.jpg)
![Alt text](https://github.com/mayubao/KuaiChuan/blob/master/ScreenShot/w_3.jpg)

## What is AnyShare

AnyShare has two point to transfer files:

1. transfer files from android device 2 android device.
2. transfer files by the web.

In the first point, I custome the protocol like http protocol, and every request has its header and its body.
its header include file info, eg: length, size, path, screenshot(or thumbnail), and its body is the real file.

In the second point, I custome the micro http server in android. And the file receiver can download files via web browser.


## Test
（Test must be in reallly Android device）
Runnig normally in android devices. eg Meilan 2, Huawei SCL-TL00, Vivo xs1.

## Thanks

google: <http://www.google.com>

stackoverflow  <http://stackoverflow.com/>


## Version

### v1.0 ###
Complete files transmission from android device to the other android device.

### v1.1 ###
Complete files transmission via web browser

## Issue

QQ:345269374

Email:345269374@qq.com

## License

    Copyright 2016 mayubao

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.