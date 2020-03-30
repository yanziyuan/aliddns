# aliddns
更新本地的公网ipv4、ipv6地址到阿里云域名，本代码在阿里云代码示例基础上，增加了对ipv6的支持

阿里示例代码地址：https://help.aliyun.com/document_detail/141482.html?spm=5176.12818093.0.0.11ae16d0mf0N6g

使用方法：

1、创建RAM子用户，添加“管理云解析（DNS）的权限”权限

2、建议使用java8

3、运行：java -jar aliddnsipv6.jar 你的AccessKeyID 你的AccessKeySecret 你的域名 你的主机记录 类型

举例：我的域名是quans.top，我可以添加不同的记录指向不同的ip地址，ipv4的类型是"A",ipv6的类型是"AAAA",假如我的主机记录是testipv4，即testipv4.quans.top

那么命令为java -jar aliddnsipv6.jar AccessKeyID AccessKeySecret quans.top testipv4 A

群晖配置如下：

安装java8，上传aliddns.jar到群晖的文件夹，本人上传到opt文件夹下，路径是/volume1/opt/aliddns.jar，设置定时启动任务，在计划任务中新增定时脚本，间隔10分钟，最后运行时间23:50，脚本代码如下

#!/bin/sh

source /etc/profile

java -jar /volume1/opt/aliddns.jar AccessKeyID AccessKeySecret quans.top testipv4 A

打个广告：www.quans.top 淘宝天猫精选大额隐藏优惠券，感谢支持
