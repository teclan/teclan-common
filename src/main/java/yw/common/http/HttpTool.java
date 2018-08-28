package yw.common.http;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import yw.common.exception.JsonPaseException;

public class HttpTool {
	private static Logger log = LoggerFactory.getLogger(HttpTool.class);

	public static String getJsonString(HttpServletRequest request) {
		String method = request.getMethod();
		if (method == "GET") {
			return request.getQueryString();
		} else {
			StringBuffer json = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = request.getReader();
				while ((line = reader.readLine()) != null) {
					json.append(line);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return json.toString();
		}
	}

	public static JSONObject getJsonObject(HttpServletRequest request) throws JsonPaseException {
		String method = request.getMethod();
		if (method == "GET") {
			return JSONObject.parseObject(request.getQueryString());
		} else {
			StringBuffer json = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = request.getReader();
				while ((line = reader.readLine()) != null) {
					json.append(line);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			try {
				return JSONObject.parseObject(json.toString());
			} catch (Exception e) {
				throw new JsonPaseException(e.getMessage());
			}
		}
	}

}
