package com.religion.zhiyun.utils.excel;

import com.religion.zhiyun.utils.enums.CellEnums;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CellValueTrans {

    /**
     * 值转换
     * @param cell
     * @param workbook
     * @param rowNum
     * @param cellNum
     * @param cellEmptyList
     * @return
     */
    public static String getCellValue(Cell cell,
                                            Workbook workbook,
                                            int rowNum,
                                            int cellNum,
                                            List<Integer> cellEmptyList) {
        int cellType = cell.getCellType();
        String cellValue="";
        //int cellNo = cellNum+1;
        int rowNo = rowNum+1;
        FormulaEvaluator evaluator=workbook.getCreationHelper().createFormulaEvaluator();
        //单元格类型判断
        if(0==cellType){
            //数值型
            //cellValue= new BigDecimal(cell.getNumericCellValue()).toPlainString();
            short format = cell.getCellStyle().getDataFormat();
            if (DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = null;
                //System.out.println("cell.getCellStyle().getDataFormat()="+cell.getCellStyle().getDataFormat());
                if (format == 20 || format == 32) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil
                            .getJavaDate(value);
                    cellValue = sdf.format(date);
                }else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
                try {
                    cellValue = sdf.format(cell.getDateCellValue());// 日期
                } catch (Exception e) {
                    try {
                        throw new Exception("exception on get date data !".concat(e.toString()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }finally{
                    sdf = null;
                }
            }  else {
                BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
                cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
            }

        }else if(1==cellType){
            //字符串
            cellValue=cell.getStringCellValue();
        }else if(4==cellType){
            //布尔型
            boolean booleanCellValue = cell.getBooleanCellValue();
            cellValue=String.valueOf(booleanCellValue);
        }else if(2==cellType){
            //公式型
            CellValue evaluate = evaluator.evaluate(cell);
            CellType cellTypeEnum = evaluate.getCellTypeEnum();
            int codeEv = cellTypeEnum.getCode();
            if(1==codeEv){
                cellValue=evaluate.getStringValue();
            }else if(0==codeEv){
                cellValue= new BigDecimal(evaluate.getNumberValue()).toPlainString();
            }
            //throw new RuntimeException("第"+rowNo+"行,第"+cellNo+"列,公式型单元格错误！");
        }else if(3==cellType){
            if(null!=cellEmptyList && cellEmptyList.size()>0){
                boolean hasCell = cellEmptyList.contains(cellNum);
                if(hasCell){
                    //单元格为空!
                    throw new RuntimeException("第"+rowNo+"行"+ CellEnums.getName(cellNum)+"列,字段不能为空！");
                }
            }

        }else if(5==cellType){
            //单元格错误
            throw new RuntimeException("第"+rowNo+"行"+CellEnums.getName(cellNum)+"列,单元格错误！");
        }else{
            throw new RuntimeException("未知错误！");
        }
        return cellValue;
    }




}
