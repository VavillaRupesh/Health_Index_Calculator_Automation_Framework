package utilities;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Font;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExcelUtility {
    public static FileInputStream fis;
    public static FileOutputStream fos;
    public static XSSFWorkbook workbook;
    public static XSSFSheet sheet;


    public static Object[][] getData(String filePath, String sheetName) throws Exception{
        fis = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet(sheetName);
        int rowNum = sheet.getLastRowNum();
        XSSFRow headerRow = sheet.getRow(0);
        int cellNum = 0;
        if(headerRow != null) {
            cellNum = headerRow.getLastCellNum()-1;
        }
        Object[][] data = new String[rowNum][cellNum];
        DataFormatter df = new DataFormatter();
        for(int i=0;i<rowNum;i++){
            XSSFRow currentRow = sheet.getRow(i+1);
            if(currentRow != null) {
                for(int j=0;j<cellNum;j++){
                    XSSFCell currentCell = currentRow.getCell(j);
                    data[i][j] = (currentCell != null) ? df.formatCellValue(currentCell) : "";
                }
            } else {
                for(int j=0;j<cellNum;j++){
                    data[i][j] = "";
                }
            }
        }
        workbook.close();
        fis.close();
        return data;
    }
    public static void writeFile(String filePath, String sheetName, int rowNum, String result) throws Exception {
        fis = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet(sheetName);

        // Get column number safely
        XSSFRow headerRow = sheet.getRow(0);
        int colNum = 0;
        if(headerRow != null) {
            colNum = headerRow.getLastCellNum()-1;
        }

        // Get existing row or create new one if it doesn't exist
        XSSFRow row1 = sheet.getRow(rowNum);
        if(row1 == null) {
            row1 = sheet.createRow(rowNum);
        }

        // Add result to the specified column
        XSSFCell cell1 = row1.createCell(colNum);
        cell1.setCellValue(result);

        // Create cell style and apply color based on result
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();

        if(result.equalsIgnoreCase("Pass")) {
            // Green background and white text for Pass
            cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            font.setColor(IndexedColors.WHITE.getIndex());
        } else if(result.equalsIgnoreCase("Fail")) {
            // Red background and white text for Fail
            cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            font.setColor(IndexedColors.WHITE.getIndex());
        }

        cellStyle.setFont(font);
        cellStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        cell1.setCellStyle(cellStyle);

        fos = new FileOutputStream(filePath);
        workbook.write(fos);
        fos.close();
        workbook.close();
        fis.close();
    }
}
