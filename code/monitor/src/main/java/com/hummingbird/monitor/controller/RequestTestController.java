package com.hummingbird.monitor.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hummingbird.common.controller.BaseController;
import com.hummingbird.common.exception.RequestException;
import com.hummingbird.common.util.RequestUtil;
import com.hummingbird.common.util.http.HttpRequester;
import com.hummingbird.common.util.http.UTF8PostMethod;

@Controller
@RequestMapping("/")
public class RequestTestController extends BaseController {
	private static final Log log = LogFactory.getLog(RequestTestController.class);

	@RequestMapping(value = "/requestTest", method = RequestMethod.POST)
	public void recv(HttpServletRequest request, HttpServletResponse response) {

		String type = request.getContentType();
		log.debug(type);

		Map<String, String> map = new HashMap<String, String>();

		RequestUtil ru = new RequestUtil();
		HttpRequester hr = new HttpRequester();
		String urld = null;
		String resurl = null;
		try {

			String str = ru.getRequestPostData(request);
			if (log.isDebugEnabled()) {
				log.debug(String.format("获取post参数成功%s", str));
			}
			String sss = URLDecoder.decode(str, "utf-8");
			log.debug(sss);
			String[] s = str.split("&");
			for (String ss : s) {
				String[] a = ss.split("=");
				if (a.length > 1) {
					map.put(a[0], a[1]);
				} else {
					map.put(a[0], "");
				}
			}
			resurl = map.get("url");
			urld = URLDecoder.decode(resurl, "utf-8");

		} catch (IOException e) {
			e.printStackTrace();
		}
		String coding = map.get("cod");
		if (log.isDebugEnabled()) {
			log.debug(String.format("指定编码%s", coding));
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("指定地址%s", urld));
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("请求方式%s", map.get("op")));
		}
		// log.debug(map.get("pa"));

		Map<String, String> m = new HashMap<String, String>();
		String p = null;
		String pa = map.get("pa");
		// log.debug(pa);
		try {
			p = URLDecoder.decode(pa, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("请求参数%s", p));
		}

		String matter = null;

		// if(type.equals("formdata")){
		//
		// log.debug("将请求参数转换为Map");
		// //m=new HashMap<String, String>();
		// try {
		// JSONObject jsonobj=new JSONObject(p);
		// Iterator<String> itr = jsonobj.keys();
		// String name;
		// while(itr.hasNext()){
		// name=itr.next();
		// m.put(name,jsonobj.getString(name));
		// }
		//
		// //向指定地址发送请求
		// matter= hr.send(urld,map.get("op"), m);
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// log.error("转换失败",e);
		// }
		//
		// }
		if (type.equals("xml")) {

			HttpPost httpPost = new HttpPost(urld);
			httpPost.setHeader("Content-Type", "xml");
			httpPost.setEntity(new StringEntity(p, "utf-8"));
			DefaultHttpClient client = new DefaultHttpClient();
			// 请求超时
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
			// 读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					60000);
			HttpResponse resp = null;
			try {
				resp = client.execute(httpPost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = null;
				try {
					result = EntityUtils.toString(resp.getEntity(), "utf-8");
					log.debug(urld + "\n" + p + "\n===============\n" + result);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				matter = result;
			} else {
				// Logger.d(TAG, url + "\n================\n retCode="
				// + resp.getStatusLine().getStatusCode());

			}

			// StringReader read = new StringReader(p);
			//
			// InputSource source = new InputSource(read);
			//
			// SAXBuilder sb = new SAXBuilder();
			// try {
			// Document doc = sb.build(source);
			// //取的根元素
			// Element root = doc.getRootElement();
			// log.debug(root.getName());
			// //得到根元素所有子元素的集合
			// List jiedian = root.getChildren();
			// log.debug(jiedian);
			// Namespace ns = root.getNamespace();
			// log.debug(ns);
			// Element et = null;
			// for(int i=0;i<jiedian.size();i++){
			// et = (Element) jiedian.get(i);//循环依次得到子元素
			// log.debug(et);
			// log.debug(et.getName());
			// log.debug(et.getValue());
			// m.put(et.getName(),et.getValue());
			// }
			//
			// et = (Element) jiedian.get(0);
			// //log.debug(et);
			// List zjiedian = et.getChildren();
			// //log.debug(zjiedian);
			// if(zjiedian.size()>0){
			// for(int j=0;j<zjiedian.size();j++){
			// Element xet = (Element) zjiedian.get(j);
			// log.debug(xet.getName());
			// log.debug(xet.getValue());
			// }
			// }
			// } catch (JDOMException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

		} else if (type.equals("application/json")) {
			// hr.isCallBackSuccessByStream(urld, p);
			try {
				matter = hr.postRequest(urld, p);
				// log.debug(matter);
			} catch (RequestException e) {
				e.printStackTrace();
			}
		} else {

			String[] pama = p.split(",");
			String[] paa = null;
			Map<String, String> mm = new HashMap<String, String>();

			if (p.equals("")) {
				// mm.put("", "");
				matter = hr.send(urld, map.get("op"), mm);
			} else {

				for (int i = 0; i < pama.length; i++) {
					paa = pama[i].split("=");
					if (paa.length > 1) {
						mm.put(paa[0], paa[1]);
					} else {
						mm.put(paa[0], "");
					}
				}
				log.debug(mm);
				// 向指定地址发送请求
				if (coding.equals("gbk")) {
					RequestTestController sc = new RequestTestController();
					matter = sc.sending(urld, map.get("op"), mm);
				} else {
					matter = hr.send(urld, map.get("op"), mm);
				}
			}
		}

		// 返回结果到网关
		setFeeBack(request, response, matter, coding);
		// 向指定地址发送请求
		// String matter= hr.send(urld,map.get("op"), m);

	}

	/**
	 * 设置返回的信息
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	private void setFeeBack(HttpServletRequest request,
			HttpServletResponse response, String matter, String coding) {
		response.setCharacterEncoding("utf-8");

		if (log.isDebugEnabled()) {
			log.debug(String.format("本地网页编码%s", request.getCharacterEncoding()));
		}

		String sout = "";
		// String returnMsg = "";
		StringBuffer sb = new StringBuffer();
		sb.append(matter);
		sout = sb.toString();
		// if (log.isDebugEnabled()) {
		// log.debug(String.format("向网关返回确认%s",sout));
		// }

		try {
			PrintWriter writer = response.getWriter();
			// sout = new String(sout.getBytes("utf-8"),"gbk");
			writer.write(sout);
			writer.flush();
		} catch (IOException e) {
			log.error("返回确认结果出错", e);
		}

	}

	public String sending(String url, String method, Map<String, String> params) {
		StringBuffer temp;
		HttpMethodBase httpMethod = null;
		try {
			HttpClient httpClient = new HttpClient();
			if ("GET".equals(method)) {
				StringBuilder sb = new StringBuilder();
				if (params != null) {
					for (String varName : params.keySet()) {
						sb = sb.length() > 0 ? sb.append("&"
								+ URLEncoder.encode(varName, "gbk")
								+ "="
								+ URLEncoder.encode(params.get(varName)
										.toString(), "gbk")) : sb
								.append(URLEncoder.encode(varName, "gbk")
										+ "="
										+ URLEncoder.encode(params.get(varName)
												.toString(), "gbk"));
						log.debug(sb);
					}
				}
				if (StringUtils.isNotBlank(sb.toString())) {
					httpMethod = new GetMethod(url);
				}
			} else {
				UTF8PostMethod postMethod = new UTF8PostMethod(url);
				if (params != null) {
					for (String varName : params.keySet()) {
						postMethod.setParameter(varName, params.get(varName));
					}
				}
				httpMethod = postMethod;
			}
			log.debug(httpMethod.getURI().toString());
			int code = httpClient.executeMethod(httpMethod);
			if (code != HttpStatus.SC_OK) {
				throw new RuntimeException("服务器错误：" + code);
			}
			InputStream in = httpMethod.getResponseBodyAsStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, "gbk"));
			temp = new StringBuffer();
			String line = bufferedReader.readLine();
			while (line != null) {
				temp.append(line).append("\r\n");
				line = bufferedReader.readLine();
			}
			bufferedReader.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("服务器错误：" + e.getMessage());
		} finally {
			httpMethod.releaseConnection();
		}
		return temp.toString();

	}
}
