package com.leadcom.android.isp.apache.poi;

import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

/**
 * 2016/9/12 0012，由 yuezc 创建 .
 * <p/>
 * 功能描述：
 * <p/>
 * 说明：
 * ---------------------------
 * 修改时间：
 * 修改说明：
 * 修改人：
 * <p/>
 * POIFS
 * POIFS是该项目的最古老，最稳定的一部分。.这是格式化OLE 2复合文档为纯Java的接口。 它同时支持读写功能。所有的组件，最终都依赖于它的定义
 * <p/>
 * HSSF 和 XSSF
 * HSSF: MS－Excel 97-2003（.xls），基于BIFF8格式的JAVA接口。
 * XSSF：MS－Excel 2007+(.xlsx),基于OOXML格式的JAVA接口。
 * <p/>
 * HWPF 和XWPF
 * HWPF: MS－Word 97-2003(.doc)，基于BIFF8格式的JAVA接口。只支持.doc文件简单的操作，读写能力有限。本API为POI项目早期开发，很不幸的 是主要负责HWPF模块开发的工程师-“Ryan Ackley”已经离开Apache组织，现在该模块没有人维护、更新、完善。
 * XWPF：MS－Word 2007+(.docx),基于OOXML格式的JAVA接口。较HWPF功能完善。
 */
public class OfficeConverter {

    public static void convert(String sourcePath) {
    }

    /**
     * doc文档转成html格式
     */
    public static void doc2html(String docPath, final String docName, String htmlName, String htmlPath) {

        HWPFDocument wordDocument = null;
        try {
            wordDocument = new HWPFDocument(new FileInputStream(docPath + docName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        WordToHtmlConverter wordToHtmlConverter = null;
        try {
            wordToHtmlConverter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        //设置图片路径
        assert wordToHtmlConverter != null;
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content,
                                      PictureType pictureType, String suggestedName,
                                      float widthInches, float heightInches) {
                String name = docName.substring(0, docName.indexOf("\\."));
                return name + "/" + suggestedName;
            }
        });

        //保存图片
        assert wordDocument != null;
        List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                System.out.println(pic.suggestFullFileName());
                try {
                    String name = docName.substring(0, docName.indexOf("\\."));
                    pic.writeImageContent(new FileOutputStream(htmlPath + name + "/"
                            + pic.suggestFullFileName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(out);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = null;
        try {
            serializer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        assert serializer != null;
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        try {
            serializer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //保存html文件
        writeFile(new String(out.toByteArray()), htmlPath + htmlName);
    }

    /**
     * docx文档转成html格式
     */
    public static void docx2html(String docxPath, String docxName, String htmlName, String htmlPath) {

        try {
            final String file = docxPath + docxName;
            File f = new File(file);
            if (!f.exists()) {
                System.out.println("Sorry File does not Exists!");
            } else {
                if (f.getName().endsWith(".docx") || f.getName().endsWith(".DOCX")) {

                    // 1) 加载word文档生成 XWPFDocument对象
                    InputStream in = new FileInputStream(f);
                    XWPFDocument document = new XWPFDocument(in);

                    // 2) 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
                    File imageFolderFile = new File(docxPath);
                    XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(imageFolderFile));
                    options.setExtractor(new FileImageExtractor(imageFolderFile));
                    options.setIgnoreStylesIfUnused(false);
                    options.setFragment(true);

                    // 3) 将 XWPFDocument转换成XHTML
                    OutputStream out = new FileOutputStream(new File(htmlPath + htmlName));
                    XHTMLConverter.getInstance().convert(document, out, options);
                } else {
                    System.out.println("Enter only MS Office 2007+ files");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * xls文档转成html格式
     */
    public static void xls2html(String xlsPath, String xlsName, String htmlName, String htmlPath) {

        try {
            InputStream input = new FileInputStream(xlsPath + xlsName);
            HSSFWorkbook excelBook = new HSSFWorkbook(input);
            ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            excelToHtmlConverter.processWorkbook(excelBook);
            List pics = excelBook.getAllPictures();
            if (pics != null) {
                for (int i = 0; i < pics.size(); i++) {
                    Picture pic = (Picture) pics.get(i);
                    try {
                        pic.writeImageContent(new FileOutputStream(xlsName.substring(0, xlsName.indexOf(".")) + pic.suggestFullFileName()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            Document htmlDocument = excelToHtmlConverter.getDocument();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(outStream);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
            outStream.close();
            //保存html文件
            writeFile(new String(outStream.toByteArray()), htmlPath + htmlName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * xlsx文档转成html格式
     */
    public static void xlsx2html(String xlsxPath, String xlsxName, String htmlName, String htmlPath) {
        writeFile(readExcelToHtml(xlsxPath + xlsxName, true), htmlPath + htmlName);
    }

    public static void ppt2html(String pptPath, String pptName, String htmlName, String htmlPath) {
        //抱歉，没有实现
    }

    public static void pptx2html(String pptxPath, String pptxName, String htmlName, String htmlPath) {
        //抱歉，没有实现
    }

    /**
     * 将html文件保存到sd卡
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    /**
     * @param filePath    文件的路径
     * @param isWithStyle 是否需要表格样式 包含 字体 颜色 边框 对齐方式
     * @return <table>
     * ...
     * </table>
     * 字符串
     */
    private static String readExcelToHtml(String filePath, boolean isWithStyle) {

        InputStream is = null;
        String htmlExcel = null;
        try {
            File sourcefile = new File(filePath);
            is = new FileInputStream(sourcefile);
            Workbook wb = WorkbookFactory.create(is);
            if (wb instanceof XSSFWorkbook) {
                XSSFWorkbook xWb = (XSSFWorkbook) wb;
                htmlExcel = getExcelInfo(xWb, isWithStyle);
            } else if (wb instanceof HSSFWorkbook) {
                HSSFWorkbook hWb = (HSSFWorkbook) wb;
                htmlExcel = getExcelInfo(hWb, isWithStyle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert is != null;
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return htmlExcel;
    }


    private static String getExcelInfo(Workbook wb, boolean isWithStyle) {

        StringBuffer sb = new StringBuffer();
        Sheet sheet = wb.getSheetAt(0);// 获取第一个Sheet的内容
        int lastRowNum = sheet.getLastRowNum();
        Map<String, String> map[] = getRowSpanColSpanMap(sheet);
        sb.append("<table style='border-collapse:collapse;' width='100%'>");
        Row row = null; // 兼容
        Cell cell = null; // 兼容
        for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
            row = sheet.getRow(rowNum);
            if (row == null) {
                sb.append("<tr><td >  </td></tr>");
                continue;
            }
            sb.append("<tr>");
            int lastColNum = row.getLastCellNum();
            for (int colNum = 0; colNum < lastColNum; colNum++) {
                cell = row.getCell(colNum);
                if (cell == null) { // 特殊情况 空白的单元格会返回null
                    sb.append("<td> </td>");
                    continue;
                }
                String stringValue = getCellValue(cell);
                if (map[0].containsKey(rowNum + "," + colNum)) {
                    String pointString = map[0].get(rowNum + "," + colNum);
                    map[0].remove(rowNum + "," + colNum);
                    int bottomeRow = Integer.valueOf(pointString.split(",")[0]);
                    int bottomeCol = Integer.valueOf(pointString.split(",")[1]);
                    int rowSpan = bottomeRow - rowNum + 1;
                    int colSpan = bottomeCol - colNum + 1;
                    sb.append("<td rowspan= '").append(rowSpan).append("' colspan= '").append(colSpan).append("' ");
                } else if (map[1].containsKey(rowNum + "," + colNum)) {
                    map[1].remove(rowNum + "," + colNum);
                    continue;
                } else {
                    sb.append("<td ");
                }
                // 判断是否需要样式
                if (isWithStyle) {
                    dealExcelStyle(wb, sheet, cell, sb);// 处理单元格样式
                }
                sb.append(">");
                if (stringValue == null || "".equals(stringValue.trim())) {
                    sb.append("   ");
                } else {
                    // 将ascii码为160的空格转换为html下的空格（ ）
                    sb.append(stringValue.replace(String.valueOf((char) 160), " "));
                }
                sb.append("</td>");
            }
            sb.append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();


    }

    @SuppressWarnings("unchecked")
    private static Map<String, String>[] getRowSpanColSpanMap(Sheet sheet) {
        Map<String, String> map0 = new HashMap<String, String>();
        Map<String, String> map1 = new HashMap<String, String>();
        int mergedNum = sheet.getNumMergedRegions();
        CellRangeAddress range = null;
        for (int i = 0; i < mergedNum; i++) {
            range = sheet.getMergedRegion(i);
            int topRow = range.getFirstRow();
            int topCol = range.getFirstColumn();
            int bottomRow = range.getLastRow();
            int bottomCol = range.getLastColumn();
            map0.put(topRow + "," + topCol, bottomRow + "," + bottomCol);
            // System.out.println(topRow + "," + topCol + "," + bottomRow + ","
            // + bottomCol);
            int tempRow = topRow;
            while (tempRow <= bottomRow) {
                int tempCol = topCol;
                while (tempCol <= bottomCol) {
                    map1.put(tempRow + "," + tempCol, "");
                    tempCol++;
                }
                tempRow++;
            }
            map1.remove(topRow + "," + topCol);
        }
        return new Map[]{map0, map1};
    }

    /**
     * 获取表格单元格Cell内容
     *
     * @param cell cell
     * @return value
     */
    @SuppressWarnings("deprecation")
    private static String getCellValue(Cell cell) {
        String result;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:// 数字类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf = null;
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    } else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    }
                    Date date = cell.getDateCellValue();
                    result = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    double value = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                    result = sdf.format(date);
                } else {
                    double value = cell.getNumericCellValue();
                    CellStyle style = cell.getCellStyle();
                    DecimalFormat format = new DecimalFormat();
                    String temp = style.getDataFormatString();
                    // 单元格设置成常规
                    if (temp.equals("General")) {
                        format.applyPattern("#");
                    }
                    result = format.format(value);
                }
                break;
            case Cell.CELL_TYPE_STRING:// String类型
                result = cell.getRichStringCellValue().toString();
                break;
            case Cell.CELL_TYPE_BLANK:
                result = "";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    /**
     * 处理表格样式
     *
     * @param wb    workbook
     * @param sheet sheet
     * @param cell  cell
     * @param sb    StringBuffer
     */
    @SuppressWarnings({"deprecation", "EqualsBetweenInconvertibleTypes"})
    private static void dealExcelStyle(Workbook wb, Sheet sheet, Cell cell, StringBuffer sb) {

        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle != null) {
            short alignment = cellStyle.getAlignment();
            sb.append("align='").append(convertAlignToHtml(alignment)).append("' ");// 单元格内容的水平对齐方式
            short verticalAlignment = cellStyle.getVerticalAlignment();
            sb.append("valign='").append(convertVerticalAlignToHtml(verticalAlignment)).append("' ");// 单元格中内容的垂直排列方式
            if (wb instanceof XSSFWorkbook) {
                XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
                boolean bold = xf.getBold();
                sb.append("style='");
                if (bold) {
                    sb.append("font-weight: bold;"); // 字体加粗
                }
                sb.append("font-size: ").append(xf.getFontHeight() / 2).append("%;"); // 字体大小
                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                sb.append("width:").append(columnWidth).append("px;");
                XSSFColor xc = xf.getXSSFColor();
                if (xc != null && !"".equals(xc)) {
                    sb.append("color:#").append(xc.getARGBHex().substring(2)).append(";"); // 字体颜色
                }

                XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
                // System.out.println("************************************");
                // System.out.println("BackgroundColorColor:
                // "+cellStyle.getFillBackgroundColorColor());
                // System.out.println("ForegroundColor:
                // "+cellStyle.getFillForegroundColor());//0
                // System.out.println("BackgroundColorColor:
                // "+cellStyle.getFillBackgroundColorColor());
                // System.out.println("ForegroundColorColor:
                // "+cellStyle.getFillForegroundColorColor());
                // String bgColorStr = bgColor.getARGBHex();
                // System.out.println("bgColorStr: "+bgColorStr);
                if (bgColor != null && !"".equals(bgColor)) {
                    sb.append("background-color:#").append(bgColor.getARGBHex().substring(2)).append(";"); // 背景颜色
                }
                sb.append(getBorderStyle(0, cellStyle.getBorderTop(),
                        ((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
                sb.append(getBorderStyle(1, cellStyle.getBorderRight(),
                        ((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
                sb.append(getBorderStyle(2, cellStyle.getBorderBottom(),
                        ((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
                sb.append(getBorderStyle(3, cellStyle.getBorderLeft(),
                        ((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));

            } else if (wb instanceof HSSFWorkbook) {

                HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
                boolean bold = hf.getBold();
                short fontColor = hf.getColor();
                sb.append("style='");
                HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
                HSSFColor hc = palette.getColor(fontColor);
                if (bold) {
                    sb.append("font-weight:bold;"); // 字体加粗
                }
                sb.append("font-size: ").append(hf.getFontHeight() / 2).append("%;"); // 字体大小
                String fontColorStr = convertToStardColor(hc);
                if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
                    sb.append("color:").append(fontColorStr).append(";"); // 字体颜色
                }
                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                sb.append("width:").append(columnWidth).append("px;");
                short bgColor = cellStyle.getFillForegroundColor();
                hc = palette.getColor(bgColor);
                String bgColorStr = convertToStardColor(hc);
                if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
                    sb.append("background-color:").append(bgColorStr).append(";"); // 背景颜色
                }
                sb.append(getBorderStyle(palette, 0, cellStyle.getBorderTop(), cellStyle.getTopBorderColor()));
                sb.append(getBorderStyle(palette, 1, cellStyle.getBorderRight(), cellStyle.getRightBorderColor()));
                sb.append(getBorderStyle(palette, 3, cellStyle.getBorderLeft(), cellStyle.getLeftBorderColor()));
                sb.append(getBorderStyle(palette, 2, cellStyle.getBorderBottom(), cellStyle.getBottomBorderColor()));
            }

            sb.append("' ");
        }
    }

    /**
     * 单元格内容的水平对齐方式
     *
     * @param alignment 水平对齐方式
     * @return css
     */
    private static String convertAlignToHtml(short alignment) {

        String align = "left";
        HorizontalAlignment ha = HorizontalAlignment.forInt(alignment);
        switch (ha) {
            case LEFT:
                //case CellStyle.ALIGN_LEFT:
                align = "left";
                break;
            case CENTER:
                //case CellStyle.ALIGN_CENTER:
                align = "center";
                break;
            case RIGHT:
                //case CellStyle.ALIGN_RIGHT:
                align = "right";
                break;
            default:
                break;
        }
        return align;
    }

    /**
     * 单元格中内容的垂直排列方式
     *
     * @param verticalAlignment 垂直对齐方式
     * @return css
     */
    private static String convertVerticalAlignToHtml(short verticalAlignment) {

        String valign = "middle";
        VerticalAlignment va = VerticalAlignment.forInt(verticalAlignment);
        switch (va) {
            case BOTTOM:
                //case CellStyle.VERTICAL_BOTTOM:
                valign = "bottom";
                break;
            case CENTER:
                //case CellStyle.VERTICAL_CENTER:
                valign = "center";
                break;
            case TOP:
                //case CellStyle.VERTICAL_TOP:
                valign = "top";
                break;
            default:
                break;
        }
        return valign;
    }

    private static String convertToStardColor(HSSFColor hc) {

        StringBuilder sb = new StringBuilder("");
        if (hc != null) {
            if (HSSFColor.AUTOMATIC.index == hc.getIndex()) {
                return null;
            }
            sb.append("#");
            for (int i = 0; i < hc.getTriplet().length; i++) {
                sb.append(fillWithZero(Integer.toHexString(hc.getTriplet()[i])));
            }
        }

        return sb.toString();
    }

    private static String fillWithZero(String str) {
        if (str != null && str.length() < 2) {
            return "0" + str;
        }
        return str;
    }

    private static String[] bordesr = {"border-top:", "border-right:", "border-bottom:", "border-left:"};
    private static String[] borderStyles = {"solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ",
            "solid ", "solid", "solid", "solid", "solid", "solid"};

    private static String getBorderStyle(HSSFPalette palette, int b, short s, short t) {
        if (s == 0)
            return bordesr[b] + borderStyles[s] + "#d0d7e5 1px;";
        String borderColorStr = convertToStardColor(palette.getColor(t));
        borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000" : borderColorStr;
        return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";

    }

    private static String getBorderStyle(int b, short s, XSSFColor xc) {

        if (s == 0)
            return bordesr[b] + borderStyles[s] + "#d0d7e5 1px;";
        if (xc != null && !"".equals(xc)) {
            String borderColorStr = xc.getARGBHex();// t.getARGBHex();
            borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000"
                    : borderColorStr.substring(2);
            return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";
        }

        return "";
    }
}
