package com.example.calltest.controller;

import com.example.calltest.entity.DataBase0Config;
import com.example.calltest.kit.ExcelKit;
import com.example.calltest.kit.ResultSetKit;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SqlResultToExcel {

    @Autowired
    DataBase0Config dataBase0Config;

    private static final Logger LOG = LoggerFactory.getLogger(SqlResultToExcel.class);


    private String filePath; //保存Excel的文件夹名称,例：F:/0219

    private String csvName; //excel文件名

//    private String zipPath = "F:/0219.zip"; //保存Excel的文件夹的压缩包名称，例：F:/0219.zip

//    private String suffix = ".csv";

    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public String saveExcel(HttpServletRequest param) {
        String sql = String.valueOf(param.getParameter("sql"));
        String filePath = String.valueOf(param.getParameter("filePath"));
        String csvName = String.valueOf(param.getParameter("fileName"));
        String suffix = String.valueOf(param.getParameter("suffix"));
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> resultMap = new HashMap<>();
        HikariDataSource dataSource = dataBase0Config.createDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.query(sql, resultSet -> {
            Set<String> columns = ResultSetKit.getColumnsByResultSet(resultSet);
            Map<String, Object> fisrtMap = new HashMap<>();
            for (String column : columns) {
                String stringValue = resultSet.getString(column);
                fisrtMap.put(column, stringValue);
            }
            resultList.add(fisrtMap);
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                for (String column : columns) {
                    String stringValue = resultSet.getString(column);
                    map.put(column, stringValue);
                }
                resultList.add(map);
            }
        });
        resultMap.put(csvName, resultList);
        // 生成Excel导出
        try {
            ExcelKit.createExcelFile(resultMap, filePath, suffix);
            LOG.info("文件生成路径:" + filePath);
        } catch (Exception e) {
            LOG.error("生成xlsx文件失败", e);
        }
        return "文件生成路径：" + filePath + "/" + csvName + suffix;
    }


}

