package aliddns;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * 动态域名解析
 */
public class DDNS {

	/**
	 * 获取当前主机公网IP
	 */
	private static String getCurrentHostIP(String type) {
		// 接口返回结果
		String result = "";

		// jsonip.com第三方接口获取本地IP(较慢)
		// https://ipv4.jsonip.com/
		// https://ipv6.jsonip.com/

		// 由于jsonip.com 经常无法连接，故尝试更换 api 接口
		String jsonip = "http://v4.ip.zxinc.org/getip";
		if ("AAAA".equals(type)) {
			jsonip = "http://v6.ip.zxinc.org/getip";

			result = DDNS.getLocalIpv6Address();
			if (!"".equals(result) && result != null && !result.isEmpty()) {
				return result;
			}
		}

		BufferedReader in = null;
		try {
			// 使用HttpURLConnection网络请求第三方接口
			URL url = new URL(jsonip);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(60000);
			urlConnection.setReadTimeout(60000);
			urlConnection.connect();

			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

		return result;

//		Gson gson = new Gson();
//		@SuppressWarnings("unchecked")
//		Map<String, String> map = gson.fromJson(result, Map.class);
//		return map.get("ip");
	}

	private static String getLocalIpv6Address() {

		int count = 0;
		InetAddress inetAddress = null;
		Enumeration<NetworkInterface> networkInterfaces = null;
		String result = "";
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (networkInterfaces.hasMoreElements()) {
			Enumeration<InetAddress> inetAds = networkInterfaces.nextElement().getInetAddresses();
			while (inetAds.hasMoreElements()) {
				inetAddress = inetAds.nextElement();
				// Check if it's ipv6 address and reserved address
				if (inetAddress instanceof Inet6Address) {

					if (inetAddress.isMulticastAddress()) {
						continue;
					}
//					当IP地址是广播地址（MulticastAddress）时返回true，否则返回false。
//					通过广播地址可以向网络中的所有计算机发送信息，而不是只向一台特定的计算机发送信息。IPv4的广播地址的范围是224.0.0.0 ~ 239.255.255.255.IPv6的广播地址第一个字节是FF，其他的字节可以是任意值。

					if (inetAddress.isAnyLocalAddress()) {
						continue;
					}
//					当IP地址是通配符地址时返回true，否则返回false。
//					IPv4的通配符地址是0.0.0.0
//					IPv6的通配符地址是0:0:0:0:0:0:0:0，也可以简写成::。

					if (inetAddress.isLoopbackAddress()) {
						continue;
					}
//					当IP地址是loopback地址时返回true，否则返回false。
//					loopback地址就是代表本机的IP地址。
//					IPv4的loopback地址的范围是127.0.0.0 ~ 127.255.255.255，也就是说，只要第一个字节是127，就是lookback地址。如127.1.2.3、127.0.200.200都是loopback地址。
//					IPv6的loopback地址是0:0:0:0:0:0:0:1，也可以简写成::1

					if (inetAddress.isLinkLocalAddress()) {
						continue;
					}
//					当IP地址是本地连接地址（LinkLocalAddress）时返回true，否则返回false。
//					IPv4的本地连接地址的范围是169.254.0.0 ~ 169.254.255.255。
//					IPv6的本地连接地址的前12位是FE8，其他的位可以是任意取值，如FE88::和FE80::ABCD::都是本地连接地址。

					if (inetAddress.isSiteLocalAddress()) {
						continue;
					}
//					当IP地址是地区本地地址（SiteLocalAddress）时返回true，否则返回false。(是不是内网ip)
//					IPv4的地址本地地址分为三段：10.0.0.0 ~ 10.255.255.255、172.16.0.0 ~ 172.31.255.255、192.168.0.0 ~ 192.168.255.255。（企业内部或个人内部的局域网内部的ip都应该在此三个网段内）
//					IPv6的地区本地地址的前12位是FEC，其他的位可以是任意取值，如FED0:: 和 FEF1:: 都是地区本地地址。

					if (inetAddress.isMCGlobal()) {
						continue;
					}
//					当IP地址是全球范围的广播地址时返回true，否则返回false。
//					全球范围的广播地址可以向Internet中的所有的计算机发送信息。
//					IPv4的广播地址除了224.0.0.0和第一个字节是239的IP地址都是全球范围的广播地址。
//					IPv6的全球范围的广播地址中第一个字节是FF，第二个字节的范围是0E ~ FE，其他的字节可以是任意值，如FFBE::、FF0E::都是全球范围的广播地址。

					if (inetAddress.isMCNodeLocal()) {
						continue;
					}
//					当IP地址是本地接口广播地址时返回true，否则返回false。
//					本地接口广播地址不能将广播信息发送到产生广播信息的网络接口，即使是同一台计算机的另一个网络接口也不行。所有的IPv4广播地址都不是本地接口广播地址。IPv6的本地接口广播地址的第一个字节是FF，第二个节字的范围是01 ~ F1，其他的字节可以是任意值，如FFB1：：、FF01：A123：：都是本地接口广播地址。

					if (inetAddress.isMCLinkLocal()) {
						continue;
					}
//					当IP地址是子网广播地址时返回true，否则返回false。
//					使用子网的广播地址只能向子网内的计算机发送信息。
//					IPv4的子网广播地址的范围是224.0.0.0 ~ 224.0.0.255。
//					IPv6的子网广播地址的第一个字节是FF，第二个字节的范围是02 ~ F2，其他的字节可以是任意值，如FFB2:: 和 FF02:ABCD:: 都是子网广播地址。

					if (inetAddress.isMCSiteLocal()) {
						continue;
					}
//					当IP地址是站点范围的广播地址时返回true，否则返回false。
//					使用站点范围的广播地址，可以向站点范围内的计算机发送广播信息。
//					IPv4的站点范围广播地址的范围是239.255.0.0 ~ 239.255.255.255，如239.255.1.1、239.255.0.0都是站点范围的广播地址。
//					IPv6的站点范围广播地址的第一个字节是FF，第二个字节的范围是05 ~ F5，其他的字节可以是任意值，如FF05:: 和 FF45:: 都是站点范围的广播地址。

					if (inetAddress.isMCOrgLocal()) {
						continue;
					}
//					当IP地址是组织范围的广播地址时返回ture，否则返回false。
//					使用组织范围广播地址可以向公司或企业内部的所有的计算机发送广播信息。
//					IPv4的组织范围广播地址的第一个字节是239，第二个字节不小于192，第三个字节不大于195，如239.193.100.200、239.192.195.0都是组织范围广播地址。
//					IPv6的组织范围广播地址的第一个字节是FF，第二个字节的范围是08 ~ F8，其他的字节可以是任意值，如FF08:: 和 FF48::都是组织范围的广播地址。

					result = inetAddress.toString().replaceAll("/", "");
					result = result.substring(0, result.indexOf("%"));
					System.out.println("inetAddress:" + result);
					count++;// windows下会分配临时ipv6，安装虚拟机会有多个ipv6，无法区分真实ipv6与临时ipv6，因此计数会大于1，群晖中使用应该只有一个
				}
			}
		}

		if (count != 1) {
			result = "";
		}

		return result;
	}

	public static void main(String[] args) {

		// 阿里云示例代码：https://help.aliyun.com/document_detail/141482.html?spm=5176.12818093.0.0.11ae16d0mf0N6g
		// 地域ID参考https://help.aliyun.com/knowledge_detail/40654.html?spm=5176.13910061.0.0.5af422c8KhBIfU&aly_as=hV5o5h29N
		if (args.length >= 5) {

			try {

				DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", // 地域ID
						args[0], // 您的AccessKey
						args[1]);// 您的AccessKey

				IAcsClient client = new DefaultAcsClient(profile);

				for (int i = 2; i < args.length; i += 3) {
					if ((args.length - i) >= 3) {
						checkAndUpdateIp(client, args[i], args[i + 1], args[i + 2]);
					}
				}

			} catch (Exception e) {
				// java.net.UnknownHostException: alidns.aliyuncs.com
				e.printStackTrace();
			}

		} else {
			System.out.println("Parameter error!");
		}
	}

	/**
	 * 
	 * @param client
	 * @param domainName  您的域名，如 baidu.com
	 * @param ipRRKeyWord 您的主机记录，如 www
	 * @param type        ipv4 填 A ，ipv6 填 AAAA
	 */
	private static void checkAndUpdateIp(IAcsClient client, String domainName, String ipRRKeyWord, String type) {

		Gson gson = new Gson();
		// 查询指定二级域名的最新解析记录
		DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
		// 主域名
		describeDomainRecordsRequest.setDomainName(domainName);
		// 主机记录
		describeDomainRecordsRequest.setRRKeyWord(ipRRKeyWord);
		// 解析记录类型
		describeDomainRecordsRequest.setType(type);

		// 获取主域名的所有解析记录列表
		DescribeDomainRecordsResponse describeDomainRecordsResponse = null;
		// 最新的一条解析记录
		List<DescribeDomainRecordsResponse.Record> domainRecords = null;
		try {

			describeDomainRecordsResponse = client.getAcsResponse(describeDomainRecordsRequest);
			System.out.println(new Date() + " " + gson.toJson(describeDomainRecordsResponse));
			domainRecords = describeDomainRecordsResponse.getDomainRecords();

		} catch (ClientException e1) {
			e1.printStackTrace();
		}

		if (domainRecords.size() != 0) {
			DescribeDomainRecordsResponse.Record record = domainRecords.get(0);
			// 记录ID
			String recordId = record.getRecordId();
			// 记录值
			String recordsValue = record.getValue();

			// 获取当前主机公网IP
			String currentHostIP = null;
			currentHostIP = DDNS.getCurrentHostIP(type);
			System.out.println(new Date() + " CurrentHost：" + currentHostIP);

			if (currentHostIP.length() > 0 && !currentHostIP.equals(recordsValue)) {
				System.out.println("Updating...");
				// 修改解析记录
				UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
				// 主机记录
				updateDomainRecordRequest.setRR(ipRRKeyWord);
				// 记录ID
				updateDomainRecordRequest.setRecordId(recordId);
				// 将主机记录值改为当前主机IP
				updateDomainRecordRequest.setValue(currentHostIP);
				// 解析记录类型
				updateDomainRecordRequest.setType(type);
				// 修改解析记录
				UpdateDomainRecordResponse updateDomainRecordResponse = null;
				try {
					updateDomainRecordResponse = client.getAcsResponse(updateDomainRecordRequest);
					System.out.println(gson.toJson(updateDomainRecordResponse));
					if (recordId.equals(updateDomainRecordResponse.getRecordId())) {
						System.out.println(new Date() + " Update success! " + ipRRKeyWord + "." + domainName + "->"
								+ currentHostIP);
						System.out.println("此软件作者推广网站：www.quans.top，帮您找到淘宝天猫隐藏大额优惠券，安全稳定无广告，感谢您的支持！");
						System.out.println();
					} else {
						System.out.println("Update failed！");
					}
				} catch (ClientException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
