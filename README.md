将本地动态公网ipv4、ipv6地址映射到阿里云域名，下载地址：https://github.com/yanziyuan/aliddns/releases

Windows中使用方法：

1、阿里云账户创建RAM子用户，添加“管理云解析（DNS）的权限”权限

2、使用java1.8

3、在release中下载.zip包，解压得到.jar包(1.1版本以上支持一次更新多个ip）

4、运行：进入控制台，java -jar path\aliddns1.x.jar AccessKeyID AccessKeySecret 域名 主机记录 类型 域名 主机记录 类型 （以此类推）...，注意以空格隔开

举例：java -jar C:\Users\yanziyuan\Desktop\aliddns1.2.jar AccessKeyID AccessKeySecret baidu.com testipv4 A quans.top testipv6 AAAA

群晖中使用方法：

1、套件中心安装java8

2、上传aliddns1.x.jar到群晖的文件夹，本人上传到opt文件夹下，文件属性可以看到路径是/volume1/opt/aliddns1.x.jar

3、设置开机启动任务（注意：1.3版本开始不要设置定时循环执行任务，因为程序中已经执行循环，间隔10分钟。更改为此策略原因是：某些情况下获取公网IP超时导致抛出异常，但是群晖不杀死java进程，导致多个进程持续占用资源，且不能重启任务，1.3版本开始更换了获取ip的api接口，同时线程内部循环执行任务）

#!/bin/sh

source /etc/profile

java -jar /volume1/opt/aliddns1.x.jar AccessKeyID AccessKeySecret quans.top testipv4 A quans.top testipv6 AAAA

最后打个广告：www.quans.top 淘宝天猫精选大额隐藏优惠券，你买东西有优惠，我也能拿到佣金，何乐而不为呢，感谢支持。
