package yw.common.properties;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 配置为文件解析工具，加载 classPath下的 properties/ 目录下的配置文件，
 * 
 * 配置文件名可通过参数指定，也可通过环境命令自定义读取配置文件的路径
 * 
 * @author dev
 *
 */
public class PropertyUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtils.class);
	private URL url;

	private static Map<String, PropertyUtils> propertyUtils = new HashMap<String, PropertyUtils>();

	private PropertyUtils(String propertiesPath) {
		this.url = PropertyUtils.class.getClassLoader().getResource(propertiesPath);
		LOGGER.info("加载文件 :{}", url);
	}

	private PropertyUtils(String propertiesPath, Class<?> cls, String env) {

		String environment = System.getenv(env);

		if (environment == null) {
			LOGGER.warn("环境变量 {} 未设置...", env);
			this.url = cls.getClassLoader().getResource("properties/" + propertiesPath);

		} else {
			this.url = cls.getClassLoader().getResource("properties/" + environment + "/" + propertiesPath);
		}
	}

	/**
	 * 加载默认环境变量（YW_ENV）指定的配置为文件，</br>
	 * 例如环境变量为 YW_ENV,指定的文件名称为 config.properties, </br>
	 * </br>
	 * 如果环境变量YW_ENV的值为 develope，则会加载 classpath下的</br>
	 * properties/develop/config.properties文件，</br>
	 * </br>
	 * 如果环境变量YW_ENV的值为 product，则加载 classpath下的</br>
	 * properties/product/config.properties文件，</br>
	 * </br>
	 * 如果环境变量YW_ENV的值为空或未定义，则加载 classpath下的</br>
	 * properties/config.properties文件，
	 * 
	 * @param propertiesPath
	 *            配置文件名
	 * @param cls
	 *            当前类
	 * @return
	 */
	public static PropertyUtils getInstance(String propertiesPath, Class<?> cls) {
		PropertyUtils propertyUtil = propertyUtils.get(propertiesPath);

		if (propertyUtil == null) {
			propertyUtil = new PropertyUtils(propertiesPath, cls, "YW_ENV");
			propertyUtils.put(propertiesPath, propertyUtil);
		}
		return propertyUtil;
	}

	/**
	 * 加载指定环境变量指定的配置为文件，</br>
	 * 例如环境变量为 YW_ENV,指定的文件名称为 config.properties, </br>
	 * </br>
	 * 如果环境变量YW_ENV的值为 develope，则会加载 classpath下的</br>
	 * properties/develop/config.properties文件，</br>
	 * </br>
	 * 如果环境变量YW_ENV的值为 product，则加载 classpath下的</br>
	 * properties/product/config.properties文件，</br>
	 * </br>
	 * 如果环境变量YW_ENV的值为空或未定义，则加载 classpath下的</br>
	 * properties/config.properties文件，
	 * 
	 * @param propertiesPath
	 *            配置文件名
	 * @param cls
	 *            当前类
	 * @param env
	 *            环境变量名
	 * @return
	 */
	public static PropertyUtils getInstance(String propertiesPath, Class<?> cls, String env) {

		PropertyUtils propertyUtil = propertyUtils.get(propertiesPath);

		if (propertyUtil == null) {
			propertyUtil = new PropertyUtils(propertiesPath, cls, env);
			propertyUtils.put(propertiesPath, propertyUtil);
		}
		return propertyUtil;
	}


	public int getIntValue(String key) {
		return Integer.parseInt(getValue(key));
	}

	public long getLongValue(String key) {
		return Long.parseLong(getValue(key));
	}

	public synchronized String getValue(String key) {
		Properties properties = new Properties();
		InputStream inputStream = null;

		try {
			inputStream = url.openStream();
			BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); // 解决中文乱码问题
			properties.load(bf); // /加载属性列表

			check(key, properties);
			String value = properties.getProperty(key).trim();
			LOGGER.debug("getValue from proerties:" + url + ":" + key + "=" + value);
			return value;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("getValue(String)resourse:" + url, e);
		} catch (IOException e) {
			throw new RuntimeException("getValue(String)", e);
		} finally {
			try {
				if (inputStream == null) {
					LOGGER.error("can not get resourse:" + url);
				} else {
					inputStream.close();
				}
			} catch (IOException e) {
				LOGGER.error("getValue(String)", e);
			}
		}
	}

	protected boolean check(String key, Properties properties) {
		if (properties.containsKey(key)) {
			return true;
		} else {
			throw new RuntimeException("the property file[" + url + "] do not have the key:" + key);
		}
	}
}
