package com.religion.zhiyun.utils.fileutil;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POIUtils {
    /**
     * Excel 解析成数据集合
     *
     * @return
     */
    public static List<Map<Integer,String>>  handler(MultipartFile file) {
        List<Map<Integer,String>> list = new ArrayList<>();
        Map<Integer,String> map=null;
        try {
            //1. 创建一个 workbook 对象
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            //2. 获取 workbook 中表单的数量
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                //3. 获取表单
                HSSFSheet sheet = workbook.getSheetAt(i);
                //4. 获取表单中的行数
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                for (int j = 0; j < physicalNumberOfRows; j++) {
                    //5. 跳过标题行
                    if (j == 0) {
                        continue;//跳过标题行
                    }
                    //6. 获取行
                    HSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;//防止数据中间有空行
                    }
                    //7. 获取列数
                    int physicalNumberOfCells =row.getPhysicalNumberOfCells();
                    map=new HashMap<>();
                    for (int k = 0; k < physicalNumberOfCells; k++) {
                        HSSFCell cell = row.getCell(k);
                        //_NONE(-1),NUMERIC(0),STRING(1), FORMULA(2), BLANK(3),BOOLEAN(4),ERROR(5);
                        int cellType = cell.getCellType();
                        String cellValue="";
                        if(0==cellType){
                            //数值型
                            double numericCellValue = cell.getNumericCellValue();
                            cellValue=String.valueOf(numericCellValue);
                        }else if(1==cellType){
                            //字符串
                            cellValue=cell.getStringCellValue();
                        }else if(4==cellType){
                            //布尔型
                            boolean booleanCellValue = cell.getBooleanCellValue();
                            cellValue=String.valueOf(booleanCellValue);
                        }/*else if(2==cellType){
                            //公式型
                            //cellValue=null;
                        }else if(3==cellType){
                            //单元格为空!
                            //cellValue=null;
                        }else if(5==cellType){
                            //单元格错误
                            //cellValue="";
                        }*/

                        //把所有数据类型清洗为String类型

                        map.put(k,cellValue);
                    }
                    // 最后将解析后的数据添加到员工集合中
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
