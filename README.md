将本地动态公网ipv4、ipv6地址映射到阿里云域名，下载地址：https://github.com/yanziyuan/aliddns/releases

Windows中使用方法：

1、阿里云账户创建RAM子用户，添加“管理云解析（DNS）的权限”权限

2、安装java1.8及以上版本

3、在release中下载aliddns.zip包，解压得到aliddns.jar包

4、运行cmd，进入控制台，输入java -jar path\aliddns.jar AccessKeyID AccessKeySecret 域名 主机记录 类型 域名 主机记录 类型 （以此类推）...，注意以空格隔开

举例：java -jar C:\Users\yanziyuan\Desktop\aliddns.jar AccessKeyID AccessKeySecret baidu.com testipv4 A quans.top testipv6 AAAA


群晖中使用方法：

1、阿里云账户创建RAM子用户，添加“管理云解析（DNS）的权限”权限

2、套件中心安装java8

3、在release中下载aliddns.zip包，解压得到aliddns.jar包

4、上传aliddns.jar到群晖的文件夹，本人上传到自建的opt文件夹下，文件属性可以看到路径是/volume1/opt/aliddns.jar

5、设置定时任务，间隔10分钟，最后执行时间为23：50，执行脚本如下

#!/bin/sh

source /etc/profile

java -jar /volume1/opt/aliddns.jar AccessKeyID AccessKeySecret quans.top testipv4 A quans.top testipv6 AAAA

最后打个广告：www.quans.top 淘宝天猫精选大额隐藏优惠券，你买东西有优惠，我也能拿到佣金，何乐而不为呢，感谢支持。
