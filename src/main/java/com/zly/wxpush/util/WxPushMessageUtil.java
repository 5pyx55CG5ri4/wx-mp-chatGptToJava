package com.zly.wxpush.util;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.xstream.XStream;
import com.zly.wxpush.config.WxConfig;
import com.zly.wxpush.constant.WxConstant;
import com.zly.wxpush.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信推送消息
 *
 * @author 5pyx55CG5ri4
 * @date 2022/09/27
 */
@Component
@Slf4j
public class WxPushMessageUtil {



    /**
     * 解析微信发来的请求（XML）
     */
    @SuppressWarnings("ALL")
    public Map<String, String> parseXml(HttpServletRequest request) {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<>();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document;
        InputStream inputStream = null;
        try {
            // 从request中取得输入流
            inputStream = request.getInputStream();
            document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点
            elementList.forEach(element -> map.put(element.getName(), element.getStringValue()));
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }




    /**
     * 发送自定义文本
     *
     * @param openId  开放编号
     * @param content 内容
     */
    public String sendCustomText(String openId, String toUserName, String content) {
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(openId);
        textMessage.setFromUserName(toUserName);
        textMessage.setContent(content);
        textMessage.setCreateTime(System.currentTimeMillis());
        return getXmlString(textMessage);
    }


    public String getXmlString(TextMessage textMessage) {
        String xml = "";
        if (textMessage != null) {
            xml = "<xml>";
            xml += "<ToUserName><![CDATA[";
            xml += textMessage.getToUserName();
            xml += "]]></ToUserName>";
            xml += "<FromUserName><![CDATA[";
            xml += textMessage.getFromUserName();
            xml += "]]></FromUserName>";
            xml += "<CreateTime>";
            xml += textMessage.getCreateTime();
            xml += "</CreateTime>";
            xml += "<MsgType><![CDATA[";
            xml += textMessage.getMsgType();
            xml += "]]></MsgType>";
            xml += "<Content><![CDATA[";
            xml += textMessage.getContent();
            xml += "]]></Content>";
            xml += "</xml>";
        }
        return xml;
    }


}
