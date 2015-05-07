/**
 * 
 * SystemUtil.java
 * 版本所有 深圳市蜂鸟娱乐有限公司 2013-2014
 */
package com.hummingbird.monitor.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangjiej_2
 * 2015年1月2日 上午12:45:18
 * 本类主要做为
 */
public class SystemUtil {

	/**
	 * 构造函数
	 */
	public SystemUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 读取输出
	 * @param process
	 * @return
	 * @throws IOException
	 */
	public static String getOutputMsg(Process process) throws IOException
	{
		StringBuilder sb =new StringBuilder();
		LineNumberReader input=null;
		InputStreamReader ir =null;
		try{
			
			ir = new InputStreamReader(process.getInputStream());
			input = new LineNumberReader(ir);
			String line;
			while((line = input.readLine()) != null){
				sb.append(line);
				System.out.println(line);
			}
		}
		finally{
			if(input!=null)
				input.close();
			if(ir!=null)
				ir.close();
		}
		return sb.toString();
	}
	
	/** 
     * 运行shell脚本 
     * @param shell 需要运行的shell脚本 
	 * @return 
	 * @throws IOException 
     */  
    public static String execShell(String shell) throws IOException{  
            Runtime rt = Runtime.getRuntime();  
            Process process = rt.exec(shell);
            return getOutputMsg(process);
    }  
  
  
/** 
     * 运行shell 
     *  
     * @param shStr 
     *            需要执行的shell 
     * @return 
     * @throws IOException 
     */  
    public static List runShell(String shStr) throws Exception {  
        List<String> strList = new ArrayList();  
  
        Process process;  
        process = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",shStr},null,null);  
        InputStreamReader ir = new InputStreamReader(process  
                .getInputStream());  
        LineNumberReader input = new LineNumberReader(ir);  
        String line;  
        process.waitFor();  
        while ((line = input.readLine()) != null){  
            strList.add(line);  
        }  
          
        return strList;  
    }  

}
