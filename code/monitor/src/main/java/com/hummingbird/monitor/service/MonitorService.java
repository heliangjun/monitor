/**
 * 
 * MonitorService.java
 * 版本所有 深圳市蜂鸟娱乐有限公司 2013-2014
 */
package com.hummingbird.monitor.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.hummingbird.common.util.PropertiesUtil;
import com.hummingbird.common.vo.BatchResultModel;
import com.hummingbird.common.vo.ResultModel;
import com.hummingbird.monitor.util.SystemUtil;

/**
 * @author huangjiej_2
 * 2014年12月22日 下午10:39:25
 * 本类主要做为
 */
public interface MonitorService {

	/**
	 * 监控
	 * @return 
	 */
	public abstract BatchResultModel dominitor();
	
	/**
	 * 监控并报告
	 */
	public void dominitorAndReport();

	public abstract ResultModel restartCmpp() throws Exception;

}
