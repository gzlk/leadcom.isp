package com.leadcom.android.isp.apache.poi;

import android.text.TextUtils;

import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.common.Attachment;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <b>功能描述：</b>Apache POI 转换 excel 文档为 html<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/14 14:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/14 14:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ExcelUtils {

    private final static String TAG = "POIExcelUtil";
    public String htmlPath;

    public ExcelUtils(String xlsName) {
        String fileName = FileUtils.getFileName(xlsName);
        htmlPath = FileUtils.createFile(fileName + ".html");
        // 判断已转换的文件是否存在，不用每次打开时都转换，节省时间
        if (!FileUtils.isFileExists(htmlPath)) {
            LogHelper.log(TAG, "htmlPath=" + htmlPath);
            excelToHtml(xlsName, htmlPath);
        } else {
            LogHelper.log(TAG, "htmlPath(exist)=" + htmlPath);
        }
    }

    private static void excelToHtml(String sourcePath, String targetPath) {

        String htmlExcel = null;
        String ext = Attachment.getExtension(sourcePath);

        if (!StringHelper.isEmpty(ext) && Attachment.isExcel(ext)) {
            assert ext != null;
            if (ext.equals("xls")) {
                InputStream is = null;
                try {
                    is = new FileInputStream(sourcePath);
                    HSSFWorkbook xls = new HSSFWorkbook(is);
                    htmlExcel = getExcelInfo(xls);
                    xls.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != is) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (ext.equals("xlsx")) {
                try {
                    XSSFWorkbook xlsx = new XSSFWorkbook(sourcePath);
                    htmlExcel = getExcelInfo(xlsx);
                    xlsx.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!StringHelper.isEmpty(htmlExcel)) {
                FileUtils.writeFile(htmlExcel, targetPath);
            }
        }
    }

    private static String getExcelInfo(Workbook wb) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>table td{border:1px solid #000000;}</style></head><body>");
        int sheetCounts = wb.getNumberOfSheets();

        for (int i = 0; i < sheetCounts; i++) {
            Sheet sheet = wb.getSheetAt(i);// 获取第一个Sheet的内容
            int lastRowNum = sheet.getLastRowNum();
            Map<String, String> map[] = getRowSpanColSpanMap(sheet);
            sb.append("<br><br>");
            sb.append(sheet.getSheetName());
            sb.append("<table style='border-collapse:collapse;' width='100%'>");
            Row row; // 兼容
            Cell cell; // 兼容
            for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
                row = sheet.getRow(rowNum);
                if (row == null) {
                    sb.append("<tr><td > &nbsp;</td></tr>");
                    continue;
                }
                sb.append("<tr>");
                int lastColNum = row.getLastCellNum();
                for (int colNum = 0; colNum < lastColNum; colNum++) {
                    cell = row.getCell(colNum);
                    if (cell == null) { // 特殊情况 空白的单元格会返回null
                        sb.append("<td>&nbsp;</td>");
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
                    //if (isWithStyle) {
                    //dealExcelStyle(wb, sheet, cell, sb);// 处理单元格样式
                    //}

                    sb.append(">");
                    if (stringValue == null || "".equals(stringValue.trim())) {
                        sb.append(" &nbsp; ");
                    } else {
                        // 将ascii码为160的空格转换为html下的空格（&nbsp;）
                        sb.append(stringValue.replace(String.valueOf((char) 160), "&nbsp;"));
                    }
                    sb.append("</td>");
                }
                sb.append("</tr>");
            }
            sb.append("</table>");
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String>[] getRowSpanColSpanMap(Sheet sheet) {

        Map<String, String> map0 = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        int mergedNum = sheet.getNumMergedRegions();
        CellRangeAddress range;
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
     * 200 * 获取表格单元格Cell内容 201 * @param cell 202 * @return 203
     */
    private static String getCellValue(Cell cell) {

        String result;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:// 数字类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf;
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    } else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    }
                    Date date = cell.getDateCellValue();
                    result = sdf.format(date);
                } else {
                    short style = cell.getCellStyle().getDataFormat();
                    if (style == 14 || style == 31 || style == 57 || style == 58
                            || (176 <= style && style <= 178) || (182 <= style && style <= 196)
                            || (210 <= style && style <= 213) || (208 == style)) {
                        // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        double value = cell.getNumericCellValue();
                        Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                        result = sdf.format(date);
                    } else if (style == 20 || style == 32 || style == 183 || (200 <= style && style <= 209)) { // 时间
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        double value = cell.getNumericCellValue();
                        Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                        result = sdf.format(date);
                    } else {
                        double value = cell.getNumericCellValue();
                        CellStyle cellStyle = cell.getCellStyle();
                        DecimalFormat format = new DecimalFormat();
                        String temp = cellStyle.getDataFormatString();
                        // 单元格设置成常规
                        if (!TextUtils.isEmpty(temp) && temp.equals("General")) {
                            format.applyPattern("#");
                        }
                        result = format.format(value);
                    }
                }
                break;
            case STRING:// String类型
                result = cell.getRichStringCellValue().toString();
                break;
            case BLANK:
                result = "";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    /**
     * 251 * 处理表格样式 252 * @param wb 253 * @param sheet 254 * @param cell 255
     * * @param sb 256
     */
    @SuppressWarnings({"deprecation", "unused"})
    private static void dealExcelStyle(Workbook wb, Sheet sheet, Cell cell, StringBuffer sb) {

        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle != null) {
            sb.append("align='").append(convertAlignToHtml(cellStyle.getAlignmentEnum())).append("' ");// 单元格内容的水平对齐方式
            sb.append("valign='").append(convertVerticalAlignToHtml(cellStyle.getVerticalAlignmentEnum())).append("' ");// 单元格中内容的垂直排列方式

            if (wb instanceof XSSFWorkbook) {

                XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
                boolean bold = xf.getBold();
                sb.append("style='");
                if (bold) {
                    sb.append("font-weight:bold;"); // 字体加粗
                }
                sb.append("font-size: ").append(xf.getFontHeight() / 2).append("%;"); // 字体大小
                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                sb.append("width:").append(columnWidth).append("px;");

                XSSFColor xc = xf.getXSSFColor();
                if (xc != null) {
                    sb.append("color:#").append(xc.getARGBHex().substring(2)).append(";"); // 字体颜色
                }

                XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
                if (bgColor != null) {
                    sb.append("background-color:#").append(bgColor.getARGBHex().substring(2)).append(";"); // 背景颜色
                }
                sb.append(getBorderStyle(0, cellStyle.getBorderTop(), ((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
                sb.append(getBorderStyle(1, cellStyle.getBorderRight(), ((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
                sb.append(getBorderStyle(2, cellStyle.getBorderBottom(), ((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
                sb.append(getBorderStyle(3, cellStyle.getBorderLeft(), ((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));

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
     * 330 * 单元格内容的水平对齐方式 331 * @param alignment 332 * @return 333
     */
    private static String convertAlignToHtml(HorizontalAlignment alignment) {

        String align = "left";
        //HorizontalAlignment ha = HorizontalAlignment.forInt(alignment);
        switch (alignment) {
            case LEFT:
                align = "left";
                break;
            case CENTER:
                align = "center";
                break;
            case RIGHT:
                align = "right";
                break;
            default:
                break;
        }
        return align;
    }

    /**
     * 354 * 单元格中内容的垂直排列方式 355 * @param verticalAlignment 356 * @return 357
     */
    private static String convertVerticalAlignToHtml(VerticalAlignment verticalAlignment) {

        String valign = "middle";
        //VerticalAlignment va = VerticalAlignment.forInt(verticalAlignment);
        switch (verticalAlignment) {
            case BOTTOM:
                valign = "bottom";
                break;
            case CENTER:
                valign = "center";
                break;
            case TOP:
                valign = "top";
                break;
            default:
                break;
        }
        return valign;
    }

    @SuppressWarnings("deprecation")
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
    private static String[] borderStyles = {"solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid", "solid", "solid", "solid", "solid"};

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

        if (xc != null) {
            String borderColorStr = xc.getARGBHex();// t.getARGBHex();
            borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000" : borderColorStr.substring(2);
            return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";
        }

        return "";
    }
}
