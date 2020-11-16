package com.example.calltest.controller;

import com.example.calltest.monitor.Sys;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@Scope("prototype")
public class FileUploadController {

    @Autowired
    private org.springframework.beans.factory.BeanFactory beanFactory;

//    @GetMapping("/tess")
//    public String index() {
//        return "upload";
//    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public RedirectView singleFileUpload(@RequestParam("file") MultipartFile file,
                                         RedirectAttributes redirectAttributes, Model model) throws IOException, TesseractException {
        byte[] bytes = file.getBytes();
        String resource = FileUploadController.class.getResource("/static").getPath().substring(1);
        String s1 = resource.replaceAll("\\\\", "/");
        String s = s1 + "/" + file.getOriginalFilename();
        Path path = Paths.get(s);
        Files.write(path, bytes);
        //加载待读取图片
        File convFile = convert(file);
//        File convFile = new File("F:\\joinforwin\\calltest\\target\\classes\\static\\20201113194133.png");
        Tesseract tesseract = beanFactory.getBean(Tesseract.class);
//        Tesseract tesseract = new Tesseract();
        //创建tess对象
        tesseract.setDatapath("E://tessdata");
        //设置训练语言
        tesseract.setLanguage("chi_sim");
        //执行转换
        String text = tesseract.doOCR(convFile);
        //设置训练语言
        System.err.println("识别后的文字:" + text);
        byte[] bytes1 = text.getBytes("UTF-8");
        String outPath = "E:\\tessdata\\" + file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf(".")) + ".txt";
        Path path1 = Paths.get(outPath);
        Files.write(path1, bytes1);
        redirectAttributes.addFlashAttribute("file", file);
        redirectAttributes.addFlashAttribute("text", text);
        return new RedirectView("result");
    }

    @RequestMapping("/result")
    public String result() {
        return "result";
    }


    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
