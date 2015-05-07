/**
 * 
 */
package com.hummingbird.monitor.service.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.hummingbird.common.exception.DataInvalidException;
import com.hummingbird.common.exception.ValidateException;
import com.hummingbird.common.face.statuscheck.AbstractStatusCheckResult;
import com.hummingbird.common.face.statuscheck.SmsReporter;
import com.hummingbird.common.util.ExHttpClientUtils;
import com.hummingbird.common.util.JsonUtil;
import com.hummingbird.common.util.PropertiesUtil;
import com.hummingbird.common.util.SmsSenderUtil;
import com.hummingbird.common.util.json.JSONException;
import com.hummingbird.common.util.json.JSONObject;
import com.hummingbird.common.vo.BatchResultModel;
import com.hummingbird.common.vo.ResultModel;
import com.hummingbird.monitor.service.MonitorService;
import com.hummingbird.monitor.util.SystemUtil;

/**
 * @author huangjiej_2
 * 2014年12月14日 上午11:00:39	
 */
@Service("monitorService")
public class MonitorServiceImpl implements MonitorService {
	
	org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(this.getClass());
	
	
	public BatchResultModel dominitor(){
		if (log.isDebugEnabled()) {
			log.debug(String.format("开始进行监控"));
		}
		//系统情况
		//具体项目情况
		PropertiesUtil pu = new PropertiesUtil();
		String monitorarr = pu.getProperty("monitor.list");
		String[] monitorlist = monitorarr.split(",");
		BatchResultModel brm = new BatchResultModel();
		for (int i = 0; i < monitorlist.length; i++) {
			String monitorapp = monitorlist[i];
			String url = pu.getProperty(String.format("monitor.%s.url", monitorapp));
			//同步访问
			if (log.isDebugEnabled()) {
				log.debug(String.format("访问应用：%S，url=%s",monitorapp,url));
			}
			ResultModel  checkresult = checkApp(monitorapp,url);
			brm.addResultModel(checkresult);
		}
		return brm;
	}
	
	/**
	 * 监控并报告
	 */
	@Override
	public void dominitorAndReport(){
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("定时任务监控并报告"));
		}
		BatchResultModel monitorresult = dominitor();
		report(monitorresult);
		if(!monitorresult.isSuccessed()){
			for (Iterator iterator = monitorresult.getError().iterator(); iterator.hasNext();) {
				ResultModel rm = (ResultModel) iterator.next();
				String monitorapp = ObjectUtils.toString(rm.get("appid"));
				if(monitorapp.equalsIgnoreCase("smsreceive")&&!rm.isSuccessed()){
					try {
						restartCmpp();
					} catch (Exception e) {
						log.error(String.format(""),e);
					}
				}
			}
		}
		
		
	}
	
	@Override
	public ResultModel restartCmpp() throws Exception{
		//短信网关，需要重启
		ResultModel rm = new ResultModel();
		rm.setErrmsg("重启网关");
		log.debug("重启短信网关...");
		String shellname="";
		String execShellout="";
		try {
			shellname = new PropertiesUtil().getProperty("monitor.smsreceive.restart");
			log.debug(shellname);
			if(StringUtils.isNotBlank(shellname)){
				List runShellout = SystemUtil.runShell(shellname);
				for (Iterator iterator = runShellout.iterator(); iterator
						.hasNext();) {
					Object object = (Object) iterator.next();
					
					execShellout += ObjectUtils.toString(object);
				}
				log.debug("执行重启脚本，返回:"+execShellout);
				rm.setErrmsg("重启网关,返回"+execShellout);
			}
		} catch (IOException e) {
			log.error(String.format("执行脚本失败,%s",shellname),e);
			rm.mergeException(e);
		}
		return rm;
	}
	
	/** 
     * 运行shell脚本 
     * @param shell 需要运行的shell脚本 
	 * @throws IOException 
     */  
    public static void execShell(String shell) throws IOException{  
            Runtime rt = Runtime.getRuntime();  
            rt.exec(shell);  
    }  

	/**
	 * 
	 * @param monitorapp
	 * @param url
	 * @return
	 */
	private ResultModel checkApp(String monitorapp, String url) {

		Map map = ExHttpClientUtils.doPostByPostData(ExHttpClientUtils.createHttpClient(), url, "");
		if (log.isDebugEnabled()) {
			log.debug(String.format("应用返回结果为%s",map));
		}
		 ResultModel rm = new ResultModel();
		 rm.put("appid", monitorapp);
		 Object resultCode = map.get("resultCode");
		 if("200".equals(ObjectUtils.toString(resultCode))){
			 //对方有返回
			 JSONObject prm;
				try {
					prm = new JSONObject(ObjectUtils.toString( map.get("resultData")));
				} catch (JSONException e) {
					log.error(String.format("结果转json出错"),e);
					rm.setErrcode(0);
					rm.setErrmsg(monitorapp+"返回失败，结果转json出错");
					return rm;
				}
			 String resultDataCode = ObjectUtils.toString( prm.optString(ResultModel.KEY_ERRCODE));
			 if(StringUtils.isNotBlank(resultDataCode)){
				 //旧版本状态检查
				 if("0".equals(resultDataCode))
				 {
					 rm.setErrcode(0);
					 rm.setErrmsg((prm.isNull(ResultModel.KEY_ERRMSG)?(monitorapp+"正常"):(prm.optString(ResultModel.KEY_ERRMSG))));
				 }
				 else{
					 rm.setErrcode(1000);
					 rm.setErrmsg(monitorapp+"返回失败，响应："+(!prm.isNull(ResultModel.KEY_ERRMSG)?prm.optString(ResultModel.KEY_ERRMSG):prm));
				 }
			 }
			 else{
				 //尝试新版状态检查
				AbstractStatusCheckResult result;
				try {
					result = JsonUtil.convertJson2Obj(ObjectUtils.toString( map.get("resultData")), AbstractStatusCheckResult.class);
					if(result.isNormal()){
						rm.setErrcode(0);
					}
					else{
						rm.setErrcode(1000);
					}
					rm.setErrmsg(new SmsReporter().getReportStr(result));
				} catch (DataInvalidException e) {
					log.error(String.format("转换出错"),e);
					rm.setErr(1000, "检查"+monitorapp+"失败，无法解析状态结果");
				}
				 
			 }
		 }
		 else{
			 rm.setErrcode(1000);
			 rm.setErrmsg(monitorapp+"访问失败，响应："+(map.containsKey("resultCode")?map.get("resultCode"):map));

		 }
		 
				 
		return rm;
	}

	private void sendReport(String message){
		PropertiesUtil pu = new PropertiesUtil();
		String mobilestr = pu.getProperty("monitor.notify.mobile");
		String[] mobilearr = mobilestr.split(",");
		for (int i = 0; i < mobilearr.length; i++) {
			String mobile  = mobilearr[i];
			try {
				log.debug(mobile+":"+message);
				SmsSenderUtil.sendSms(mobile,message);
			} catch (ValidateException e) {
				log.error(String.format("发送信息失败"),e);
			}
		}
	}
	
	/**
	 * 报告情况
	 * @param brm
	 */
	private void report(BatchResultModel brm) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("报告情况"));
		}
		if(!brm.isSuccessed()){
			//报告情况
			String message = "";
			boolean allsuccess = true;
			for (Iterator iterator = brm.getError().iterator(); iterator.hasNext();) {
				ResultModel rm = (ResultModel) iterator.next();
				allsuccess&=rm.isSuccessed();
				message+=rm.getErrmsg();
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("所有系统是否正常=%s,信息为%s",allsuccess,message));
			}
			//正常时，如果设置为正常也发送，就发送
			PropertiesUtil pu = new PropertiesUtil();
			if(!allsuccess||"1".equals(pu.getProperty("reportSuccessed"))){
				if(!message.equals("")){
					if (log.isDebugEnabled()) {
						log.debug(String.format("向手机发送报告信息:%s",message));
					}
					sendReport(message);
				}
				else{
					if (log.isDebugEnabled()) {
						log.debug(String.format("短信内容为空，不发送短信"));
					}
				}
			}
			else{
				if (log.isDebugEnabled()) {
					log.debug(String.format("不发送短信"));
				}
			}
			//如果时间在工作时间则报告
		}
		else{
			//正常时，如果设置为正常也发送，就发送
			PropertiesUtil pu = new PropertiesUtil();
			if("1".equals(pu.getProperty("reportSuccessed"))){
				sendReport(brm.getErrmsg());
			}
		}
		
	}
	

}
