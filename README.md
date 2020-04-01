aliddns

更新本地公网ipv4、ipv6到阿里云域名

阿里示例代码地址：https://help.aliyun.com/document_detail/141482.html?spm=5176.12818093.0.0.11ae16d0mf0N6g

使用方法：

1、创建RAM子用户，添加“管理云解析（DNS）的权限”权限

2、建议使用java8

3、在release中下载jar包(1.1版本以上支持一次设置多个ip更新）

4、运行：进入控制台，java -jar C:\Users\yanziyuan\Desktop\aliddns1.2.jar AccessKeyID AccessKeySecret 域名 主机记录 类型 域名 主机记录 类型 （以此类推）...，注意以空格隔开

举例：

AccessKeyID：aaaaaaaaaaaaa，AccessKeySecret：bbbbbbbbbbbbbb，域名：quans.top，主机记录：testipv4，ipv4 type："A"，ipv6 type："AAAA"，

那么命令为：java -jar C:\Users\yanziyuan\Desktop\aliddns1.2.jar aaaaaaaaaaaaa bbbbbbbbbbbbbb quans.top testipv4 A

设置多个ip命令如下：java -jar C:\Users\yanziyuan\Desktop\aliddns1.2.jar aaaaaaaaaaaaa bbbbbbbbbbbbbb quans.top testipv4 A quans.top testipv6 AAAA

群晖使用配置如下：

套件中心安装java8，上传aliddns.jar到群晖的文件夹，本人上传到opt文件夹下，文件属性可以看到路径是/volume1/opt/aliddns.jar，设置定时启动任务，在计划任务中新增定时脚本，间隔=10分钟，最后运行时间23:50，脚本代码如下

#!/bin/sh

source /etc/profile

java -jar /volume1/opt/aliddns.jar AccessKeyID AccessKeySecret quans.top testipv4 A quans.top testipv6 AAAA

最后打个广告：www.quans.top 淘宝天猫精选大额隐藏优惠券，你买东西有优惠，我也能拿到佣金，何乐而不为呢，感谢支持。
