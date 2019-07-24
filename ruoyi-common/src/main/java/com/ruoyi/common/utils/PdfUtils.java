package com.ruoyi.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.*;
import com.ruoyi.common.config.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

public class PdfUtils {

    private static Logger log = LoggerFactory.getLogger(PdfUtils.class);

    static HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();
    private static String localchinese = request.getSession().getServletContext()
            .getRealPath("pdfFile/simsun.ttf");

    /**
     * 设置字体
     *
     * @param size 字体大小
     * @return
     */
    public static Font createFont(int size) {
        Font font = null;
        try {
            BaseFont bfChinese = BaseFont.createFont(localchinese, "Identity-H", true);
            font = new Font(bfChinese, size, Font.BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }

    /**
     * 设置字体
     *
     * @param name
     * @param encoding
     * @param embedded
     * @param size     字体大小
     * @return
     */
    public static Font createFont(String name, String encoding, boolean embedded, int size) {
        Font font = null;
        try {
            BaseFont bfChinese = BaseFont.createFont(name, encoding, embedded);
            font = new Font(bfChinese, size, Font.BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }

    /**
     * 创建cell
     *
     * @param value 内容
     * @param font  字体样式
     * @return
     */
    public static PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    /**
     * 创建cell，水平垂直样式
     *
     * @param value    内容
     * @param font     字体样式
     * @param verAlign 垂直样式
     * @param horAlign 水平样式
     * @return
     */
    public static PdfPCell createCell(String value, Font font, int verAlign, int horAlign) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(verAlign);
        cell.setHorizontalAlignment(horAlign);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    /**
     * 创建cell,合并单元格
     *
     * @param value    内容
     * @param font     字体样式
     * @param verAlign 垂直样式
     * @param horAlign 水平样式
     * @param colspan  和并列数
     * @return
     */
    public static PdfPCell createCell(String value, Font font, int verAlign, int horAlign, int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(verAlign);
        cell.setHorizontalAlignment(horAlign);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    /**
     * 创建表格
     *
     * @param colNumber 列数
     * @return
     */
    public static PdfPTable createTable(int colNumber, int totalWidth) {
        PdfPTable table = new PdfPTable(colNumber);
        try {
            table.setTotalWidth(totalWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 创建表格
     *
     * @param widths 列宽数组
     * @return
     */
    public static PdfPTable createTable(float[] widths, int totalWidth) {
        PdfPTable table = new PdfPTable(widths);
        try {
            table.setTotalWidth(totalWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 创建表格
     *
     * @param colNumber 表格列数
     * @param widths    每列的宽度
     * @return
     */
    public static PdfPTable createTable(int colNumber, float[] widths) {
        PdfPTable table = new PdfPTable(colNumber);
        try {
            table.setWidthPercentage(90.0F);
            table.setWidths(widths);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 创建表格
     *
     * @param colNumber 表格列数
     * @param widths    每列的宽度
     * @return
     */
    public static PdfPTable createTable(int colNumber, int[] widths) {
        PdfPTable table = new PdfPTable(colNumber);
        try {
            table.setWidthPercentage(90.0F);
            table.setWidths(widths);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }


    /***
     * 根据模版生成PDF文件
     * @param tempPath
     * @param fileName
     * @param dataMap
     */
    public static String createPDF(String tempPath, String fileName,Map<String, String> dataMap ) {

        String filePath = "";
        try {

            PdfReader reader = new PdfReader(tempPath);// pdf模板
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            /* 将要生成的目标PDF文件名称 */
            PdfStamper ps = new PdfStamper(reader, bos);
            PdfContentByte under = ps.getUnderContent(1);

            /* 使用中文字体 */
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            ArrayList<BaseFont> fontList = new ArrayList<BaseFont>();
            fontList.add(bf);
            /* 取出报表模板中的所有字段 */
            AcroFields fields = ps.getAcroFields();
            fields.setSubstitutionFonts(fontList);
            fillData(fields, dataMap);

            /* 必须要调用这个，否则文档不会生成的 */
            ps.setFormFlattening(true);
            ps.close();

            //文件存储的路径（需要先创建好文件夹）
            String realPath = Global.getDownloadPath();
            realPath += "pdf/"+ DateUtil.date().toString("yyyy") + "/" + DateUtil.date().toString("MM") + "/" + DateUtil.date().toString("dd") + "/";
            FileUtil.mkdir(realPath);
            filePath = realPath+ "\\" +fileName;
            OutputStream fos = new FileOutputStream(filePath);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }


    public static void fillData(AcroFields fields, Map<String, String> data)
            throws IOException, DocumentException {
        for (String key : data.keySet()) {
            String value = data.get(key);
            fields.setField(key, value); // 为字段赋值,注意字段名称是区分大小写的
        }
    }

}
