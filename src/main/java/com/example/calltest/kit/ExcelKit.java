package com.example.calltest.kit;

import cn.hutool.core.convert.Convert;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.util.FileCopyUtils.BUFFER_SIZE;

/**
 * Created by 14069 on 2020/2/19.
 */
public class ExcelKit {


    /**
     * @param fieldValueMap 字段值集合
     * @param packageSrc    excel表格所存放文件夹路径
     * @param //zipPath       excel表格所存放文件夹的压缩包路径
     * @return
     */
    public static void createExcelFile(Map<String, List<Map<String, Object>>> fieldValueMap, String packageSrc, String suffix) throws Exception {
        File file = new File(packageSrc);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (String excelName : fieldValueMap.keySet()) {
            try {
                List<Map<String, Object>> mapList = fieldValueMap.get(excelName);
                for (Map<String, Object> map : mapList) {
                    CreateCsvUtil.createCSVFile(mapList, map, packageSrc, excelName, suffix);
                    /**
                     * 压缩文件夹
                     */
//                    OutputStream zipOut = null;
//                    try {
//                        zipOut = new FileOutputStream(new File(zipPath));
//                        toZip(packageSrc, zipOut, false);
//                    } catch (Exception e) {
//                        throw new Exception("压缩文件夹失败", e);
//                    } finally {
//                        CloseKit.close(zipOut);
//                    }
                    break;
                }
            } catch (Exception e) {
                throw new Exception("写入Excel数据失败", e);
            }
        }
    }


    /**
     * 压缩文件夹
     *
     * @param srcDir
     * @param out
     * @param keepDirStructure
     * @throws RuntimeException
     */
    public static void toZip(String srcDir, OutputStream out, boolean keepDirStructure)
            throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), keepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (keepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (keepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }
                }
            }
        }
    }

    public static Object typeJudge(Object obj) {
        if (obj instanceof String) {
            return Convert.toStr(obj);
        } else if (obj instanceof Long) {
            return new Date((Long) obj);
        } else if (obj instanceof Integer) {
            return Convert.toInt(obj);
        } else {
            return Convert.toStr(obj);
        }
    }


    /**
     * 导出Excel
     */
    public void exportExcel(HttpServletResponse response, String fileName, HSSFWorkbook wb) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            wb.write(os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            response.reset();
            response.setContentType("text/html;charset=utf-8");//处理乱码问题
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
            ServletOutputStream out = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length - 1))) {
                bos.write(buff, 0, bytesRead);
            }
            response.flushBuffer();
        } catch (IOException e) {
            //throw new SieException("导出数据异常");
        } finally {
            CloseKit.close(bis);
            CloseKit.close(bos);
        }
    }

}
