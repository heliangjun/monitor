package com.hummingbird.monitor.controller;



import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hummingbird.common.controller.BaseController;
import com.hummingbird.common.util.PropertiesUtil;
import com.hummingbird.common.vo.ResultModel;
import com.hummingbird.monitor.service.MonitorService;
import com.hummingbird.monitor.util.SystemUtil;

/**
 * 监控controller
 * 
 * @author huangjiej_2 2014年11月10日 下午11:42:07
 */
@Controller
public class MonitorController extends BaseController {

	@Autowired(required = true)
	private MonitorService monitorSrv;
	

	/**
	 * 监控
	 * @return
	 */
	@RequestMapping("/monitor")
	public @ResponseBody Object monitor() {
		return monitorSrv.dominitor();
	}
	@RequestMapping("/restartcmpp")
	public @ResponseBody Object restartcmpp() {
		//通过调用脚本实现重启
		String shellname=null;
		ResultModel rm=new ResultModel(0,"重启完成");
		try {
			rm = monitorSrv.restartCmpp();
		} catch (Exception e) {
			log.error(String.format(""),e);
			rm.mergeException(e);
		}
		return rm;
	}
	
	
	
	
}
