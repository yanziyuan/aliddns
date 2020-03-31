aliddns
更新本地的公网ipv4、ipv6地址到阿里云域名，本代码在阿里代码示例基础上，增加了对ipv6的支持

阿里示例代码地址：https://help.aliyun.com/document_detail/141482.html?spm=5176.12818093.0.0.11ae16d0mf0N6g

使用方法：

1、创建RAM子用户，添加“管理云解析（DNS）的权限”权限

2、建议使用java8

3、在release中下载aliddns.zip，解压得到aliddns.jar(1.1版本以上支持一次设置多个ip更新，运行方法请看4）

4、运行：进入控制台，java -jar aliddns.jar 你的AccessKeyID 你的AccessKeySecret 你的域名 你的主机记录 类型 你的域名 你的主机记录 类型 ...，注意以空格隔开

举例：我的域名是quans.top，我可以添加不同的记录指向不同的ip地址，ipv4的类型是"A"，ipv6的类型是"AAAA"，假如我的主机记录是testipv4，即testipv4.quans.top，那么命令为：java -jar aliddns.jar AccessKeyID AccessKeySecret quans.top testipv4 A，设置多个ip命令如下：java -jar aliddns.jar AccessKeyID AccessKeySecret quans.top testipv4 A quans.top testipv6 AAAA

群晖使用配置如下：

软件中心安装java8，上传aliddns.jar到群晖的文件夹，本人上传到opt文件夹下，文件属性可以看到路径是/volume1/opt/aliddns.jar，设置定时启动任务，在计划任务中新增定时脚本，间隔>=10分钟，最后运行时间23:50，脚本代码如下

#!/bin/sh

source /etc/profile

java -jar /volume1/opt/aliddns.jar AccessKeyID AccessKeySecret quans.top testipv4 A quans.top testipv6 AAAA

最后打个广告：www.quans.top 淘宝天猫精选大额隐藏优惠券，你买东西有优惠，我也能拿到佣金，何乐而不为呢，感谢支持。
