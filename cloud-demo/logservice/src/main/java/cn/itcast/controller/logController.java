package cn.itcast.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author LiChenLin
 * @Date 2022/5/2 20:25
 */
@RestController
@RequestMapping("/log")
public class logController {

    @PostMapping
    public void log(@RequestBody String log){
        //把日志写到本地磁盘

        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter("d:\\log.log", true);
            bw = new BufferedWriter(fw);
            bw.write(log);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
