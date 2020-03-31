package aliddns;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

/**
 * 动态域名解析
 */
public class DDNS {

	/**
	 * 获取主域名的所有解析记录列表
	 */
	private DescribeDomainRecordsResponse describeDomainRecords(DescribeDomainRecordsRequest request,
			IAcsClient client) {
		try {
			// 调用SDK发送请求
			return client.getAcsResponse(request);
		} catch (ClientException e) {
			e.printStackTrace();
			// 发生调用错误，抛出运行时异常
			throw new RuntimeException();
		}
	}

	/**
	 * 获取当前主机公网IP
	 */
	private String getCurrentHostIP(String type) {
		// 这里使用jsonip.com第三方接口获取本地IP
		String jsonip = "https://ipv4.jsonip.com/";
		if ("AAAA".equals(type)) {
			jsonip = "https://ipv6.jsonip.com/";
		}
		// 接口返回结果
		String result = "";
		BufferedReader in = null;
		try {
			// 使用HttpURLConnection网络请求第三方接口
			URL url = new URL(jsonip);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
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

		@SuppressWarnings("unchecked")
		Map<String, String> maps = (Map<String, String>) JSON.parse(result);

		String res = "";
		res = maps.get("ip");
		return res;

//		// 正则表达式，提取xxx.xxx.xxx.xxx，将IP地址从接口返回结果中提取出来
//		String rexp = "(\\d{1,3}\\.){3}\\d{1,3}";
//		Pattern pat = Pattern.compile(rexp);
//		Matcher mat = pat.matcher(result);
//		String res = "";
//		while (mat.find()) {
//			res = mat.group();
//			break;
//		}
//		return res;
	}

	/**
	 * 修改解析记录
	 */
	private UpdateDomainRecordResponse updateDomainRecord(UpdateDomainRecordRequest request, IAcsClient client) {
		try {
			// 调用SDK发送请求
			return client.getAcsResponse(request);
		} catch (ClientException e) {
			e.printStackTrace();
			// 发生调用错误，抛出运行时异常
			throw new RuntimeException();
		}
	}

	public static void main(String[] args) {

		// 地域ID参考https://help.aliyun.com/knowledge_detail/40654.html?spm=5176.13910061.0.0.5af422c8KhBIfU&aly_as=hV5o5h29N
		if (args.length >= 5) {

			DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", // 地域ID
					args[0], // 您的AccessKey ID
					args[1]);// 您的AccessKey Secret
			
			IAcsClient client = new DefaultAcsClient(profile);

			for (int i = 2; i < args.length; i += 3) {
				if ((args.length - i) >= 3) {
					checkAndUpdateIp(client, args[i], args[i + 1], args[i + 2]);
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
		// 查询指定二级域名的最新解析记录
		DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
		// 主域名
		describeDomainRecordsRequest.setDomainName(domainName);
		// 主机记录
		describeDomainRecordsRequest.setRRKeyWord(ipRRKeyWord);
		// 解析记录类型
		describeDomainRecordsRequest.setType(type);

		DescribeDomainRecordsResponse describeDomainRecordsResponse = ddns
				.describeDomainRecords(describeDomainRecordsRequest, client);

		System.out.println(JSON.toJSON(describeDomainRecordsResponse));

		List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();
		// 最新的一条解析记录

		if (domainRecords.size() != 0) {
			DescribeDomainRecordsResponse.Record record = domainRecords.get(0);
			// 记录ID
			String recordId = record.getRecordId();
			// 记录值
			String recordsValue = record.getValue();
			// 当前主机公网IP
			String currentHostIP = null;

			currentHostIP = ddns.getCurrentHostIP(type);

			System.out.println("当前主机公网IP为：" + currentHostIP);

			if (!currentHostIP.equals(recordsValue)) {
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
				UpdateDomainRecordResponse updateDomainRecordResponse = ddns
						.updateDomainRecord(updateDomainRecordRequest, client);

				System.out.println(JSON.toJSON(updateDomainRecordResponse));
				System.out.println("Update ip success! 此软件作者网站：www.quans.top，帮您找到淘宝天猫隐藏大额优惠券，感谢您的支持！");
			}
		}
	}
}
