package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.OperaEnums;
import com.religion.zhiyun.utils.map.GetLngAndLagGaoDe;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.dao.VenuesManagerMapper;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;
import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.services.RmVenuesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class RmVenuesInfoServiceImpl implements RmVenuesInfoService {
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;
    @Autowired
    private RmFileMapper rmFileMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VenuesManagerMapper venuesManagerMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RmStaffInfoMapper staffInfoMapper;
    @Autowired
    private RmFileService rmFileService;
    @Autowired
    private OperateRecordService operateRecordService;

    long code= ResultCode.FAILED.getCode();
    String message="场所信息数据处理！";
    @Override
    @Transactional
    public RespPageBean add(VenuesEntity venuesEnti,String token) {
        code= ResultCode.FAILED.getCode();
        message="场所信息保存失败！";
        try{
            //数据校验
            //校验场所名称不能重复
            List<Map<String, Object>> venuesByNm = rmVenuesInfoMapper.getVenuesByNm(venuesEnti.getVenuesName());
            if(venuesByNm.size()>0){
                throw new RuntimeException("场所名称已存在，请重新填写");
            }
            VenuesEntity venuesEntity = this.checkVenuesData(venuesEnti);
            //添加处理人
            String loginNm = this.getLogin(token);
            venuesEntity.setCreator(loginNm);
            venuesEntity.setCreateTime(TimeTool.getYmdHms());
            venuesEntity.setLastModifier(loginNm);
            venuesEntity.setLastModifyTime(TimeTool.getYmdHms());
            venuesEntity.setVenuesStatus(ParamCode.VENUES_STATUS_01.getCode());
            //处理图片
            String picturesPath = venuesEntity.getPicturesPath();
            String picturesPathRemove = venuesEntity.getPicturesPathRemove();
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
                    venuesEntity.setPicturesPath(picturesPath);
                }
            }
            //场所信息数据保存
            rmVenuesInfoMapper.add(venuesEntity);

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            vuMap.put("operator",loginNm);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",String.valueOf(venuesEntity.getVenuesId()));
            vuMap.put("operateType", OperaEnums.venue_add.getCode());
            vuMap.put("operateContent", JsonUtils.beanToJson(venuesEntity));
            String operateDetail="场所："+venuesEntity.getVenuesName()+"（"+venuesEntity.getVenuesAddres()+"）";
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code=ResultCode.SUCCESS.getCode();
            message="新增场所信息成功！";
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e){
            message="新增场所失败,请联系管理员！";
            e.printStackTrace();
        }
        RespPageBean bean=new RespPageBean(code,message);
        return bean;
    }

    @Override
    public RespPageBean update(VenuesEntity venuesEnti,String token) {
        code= ResultCode.FAILED.getCode();
        message="场所信息更新！";
        try {
            //数据校验
            VenuesEntity venuesEntity = this.checkVenuesData(venuesEnti);
            //处理图片
            String picturesPath = venuesEntity.getPicturesPath();
            String picturesPathRemove = venuesEntity.getPicturesPathRemove();
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
                    venuesEntity.setPicturesPath(picturesPath);
                }
            }
            //添加处理人
            String login = this.getLogin(token);
            venuesEntity.setLastModifyTime(TimeTool.getYmdHms());
            venuesEntity.setLastModifier(login);
            //更新
            rmVenuesInfoMapper.update(venuesEntity);

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            vuMap.put("operator",login);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",String.valueOf(venuesEntity.getVenuesId()));
            vuMap.put("operateType", OperaEnums.venue_update.getCode());
            vuMap.put("operateContent", JsonUtils.beanToJson(venuesEntity));
            String operateDetail="场所："+venuesEntity.getVenuesName()+"（"+venuesEntity.getVenuesAddres()+"）";
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            //成功
            code= ResultCode.SUCCESS.getCode();
            message="场所信息更新成功！";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new RespPageBean(code);
    }

    @Override
    public int delete(int venuesId,String token) {
        String veId = String.valueOf(venuesId);
        VenuesEntity venueByID = rmVenuesInfoMapper.getVenueByID(veId);
        int delete = rmVenuesInfoMapper.delete(venuesId);
        //增加日志信息
        Map<String,Object> vuMap=new HashMap<>();
        vuMap.put("operator",this.getLogin(token));
        vuMap.put("operateTime",new Date());
        vuMap.put("operateRef",veId);
        vuMap.put("operateType", OperaEnums.venue_delete.getCode());
        vuMap.put("operateContent", JsonUtils.beanToJson(venueByID));
        String operateDetail="场所："+venueByID.getVenuesName()+"（"+venueByID.getVenuesAddres()+"）";
        vuMap.put("operateDetail",operateDetail);
        operateRecordService.addRecord(vuMap);

        return delete;
    }

    @Override
    public VenuesEntity getByResponsiblePerson(String responsiblePerson) {
        return rmVenuesInfoMapper.getByResponsiblePerson(responsiblePerson
        );
    }

    @Override
    public RespPageBean querySelect(String search,String town) {
        code=ResultCode.FAILED.getCode();
        message="场所信息获取(下拉框)";
        List<VenuesEntity>  dataList=new ArrayList<>();
        try {
            ParamsVo vo=new ParamsVo();
            vo.setSearchOne(search);
            vo.setTown(town);
            dataList=rmVenuesInfoMapper.querySelect(vo);
            if(null==dataList || dataList.size()<1){
                throw new RuntimeException("该区域无相关场所，请先添加场所信息！");
            }
            code=ResultCode.SUCCESS.getCode();
            message="场所信息获取成功(下拉框)";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new RespPageBean(code,message,dataList.toArray());
    }

    @Override
    public List<VenuesEntity> querySectAll(String religiousSect) {
        return rmVenuesInfoMapper.querySectAll(religiousSect);
    }

    @Override
    public List<VenuesEntity> getByVenuesFaculty(String venuesName, String responsiblePerson) {
        return rmVenuesInfoMapper.getByVenuesFaculty(venuesName,responsiblePerson);
    }

    @Override
    public AppResponse getVenueByID(String venuesId) {
        long code=ResultCode.FAILED.getCode();
        String message="获取场所信息";
        List<VenuesEntity> venues=new ArrayList<>();
        try {
            VenuesEntity venuesentity = rmVenuesInfoMapper.getVenueByID(venuesId);
            
            if(null!=venuesentity){
                //1.场所管理人员
                String responsiblePerson = venuesentity.getResponsiblePerson();
                if(responsiblePerson!=null && !responsiblePerson.isEmpty()){
                    String manager= venuesManagerMapper.getManagerById(responsiblePerson);
                    venuesentity.setResponsiblePerson(manager);
                }
                String liaisonMan = venuesentity.getLiaisonMan();
                if(liaisonMan!=null && !liaisonMan.isEmpty()){
                    String manager= venuesManagerMapper.getManagerById(liaisonMan);
                    venuesentity.setLiaisonMan(manager);
                }
                String groupMembers = venuesentity.getGroupMembers();
                if(groupMembers!=null && !groupMembers.isEmpty()){
                    String manager= venuesManagerMapper.getManagerById(groupMembers);
                    venuesentity.setGroupMembers(manager);
                }
                //2.教职人员
                String venuesStaff = venuesentity.getVenuesStaff();
                if(venuesStaff!=null && !venuesStaff.isEmpty()){
                    String[] split = venuesStaff.split(",");
                    String venuesStaffs = staffInfoMapper.findVenuesStaffs(split);
                    venuesentity.setVenuesStaff(venuesStaffs+",");
                    venuesentity.setVenuesStaffId(venuesStaff);
                }
                //3.图片
                String picturesPath = venuesentity.getPicturesPath();
                if(null!=picturesPath && !picturesPath.isEmpty()){
                    String[] split = picturesPath.split(",");
                    List<Map<String, Object>> fileUrl = rmFileMapper.getFileUrl(split);
                    venuesentity.setFileList(fileUrl.toArray());
                }
            }else{
                throw new RuntimeException("场所信息丢失，请联系管理员！");
            }
            venues.add(venuesentity);
            code=ResultCode.SUCCESS.getCode();
            message="获取场所信息成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,venues.toArray());
    }

    @Override
    public PageResponse getAllNum(String token) {
        long code=ResultCode.FAILED.getCode();
        String message="统计场所数量";
        List<Map<String, Object>> list=new ArrayList<>();
        try {
            ParamsVo auth = this.getAuth(token);
            Map<String, Object> allNum = rmVenuesInfoMapper.getAllNum(auth);
            list.add(allNum);
            code=ResultCode.SUCCESS.getCode();
            message="统计场所数量成功";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="统计场所数量失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
    }

    @Override
    public PageResponse getVenueNum(String type,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="地图统计场所数量";
        List<Map<String, Object>> list=new ArrayList<>();
        try {
            ParamsVo auth = new ParamsVo();
            if("01".equals(type)){

            }else if("02".equals(type)){
                auth =this.getAuth(token);
            }
            Map<String, Object> allNum = rmVenuesInfoMapper.getAllNum(auth);
            list.add(allNum);
            code=ResultCode.SUCCESS.getCode();
            message="地图统计场所数量成功";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="地图统计场所数量失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
    }

    @Override
    public PageResponse getDialogVenue(Map<String, Object> map, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="统计场所弹框";
        List<Map<String, Object>> list=new ArrayList<>();
        long total=0l;
        try {
            ParamsVo auth = this.getAuth(token);
            String religiousSect = (String) map.get("religiousSect");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(religiousSect);

            list = rmVenuesInfoMapper.getDialogVenue(auth);
            total=rmVenuesInfoMapper.getDialogVenueTotal(auth);

            code=ResultCode.SUCCESS.getCode();
            message="统计场所弹框成功";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="统计场所弹框失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,total,list.toArray());
    }

    @Override
    public RespPageBean getVenuesByPage(Integer page, Integer size, String venuesName, String responsiblePerson,
                                        String religiousSect,String venuesPhone,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="PC场所信息获取";
        Long total=0l;
        List<VenuesEntity>  dataList=new ArrayList<>();
        String[] relVenuesArr={};
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            String login = this.getLogin(token);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
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
            VenuesEntity ve=new VenuesEntity();
            ve.setResponsiblePerson(responsiblePerson);
            ve.setVenuesName(venuesName);
            ve.setReligiousSect(religiousSect);
            ve.setVenuesPhone(venuesPhone);
            ve.setArea(area);
            ve.setTown(town);
            ve.setVenuesAddres(relVenuesId);
            if(null!=relVenuesId && !relVenuesId.isEmpty()){
                relVenuesArr=relVenuesId.split(",");
            }
            dataList=rmVenuesInfoMapper.getVenuesByPage(page,size,ve,relVenuesArr);
            total=rmVenuesInfoMapper.getTotal(ve,relVenuesArr);
            code= ResultCode.SUCCESS.getCode();
            message="PC场所信息获取成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
        }catch (Exception e) {
            message="PC场所信息获取失败！";
            e.printStackTrace();
        }
        return new RespPageBean(code,message,total,dataList.toArray());
    }

    @Override
    public AppResponse queryVenues(Map<String, Object> map,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="app场所下拉(监管)";
        List<Map<String, Object>> venuesList =new ArrayList<>();
        try {
            //ParamsVo auth = this.getAuth(token);
            ParamsVo auth =new ParamsVo();
            String search = (String)map.get("search");
            String province = (String)map.get("province");
            String city = (String)map.get("city");
            String area = (String)map.get("area");
            String town = (String)map.get("town");
            auth.setArea(area);
            auth.setTown(town);
            auth.setSearchOne(search);

            venuesList = rmVenuesInfoMapper.queryVenues(auth);
            code= ResultCode.SUCCESS.getCode();
            message="场所下拉(监管)数据获取成功！";
        } catch (RuntimeException r){
            message=r.getMessage();
        } catch (Exception e) {
            message="场所下拉(监管)数据获取失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,venuesList.toArray());
    }

    @Override
    public AppResponse queryStaffVenues(Map<String, Object> map, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="app场所下拉(PC教职)";
        List<Map<String, Object>> venuesList =new ArrayList<>();
        try {
            ParamsVo auth = this.getAuth(token);
            String search = (String)map.get("search");
            auth.setSearchOne(search);
            venuesList = rmVenuesInfoMapper.queryVenues(auth);

            code= ResultCode.SUCCESS.getCode();
            message="场所下拉(PC教职)数据获取成功！";
        } catch (RuntimeException r){
            message=r.getMessage();
        } catch (Exception e) {
            message="场所下拉(PC教职)数据获取失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,venuesList.toArray());
    }

    @Override
    public AppDetailRes getMapVenuesDetail(String venuesId,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="获取地图场所详情失败！";
        Map<String, Object> venuesMap =new HashMap<>();
        try {
            venuesMap = rmVenuesInfoMapper.getVenuesByID(venuesId);

            if(null!=venuesMap && venuesMap.size()>0){
                //获取图片地址
                String picturesPath= (String) venuesMap.get("picturesPath");
                Object[] file ={};
                if(!picturesPath.isEmpty()){
                    //查询地图地址
                    String[] split = picturesPath.split(",");
                    List<Map<String,Object>> imgMap=rmFileMapper.getPath(split);
                    if(null!=imgMap && imgMap.size()>0){
                        file = imgMap.toArray();
                    }
                }
                venuesMap.put("images",file);

                //关联活动
                //String relVenuesId= (String) venuesMap.get("relVenuesId");
                List<Map<String, Object>> joinActivity = rmVenuesInfoMapper.getFiling(venuesId);
                venuesMap.put("joinActivity",joinActivity.toArray());

                //三人驻堂
                List<Map<String, Object>> users = rmVenuesInfoMapper.getUsers(venuesId);
                venuesMap.put("garrison",users.toArray());

                /*//监管干部
                List<Map<String, Object>> ganUsers = rmVenuesInfoMapper.getGanUsers(venuesId);
                venuesMap.put("janGan",ganUsers.toArray());*/

                //教职人员
                List<Map<String, Object>> venuesStaffs =new ArrayList<>();
                String venuesStaff = (String) venuesMap.get("venuesStaff");
                if(null!=venuesStaff && !venuesStaff.isEmpty()){
                    venuesStaffs = rmVenuesInfoMapper.getVenuesStaffs(venuesStaff.split(","));
                }
                String veStaffStr="";
                if(null!=venuesStaffs && venuesStaffs.size()>0){
                    for (int k=0;k<venuesStaffs.size();k++) {
                        Map<String, Object> sMap = venuesStaffs.get(k);
                        String name = (String) sMap.get("name");
                        veStaffStr=veStaffStr+name+"、";
                        if(k==venuesStaffs.size()-1){
                            veStaffStr=veStaffStr+name;
                        }
                    }
                }
                venuesMap.put("veStaffStr",veStaffStr);
                venuesMap.put("venuesStaff",venuesStaffs.toArray());
               /* List<Map<String, Object>> staffs = rmVenuesInfoMapper.getStaffs(venuesId);
                Map<String, Object> oneStaffDirector=new HashMap<>();
                Map<String, Object> twoStaffDirector=new HashMap<>();
                Map<String, Object> refStaffDirector=new HashMap<>();
                if(null!=staffs && staffs.size()>0){
                    for(int i=0;i<staffs.size();i++){
                        if(i==0){
                            oneStaffDirector= staffs.get(0);
                            staffs.remove(0);
                        }else if(i==1){
                            twoStaffDirector= staffs.get(1);
                            staffs.remove(1);
                        }
                    }
                }
                venuesMap.put("oneStaffDirector",oneStaffDirector);
                venuesMap.put("twoStaffDirector",twoStaffDirector);
                venuesMap.put("refStaffDirector",refStaffDirector);*/

                //负责人
                Map<String, Object> oneDirector=new HashMap<>();
                String resId= (String) venuesMap.get("resId");
                String resNm= (String) venuesMap.get("resNm");
                String resMo= (String) venuesMap.get("resMo");
                if(!GeneTool.isEmpty(resId)){
                    oneDirector.put("id",resId);
                    oneDirector.put("name",resNm);
                    oneDirector.put("phone",resMo);
                }

                Map<String, Object> twoDirector=new HashMap<>();
                String groId= (String) venuesMap.get("groId");
                String groNm= (String) venuesMap.get("groNm");
                String groMo= (String) venuesMap.get("groMo");
                if(!GeneTool.isEmpty(groId)){
                    twoDirector.put("id",groId);
                    twoDirector.put("name",groNm);
                    twoDirector.put("phone",groMo);
                }

                Map<String, Object> workDirector=new HashMap<>();
                String liaId= (String) venuesMap.get("liaId");
                String liaNm= (String) venuesMap.get("liaNm");
                String liaMo= (String) venuesMap.get("liaMo");
                if(!GeneTool.isEmpty(liaId)){
                    workDirector.put("id",liaId);
                    workDirector.put("name",liaNm);
                    workDirector.put("phone",liaMo);
                }

                venuesMap.put("oneDirector",oneDirector);
                venuesMap.put("twoDirector",twoDirector);
                venuesMap.put("workDirector",workDirector);

                //监控查看权限
                String inArea="0"; //1在辖区 0不在辖区
                String login = this.getLogin(token);
                SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
                if(null==sysUserEntity){
                    throw  new RuntimeException("登录用户信息丢失！！");
                }
                String area = (String) venuesMap.get("area");
                String town = (String) venuesMap.get("town");
                String identity = sysUserEntity.getIdentity();
                String areaUser = sysUserEntity.getArea();
                String townUser  = sysUserEntity.getTown();
                String relVenuesId = sysUserEntity.getRelVenuesId();
                //venuesId
                if(("10000002".equals(identity) || "10000003".equals(identity)) && area.equals(areaUser)){
                    inArea="1";
                }else if(("10000004".equals(identity) || "10000005".equals(identity)) && town.equals(townUser)){
                    inArea="1";
                }else if(("10000006".equals(identity) || "10000007".equals(identity)) && relVenuesId.contains(venuesId)){
                    inArea="1";
                }
                venuesMap.put("inArea",inArea);

            }else{
                throw  new RuntimeException("获取地图场所详情失败！！");
            }
            code= ResultCode.SUCCESS.getCode();
            message="获取地图场所详情成功！";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        } catch(Exception e) {
            message="获取地图场所详情失败！！";
            e.printStackTrace();
        }

        return new AppDetailRes(code,message, venuesMap);
    }

    @Override
    public AppResponse getMapVenues(Map<String, Object> map,String token) {
        long code = ResultCode.FAILED.getCode();
        String  message = "获取地图场所信息失败！";
        List<Map<String, Object>> mapVenues = new ArrayList<>();
        try {
            String search = (String) map.get("search");
            String religiousSect = (String) map.get("religiousSect");
            String type = (String) map.get("type");
            String town = (String) map.get("town");
            String[] religiousSectArr ={};
            if(null!=religiousSect && !religiousSect.isEmpty()){
                religiousSectArr = religiousSect.split(",");
            }
            ParamsVo auth = new ParamsVo();
            if("01".equals(type)){//全部

            }else if("02".equals(type)){//我的辖区
                auth =this.getAuth(token);
            }
            auth.setSearchOne(search);
            auth.setSearchTwo(religiousSect);
            auth.setSearchThree(town);

            auth.setSearchArr(religiousSectArr);
            mapVenues = rmVenuesInfoMapper.getMapVenues(auth);

            code= ResultCode.SUCCESS.getCode();
            message="获取地图场所信息成功！";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message = "获取地图场所信息失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,mapVenues.toArray());
    }

    @Override
    public AppResponse getMapsVenues(Map<String, Object> map, String token) {
        long code = ResultCode.FAILED.getCode();
        String  message = "获取地图场所信息失败！";
        List<Map<String, Object>> mapVenues = new ArrayList<>();
        try {
            String search = (String) map.get("search");
            String religiousSect = (String) map.get("religiousSect");
            String type = (String) map.get("type");
            String[] religiousSectArr ={};
            if(null!=religiousSect && !religiousSect.isEmpty()){
                religiousSectArr = religiousSect.split(",");
            }
            ParamsVo auth = new ParamsVo();
            if("01".equals(type)){//全部

            }else if("02".equals(type)){//我的辖区
                auth =this.getAuth(token);
            }
            auth.setSearchOne(search);
            auth.setSearchTwo(religiousSect);
            auth.setSearchArr(religiousSectArr);
            List<Map<String, Object>> mapVenue = rmVenuesInfoMapper.getMapVenues(auth);
            if(null!=mapVenue && mapVenue.size()>0){
                for(int i=0;i<mapVenue.size();i++){
                    Map<String, Object> maps = mapVenue.get(i);
                    String latitude = (String) maps.get("Latitude");
                    String longitude = (String) maps.get("longitude");
                    Integer venuesId = (Integer) maps.get("venuesId");
                    String religiousSec = (String) maps.get("religiousSect");
                    Map<String, Object> mapNew =new HashMap<>();
                    Double[] jw={Double.valueOf(latitude),Double.valueOf(longitude)};
                    mapNew.put("position",jw);
                    mapNew.put("venuesId",venuesId);
                    mapNew.put("religiousSec",religiousSec);
                    mapVenues.add(mapNew);
                }
            }

            code= ResultCode.SUCCESS.getCode();
            message="获取地图场所信息成功！";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message = "获取地图场所信息失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,mapVenues.toArray());
    }

    @Override
    public AppResponse queryVenuesJz(String token, String search) {

        long code= ResultCode.FAILED.getCode();
        String message= "获取更新场所信息失败！";
        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            String login = this.getLogin(token);
            mapList = rmVenuesInfoMapper.queryVenuesJz(login, search);
            if(null!=mapList && mapList.size()>0){
                for(int i=0;i<mapList.size();i++){
                    Map<String, Object> map = mapList.get(i);
                    //封装图片信息
                    String picturesPath = (String) map.get("picturesPath");
                    List<Map<String, Object>> path =new ArrayList<>();
                    if(null!=picturesPath && !picturesPath.isEmpty()){
                        path = rmFileMapper.getPath(picturesPath.split(","));
                    }
                    map.put("picturesPath",path.toArray());
                    //封装教职人员
                    Integer venuesId = (Integer) map.get("venuesId");
                    String staffJz = rmVenuesInfoMapper.getStaffJz(String.valueOf(venuesId));
                    map.put("staffs",staffJz);
                }
            }

            code= ResultCode.SUCCESS.getCode();
            message="获取更新场所信息成功！";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message = "获取更新场所信息失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,mapList.toArray());
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
     * 获取
     * @return
     */
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

    /**
     * 数据校验
     * @param venuesEntity
     * @return
     */
    public VenuesEntity checkVenuesData(VenuesEntity venuesEntity) throws UnsupportedEncodingException {
        code= ResultCode.FAILED.getCode();
        message="场所信息数据校验！";
        if(!GeneTool.isEmpty(venuesEntity.getResponsiblePerson())){
            //场所人员校验(负责人)
            List<Map<String, Object>> managerByNm = venuesManagerMapper.getManagerByNm(venuesEntity.getResponsiblePerson());
            if(managerByNm.size()<1){
                code= 101l;
                throw new RuntimeException("负责人在系统不存在，请先添加负责人！");
            }else{
                Integer managerId = (Integer) managerByNm.get(0).get("managerId");
                venuesEntity.setResponsiblePerson(String.valueOf(managerId.intValue()));
            }
        }
        if(!GeneTool.isEmpty(venuesEntity.getLiaisonMan())){
            //场所人员校验(工作联络员)
            List<Map<String, Object>> liaByNm = venuesManagerMapper.getManagerByNm(venuesEntity.getLiaisonMan());
            if(liaByNm.size()<1){
                code= 102l;
                throw new RuntimeException("工作联络员在系统不存在，请先添加工作联络员！");
            }else{
                Integer managerId = (Integer) liaByNm.get(0).get("managerId");
                venuesEntity.setLiaisonMan(String.valueOf(managerId.intValue()));
            }
        }
        if(!GeneTool.isEmpty(venuesEntity.getGroupMembers())){
            //场所人员校验(管理组成员)
            List<Map<String, Object>> groByNm = venuesManagerMapper.getManagerByNm(venuesEntity.getGroupMembers());
            if(groByNm.size()<1){
                code= 103l;
                throw new RuntimeException("管理组成员在系统不存在，请先添加管理组成员！");
            }else{
                Integer managerId = (Integer) groByNm.get(0).get("managerId");
                venuesEntity.setGroupMembers(String.valueOf(managerId.intValue()));
            }
        }

        //地址校验
        String venuesAddres = venuesEntity.getVenuesAddres();
        int venuesId = venuesEntity.getVenuesId();
        if(0!=venuesId){
            VenuesEntity venues= rmVenuesInfoMapper.getVenueByID(String.valueOf(venuesId));
            String venuesAddresOld = venues.getVenuesAddres();
            if(!venuesAddres.equals(venuesAddresOld)){
                String lngAndLag = this.getLngAndLag(venuesAddres);
                String[] split = lngAndLag.split(",");
                String lng=split[0];
                String lat=split[1];

                venuesEntity.setLongitude(lng);
                venuesEntity.setLatitude(lat);
                venuesEntity.setLatitudes(lat);
            }else{
                String latitudeOld = venues.getLatitude();
                String latitudes = venuesEntity.getLatitudes();
                if(!latitudeOld.equals(latitudes)){
                    venuesEntity.setLatitudes(latitudes);
                    venuesEntity.setLatitude(latitudes);
                }
                String longitudeOld = venues.getLongitude();
                String longitudes = venuesEntity.getLongitude();
                if(!longitudeOld.equals(longitudes)){
                    venuesEntity.setLongitude(longitudes);
                }
            }
        }else{
            String lngAndLag = this.getLngAndLag(venuesAddres);
            String[] split = lngAndLag.split(",");
            String lng=split[0];
            String lat=split[1];

            venuesEntity.setLongitude(lng);
            venuesEntity.setLatitude(lat);
            venuesEntity.setLatitudes(lat);
        }

        return venuesEntity;
    }

    /**
     * 根据地址获取经纬度
     * @param venuesAddres
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getLngAndLag(String venuesAddres) throws UnsupportedEncodingException {
        //获取经纬度
        String lngAndLag ="";
        if(null!=venuesAddres && ""!=venuesAddres ){
            //String coordinate = GeocoderLatitudeUtil.getCoordinate(venuesAddres);
            lngAndLag = GetLngAndLagGaoDe.getLngAndLag(venuesAddres);
            if(null==lngAndLag || lngAndLag.isEmpty()){
                throw new RuntimeException("无法获取经纬度，请重新填写详细地址！");
            }
        }else{
            throw new RuntimeException("场所地址信息丢失！");
        }

        return lngAndLag;
    }

    @Override
    public AppResponse getvenuesByType(String Type) {
        long code=ResultCode.FAILED.getCode();
        String message="获取场所列表失败！";
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            list=rmVenuesInfoMapper.getvenuesByType(Type);
            code= ResultCode.SUCCESS.getCode();
            message="获取场所列表成功！";
        } catch (RuntimeException r){
            message=r.getMessage();
        } catch (Exception e) {
            message="获取场所列表失败！";
            e.printStackTrace();
        }


        return new AppResponse(code,message,list.toArray());
    }

    @Override
    public AppResponse getVenuesScore() {
        long code=ResultCode.FAILED.getCode();
        String message="场所综合得分获取失败！";
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            List<Map<String,Object>> listArr=rmVenuesInfoMapper.getVenuesScore(10);
            if(null!=listArr && listArr.size()>0){
                for(int i=0;i<listArr.size();i++){
                    Map<String, Object> map = listArr.get(i);
                    Map<String, Object> mapNew=new HashMap<>() ;
                    mapNew.put("name",map.get("venuesName"));
                    mapNew.put("value",map.get("score"));
                    list.add(mapNew);
                }
            }

            code= ResultCode.SUCCESS.getCode();
            message="场所综合得分获取成功！";
        } catch (RuntimeException r){
            message=r.getMessage();
        } catch (Exception e) {
            message="场所综合得分获取失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,list.toArray());
    }

}
