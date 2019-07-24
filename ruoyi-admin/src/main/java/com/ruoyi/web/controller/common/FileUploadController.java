package com.ruoyi.web.controller.common;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.system.domain.SysAttachment;
import com.ruoyi.system.service.ISysAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/common/upload")
public class FileUploadController extends BaseController {


    @Autowired
    private ISysAttachmentService sysAttachmentService;
    /***
     * 单个文件上传
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value="/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam(required = false) String attachmentNo,
                                 @RequestParam(required = false) String single,
                                HttpServletRequest request) {

        String contentType = file.getContentType();   //图片文件类型
        String fileName=file.getOriginalFilename();
        String fileExt = "."+FileUtil.extName(fileName);//后缀名
        Double fileSize=(double)file.getSize()/1024;
        //文件存放路径
        String filePath = Global.getUploadPath();

        String path = "";
        AjaxResult json =AjaxResult.success();
        //调用文件处理类FileUtil，处理文件，将文件写入指定位置
        try {
            path=  FileUploadUtils.upload(filePath,file,fileExt);

            if(attachmentNo!=null){
                if(StrUtil.isBlank(attachmentNo)){
                    attachmentNo=IdUtil.randomUUID();
                }
                if(StrUtil.isNotEmpty(single)){
                    //如果是单文件上传返回attno的情况
                    //删除之前上传的所有文件，只保留当前一个
                    SysAttachment satt=new SysAttachment();
                    satt.setAttachmentNo(attachmentNo);
                    List<SysAttachment> list=sysAttachmentService.selectList(satt);
                    List<String> ids=new ArrayList<>();
                    for(SysAttachment att:list ){
                        FileUtil.del(Global.getUploadPath()+att.getPath());
                        ids.add(att.getId());
                    }
                    if(ids.size()>0){
                    sysAttachmentService.removeByIds(ids);}
                }
                //需要存储附件表
                SysAttachment attachment=new SysAttachment();
                attachment.setFileName(fileName);
                attachment.setPath(path);
                attachment.setAttachmentNo(attachmentNo);
                attachment.setFileSize(NumberUtil.round(fileSize,1).doubleValue());
                sysAttachmentService.save(attachment);
                json.put("attachmentNo",attachmentNo);
                json.put("attid",attachment.getId());
            }else{
                //返回文件路径，无需存储附件表
                json.put("filePath",path);
            }

        } catch (Exception e) {
            // TODO: handle exception
            return error(e.getMessage());
        }

        return json;
    }


    /***
     * 多个文件上传
     * @param files
     * @param request
     * @return
     */
    @RequestMapping(value="/uploadFiles", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult uploadFiles(@RequestParam("file") MultipartFile[] files,
                                  @RequestParam(required = false)String attachmentNo,
                                 HttpServletRequest request) {

        List attids =  new ArrayList();
        List attList =  new ArrayList();
        if(attachmentNo==null){
            attachmentNo=IdUtil.randomUUID();
        }
        for (MultipartFile file: files) {
            String contentType = file.getContentType();   //图片文件类型
            String fileName = file.getOriginalFilename();  //图片名字

            //文件存放路径
            String filePath = Global.getUploadPath();

            //调用文件处理类FileUtil，处理文件，将文件写入指定位置
            try {
                // 上传物理文件
                String path = FileUploadUtils.upload(filePath,file,fileName);
                attids.add(IdUtil.randomUUID());
                SysAttachment attachment=new SysAttachment();
                attachment.setFileName(fileName);
                attachment.setPath(path);
                attachment.setAttachmentNo(attachmentNo);
                attList.add(attachment);
            } catch (Exception e) {
                // TODO: handle exception
                return error(e.getMessage());
            }
        }
        sysAttachmentService.saveBatch(attList);

        // 返回附件编号
        AjaxResult json = new AjaxResult();
        json.put("attno", attachmentNo);
        json.put("attids", JSONUtil.parseArray(attids));
        json.put("code", 0);

        return json;
    }



//    /***
//     * 删除附件
//     * @param attno 附件编号
//     * @param request
//     * @return
//     */
//    @RequestMapping(value="/delFiles", method = RequestMethod.POST)
//    @ResponseBody
//    public AjaxResult delFiles(String attno,String path,
//                                  HttpServletRequest request) {
//
//        try {
//            //TODO 从附件表取得所有的附件数据（循环删除文件）
//
//            //TODO 根据attno删除附件表
//
//        } catch (Exception e) {
//            // TODO: handle exception
//            return error(e.getMessage());
//        }
//
//        return AjaxResult.success();
//    }


    /***
     * 删除附件中的当个文件
     * @param request
     * @return
     */
    @RequestMapping(value="/delFile", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult delFile(String id,String attno,String path,
                               HttpServletRequest request) {

        try {
            //TODO 根据AttID删除附件表
            //TODO 删除物理文件

            if(StrUtil.isNotEmpty(id)){
                //Hutool读取文件
                SysAttachment att= sysAttachmentService.getById(id);
                FileUtil.del(Global.getUploadPath()+att.getPath());
                sysAttachmentService.removeById(id);
            }else if(StrUtil.isNotEmpty(attno)){
               // sysAttachmentService.selectList(new SysAttachment());
//                filePath=path;
            }else{
                FileUtil.del(Global.getUploadPath()+path);
//                FileReader fileReader = new FileReader(Global.getUploadPath()+path);
//                FileUtil.del(fileReader.getFile());
            }

        } catch (Exception e) {
            return error(e.getMessage());
        }

        return AjaxResult.success();
    }



    @RequestMapping(value="/downloadFile", method = RequestMethod.GET)
    public HttpServletResponse downloadFile(String id,String path,HttpServletResponse response) {
        try {
            String filePath="";
            String fileName="";
            if(id!=null&&!id.isEmpty()){
                SysAttachment att= sysAttachmentService.getById(id);
                filePath=att.getPath();
                fileName=att.getFileName();
            }else{
                filePath=path;
            }
            ;
            //Hutool读取文件
            FileReader fileReader = new FileReader(FileUtil.file( Global.getUploadPath()+filePath));
            if(fileName.isEmpty()){
                fileName=FileUtil.getName(fileReader.getFile());
                //fileName=fileName.substring(31,fileName.length()-31);
            }
            byte[] buffer = fileReader.readBytes();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\".xlsx;filename*=UTF-8''" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));

            response.addHeader("Content-Length", "" + fileReader.getFile().length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    @RequestMapping("/getFileList")
    @ResponseBody
    public AjaxResult getFileList(SysAttachment attachment,String path) {
        List<SysAttachment> list=new ArrayList<SysAttachment>();
        if(StrUtil.isNotEmpty(path)){
            try{
            FileReader fileReader = new FileReader(Global.getUploadPath()+path);
            String fileName="";
            fileName=FileUtil.getName(fileReader.getFile());
            SysAttachment att=new SysAttachment();
            att.setFileName(fileName);
            att.setPath(path);
            list.add(att);}catch (Exception ex){}
        }else if(StrUtil.isNotEmpty(attachment.getAttachmentNo())){
            list=sysAttachmentService.selectList(attachment);
        }
        List rlist=new ArrayList<Map<String,String>>();
        for(SysAttachment att:list){
            Map<String,String> m=new HashMap<>();
            m.put("fileName",att.getFileName());
            m.put("fileSize",StrUtil.toString(att.getFileSize()));
            m.put("path",att.getPath());
            m.put("id",att.getId());
            String ext=FileUtil.extName(att.getFileName());
            if("png".equals(ext)||"jpg".equals(ext)||"jpeg".equals(ext)||"bmp".equals(ext)) {
                try {
                    byte[] b = FileUtil.readBytes(Global.getUploadPath() + att.getPath());
                    m.put("byte", Base64.encode(b));
                } catch (Exception ex) {
                    m.put("byte", "");
                }
            }
            rlist.add(m);
        }
        AjaxResult json =AjaxResult.success();
        json.put("attachmentNo",attachment.getAttachmentNo());
        json.put("fileList",rlist);
        return json;
    }


}
