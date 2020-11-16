package com.example.calltest.kit;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.commons.beanutils.BeanUtils;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CreateCsvUtil {

    private static Log LOG = LogFactory.get();


    public static File createCSVFile(List exportData, Map map, String outPutPath, String fileName, String suffix) {
        File csvFile = null;
        BufferedWriter csvFileOutputStream = null;
        try {
            File file = new File(outPutPath);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }
            //定义文件名格式并创建
//            csvFile = File.createTempFile(fileName, suffix, new File(outPutPath));
            String separator = File.separator;
            csvFile = new File(outPutPath + separator + fileName + suffix);
            LOG.info("csvFile：" + csvFile);
            FileOutputStream fileOutputStream = new FileOutputStream(csvFile);
            //加入bom 否则生成的csv文件 用excel乱码
            fileOutputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            // UTF-8使正确读取分隔符","
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream
                    , "utf-8");
            csvFileOutputStream = new BufferedWriter(outputStreamWriter, 1024);
            LOG.info("csvFileOutputStream：" + csvFileOutputStream);
            // 写入文件头部
            for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator.hasNext(); ) {
                Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                csvFileOutputStream.write((String) propertyEntry.getKey() != null ? (String) propertyEntry.getKey() : "");
                if (propertyIterator.hasNext()) {
                    csvFileOutputStream.write(",");
                }
            }
            csvFileOutputStream.newLine();
            // 写入文件内容
            for (Iterator iterator = exportData.iterator(); iterator.hasNext(); ) {
                Object row = (Object) iterator.next();
                for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator.hasNext(); ) {
                    Map.Entry propertyEntry = (Map.Entry) propertyIterator
                            .next();
                    //如果是空值则进行处理用 "" 号填充 否则会抛空指针
                    String va = "";
                    String property = BeanUtils.getProperty(row, (String) propertyEntry.getKey());
                    if (property != null && !property.equals("")) {
                        //CSV文件默认逗号换单元格，默认将内容加上双引号包含起来
                        csvFileOutputStream.write("\"" + property + "\"");
//                        csvFileOutputStream.write(property);
                    } else {
                        csvFileOutputStream.write(va);
                    }
                    if (propertyIterator.hasNext()) {
                        csvFileOutputStream.write(",");
                    }
                }
                if (iterator.hasNext()) {
                    csvFileOutputStream.newLine();
                }
            }
            csvFileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }
}
