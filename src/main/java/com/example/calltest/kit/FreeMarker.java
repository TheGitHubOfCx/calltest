package com.example.calltest.kit;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class FreeMarker {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected Map<String, Object> messageMap = new HashMap<>();

    public String parseExpression(String config) {
        StringReader stringReader = null;
        Writer writer = null;
        String isMatch = "0";
        try {
            stringReader = new StringReader(config);
            writer = new StringWriter();
            Template template = new Template(null, stringReader, null);
            template.process(messageMap, writer);
            isMatch = "1";
            return writer.toString();
        } catch (IOException e) {
            logger.error("FreeMarker输入输出异常:{}", e.getMessage());
            throw new Exception("FreeMarker输入输出异常:" + e.getMessage());
        } catch (TemplateException e) {
            logger.error("FreeMarker输入输出异常:{}", e.getMessage());
            throw new Exception("FreeMarker模板异常:" + e.getMessage());
        } finally {
            CloseableKit.close(writer);
            CloseableKit.close(stringReader);
            return "1".equals(isMatch) ? writer.toString() : "false";
        }
    }

    public void setVariable(String key, Object value) {
        messageMap.put(key, value);
    }

    public Object parseExpressionExtend(String config) throws Exception {
        if (config != null && config.startsWith("Timestamp(")) {
            try {
                String timestampValues[] = config.split(",");
                String timestamp = timestampValues[0].replace("Timestamp(", "");
                String dateFormat = timestampValues[1].replace(")", "");
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                return new Timestamp(format.parse(timestamp).getTime());
            } catch (Exception e) {
                throw new Exception("日期转换失败，请使用Timestamp(日期时间值,日期时间格式)标识");
            }
        }
        return config;
    }

    public void setMap(Map map) {
        messageMap.putAll(map);
    }
}
