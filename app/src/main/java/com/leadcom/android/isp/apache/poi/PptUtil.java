package com.leadcom.android.isp.apache.poi;

import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.common.Attachment;

import org.apache.poi.hslf.record.DocumentAtom;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideSize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ae.java.awt.Color;
import ae.java.awt.Graphics2D;
import ae.java.awt.Image;
import ae.java.awt.geom.Rectangle2D;
import ae.java.awt.image.BufferedImage;
import ae.javax.imageio.ImageIO;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/10 09:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class PptUtil {

    private final static String TAG = "POIPPTUtil";
    public String htmlPath;
    private final static String PPT = "ppt";
    private final static String PPTX = "pptx";

    public PptUtil(String pptName) {
        String fileName = FileUtil.getFileName(pptName);
        htmlPath = FileUtil.createFile(fileName + ".html");
        // 判断已转换的文件是否存在，不用每次打开时都转换，节省时间
        if (!FileUtil.isFileExists(htmlPath)) {
            LogHelper.log(TAG, "htmlPath=" + htmlPath);
            String ext = Attachment.getExtension(pptName);
            assert ext != null;
            if (!StringHelper.isEmpty(ext)) {
                String pptHtml = "";
                if (ext.equals(PPT)) {
                    pptHtml = toImage2003(pptName);
                } else if (ext.equals(PPTX)) {
                    pptHtml = toImage2007(pptName);
                }
                if (!StringHelper.isEmpty(pptHtml)) {
                    FileUtil.writeFile(pptHtml, htmlPath);
                }
            }
        } else {
            LogHelper.log(TAG, "htmlPath(exist)=" + htmlPath);
        }
    }

    @SuppressWarnings("SingleStatementInBlock")
    private static String toImage2007(String sourcePath) {
        String htmlStr = "";
        try {
            FileInputStream is = new FileInputStream(sourcePath);
            XMLSlideShow ppt = new XMLSlideShow(is);
            is.close();
            // 获取ppt尺寸
            CTSlideSize size = ppt.getCTPresentation().getSldSz();
            //Dimension pgsize = (Dimension) ppt.getPageSize();
            int width = size.getCx(), height = size.getCy();
            System.out.println(width + "--" + height);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ppt.getSlides().size(); i++) {
                try {
                    // 防止中文乱码
                    for (XSLFShape shape : ppt.getSlides().get(i).getShapes()) {
                        if (shape instanceof XSLFTextShape) {
                            XSLFTextShape tsh = (XSLFTextShape) shape;
                            for (XSLFTextParagraph p : tsh) {
                                for (XSLFTextRun r : p) {
                                    r.setFontFamily("宋体");
                                }
                            }
                        }
                    }
                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = img.createGraphics();
                    // clear the drawing area
                    graphics.setPaint(Color.white);
                    graphics.fill(new Rectangle2D.Float(0, 0, width, height));
                    // render
                    ppt.getSlides().get(i).draw(graphics);
                    // save the output
                    String imagePath = FileUtil.createFile(FileUtil.getFileName(sourcePath) + i + ".png");
                    sb.append("<br>");
                    sb.append("<img src=" + "\"").append(imagePath).append("\"").append("/>");
                    FileOutputStream out = new FileOutputStream(imagePath);
                    ImageIO.write(img, "png", out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("transfer pptx page " + i + " error: " + e.getMessage());
                }
            }
            System.out.println("success");
            htmlStr = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlStr;
    }

    @SuppressWarnings("SingleStatementInBlock")
    private static String toImage2003(String sourcePath) {
        String htmlStr = "";
        try {
            HSLFSlideShow ppt = new HSLFSlideShow(new HSLFSlideShowImpl(sourcePath));
            // 获取ppt尺寸
            DocumentAtom docatom = ppt.getDocumentRecord().getDocumentAtom();
            //Dimension pgsize = (Dimension) ppt.getPageSize();
            int width = (int) Units.masterToPoints((int) docatom.getSlideSizeX());
            int height = (int)Units.masterToPoints((int)docatom.getSlideSizeY());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ppt.getSlides().size(); i++) {
                try {
                    // 防止中文乱码
                    for (HSLFShape shape : ppt.getSlides().get(i).getShapes()) {
                        if (shape instanceof HSLFTextShape) {
                            HSLFTextShape tsh = (HSLFTextShape) shape;
                            for (HSLFTextParagraph p : tsh) {
                                for (HSLFTextRun r : p) {
                                    r.setFontFamily("宋体");
                                }
                            }
                        }
                    }
                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = img.createGraphics();
                    // clear the drawing area
                    graphics.setPaint(Color.white);
                    graphics.fill(new Rectangle2D.Float(0, 0, width, height));
                    // render
                    ppt.getSlides().get(i).draw(graphics);
                    String imagePath = FileUtil.createFile(FileUtil.getFileName(sourcePath) + i + ".png");
                    sb.append("<br>");
                    sb.append("<img src=" + "\"").append(imagePath).append("\"").append("/>");
                    FileOutputStream out = new FileOutputStream(imagePath);
                    ImageIO.write(img, "png", out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("transfer ppt page " + i + " error: " + e.getMessage());
                }
            }
            System.out.println("success");
            htmlStr = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlStr;
    }

    /***
     * 功能 :调整图片大小
     *
     * @param srcImgPath
     *            原图片路径
     * @param distImgPath
     *            转换大小后图片路径
     * @param width
     *            转换后图片宽度
     * @param height
     *            转换后图片高度
     */
    @SuppressWarnings("unused")
    private static void resizeImage(String srcImgPath, String distImgPath, int width, int height) throws IOException {

        File srcFile = new File(srcImgPath);
        Image srcImg = ImageIO.read(srcFile);
        BufferedImage buffImg;
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg.getGraphics().drawImage(srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

        ImageIO.write(buffImg, "JPEG", new File(distImgPath));

    }
}
