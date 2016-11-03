package com.zlebank.zplatform.cmbc.withholding.net.netty;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlebank.zplatform.cmbc.common.utils.Constant;

/**
 * <strong>Title : MessageConfigService</strong><br>
 * <strong>Description : 报文配置服务</strong><br>
 * <strong>Create on : 2016-3-15</strong><br>
 * 
 * @author linda1@cmbc.com.cn<br>
 */
public class MessageConfigService {

	/**
	 * 日志对象
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 配置参数集合
	 */
	private final Map<String, Object> configParams = new HashMap<String, Object>();

	/**
	 * 允许运行标识
	 */
	private volatile boolean canRun = true;

	/**
	 * 初始化
	 */
	public void init() {
		// 加载参数
		refresh();
		/**
		 * 运行内存刷新线程
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (canRun) {
					try {
						refresh();
						int interval = NumberUtils.toInt((String) configParams.get("REFRESH_INTERVAL"), 60) * 1000;// 刷新间隔，单位：秒
						Thread.sleep(interval);
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			}
		}).start();
	}

	/**
	 * 刷新参数
	 */
	private void refresh() {
		// TODO
		// 读取参数功能自行实现
		String canRunStr = (String) configParams.get("CAN_RUN");// 是否允许运行标识
		if ("false".equalsIgnoreCase(canRunStr)) {
			canRun = false;
		} else {
			canRun = true;
		}
		configParams.put("CAN_RUN", String.valueOf(canRun));
		String hostName = StringUtils.trimToNull((String) configParams.get("HOST_NAME"));// 主机名称
		if (hostName == null) {
			configParams.put("HOST_NAME", "未知");
		}
		String hostAddress = StringUtils.trimToNull((String) configParams.get("HOST_ADDRESS"));// 主机地址
		if (hostAddress == null) {
			configParams.put("HOST_ADDRESS", Constant.getInstance().getCmbc_withholding_ip());
		}
		
		String hostPort = StringUtils.trimToNull((String) configParams.get("HOST_PORT"));// 主机端口
		if (hostPort == null) {
			configParams.put("HOST_PORT",Constant.getInstance().getCmbc_withholding_port()+"" );
		}
		String charset = StringUtils.trimToNull((String) configParams.get("CHARSET"));// 字符集
		if (charset == null) {
			configParams.put("CHARSET", "UTF-8");
		}
		int headLength = NumberUtils.toInt((String) configParams.get("HEAD_LENGTH"), 8);// 报文头长度
		configParams.put("HEAD_LENGTH", String.valueOf(headLength));
		int companyCodeLength = NumberUtils.toInt((String) configParams.get("COMPANY_CODE_LENGTH"), 8);// 合作方编码长度
		configParams.put("COMPANY_CODE_LENGTH", String.valueOf(companyCodeLength));
		int messageCodeLength = NumberUtils.toInt((String) configParams.get("MESSAGE_CODE_LENGTH"), 8);// 报文码长度
		configParams.put("MESSAGE_CODE_LENGTH", String.valueOf(messageCodeLength));
		int signCodeLength = NumberUtils.toInt((String) configParams.get("SIGN_CODE_LENGTH"), 4);// 签名编码长度
		configParams.put("SIGN_CODE_LENGTH", String.valueOf(signCodeLength));

		String heartbeatMessage = StringUtils.trimToNull((String) configParams.get("HEARTBEAT_MESSAGE"));// 心跳报文
		if (heartbeatMessage == null) {
			configParams.put("HEARTBEAT_MESSAGE", StringUtils.leftPad("", headLength, "0"));
		}
		String companyCode = StringUtils.trimToNull((String) configParams.get("COMPANY_CODE"));// 合作方编码
		if (companyCode == null) {
			//configParams.put("COMPANY_CODE", "CS");
			configParams.put("COMPANY_CODE", Constant.getInstance().getCmbc_merid());
		}
		String tranMessageCode = StringUtils.trimToNull((String) configParams.get("MESSAGE_CODE_TRAN"));// 报文码:代付交易
		if (tranMessageCode == null) {
			configParams.put("MESSAGE_CODE_TRAN", Constant.WITHHOLDING);
		}
		String queryMessageCode = StringUtils.trimToNull((String) configParams.get("MESSAGE_CODE_QUERY"));// 报文码:查询交易
		if (queryMessageCode == null) {
			configParams.put("MESSAGE_CODE_TRAN_QUERY", Constant.WITHHOLDINGQUERY);
		}
		
		String realNameMessageCode = StringUtils.trimToNull((String) configParams.get("MESSAGE_CODE_REALNAME"));// 报文码:代付交易
		if (realNameMessageCode == null) {
			configParams.put("MESSAGE_CODE_REALNAME", Constant.REALNAMEAUTH);
		}
		String queryRealNameMessageCode = StringUtils.trimToNull((String) configParams.get("MESSAGE_CODE_REALNAME_QUERY"));// 报文码:查询交易
		if (queryRealNameMessageCode == null) {
			configParams.put("MESSAGE_CODE_REALNAME_QUERY", Constant.REALNAMEAUTHQUERY);
		}
		
		String whiteListMessageCode = StringUtils.trimToNull((String) configParams.get("MESSAGE_CODE_WHITELIST"));// 报文码:代付交易
		if (whiteListMessageCode == null) {
			configParams.put("MESSAGE_CODE_WHITELIST", Constant.WHITELIST);
		}
		String queryWhiteListMessageCode = StringUtils.trimToNull((String) configParams.get("MESSAGE_CODE_WHITELIST_QUERY"));// 报文码:查询交易
		if (queryWhiteListMessageCode == null) {
			configParams.put("MESSAGE_CODE_WHITELIST_QUERY", Constant.WHITELISTQUERY);
		}
		
		
		
		
		//报文由xStream进行配置
		/*String reqPath = StringUtils.trimToNull((String) configParams.get("MSG_CFG_PATH_REQ"));// 请求报文配置路径
		if (reqPath == null) {
			configParams.put("MSG_CFG_PATH_REQ", "classpath:msg-cfg-req.xml");
		}
		String resPath = StringUtils.trimToNull((String) configParams.get("MSG_CFG_PATH_RES"));// 应答报文配置路径
		if (resPath == null) {
			configParams.put("MSG_CFG_PATH_RES", "classpath:msg-cfg-res.xml");
		}*/

		//秘钥加密和证联现有秘钥加密方式一样，使用现有秘钥加密方式
		/*String classpathKey = "classpath:";
		String publicKeyPath = StringUtils.trimToNull((String) configParams.get("PUBLIC_KEY_PATH"));// 银行公钥路径
		if (publicKeyPath == null) {
			publicKeyPath = "classpath:bank_rsa_public_key_2048.pem";
		}
		if (publicKeyPath != null) {
			try {
				InputStream inputStream = null;
				if (publicKeyPath.startsWith(classpathKey)) {
					inputStream = this.getClass().getClassLoader().getResourceAsStream(publicKeyPath.substring(classpathKey.length()));
				} else {
					inputStream = new FileInputStream(publicKeyPath);
				}
				PublicKey publicKey = CryptoUtil.getPublicKey(inputStream, "RSA");
				configParams.put("PUBLIC_KEY", publicKey);
			} catch (Exception e) {
				logger.error("无法加载银行公钥[{}]", new Object[] { publicKeyPath });
				logger.error(e.getMessage(), e);
			}
		}
		String privateKeyPath = StringUtils.trimToNull((String) configParams.get("PRIVATE_KEY_PATH"));// 合作方私钥路径
		if (privateKeyPath == null) {
			privateKeyPath = "classpath:company_rsa_private_key_2048.pem";
		}
		if (publicKeyPath != null) {
			try {
				InputStream inputStream = null;
				if (privateKeyPath.startsWith(classpathKey)) {
					inputStream = this.getClass().getClassLoader().getResourceAsStream(privateKeyPath.substring(classpathKey.length()));
				} else {
					inputStream = new FileInputStream(privateKeyPath);
				}
				PrivateKey privateKey = CryptoUtil.getPrivateKey(inputStream, "RSA");
				configParams.put("PRIVATE_KEY", privateKey);
			} catch (Exception e) {
				logger.error("无法加载本地私银[{}]", new Object[] { publicKeyPath });
				logger.error(e.getMessage(), e);
			}
		}*/
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public Object set(String key, Object value) {
		return configParams.put(key, value);
	}

	/**
	 * @param key
	 * @return
	 */
	public Object getObject(String key) {
		return configParams.get(key);
	}

	/**
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		Object obj = configParams.get(key);
		if (obj == null) {
			return null;
		}
		return StringUtils.trimToNull(String.valueOf(configParams.get(key)));
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getString(String key, String defaultValue) {
		Object obj = configParams.get(key);
		if (obj == null) {
			return defaultValue;
		}
		String value = StringUtils.trimToNull(String.valueOf(configParams.get(key)));
		return (value != null ? value : defaultValue);
	}

	/**
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		Object value = configParams.get(key);
		if (value == null) {
			return 0;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		return NumberUtils.toInt(String.valueOf(configParams.get(key)));
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getInt(String key, int defaultValue) {
		Object value = configParams.get(key);
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		return NumberUtils.toInt(String.valueOf(configParams.get(key)), defaultValue);
	}

	/**
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		Object value = configParams.get(key);
		if (value == null) {
			return 0;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		return NumberUtils.toLong(String.valueOf(configParams.get(key)));
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLong(String key, long defaultValue) {
		Object value = configParams.get(key);
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		return NumberUtils.toLong(String.valueOf(configParams.get(key)), defaultValue);
	}

	/**
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		Object value = configParams.get(key);
		if (value == null) {
			return 0;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		return NumberUtils.toDouble(String.valueOf(configParams.get(key)));
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public double getDouble(String key, double defaultValue) {
		Object value = configParams.get(key);
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		return NumberUtils.toDouble(String.valueOf(configParams.get(key)), defaultValue);
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		Object value = configParams.get(key);
		if (value == null) {
			return false;
		}
		return BooleanUtils.toBoolean(String.valueOf(configParams.get(key)));
	}
}
