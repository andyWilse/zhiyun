package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.OperaEnums;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.PcResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.dao.VenuesManagerMapper;
import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesManagerEntity;
import com.religion.zhiyun.venues.services.VenuesManagerService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VenuesManagerServiceImpl implements VenuesManagerService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VenuesManagerMapper venuesManagerMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RmFileService rmFileService;
    @Autowired
    private RmFileMapper rmFileMapper;
    @Autowired
    private OperateRecordService operateRecordService;


    @Override
    public PageResponse add(VenuesManagerEntity venuesManagerEntity, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="添加数据！";

        try {
            String managerTypeCd = venuesManagerEntity.getManagerTypeCd();
            String certTypeCd = venuesManagerEntity.getCertTypeCd();
            if(GeneTool.isEmpty(certTypeCd)){
                throw new RuntimeException("证件类型不能为空！");
            }
            String managerMobile = venuesManagerEntity.getManagerMobile();
            long numTel = sysUserMapper.queryTelNum(managerMobile);
            if(numTel>0l){
                throw new RuntimeException("电话号码："+managerMobile+"已被占用");
            }
            String passwords = venuesManagerEntity.getPasswords();
            if(null==passwords){
                passwords="Zhi!@#123";
            }
            String pass = this.passwordSalt(managerMobile, passwords, managerTypeCd);
            venuesManagerEntity.setPasswords(pass);

            String login = this.getLogin(token);
            venuesManagerEntity.setValidInd("1");//有效
            venuesManagerEntity.setCreator(login);
            venuesManagerEntity.setCreateTime(new Date());
            venuesManagerEntity.setLastModifier(login);
            venuesManagerEntity.setLastModifyTime(new Date());
            //处理图片
            String picturesPath = venuesManagerEntity.getManagerPhotoPath();
            String picturesPathRemove = venuesManagerEntity.getPicturesPathRemove();
            //清理图片
            if(null!=picturesPathRemove && !picturesPathRemove.isEmpty()){
                rmFileService.deletePicture(picturesPathRemove);
                String[] split = picturesPathRemove.split(",");
                if(null!=split && split.length>0 ){
                    for (int i=0;i<split.length;i++){
                        String re = split[i];
                        if(picturesPath.contains(re)){
                            String replace = picturesPath.replace(re+",",  "");
                            picturesPath=replace;
                        }
                    }
                    //保存图片
                    venuesManagerEntity.setManagerPhotoPath(picturesPath);
                }
            }
            venuesManagerMapper.add(venuesManagerEntity);

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            vuMap.put("operator",login);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",String.valueOf(venuesManagerEntity.getManagerId()));
            vuMap.put("operateType", OperaEnums.venue_manager_add.getCode());
            vuMap.put("operateContent", JsonUtils.beanToJson(venuesManagerEntity));
            String operateDetail="场所负责人："+venuesManagerEntity.getManagerCnNm();
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code= ResultCode.SUCCESS.getCode();
            message="添加数据成功！";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="添加数据失败！";
            e.printStackTrace();
        }

        return new PageResponse(code,message);
    }

    @Override
    public PageResponse updateManager(VenuesManagerEntity venuesManagerEntity, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="修改数据！";

        try {
            String managerMobile = venuesManagerEntity.getManagerMobile();
            String certTypeCd = venuesManagerEntity.getCertTypeCd();
            if(GeneTool.isEmpty(certTypeCd)){
                throw new RuntimeException("证件类型不能为空！");
            }

            //电话号码重复性校验
            String originMobile = venuesManagerEntity.getOriginMobile();
            if(!managerMobile.equals(originMobile)){
                long numTel = sysUserMapper.queryTelNum(managerMobile);
                if(numTel>0l){
                    throw new RuntimeException("电话号码："+managerMobile+"已被占用");
                }
            }

            //密码重置
            String managerTypeCd = venuesManagerEntity.getManagerTypeCd();
            String passwordsOrigin = "ASqw@!12";
            String pass = this.passwordSalt(managerMobile, passwordsOrigin, managerTypeCd);
            venuesManagerEntity.setPasswords(pass);

            //修改记录
            String login = this.getLogin(token);
            venuesManagerEntity.setLastModifier(login);
            venuesManagerEntity.setLastModifyTime(new Date());
            //处理图片
            String picturesPath = venuesManagerEntity.getManagerPhotoPath();
            String picturesPathRemove = venuesManagerEntity.getPicturesPathRemove();
            //清理图片
            if(null!=picturesPathRemove && !picturesPathRemove.isEmpty()){
                rmFileService.deletePicture(picturesPathRemove);
                String[] split = picturesPathRemove.split(",");
                if(null!=split && split.length>0 ){
                    for (int i=0;i<split.length;i++){
                        String re = split[i];
                        if(picturesPath.contains(re)){
                            String replace = picturesPath.replace(re+",",  "");
                            picturesPath=replace;
                        }
                    }
                    //保存图片
                    venuesManagerEntity.setManagerPhotoPath(picturesPath);
                }
            }
            venuesManagerMapper.update(venuesManagerEntity);

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            vuMap.put("operator",login);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",String.valueOf(venuesManagerEntity.getManagerId()));
            vuMap.put("operateType", OperaEnums.venue_manager_update.getCode());
            vuMap.put("operateContent", JsonUtils.beanToJson(venuesManagerEntity));
            String operateDetail="场所负责人："+venuesManagerEntity.getManagerCnNm();
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code= ResultCode.SUCCESS.getCode();
            message="修改数据成功！";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="修改数据失败！";
            e.printStackTrace();
        }

        return new PageResponse(code,message);
    }

    @Override
    public void update(VenuesManagerEntity venuesManagerEntity) {

    }

    @Override
    public PageResponse delete(int managerId, String token) {

        long code=ResultCode.FAILED.getCode();
        String message="场所管理人员删除";
        try {
            String manaId = String.valueOf(managerId);
            List<VenuesManagerEntity> byManagerId = venuesManagerMapper.getByManagerId(manaId);
            String login = this.getLogin(token);
            venuesManagerMapper.delete(managerId,login, TimeTool.getYmdHms());

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            vuMap.put("operator",login);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",manaId);
            vuMap.put("operateType", OperaEnums.venue_manager_delete.getCode());
            vuMap.put("operateContent", JsonUtils.listTOJson(byManagerId));
            String operateDetail="场所负责人："+byManagerId.get(0).getManagerCnNm();
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code=ResultCode.SUCCESS.getCode();
            message="场所管理人员删除成功";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="场所管理人员删除失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message);

    }

    @Override
    public PcResponse query() {
        return null;
    }

    @Override
    public PageResponse findManager(Map<String, Object> map, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="场所管理人员查询";
        List<Map<String, Object>> list=new ArrayList<>();
        long total=0l;
        try {
            ParamsVo auth = this.getAuth(token);
            String managerCnNm = (String) map.get("managerCnNm");
            String managerMobile = (String) map.get("managerMobile");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(managerCnNm);
            auth.setSearchTwo(managerMobile);

            list = venuesManagerMapper.findManager(auth);
            total=venuesManagerMapper.findManagerTotal(auth);

            code=ResultCode.SUCCESS.getCode();
            message="场所管理人员查询成功";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="场所管理人员查询失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,total,list.toArray());
    }

    @Override
    public PageResponse getByManagerId(String managerId) {
        long code=ResultCode.FAILED.getCode();
        String message="获取场所管理人员信息";
        List<VenuesManagerEntity> listManager=new ArrayList<>();
        try {

            List<VenuesManagerEntity> list = venuesManagerMapper.getByManagerId(managerId);
            if(null!=list && list.size()>0){
                VenuesManagerEntity venuesManagerEntity = list.get(0);
                //3.图片
                String picturesPath = venuesManagerEntity.getManagerPhotoPath();
                if(null!=picturesPath && !picturesPath.isEmpty()){
                    String[] split = picturesPath.split(",");
                    List<Map<String, Object>> fileUrl = rmFileMapper.getFileUrl(split);
                    venuesManagerEntity.setFileList(fileUrl.toArray());
                }
                listManager.add(venuesManagerEntity);
            }

            code=ResultCode.SUCCESS.getCode();
            message="获取场所管理人员信息成功";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="获取场所管理人员信息失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,listManager.toArray());
    }

    /**
     * 获取登录人
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }
        return loginNm;
    }

    /**
     * 密码加密
     * @param userName
     * @param password
     * @return
     */
    public String passwordSalt(String userName,String password,String identity) {
        //String saltOrigin=null;
        Object salt= ByteSource.Util.bytes(userName+identity);
        String hashAlgorithmName = "MD5";//加密方式
        char[]  credential=(char[])(password != null ? password.toCharArray() : null);//密码
        int hashIterations = 1024;//加密1024次
        Hash result = new SimpleHash(hashAlgorithmName,credential,salt,hashIterations);
        return String.valueOf(result);
    }
    public ParamsVo getAuth(String token){
        String login = this.getLogin(token);
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
        String area="";
        String town ="";
        String relVenuesId="";
        String[] venuesArr={};
        if(null!=sysUserEntity){
            relVenuesId = sysUserEntity.getRelVenuesId();
            town = sysUserEntity.getTown();
            area = sysUserEntity.getArea();
        }else{
            throw new RuntimeException("用户已过期，请重新登录！");
        }
        ParamsVo vo=new ParamsVo();
        vo.setArea(area);
        vo.setTown(town);
        vo.setVenues(relVenuesId);
        if(null!=relVenuesId && !relVenuesId.isEmpty()){
            venuesArr=relVenuesId.split(",");
            vo.setVenuesArr(venuesArr);
        }
        return vo;
    }
}
