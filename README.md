 **GeekServer简介** 

GeekServer(极客服务器)是由国人开发的服务器软件，采用独特的防拥堵结构，结合长连
接和压缩传输等功能为一体，基本可以满足高并发的要求，经过测试在1000并发下一样可以
在80ms内将任务执行完。这个服务器不仅可以做静态服务器，而且能直接使用Java编写页
面，并且能嵌入而且Java项目。将多线程和Socket运用到极致，后续将添加NativePage
和CDN功能。
![结构图](https://static.oschina.net/uploads/space/2017/1014/122659_HXyQ_3243585.png "在这里输入图片标题")


 **防拥堵结构和内存缓存功能** 

GeekServer按照Create when needed原则采用多线程方式将连接进行分散异步式处理，
并且能够将文件缓存到内存中加速处理。

 **适用场景** 

GeekServer拥有强大的并发能力和快速响应能力，非常适合于静态服务器、Java和静态交叉编程，它不需要任何依赖，并且可以在纯Java环境（不要依赖其它任何服务器）下编写页面，最重要的是它没有Servlet下的任何限制。


 **加速功能** 

GeekServer后续将引入浏览器加速功能和浏览器检查功能


 **完全中文** 

面板完全采用中文编写，简单，实用，方便。



