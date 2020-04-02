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
import java.net.URL;
import java.util.List;

/**
 * 动态域名解析
 */
public class DDNS {

	/**
	 * 获取当前主机公网IP
	 */
	private String getCurrentHostIP(String type) {
		// jsonip.com第三方接口获取本地IP(较慢)
		// https://ipv4.jsonip.com/
		// https://ipv6.jsonip.com/

		// 由于jsonip.com 经常无法连接，故尝试更换 api 接口
		String jsonip = "http://v4.ip.zxinc.org/getip";
		if ("AAAA".equals(type)) {
			jsonip = "http://v6.ip.zxinc.org/getip";
		}
		// 接口返回结果
		String result = "";
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

	public static void main(String[] args) {

		// 阿里云示例代码：https://help.aliyun.com/document_detail/141482.html?spm=5176.12818093.0.0.11ae16d0mf0N6g
		// 地域ID参考https://help.aliyun.com/knowledge_detail/40654.html?spm=5176.13910061.0.0.5af422c8KhBIfU&aly_as=hV5o5h29N
		if (args.length >= 5) {

			DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", // 地域ID
					args[0], // 您的AccessKey
					args[1]);// 您的AccessKey

			IAcsClient client = new DefaultAcsClient(profile);

			while (true) {

				for (int i = 2; i < args.length; i += 3) {
					if ((args.length - i) >= 3) {
						checkAndUpdateIp(client, args[i], args[i + 1], args[i + 2]);
					}
				}

				try {

					Thread.sleep(10 * 60 * 1000);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

		DDNS ddns = new DDNS();
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
		try {
			describeDomainRecordsResponse = client.getAcsResponse(describeDomainRecordsRequest);
		} catch (ClientException e1) {
			e1.printStackTrace();
		}

		System.out.println(gson.toJson(describeDomainRecordsResponse));

		List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();
		// 最新的一条解析记录
		if (domainRecords.size() != 0) {
			DescribeDomainRecordsResponse.Record record = domainRecords.get(0);
			// 记录ID
			String recordId = record.getRecordId();
			// 记录值
			String recordsValue = record.getValue();

			// 获取当前主机公网IP
			String currentHostIP = null;
			currentHostIP = ddns.getCurrentHostIP(type);
			System.out.println("CurrentHost：" + currentHostIP);

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
				} catch (ClientException e) {
					e.printStackTrace();
				}

				System.out.println(gson.toJson(updateDomainRecordResponse));
				if (recordId.equals(updateDomainRecordResponse.getRecordId())) {
					System.out.println("Update success! " + ipRRKeyWord + "." + domainName + "->" + currentHostIP);
					System.out.println("此软件作者推广网站：www.quans.top，帮您找到淘宝天猫隐藏大额优惠券，安全稳定无广告，感谢您的支持！");
					System.out.println();
				} else {
					System.out.println("Update failed！");
				}
			}
		}
	}
}
