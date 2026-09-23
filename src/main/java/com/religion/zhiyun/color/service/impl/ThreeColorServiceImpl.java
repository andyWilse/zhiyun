package com.religion.zhiyun.color.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.religion.zhiyun.color.dao.ThreeColorMapper;
import com.religion.zhiyun.color.entity.ThreeColorEntity;
import com.religion.zhiyun.color.service.ThreeColorService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.base.TransParam;
import com.religion.zhiyun.utils.enums.CellEnums;
import com.religion.zhiyun.utils.excel.CellValueTrans;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.venues.entity.ParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service
public class ThreeColorServiceImpl implements ThreeColorService {
    @Autowired
    private ThreeColorMapper threeColorMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public AppResponse threeColorAdd(ThreeColorEntity threeColorEntity) {
        long code= ResultCode.FAILED.getCode();
        String message="三色要素信息新增失败！";

        try{
            String loginName = TransParam.loginName;
            threeColorEntity.setCoCreateTm(TimeTool.getTimestamp());
            threeColorEntity.setCoCreator(loginName);
            threeColorMapper.addThreeColor(threeColorEntity);

            code= ResultCode.SUCCESS.getCode();
            message="三色要素信息新增成功！";
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());

        }
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse threeColorUpload(Map<String, Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="三色要素excel上传失败！";
        ArrayList<ThreeColorEntity> tcUploadList = new ArrayList<>();
        try{
            String fileContent = map.get("fileContent")==null?"": (String) map.get("fileContent");
            String fileName = map.get("fileName")==null?"": (String) map.get("fileName");
            //数据转换
            String[] parts = fileContent.split(",");
            String base64File = parts[1].split("\"")[0];
            byte[] bytes = Base64.getDecoder().decode(base64File);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            //表格
            Workbook workbook=null;
            if (fileName.endsWith("xls")) {
                //使用 HSSFWorkbook 解析
                workbook = new HSSFWorkbook(inputStream);
            } else {
                //使用 XSSFWorkbook 解析
                workbook = new XSSFWorkbook(inputStream);
            }
            //2. 获取 workbook 中表单的数量
            int numberOfSheets = workbook.getNumberOfSheets();
            if (numberOfSheets!=0) {
                //3. 获取表单
                //只读第一张表格
                Sheet sheet = workbook.getSheetAt(0);
                //4. 获取表单中的行数
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                for (int j = 0; j < physicalNumberOfRows; j++) {
                    //5. 跳过标题行
                    if (j == 0) {
                        continue;//跳过标题行
                    }
                    //6. 获取行
                    Row row = sheet.getRow(j);
                    //跳过空行
                    Row rowCheck = row;
                    boolean skipRow = this.skipRow(rowCheck);
                    if (row == null || skipRow) {
                        continue;//防止数据中间有空行
                    }
                    //7. 获取列数
                    int physicalNumberOfCells =row.getPhysicalNumberOfCells();
                    //三色要素
                    ThreeColorEntity threeColorEntity=new ThreeColorEntity();
                    //不能为空的列
                    //List<Integer> listCellEmpty = Arrays.asList(0,1, 2, 3,4,5,6,7,8,9,10,11);
                    List<Integer> listCellEmpty = Arrays.asList(0,3,5,7,8,9);
                    //列遍历
                    for (int k = 0; k < physicalNumberOfCells; k++) {
                        Cell cell = row.getCell(k);
                        int ro = j + 1;
                        //获取值
                        if(null==cell){
                            //break;
                            if(listCellEmpty.contains(k)){
                                throw new RuntimeException("第"+ro+"行"+ CellEnums.getName(k)+"列,字段不能为空！");
                            }
                        }
                        String cellValue = CellValueTrans.getCellValue(cell, workbook, j, k, listCellEmpty);

                        //处理值
                        if(k==2){
                            threeColorEntity.setCoVenuesId(cellValue);
                        }else if(k==4){
                            threeColorEntity.setCoType(cellValue);
                        }else if(k==6){
                            threeColorEntity.setCoColor(cellValue);
                        }else if(k==7){
                            threeColorEntity.setOccurTm(cellValue.replace("\n",""));
                        }else if(k==8){
                            threeColorEntity.setCoContent(cellValue);
                        }else if(k==10){
                            threeColorEntity.setCoState(cellValue);
                        }else if(k==11){
                            //已处理，完成时间不能为空
                            threeColorEntity.setHandleTm(cellValue.replace("\n",""));
                            if("01".equals(threeColorEntity.getCoState())){
                                if(GeneTool.isEmpty(cellValue)){
                                    throw new RuntimeException("第"+ro+"行"+k+"列,字段不能为空！");
                                }
                            }

                        }else if(k==12){
                            threeColorEntity.setCoProgress(cellValue);
                        }else if(k==13){
                            threeColorEntity.setCoRemark(cellValue);
                        }

                    }
                    //添加list
                    tcUploadList.add(threeColorEntity);
                }
            }

            code= ResultCode.SUCCESS.getCode();
            message="三色要素excel上传成功！";
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());

        }
        return new AppResponse(code,message,tcUploadList.toArray());
    }

    /**
     * 为空校验
     * @param rowCheck
     * @return
     */
    public boolean skipRow(Row rowCheck) {
        //去掉公式单元格
       /* Cell cell1 = rowCheck.getCell(1);
        if (cell1 != null) {
            rowCheck.removeCell(cell1);
        }
        Cell cell2 = rowCheck.getCell(2);
        if (cell2 != null) {
            rowCheck.removeCell(cell2);
        }
        Cell cell4= rowCheck.getCell(4);
        if (cell4 != null) {
            rowCheck.removeCell(cell4);
        }
        Cell cell6 = rowCheck.getCell(6);
        if (cell6 != null) {
            rowCheck.removeCell(cell6);
        }
        Cell cell10 = rowCheck.getCell(10);
        if (cell10 != null) {
            rowCheck.removeCell(cell10);
        }*/

        if (rowCheck == null) return true;

        int[] colColumns={0,3,5,7,8,9};
        //同时为空跳过
        for (int col : colColumns) {
            Cell cell = rowCheck.getCell(col);
            if (cell != null && cell.getCellType() != 3) {
                return false; // 常规列有数据，不跳过
            }
        }
        return true; // 所有常规列都为空，跳过该行
    }

    @Override
    public AppResponse threeColorImport(Map<String,Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="三色要素excel数据保存失败！";

        try{
            Object threeColorList = map.get("threeColorList");
            Object[] tcArr=null;
            if(null!=threeColorList){
                tcArr = ((List<?>) threeColorList).toArray();
            }
            for(int i=0;i<tcArr.length;i++) {
                HashMap<String, Object> threeColor = (HashMap<String, Object>) tcArr[i];
                ThreeColorEntity threeColorEntity = BeanUtil.mapToBean(threeColor, ThreeColorEntity.class, false);
                threeColorEntity.setCoOccurTm(threeColor.get("occurTm")==null?null:TimeTool.toTimestamp((String) threeColor.get("occurTm")));
                threeColorEntity.setCoHandleTm(threeColor.get("handleTm")==null?null:TimeTool.toTimestamp((String) threeColor.get("handleTm")));
                threeColorEntity.setCoCreateTm(TimeTool.getTimestamp());
                threeColorEntity.setCoCreator(TransParam.loginName);
                threeColorMapper.addThreeColor(threeColorEntity);


            }
            code= ResultCode.SUCCESS.getCode();
            message="三色要素excel数据保存成功！";
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());

        }
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse threeColorUpdate(ThreeColorEntity threeColorEntity) {
        long code= ResultCode.FAILED.getCode();
        String message="三色要素信息修改失败！";

        try{
            String loginName = TransParam.loginName;
            threeColorEntity.setCoModifyTm(TimeTool.getTimestamp());
            threeColorEntity.setCoModifier(loginName);
            threeColorMapper.updateThreeColor(threeColorEntity);

            code= ResultCode.SUCCESS.getCode();
            message="三色要素信息修改成功！";
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());

        }
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse threeColorDelete(int coId) {
        long code= ResultCode.FAILED.getCode();
        String message="三色要素信息删除失败！";

        try{
            String loginName = TransParam.loginName;

            threeColorMapper.deleteThreeColor(coId,loginName);

            code= ResultCode.SUCCESS.getCode();
            message="三色要素信息删除成功！";
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());

        }
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse getThreeColor(int coId) {
        return null;
    }

    @Override
    public AppResponse getThreeColorList(ParamsVo vo) {
        long code= ResultCode.FAILED.getCode();
        String message="获取三色要素信息列表失败！";
        Long threeColorTotal =0l;
        List<ThreeColorEntity> threeColorList=null;
        try{
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(TransParam.loginName);
            String area="";
            String town ="";
            String relVenuesId="";
            if(null!=sysUserEntity){
                relVenuesId = sysUserEntity.getRelVenuesId();
                town = sysUserEntity.getTown();
                area = sysUserEntity.getArea();
            }else{
                throw new RuntimeException("用户已过期，请重新登录！");
            }
            vo.setArea(area);
            vo.setTown(town);
            String[] relVenuesArr={};
            if(null!=relVenuesId && !relVenuesId.isEmpty()){
                relVenuesArr=relVenuesId.split(",");
            }
            vo.setVenuesArr(relVenuesArr);
            threeColorList = threeColorMapper.getThreeColorList(vo);
            threeColorTotal = threeColorMapper.getThreeColorTotal(vo);

            code= ResultCode.SUCCESS.getCode();
            message="获取三色要素信息列表成功！";
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());

        }
        return new AppResponse(code,message,threeColorTotal,threeColorList.toArray());
    }
}
