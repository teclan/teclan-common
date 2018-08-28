package yw.common.utils;

import com.alibaba.fastjson.JSONObject;

public class ResultUtil {

	/**
	 * 构造返回JSON
	 * 
	 * @param code 返回代码，遵循 html 状态码
	 * @param message 消息摘要，一般为操作结果（成功或失败）
	 * @return
	 */
	public static JSONObject getResponse(String code, String message ) {
		return getResponse(code, message, null,null);
	}
	
	/**
	 * 构造返回JSON
	 * 
	 * @param code 返回代码，遵循 html 状态码
	 * @param message 消息摘要，一般为操作结果（成功或失败）
	 * @param exception 异常原因
	 * @return
	 */
	public static JSONObject getResponse(String code, String message, String exception) {
		return getResponse(code, message, exception, null);
	}
	
	/**
	 * 构造返回JSON
	 * 
	 * @param code 返回代码，遵循 html 状态码
	 * @param message 消息摘要，一般为操作结果（成功或失败）
	 * @param exception 异常原因
	 * @param object 响应的数据内容，例如查询等操作
	 * @return
	 */
	public static JSONObject getResponse(String code, String message, String exception,Object object) {
		JSONObject result = new JSONObject();
		result.put("code", code);
		result.put("message", message);
		result.put("exception", exception);
		result.put("data", Objects.isNull(object)?"":object);
		return result;
	}

}
