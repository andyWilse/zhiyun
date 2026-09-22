package com.religion.zhiyun.utils.excel;

import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class CellValueTrans {

    public static String getCellValue(Cell cell,
                                            Workbook workbook,
                                            int rowNum,
                                            int cellNum,
                                            List<Integer> cellEmptyList) {
        int cellType = cell.getCellType();
        String cellValue="";
        int cellNo = cellNum+1;
        int rowNo = rowNum+1;
        FormulaEvaluator evaluator=workbook.getCreationHelper().createFormulaEvaluator();
        //单元格类型判断
        if(0==cellType){
            //数值型
            cellValue= new BigDecimal(cell.getNumericCellValue()).toPlainString();
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
                    throw new RuntimeException("第"+rowNo+"行,第"+cellNo+"列,字段不能为空！");
                }
            }

        }else if(5==cellType){
            //单元格错误
            throw new RuntimeException("第"+rowNo+"行,第"+cellNo+"列,单元格错误！");
        }else{
            throw new RuntimeException("未知错误！");
        }
        return cellValue;
    }
}
