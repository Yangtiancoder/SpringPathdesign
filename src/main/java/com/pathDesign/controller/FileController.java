package com.pathDesign.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : tianyang
 * @Date: 2018/06/02
 * @describe: 读取自定义配置文件信息
 */
@RestController
@ConfigurationProperties(prefix = "file")
@PropertySource("classpath:/file.properties")
public class FileController {

    /**
     * 文件下载路径
     */
    @Value("${fileUpload.path}")
    private String filePath;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);


    /**
     * 读取task
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/loadTask")
    public Object loadTask( HttpServletRequest request, HttpServletResponse response ) {
        String projectPath = new File("").getAbsolutePath();
        //文件名File dest = new File(projectPath + File.separator + filePath + File.separator + fileName);
        String fileName = "test.task";
        ArrayList<String> arrayList1 = new ArrayList<>();
        ArrayList<String> arrayList2 = new ArrayList<>();
        ArrayList<String> arrayList3 = new ArrayList<>();
        ArrayList<String> arrayList4 = new ArrayList<>();
        String fullPath = projectPath + File.separator + filePath + File.separator + fileName;
        StringBuffer sb=new StringBuffer();
        try {
            FileReader fr = new FileReader(fullPath);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            int flag = 6;
            while ((str = bf.readLine()) != null) {
                if (flag != 0) {
                    flag--;
                    continue;
                }
                String[] s = str.split(" ");
                arrayList1.add(s[0]);
                arrayList2.add(s[1]);
                arrayList3.add(s[2]);
                arrayList4.add(s[3]);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int length = arrayList1.size();
        int[] array = new int[length];
        sb.append("[");
        for (int i = 0; i < length; i++) {
            sb.append("[");
            sb.append(arrayList1.get(i) + ",");
            sb.append(arrayList2.get(i) + ",");
            sb.append(arrayList3.get(i) + ",");
            sb.append(arrayList4.get(i));
            if (i == length - 1) {
                sb.append("]");
                break;
            }
            sb.append("],");
        }
        sb.append("]");
        Map map=new HashMap();
        map.put("task",sb.toString());
        return JSONObject.parseObject(JSON.toJSONString(map));
    }


    /**
     * plan相关代码
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/loadPlan")
    public Object loadPlan( HttpServletRequest request, HttpServletResponse response ) {
        ArrayList<String[]> arrayList1 = new ArrayList<String[]>(146);
        String projectPath = new File("").getAbsolutePath();
        //文件名File dest = new File(projectPath + File.separator + filePath + File.separator + fileName);
        String fileName = "test.plan";
        String fullPath = projectPath + File.separator + filePath + File.separator + fileName;
        StringBuffer sb=new StringBuffer();
        try {
            FileReader fr = new FileReader(fullPath);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            boolean first = true;
            while ((str = bf.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] s = str.split(" ");
                String[] a = new String[s.length];
                for (int i = 0; i < s.length; i++) {
                    a[i] = s[i];
                }
                arrayList1.add(a);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int length = arrayList1.size();
        sb.append("[");
        int count = 0;
        for (String[] str : arrayList1) {
            count++;
            if (count <= 109) {
                sb.append("[0,");
            } else if (count > 109 && count <= 115) {
                sb.append("[1,");
            } else if (count > 115 && count <= 134) {
                sb.append("[2,");
            } else if (count > 134 && count <= 142) {
                sb.append("[3,");
            } else {
                sb.append("[4,");
            }

            int a = Integer.parseInt(str[str.length - 1]);
            int len = str.length;
            int p = 0;
            boolean first = true;
            for (int x = 0; x < a; x++) {
                if (x == Integer.parseInt(str[4 * p + 4])) {
                    if (first) {
                        first = false;
                        p++;
                    } else {
                        p++;
                    }
                }
                sb.append("[");

                sb.append(str[4 * p + 1] + ",");
                sb.append(str[4 * p + 2] + ",");
                sb.append(str[4 * p + 3]);
                sb.append("],");
            }
            if (count == length) {
                sb.append(",]");
                break;
            }
            sb.append(",],");
        }
        sb.append("]");
        Map map=new HashMap();
        map.put("plan",sb.toString());
        return JSONObject.parseObject(JSON.toJSONString(map));
    }

    /**
     * map相关代码
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/loadMap")
    public Object loadMap( HttpServletRequest request, HttpServletResponse response) {
        String projectPath = new File("").getAbsolutePath();
        //文件名File dest = new File(projectPath + File.separator + filePath + File.separator + fileName);
        String fileName = "test.map";
        String fullPath = projectPath + File.separator + filePath + File.separator + fileName;
        StringBuffer sb=new StringBuffer();
        try {
            FileReader fr = new FileReader(fullPath);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            boolean first = true;
            int count=0;
            int size=0;
            sb.append("[");
            while ((str = bf.readLine()) != null) {
                if (first) {
                    String[] s0 = str.split(" ");
                    size=Integer.parseInt(s0[0])*Integer.parseInt(s0[1])*3;
                    first = false;
                    continue;
                }
                count++;
                String[] s1 = str.split(" ");
                if(count==size){
                    sb.append(s1[0]+"]");
                    continue;
                }
                sb.append(s1[0]+",");

            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map map=new HashMap();
        map.put("map",sb.toString());
        return JSONObject.parseObject(JSON.toJSONString(map));

    }
}

