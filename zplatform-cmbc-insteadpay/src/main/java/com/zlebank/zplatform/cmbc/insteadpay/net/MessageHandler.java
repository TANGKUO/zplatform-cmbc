package com.zlebank.zplatform.cmbc.insteadpay.net;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayBean;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayResultBean;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimeQueryBean;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimeQueryResultBean;
import com.zlebank.zplatform.cmbc.security.CryptoUtil;

/**
 * <strong>Title : MessageHandler</strong><br>
 * <strong>Description : 报文处理器</strong><br>
 * <strong>Create on : 2015-9-30</strong><br>
 * 
 * @author linda1@cmbc.com.cn<br>
 */
public class MessageHandler {

	/**
	 * 日志对象
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 报文配置服务
	 */
	private MessageConfigService messageConfigService;

	/**
	 * 请求消息映射集合
	 */
	private final Map<String, Element> reqMsgMapping = new ConcurrentHashMap<String, Element>();

	/**
	 * 响应消映射集合
	 */
	private final Map<String, Element> resMsgMapping = new ConcurrentHashMap<String, Element>();

	/**
	 * @param messageConfigService
	 *            the messageConfigService to set
	 */
	public void setMessageConfigService(MessageConfigService messageConfigService) {
		this.messageConfigService = messageConfigService;
	}

	/**
	 * 启动
	 * 
	 * @return
	 */
	public void init() {
		/*try {
			// 加载请求报文域配置
			this.loadConfig("REQUEST", messageConfigService.getString("MSG_CFG_PATH_REQ"));
			// 加载响应报文域配置
			this.loadConfig("RESPONSE", messageConfigService.getString("MSG_CFG_PATH_RES"));
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}*/
	}

	/**
	 * 加载配置文件
	 * 
	 * @param messageNature
	 *            报文性质
	 * @param inputStream
	 *            文件输入流
	 * @throws Exception
	 */
	protected void loadConfig(String messageNature, String filePath) throws Exception {
		Map<String, Element> mapping = reqMsgMapping;
		if ("RESPONSE".equalsIgnoreCase(messageNature)) {
			mapping = resMsgMapping;
		}

		String classpathKey = "classpath:";
		InputStream inputStream = null;
		if (filePath.startsWith(classpathKey)) {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath.substring(classpathKey.length()));
		} else {
			inputStream = new FileInputStream(filePath);
		}

		SAXReader reader = new SAXReader();
		Document doc = reader.read(inputStream);
		Element root = doc.getRootElement();
		for (Element element : (List<Element>) root.elements()) {
			String messageId = element.attributeValue("id");
			if (!mapping.containsKey(messageId)) {
				mapping.put(messageId, element);
			} else {
				logger.error("报文[{}]配置已存在", new Object[] { messageId });
			}
		}
		reader = null;
		mapping = null;
		doc = null;
	}

	/**
	 * 打包
	 * 
	 * @param dataContainer
	 * @return
	 */
	public byte[] pack(Map<String, Object> dataContainer) {
		byte[] bytes = null;
		try {
			String charset = messageConfigService.getString("CHARSET");// 字符集
			int headLength = messageConfigService.getInt("HEAD_LENGTH", 8);// 报文头长度
			int companyCodeLength = messageConfigService.getInt("COMPANY_CODE_LENGTH", 15);// 合作方编码长度
			int messageCodeLength = messageConfigService.getInt("MESSAGE_CODE_LENGTH", 8);// 报文码长度
			int signCodeLength = messageConfigService.getInt("SIGN_CODE_LENGTH", 4);// 签名编码长度
			String companyCode = messageConfigService.getString("COMPANY_CODE");// 合作方编码
			//PublicKey publicKey = (PublicKey) messageConfigService.getObject("PUBLIC_KEY");// 银行公钥
			//PrivateKey privateKey = (PrivateKey) messageConfigService.getObject("PRIVATE_KEY");// 合作方私钥

			String privateKey = "";
			String publicKey = "";
			
			String messageCode = (String) dataContainer.get("MESSAGE_CODE");
			Element configRootElement = (Element) reqMsgMapping.get(messageCode).elements().get(0);
			/*Document doc = DocumentHelper.createDocument();
			doc.setXMLEncoding(charset);
			Element rootMessageElement = doc.addElement(configRootElement.getName());
			for (Element configElement : (List<Element>) configRootElement.elements()) {
				if (!XMLMessageUtil.packField(dataContainer, configElement, rootMessageElement, "", charset)) {
					return null;
				}
			}
			String xml = doc.asXML().replaceAll("\r", "").replaceAll("\n", "");*/
			String xml = "";
			logger.info("<<<---{}:{}", new Object[] { messageCode, xml });
			byte[] xmlBytes = xml.getBytes(charset);

			byte[] signBytes = CryptoUtil.digitalSign(xmlBytes, privateKey, "SHA1WithRSA");// 签名
			byte[] encryptedBytes = CryptoUtil.encrypt(xmlBytes, publicKey, 2048, 11, "RSA/ECB/PKCS1Padding");// 加密

			StringBuffer buffer = new StringBuffer();
			buffer.append(StringUtils.leftPad(String.valueOf(companyCodeLength + messageCodeLength + signCodeLength + signBytes.length + encryptedBytes.length), headLength, "0"));
			buffer.append(StringUtils.leftPad(companyCode, companyCodeLength, " "));
			buffer.append(StringUtils.leftPad(messageCode, messageCodeLength, " "));
			buffer.append(StringUtils.leftPad(String.valueOf(signBytes.length), signCodeLength, "0"));

			bytes = ArrayUtils.addAll(bytes, buffer.toString().getBytes(charset));
			bytes = ArrayUtils.addAll(bytes, signBytes);
			bytes = ArrayUtils.addAll(bytes, encryptedBytes);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return bytes;
	}
	
	public byte[] pack(RealTimePayBean realTimePayBean) {
		byte[] bytes = null;
		try {
			String charset = messageConfigService.getString("CHARSET");// 字符集
			int headLength = messageConfigService.getInt("HEAD_LENGTH", 8);// 报文头长度
			int companyCodeLength = messageConfigService.getInt("COMPANY_CODE_LENGTH", 15);// 合作方编码长度
			int messageCodeLength = messageConfigService.getInt("MESSAGE_CODE_LENGTH", 8);// 报文码长度
			int signCodeLength = messageConfigService.getInt("SIGN_CODE_LENGTH", 4);// 签名编码长度
			String companyCode = messageConfigService.getString("COMPANY_CODE");// 合作方编码
			//PublicKey publicKey = (PublicKey) messageConfigService.getObject("PUBLIC_KEY");// 银行公钥
			//PrivateKey privateKey = (PrivateKey) messageConfigService.getObject("PRIVATE_KEY");// 合作方私钥

			String privateKey = Constant.getInstance().getCmbc_insteadpay_privatekey();
			String publicKey = Constant.getInstance().getCmbc_insteadpay_publickey();
			
			String messageCode = Constant.REALTIME_INSTEADPAY;
			String xml = realTimePayBean.toXML();
			logger.info("<<<---{}:{}", new Object[] { messageCode, xml });
			byte[] xmlBytes = xml.getBytes(charset);

			byte[] signBytes = CryptoUtil.digitalSign(xmlBytes, privateKey, "SHA1WithRSA");// 签名
			byte[] encryptedBytes = CryptoUtil.encrypt(xmlBytes, publicKey, 2048, 11, "RSA/ECB/PKCS1Padding");// 加密

			StringBuffer buffer = new StringBuffer();
			buffer.append(StringUtils.leftPad(String.valueOf(companyCodeLength + messageCodeLength + signCodeLength + signBytes.length + encryptedBytes.length), headLength, "0"));
			buffer.append(StringUtils.leftPad(companyCode, companyCodeLength, " "));
			buffer.append(StringUtils.leftPad(messageCode, messageCodeLength, " "));
			buffer.append(StringUtils.leftPad(String.valueOf(signBytes.length), signCodeLength, "0"));

			bytes = ArrayUtils.addAll(bytes, buffer.toString().getBytes(charset));
			bytes = ArrayUtils.addAll(bytes, signBytes);
			bytes = ArrayUtils.addAll(bytes, encryptedBytes);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return bytes;
	}
	public byte[] pack(RealTimeQueryBean queryBean) {
		byte[] bytes = null;
		try {
			String charset = messageConfigService.getString("CHARSET");// 字符集
			int headLength = messageConfigService.getInt("HEAD_LENGTH", 8);// 报文头长度
			int companyCodeLength = messageConfigService.getInt("COMPANY_CODE_LENGTH", 15);// 合作方编码长度
			int messageCodeLength = messageConfigService.getInt("MESSAGE_CODE_LENGTH", 8);// 报文码长度
			int signCodeLength = messageConfigService.getInt("SIGN_CODE_LENGTH", 4);// 签名编码长度
			String companyCode = messageConfigService.getString("COMPANY_CODE");// 合作方编码
			//PublicKey publicKey = (PublicKey) messageConfigService.getObject("PUBLIC_KEY");// 银行公钥
			//PrivateKey privateKey = (PrivateKey) messageConfigService.getObject("PRIVATE_KEY");// 合作方私钥

			String privateKey = Constant.getInstance().getCmbc_insteadpay_privatekey();
			String publicKey = Constant.getInstance().getCmbc_insteadpay_publickey();
			
			String messageCode = Constant.REALTIME_INSTEADPAY_QUERY;
			String xml = queryBean.toXML();
			logger.info("<<<---{}:{}", new Object[] { messageCode, xml });
			byte[] xmlBytes = xml.getBytes(charset);

			byte[] signBytes = CryptoUtil.digitalSign(xmlBytes, privateKey, "SHA1WithRSA");// 签名
			byte[] encryptedBytes = CryptoUtil.encrypt(xmlBytes, publicKey, 2048, 11, "RSA/ECB/PKCS1Padding");// 加密

			StringBuffer buffer = new StringBuffer();
			buffer.append(StringUtils.leftPad(String.valueOf(companyCodeLength + messageCodeLength + signCodeLength + signBytes.length + encryptedBytes.length), headLength, "0"));
			buffer.append(StringUtils.leftPad(companyCode, companyCodeLength, " "));
			buffer.append(StringUtils.leftPad(messageCode, messageCodeLength, " "));
			buffer.append(StringUtils.leftPad(String.valueOf(signBytes.length), signCodeLength, "0"));

			bytes = ArrayUtils.addAll(bytes, buffer.toString().getBytes(charset));
			bytes = ArrayUtils.addAll(bytes, signBytes);
			bytes = ArrayUtils.addAll(bytes, encryptedBytes);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return bytes;
	}
	/**
	 * 解包
	 * 
	 * @param bytes
	 * @return
	 */
	public Map<String, Object> unpack(byte[] bytes) {
		Map<String, Object> dataContainer = Maps.newHashMap();
		Map<String, Object> resultMap = Maps.newHashMap();
		try {
			String charset = messageConfigService.getString("CHARSET");// 字符集
			int companyCodeLength = messageConfigService.getInt("COMPANY_CODE_LENGTH", 15);// 合作方编码长度
			int messageCodeLength = messageConfigService.getInt("MESSAGE_CODE_LENGTH", 8);// 报文码长度
			int signCodeLength = messageConfigService.getInt("SIGN_CODE_LENGTH", 4);// 签名编码长度
			//PublicKey publicKey = (PublicKey) messageConfigService.getObject("PUBLIC_KEY");// 银行公钥
			//PrivateKey privateKey = (PrivateKey) messageConfigService.getObject("PRIVATE_KEY");
			String privateKey = Constant.getInstance().getCmbc_insteadpay_privatekey();// 合作方私钥
			String publicKey = Constant.getInstance().getCmbc_insteadpay_publickey();// 银行公钥
			

			int headLength = companyCodeLength + messageCodeLength + signCodeLength;
			// 提取交易服务码
			String messageCode = new String(ArrayUtils.subarray(bytes, companyCodeLength, companyCodeLength + messageCodeLength)).trim();
			logger.debug("messageCode:" + messageCode);
			//dataContainer.put("MESSAGE_CODE", messageCode);
			// 提取签名长度
			int signlength = NumberUtils.toInt(new String(ArrayUtils.subarray(bytes, companyCodeLength + messageCodeLength, headLength)).trim());
			logger.debug("signlength:" + signlength);
			// 提取签名域
			byte[] signBytes = ArrayUtils.subarray(bytes, headLength, headLength + signlength);
			// 提取xml密文
			byte[] encryptedBytes = ArrayUtils.subarray(bytes, headLength + signlength, bytes.length);

			byte[] xmlBytes = CryptoUtil.decrypt(encryptedBytes, privateKey, 2048, 11, "RSA/ECB/PKCS1Padding");// 解密
			String xml = new String(xmlBytes, charset);
			logger.info("--->>>{}:{}", new Object[] { messageCode, xml });
			XStream xstream = new XStream(new DomDriver(null,new XmlFriendlyNameCoder("_-", "_")));
			
			if(Constant.REALTIME_INSTEADPAY.equals(messageCode)){
				xstream.processAnnotations(RealTimePayResultBean.class);
	            xstream.autodetectAnnotations(true);
	            RealTimePayResultBean realTimePayResultBean =  (RealTimePayResultBean) xstream.fromXML(xml);
	            resultMap.put("messagecode", Constant.REALTIME_INSTEADPAY);
	            resultMap.put("result", realTimePayResultBean);
			}else if(Constant.REALTIME_INSTEADPAY_QUERY.equals(messageCode)){
				xstream.processAnnotations(RealTimeQueryResultBean.class);
	            xstream.autodetectAnnotations(true);
	            RealTimeQueryResultBean queryResultBean = (RealTimeQueryResultBean) xstream.fromXML(xml);
	            resultMap.put("messagecode", Constant.REALTIME_INSTEADPAY_QUERY);
	            resultMap.put("result", queryResultBean);
			}
			
			
			/*Document doc = DocumentHelper.parseText(xml);
			Element rootMessageElement = doc.getRootElement();
			Element configRootElement = (Element) resMsgMapping.get(messageCode).elements().get(0);
			for (Element configElement : (List<Element>) configRootElement.elements()) {
				if (!XMLMessageUtil.unpackField(dataContainer, rootMessageElement, configElement, "", charset)) {
					return dataContainer;
				}
			}*/

			boolean isValid = CryptoUtil.verifyDigitalSign(xmlBytes, signBytes, publicKey, "SHA1WithRSA");// 验签
			if (!isValid) {
				logger.error("报文验签不通过");
				dataContainer.put("YHYDLX", "FAIL");
				dataContainer.put("YHYDM", "97");
				dataContainer.put("YHYDMS", "验签失败");
				return dataContainer;
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return resultMap;
	}
}
