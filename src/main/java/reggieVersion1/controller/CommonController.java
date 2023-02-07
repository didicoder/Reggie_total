package reggieVersion1.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reggieVersion1.controller.utils.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.controller
 * @className: CommonController
 * @author: White
 * @description: 处理文件上传下载的控制器
 * @date: 2023/1/26 14:40
 * @version: 1.0
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;


    /**
     * 文件上传（参数名必须和前端一致）
     * 1.@RequestPart用于将multipart/form-data类型数据映射到控制器处理方法的参数中
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestPart MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会被删除

        //获取文件原始文件名
        String originalName = file.getOriginalFilename();

        //获取文件名和扩展名
        String fileName = UUID.randomUUID().toString()
                //从原始文件名的最后一个"."开始截取，就可以获得文件的扩展名
                + originalName.substring(originalName.lastIndexOf('.'));

        //判断存储目录是否存在，若不存在则进行创建
        File dir = new File(basePath);
        if (!dir.exists()) {
            //不存在，创建该目录
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置，防止重名使用UUID作为文件名
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param filename
     * @param response
     */
    @GetMapping("/download/{filename}")
    public void download(@PathVariable String filename, HttpServletResponse response) {
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream inputStream = new FileInputStream(new File(basePath + filename));

            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应的文件格式
            response.setContentType("image/jpeg");

            //将输入流的文件写到输出流
            byte[] bytes = new byte[inputStream.available()];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
