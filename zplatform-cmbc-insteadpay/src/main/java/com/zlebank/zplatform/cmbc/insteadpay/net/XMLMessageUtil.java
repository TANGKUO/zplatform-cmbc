package com.zlebank.zplatform.cmbc.insteadpay.net;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Title : XMLMessageUtil</strong><br>
 * <strong>Description : XML报文工具</strong><br>
 * <strong>Create on : 2014-6-10</strong><br>
 * 
 * @author linda1@cmbc.com.cn<br>
 */
public final class XMLMessageUtil {

	/**
	 * 日志对象
	 */
	private static final Logger logger = LoggerFactory.getLogger(XMLMessageUtil.class);

	/**
	 * 解析报文域
	 * 
	 * @param dataContainer
	 *            数据载体
	 * @param messageElement
	 *            报文节点
	 * @param parentConfigElement
	 *            属性配置节点
	 * @param itemNamePreffix
	 *            域名前缀
	 * @param charset
	 *            字符集
	 */
	@SuppressWarnings("unchecked")
	public static boolean unpackField(Map<String, Object> dataContainer, Element parentMessageElement, Element configElement, String itemNamePreffix, String charset) {
		// 标签名
		String tagName = configElement.getName();
		// 域描述
		String tagDesc = StringUtils.trimToEmpty(configElement.attributeValue("desc"));
		// 元素名称
		String itemName = StringUtils.trimToNull(configElement.attributeValue("item"));
		try {
			if (itemName == null) {
				List<Element> subConfigElements = configElement.elements();
				if (subConfigElements != null && !subConfigElements.isEmpty()) {
					Element messageElement = parentMessageElement.element(tagName);
					if (messageElement == null) {
						List nodes = configElement.selectNodes(configElement.getPath() + "//*[@isNotEmpty=\"true\"]");
						if (nodes == null || nodes.isEmpty()) {
							logger.debug("报文未提供节点[{}]", new Object[] { tagName });
							return true;
						} else {
							dataContainer.put("YHYDLX", "FAIL");
							dataContainer.put("YHYDM", "97");
							dataContainer.put("YHYDMS", String.format("域[{}]存在不能为空的子节点", tagDesc));
							return false;
						}
					}

					for (Element subConfigElement : subConfigElements) {
						if (!unpackField(dataContainer, messageElement, subConfigElement, "", charset)) {
							return false;
						}
					}
				} else {
					logger.debug("报文节点[{}]未配置，不执行解析", new Object[] { tagName });
				}
				return true;
			} else if (itemName != null) {
				boolean isArray = BooleanUtils.toBoolean(configElement.attributeValue("isArray"));
				if (isArray) {
					List<Element> messageElements = parentMessageElement.elements(tagName);
					if (messageElements == null) {
						List nodes = configElement.selectNodes(configElement.getPath() + "//*[@isNotEmpty=\"true\"]");
						if (nodes == null || nodes.isEmpty()) {
							logger.debug("报文未提供节点[{}]", new Object[] { tagName });
							return true;
						} else {
							dataContainer.put("YHYDLX", "FAIL");
							dataContainer.put("YHYDM", "97");
							dataContainer.put("YHYDMS", String.format("域[%s]存在不能为空的子节点", tagDesc));
							return false;
						}
					}

					// 处理子标签
					int counter = NumberUtils.toInt((String) dataContainer.get(itemNamePreffix + itemName));
					for (Element subMessageElement : (List<Element>) parentMessageElement.elements(tagName)) {
						for (Element subConfigElement : (List<Element>) configElement.elements()) {
							if (!unpackField(dataContainer, subMessageElement, subConfigElement, itemNamePreffix + itemName + "[" + counter + "]", charset)) {
								return false;
							}
						}
					}
					counter++;
					dataContainer.put(itemNamePreffix + itemName, String.valueOf(counter));
					return true;
				}
			}

			// 默认值
			String defaultValue = StringUtils.trimToNull(configElement.attributeValue("default"));
			// 检测不可为空或空串
			boolean isNotEmpty = BooleanUtils.toBoolean(configElement.attributeValue("isNotEmpty"));
			Element messageElement = parentMessageElement.element(tagName);
			if (messageElement == null) {
				if (isNotEmpty) {
					dataContainer.put("YHYDLX", "FAIL");
					dataContainer.put("YHYDM", "97");
					dataContainer.put("YHYDMS", String.format("域[%s]不能为空", tagDesc));
					return false;
				} else {
					if (defaultValue == null) {
						logger.debug("报文未提供节点[{}]", new Object[] { tagName });
						return true;
					}
				}
			}

			// 正则表达式
			String validateRegx = StringUtils.trimToNull(configElement.attributeValue("validateRegx"));
			// 源域值
			String srcValue = null;
			if (messageElement != null && (srcValue = StringUtils.trimToNull(messageElement.getText())) != null) {
				// 长度
				int length = NumberUtils.toInt(configElement.attributeValue("length"));
				if (length > 0) {
					byte[] bytes = srcValue.replaceAll("\\.", "").getBytes(charset);
					if (bytes.length > length) {
						dataContainer.put("YHYDLX", "FAIL");
						dataContainer.put("YHYDM", "97");
						dataContainer.put("YHYDMS", String.format("域[%s]的值超出最大长度[%d]", tagDesc, length));
						return false;
					}
				}

				if (validateRegx != null && !srcValue.matches(validateRegx)) {
					dataContainer.put("YHYDLX", "FAIL");
					dataContainer.put("YHYDM", "97");
					dataContainer.put("YHYDMS", String.format("域[%s]的值[%s]校验失败", tagDesc, srcValue));
					return false;
				}
			} else {
				srcValue = defaultValue;
			}

			// 目标域值
			Object tgtValue = srcValue;
			if (srcValue != null) {

				// 数据类型
				String type = StringUtils.trimToNull(configElement.attributeValue("type"));
				// 移位数，大于0则向右移位，小于0则向左移位
				int movePoint = NumberUtils.toInt(configElement.attributeValue("movePoint"));
				// 精度，用于形成浮点值
				int scale = NumberUtils.toInt(configElement.attributeValue("scale"));
				if ("integer".equalsIgnoreCase(type)) {
					BigDecimal decimal = new BigDecimal(srcValue);
					if (movePoint != 0) {
						decimal = decimal.movePointRight(movePoint);
					}
					tgtValue = decimal.intValue();
				} else if ("long".equalsIgnoreCase(type)) {
					BigDecimal decimal = new BigDecimal(srcValue);
					if (movePoint != 0) {
						decimal = decimal.movePointRight(movePoint);
					}
					tgtValue = decimal.longValue();
				} else if ("double".equalsIgnoreCase(type)) {
					BigDecimal decimal = new BigDecimal(srcValue);
					if (movePoint != 0) {
						decimal = decimal.movePointRight(movePoint);
					}
					if (scale == 0) {
						scale = 2;
					}
					decimal = decimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
					tgtValue = String.valueOf(decimal.doubleValue());
				} else if ("date".equalsIgnoreCase(type)) {
					// 源日期时间格式
					String srcFormat = StringUtils.trimToNull(configElement.attributeValue("srcFormat"));
					if (srcFormat == null) {
						srcFormat = "yyyy-MM-dd";
					}
					// 目标日期时间格式
					String tgtFormat = StringUtils.trimToNull(configElement.attributeValue("tgtFormat"));
					if (tgtFormat == null) {
						tgtFormat = "yyyy-MM-dd";
					}

					Date date = DateUtils.parseDate(srcValue, new String[] { srcFormat });
					tgtValue = DateFormatUtils.format(date, tgtFormat);
				} else {
					// 当前时间，具体格式自定
					if (srcValue.startsWith("CUR_TIME:")) {
						String pattern = srcValue.substring("CUR_TIME:".length());
						tgtValue = DateFormatUtils.format(new Date(), pattern);
					} else if (srcValue.equals("UUID")) {
						tgtValue = uuid();
					}
				}
			}

			if (isNotEmpty && tgtValue == null) {
				dataContainer.put("YHYDLX", "FAIL");
				dataContainer.put("YHYDM", "97");
				dataContainer.put("YHYDMS", String.format("域[%s]不能为空", tagDesc, srcValue));
				return false;
			}
			logger.debug("域[{}，域名{}，标签名{}]：{}", new Object[] { tagDesc, itemName, tagName, tgtValue });
			dataContainer.put(itemNamePreffix + itemName, tgtValue);
		} catch (Exception e) {
			logger.error("域[{}]解析出现异常", new Object[] { tagDesc });
			logger.error(e.getLocalizedMessage(), e);
			dataContainer.put("YHYDLX", "FAIL");
			dataContainer.put("YHYDM", "97");
			dataContainer.put("YHYDMS", String.format("域[%s]解析出现异常", tagDesc));
			return false;

		}
		return true;
	}

	/**
	 * 打包报文域
	 * 
	 * @param dataContainer
	 *            数据载体
	 * @param configElement
	 *            配置节点
	 * @param parentMessageElement
	 *            父报文节点
	 * @param itemNamePreffix
	 *            域名前缀
	 * @param charset
	 *            字符集
	 */
	@SuppressWarnings("unchecked")
	public static boolean packField(Map<String, Object> dataContainer, Element configElement, Element parentMessageElement, String itemNamePreffix, String charset) {
		// 标签名
		String tagName = StringUtils.trimToNull(configElement.attributeValue("id"));
		if (tagName == null) {
			tagName = configElement.getName();
		}
		// 标签描述
		String tagDesc = StringUtils.trimToNull(configElement.attributeValue("desc"));
		// 域名
		String itemName = StringUtils.trimToNull(configElement.attributeValue("item"));
		if (itemName == null) {
			itemName = tagName;
		}
		itemName = itemNamePreffix + itemName;
		try {
			// 检测域名
			String checkItemName = StringUtils.trimToNull(configElement.attributeValue("checkItem"));
			if (checkItemName != null) {
				String checkValue = (String) dataContainer.get(checkItemName);
				// 检测域值列表
				String needValues = StringUtils.trimToEmpty(configElement.attributeValue("needValues"));
				if (needValues.indexOf(checkValue + "|") < 0) {
					return true;
				}
			}

			List<Element> subConfigElements = configElement.elements();
			if (subConfigElements != null && !subConfigElements.isEmpty()) {
				int rows = NumberUtils.toInt((String) dataContainer.get(itemName));
				if (rows > 0) {
					for (int i = 0; i < rows; i++) {
						Element subMessageElement = parentMessageElement.addElement(tagName);
						for (Element subConfigElement : subConfigElements) {
							if (!packField(dataContainer, subConfigElement, subMessageElement, itemName + "[" + i + "]", charset)) {
								return false;
							}
						}
					}
				} else {
					Element subMessageElement = parentMessageElement.addElement(tagName);
					for (Element subConfigElement : subConfigElements) {
						if (!packField(dataContainer, subConfigElement, subMessageElement, "", charset)) {
							return false;
						}
					}
				}
				return true;
			}

			// 数据类型
			String type = StringUtils.trimToNull(configElement.attributeValue("type"));
			// 移位数，大于0则向右移位，小于0则向左移位
			int movePoint = NumberUtils.toInt(configElement.attributeValue("movePoint"));
			// 精度，用于形成浮点值
			int scale = NumberUtils.toInt(configElement.attributeValue("scale"));
			// 格式化匹配符
			String formatRegx = StringUtils.trimToNull(configElement.attributeValue("formatRegx"));
			// 检测不可为空或空串
			boolean isNotEmpty = BooleanUtils.toBoolean(configElement.attributeValue("isNotEmpty"));
			// 正则表达式
			String validateRegx = StringUtils.trimToNull(configElement.attributeValue("validateRegx"));
			// 默认值
			String defaultValue = StringUtils.trimToNull(configElement.attributeValue("default"));

			Object obj = dataContainer.get(itemName);
			if (obj == null) {
				obj = defaultValue;
			}

			String srcValue = String.valueOf(obj);
			// 标签文本
			String tagText = null;
			if (obj == null || StringUtils.trimToNull(srcValue) == null) {

			} else if ("integer".equalsIgnoreCase(type)) {
				BigDecimal decimal = new BigDecimal(srcValue);
				if (movePoint != 0) {
					decimal = decimal.movePointRight(movePoint);
				}

				if (formatRegx != null) {
					tagText = formatNumber(decimal, formatRegx);
				} else {
					tagText = decimal.toPlainString();
				}
			} else if ("long".equalsIgnoreCase(type)) {
				BigDecimal decimal = new BigDecimal(srcValue);
				if (movePoint != 0) {
					decimal = decimal.movePointRight(movePoint);
				}

				if (formatRegx != null) {
					tagText = formatNumber(decimal, formatRegx);
				} else {
					tagText = decimal.toPlainString();
				}
			} else if ("double".equalsIgnoreCase(type)) {
				BigDecimal decimal = new BigDecimal(srcValue);
				if (movePoint != 0) {
					decimal = decimal.movePointRight(movePoint);
				}

				if (formatRegx != null) {
					tagText = formatNumber(decimal, formatRegx);
				} else {
					if (scale == 0) {
						scale = 2;
					}
					decimal.setScale(scale, BigDecimal.ROUND_UP);

					tagText = decimal.toPlainString();
				}
			} else if ("date".equalsIgnoreCase(type)) {
				// 源日期时间格式
				String srcFormat = StringUtils.trimToNull(configElement.attributeValue("srcFormat"));
				if (srcFormat == null) {
					srcFormat = "yyyy-MM-dd";
				}
				// 目标日期时间格式
				String tgtFormat = StringUtils.trimToNull(configElement.attributeValue("tgtFormat"));
				if (tgtFormat == null) {
					tgtFormat = "yyyy-MM-dd";
				}
				Date date = null;
				if (obj instanceof java.util.Date) {
					date = (Date) obj;
				} else if (obj instanceof java.sql.Date) {
					date = new Date(((java.sql.Date) obj).getTime());
				} else if (obj instanceof java.sql.Timestamp) {
					date = new Date(((java.sql.Timestamp) obj).getTime());
				} else {
					if (srcValue.indexOf("CUR_TIME") >= 0) {
						date = new Date();
						if (srcValue.indexOf(":") > 0) {
							tgtFormat = srcValue.substring(srcValue.indexOf(":") + 1);
						}
					} else {
						date = DateUtils.parseDate(srcValue, new String[] { srcFormat });
					}
				}
				tagText = DateFormatUtils.format(date, tgtFormat);
			} else {
				tagText = srcValue;
				
				// 当前时间，具体格式自定
				if (tagText.startsWith("CUR_TIME:")) {
					String pattern = tagText.substring("CUR_TIME:".length());
					tagText = DateFormatUtils.format(new Date(), pattern);
				}

				// 是否需要去掉尾部空格
				String canTrim = StringUtils.trimToNull(configElement.attributeValue("canTrim"));
				if (!"false".equals(canTrim)) {
					tagText = tagText.trim();
				}

				if (tagText != null && validateRegx != null && !tagText.matches(validateRegx)) {
					dataContainer.put("YHYDLX", "FAIL");
					dataContainer.put("YHYDM", "97");
					dataContainer.put("YHYDMS", String.format("域[%s]的值[%s]校验失败", tagDesc, tagText));
					return false;
				}
			}

			if (tagText != null) {
				// 数据字节长度
				int length = NumberUtils.toInt(configElement.attributeValue("length"), 0);
				// 数据字符长度
				int charLength = NumberUtils.toInt(configElement.attributeValue("charLength"), 0);
				byte[] bytes = tagText.getBytes(charset);
				if (length > 0 && bytes.length <= length) {
					// 对齐方式，left、right
					String align = StringUtils.trimToNull(configElement.attributeValue("align"));
					if (align != null) {
						// 补位字符
						String pad = StringUtils.trimToNull(configElement.attributeValue("pad"));
						if (pad == null) {
							if ("integer|long|double|".indexOf(type + "|") >= 0) {
								pad = "0";
							} else {
								pad = " ";
							}
						}

						// 左对齐，右边补位
						if ("left".equalsIgnoreCase(align)) {
							tagText = StringUtils.rightPad(tagText, length, pad);
						}
						// 右对齐，左边补位，默认值
						else {
							tagText = StringUtils.leftPad(tagText, length, pad);
						}
					}
				} else if (length > 0) {
					// 数据过长，截断
					tagText = new String(ArrayUtils.subarray(bytes, 0, length), charset);
				} else if (length == 0) {
					if (charLength > 0 && tagText.length() <= charLength) {

					} else if (charLength > 0) {
						// 数据过长，截断
						tagText = tagText.substring(0, charLength);
					}
				}
			} else {
				tagText = "";
			}

			if (isNotEmpty && (tagText == null || "".equals(tagText))) {
				dataContainer.put("YHYDLX", "FAIL");
				dataContainer.put("YHYDM", "97");
				dataContainer.put("YHYDMS", String.format("域[%s]不能为空", tagDesc));
				return false;
			}
			logger.debug("域[{}，域名{}，标签名{}]：{}", new Object[] { tagDesc, itemName, tagName, tagText });

			// 是否允许空节点
			String isAllowEmptyNode = StringUtils.trimToNull(configElement.attributeValue("isAllowEmptyNode"));
			if ("false".equals(isAllowEmptyNode) && "".equals(tagText)) {
				return true;
			}
			Element messageElement = parentMessageElement.addElement(tagName);
			messageElement.setText(tagText);
		} catch (Exception e) {
			logger.error("域[{}]处理出现异常", new Object[] { tagDesc });
			logger.error(e.getLocalizedMessage(), e);
			dataContainer.put("YHYDLX", "FAIL");
			dataContainer.put("YHYDM", "97");
			dataContainer.put("YHYDMS", String.format("域[%s]处理出现异常", tagDesc));
			return false;
		}
		return true;
	}

	/**
	 * @return
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("\\-", "").toUpperCase();
	}

	/**
	 * 对浮点值四舍五入
	 * 
	 * @param value
	 *            浮点值
	 * @param regx
	 *            格式化匹配符
	 * @return
	 */
	public static String formatNumber(Number value, String regx) {
		if (value == null || value.doubleValue() == 0) {
			return "";
		}
		if (regx == null && value instanceof Double) {
			regx = "#,##0.00";
		} else if (regx == null) {
			regx = "#,##0";
		}
		DecimalFormat format = new DecimalFormat(regx);
		return format.format(value);
	}
}
