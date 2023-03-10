package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.record.dao.OperateRecordMapper;
import com.religion.zhiyun.record.entity.RecordEntity;
import com.religion.zhiyun.utils.map.GeocoderLatitudeUtil;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
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
    private OperateRecordMapper userLogsInfoMapper;


    @Override
    @Transactional
    public RespPageBean add(VenuesEntity venuesEntity,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="???????????????????????????";
        try{
            //??????????????????????????????
            List<Map<String, Object>> venuesByNm = rmVenuesInfoMapper.getVenuesByNm(venuesEntity.getVenuesName());
            if(venuesByNm.size()>0){
                throw new RuntimeException("???????????????????????????????????????");
            }
            //??????????????????(?????????)
            List<Map<String, Object>> managerByNm = venuesManagerMapper.getManagerByNm(venuesEntity.getResponsiblePerson());
            if(managerByNm.size()<1){
                code= ResultCode.VALIDATE_FAILED.getCode();
                throw new RuntimeException("??????????????????????????????????????????????????????");
            }else{
                Integer managerId = (Integer) managerByNm.get(0).get("managerId");
                venuesEntity.setResponsiblePerson(String.valueOf(managerId.intValue()));
            }

            //???????????????
            String venuesAddres = venuesEntity.getVenuesAddres();
            if(null!=venuesAddres && ""!=venuesAddres){
                String coordinate = GeocoderLatitudeUtil.getCoordinate(venuesAddres);
                String[] split = coordinate.split(",");
                String lng=split[0];
                String lat=split[1];
                if("1".equals(lng) || "1".equals(lat) ){
                    code= ResultCode.FAILED.getCode();
                    message="??????????????????????????????????????????????????????";
                    throw new RuntimeException(message);
                }
                venuesEntity.setLongitude(lng);
                venuesEntity.setLatitude(lat);
            }else{
                code= ResultCode.FAILED.getCode();
                message="???????????????????????????";
                throw new RuntimeException(message);
            }

            Timestamp timestamp = new Timestamp(new Date().getTime());
            venuesEntity.setCreateTime(timestamp);
            venuesEntity.setLastModifyTime(timestamp);
            String loginNm = this.getLogin(token);
            venuesEntity.setCreator(loginNm);
            venuesEntity.setLastModifier(loginNm);
            venuesEntity.setVenuesStatus(ParamCode.VENUES_STATUS_01.getCode());
            int venuesId = rmVenuesInfoMapper.add(venuesEntity);

            //??????????????????
            RecordEntity en=new RecordEntity();
            en.setOperateContent("????????????");
            en.setOperator(loginNm);
            en.setOperateDetail("????????????:?????????"+venuesEntity.getVenuesName()+"???????????????"+venuesEntity.getVenuesAddres()+"???");
            en.setOperateTime( new Date());
            en.setOperateRef(String.valueOf(venuesEntity.getVenuesId()));
            en.setOperateType("01");
            userLogsInfoMapper.add(en);

            code=ResultCode.SUCCESS.getCode();
            message="???????????????????????????";
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e){
            message="??????????????????,?????????????????????";
            e.printStackTrace();
        }
        RespPageBean bean=new RespPageBean(code,message);
        return bean;
    }

    @Override
    public RespPageBean update(VenuesEntity venuesEntity) {
        long code= ResultCode.SUCCESS.getCode();
        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(new Date().getTime());
            venuesEntity.setLastModifyTime(timestamp);
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String nbr = request.getHeader("login-name");
            venuesEntity.setLastModifier(nbr);
            rmVenuesInfoMapper.update(venuesEntity);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        return new RespPageBean(code);
    }

    @Override
    public int delete(int venuesId) {
        return rmVenuesInfoMapper.delete(venuesId);
    }

    @Override
    public VenuesEntity getByResponsiblePerson(String responsiblePerson) {
        return rmVenuesInfoMapper.getByResponsiblePerson(responsiblePerson
        );
    }

    @Override
    public RespPageBean querySelect(String search,String town) {
        long code=ResultCode.FAILED.getCode();
        String message="??????????????????(?????????)";
        List<VenuesEntity>  dataList=new ArrayList<>();
        try {
            dataList=rmVenuesInfoMapper.querySelect(search,town);
            if(null==dataList || dataList.size()<1){
                throw new RuntimeException("??????????????????????????????????????????????????????");
            }
            code=ResultCode.SUCCESS.getCode();
            message="????????????????????????(?????????)";
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
    public VenuesEntity getVenueByID(String venuesId) {
        return rmVenuesInfoMapper.getVenueByID(venuesId);
    }

    @Override
    public PageResponse getAllNum(String token) {
        long code=ResultCode.FAILED.getCode();
        String message="??????????????????";
        List<Map<String, Object>> list=new ArrayList<>();
        try {
            ParamsVo auth = this.getAuth(token);
            Map<String, Object> allNum = rmVenuesInfoMapper.getAllNum(auth);
            list.add(allNum);
            code=ResultCode.SUCCESS.getCode();
            message="????????????????????????";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="???????????????????????????";
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
    }

    @Override
    public RespPageBean getVenuesByPage(Integer page, Integer size, String venuesName, String responsiblePerson, String religiousSect,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="PC??????????????????";
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
                throw new RuntimeException("????????????????????????????????????");
            }
            VenuesEntity ve=new VenuesEntity();
            ve.setResponsiblePerson(responsiblePerson);
            ve.setVenuesName(venuesName);
            ve.setReligiousSect(religiousSect);
            ve.setArea(area);
            ve.setTown(town);
            ve.setVenuesAddres(relVenuesId);
            if(null!=relVenuesId && !relVenuesId.isEmpty()){
                relVenuesArr=relVenuesId.split(",");
            }
            dataList=rmVenuesInfoMapper.getVenuesByPage(page,size,ve,relVenuesArr);
            total=rmVenuesInfoMapper.getTotal(ve,relVenuesArr);
            code= ResultCode.SUCCESS.getCode();
            message="PC???????????????????????????";
        } catch (RuntimeException r) {
            message=r.getMessage();
        }catch (Exception e) {
            message="PC???????????????????????????";
            e.printStackTrace();
        }
        return new RespPageBean(code,message,total,dataList.toArray());
    }

    @Override
    public AppResponse queryVenues(String search) {
        long code=ResultCode.FAILED.getCode();
        String message="app????????????";
        List<Map<String, Object>> venuesList =null;
        try {
            venuesList = rmVenuesInfoMapper.queryVenues(search);
            code= ResultCode.SUCCESS.getCode();
            message="?????????????????????????????????";
        } catch (RuntimeException r){
            message=r.getMessage();
        } catch (Exception e) {
            message="?????????????????????????????????";
            e.printStackTrace();
        }

        return new AppResponse(code,message,venuesList.toArray());
    }

    @Override
    public AppDetailRes getMapVenuesDetail(String venuesId) {
        long code=ResultCode.FAILED.getCode();
        String message="?????????????????????????????????";
        Map<String, Object> venuesMap =new HashMap<>();
        try {
            venuesMap = rmVenuesInfoMapper.getVenuesByID(venuesId);

            if(null!=venuesMap && venuesMap.size()>0){
                //??????????????????
                String picturesPath= (String) venuesMap.get("picturesPath");
                Object[] file ={};
                if(!picturesPath.isEmpty()){
                    //??????????????????
                    String[] split = picturesPath.split(",");
                    List<Map<String,Object>> imgMap=rmFileMapper.getPath(split);
                    if(null!=imgMap && imgMap.size()>0){
                        file = imgMap.toArray();
                    }
                }
                venuesMap.put("images",file);

                //????????????
                //String relVenuesId= (String) venuesMap.get("relVenuesId");
                List<Map<String, Object>> joinActivity = rmVenuesInfoMapper.getFiling(venuesId);
                venuesMap.put("joinActivity",joinActivity.toArray());

                //????????????
                List<Map<String, Object>> users = rmVenuesInfoMapper.getUsers(venuesId);
                venuesMap.put("garrison",users.toArray());

                //????????????
                List<Map<String, Object>> ganUsers = rmVenuesInfoMapper.getGanUsers(venuesId);
                venuesMap.put("janGan",ganUsers.toArray());

                //????????????
                Map<String, Object> oneStaffDirector=new HashMap<>();
                Map<String, Object> twoStaffDirector=new HashMap<>();
                Map<String, Object> refStaffDirector=new HashMap<>();
                List<Map<String, Object>> staffs = rmVenuesInfoMapper.getStaffs(venuesId);
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
                venuesMap.put("refStaffDirector",refStaffDirector);

                //?????????
                Map<String, Object> oneDirector=new HashMap<>();
                String resId= (String) venuesMap.get("resId");
                String resNm= (String) venuesMap.get("resNm");
                String resMo= (String) venuesMap.get("resMo");
                oneDirector.put("id",resId);
                oneDirector.put("name",resNm);
                oneDirector.put("phone",resMo);
                Map<String, Object> twoDirector=new HashMap<>();
                String groId= (String) venuesMap.get("groId");
                String groNm= (String) venuesMap.get("groNm");
                String groMo= (String) venuesMap.get("groMo");
                twoDirector.put("id",groId);
                twoDirector.put("name",groNm);
                twoDirector.put("phone",groMo);
                Map<String, Object> workDirector=new HashMap<>();
                String liaId= (String) venuesMap.get("liaId");
                String liaNm= (String) venuesMap.get("liaNm");
                String liaMo= (String) venuesMap.get("liaMo");
                workDirector.put("id",liaId);
                workDirector.put("name",liaNm);
                workDirector.put("phone",liaMo);

                venuesMap.put("oneDirector",oneDirector);
                venuesMap.put("twoDirector",twoDirector);
                venuesMap.put("workDirector",workDirector);

            }else{
                message="????????????????????????????????????";
                throw  new RuntimeException(message);
            }
            code= ResultCode.SUCCESS.getCode();
            message="?????????????????????????????????";
        } catch (RuntimeException r ){
            r.printStackTrace();
        } catch(Exception e) {
            message="????????????????????????????????????";
            e.printStackTrace();
        }

        return new AppDetailRes(code,message, venuesMap);
    }

    @Override
    public AppResponse getMapVenues(String search, String religiousSect) {
        long code= 0;
        String message= null;
        List<Map<String, Object>> mapVenues = null;
        try {
            code = ResultCode.FAILED.getCode();
            message = "?????????????????????????????????";
            mapVenues = rmVenuesInfoMapper.getMapVenues(search, religiousSect);
            if(mapVenues!=null && mapVenues.size()>0){
                code= ResultCode.SUCCESS.getCode();
                message="?????????????????????????????????";
            }
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            code = ResultCode.FAILED.getCode();
            message = "?????????????????????????????????";
            e.printStackTrace();
        }
        return new AppResponse(code,message,mapVenues.toArray());
    }

    @Override
    public AppResponse queryVenuesJz(String token, String search) {

        long code= ResultCode.FAILED.getCode();
        String message= "?????????????????????????????????";
        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            String login = this.getLogin(token);
            mapList = rmVenuesInfoMapper.queryVenuesJz(login, search);
            if(null!=mapList && mapList.size()>0){
                for(int i=0;i<mapList.size();i++){
                    Map<String, Object> map = mapList.get(i);
                    //??????????????????
                    String picturesPath = (String) map.get("picturesPath");
                    List<Map<String, Object>> path =new ArrayList<>();
                    if(null!=picturesPath && !picturesPath.isEmpty()){
                        path = rmFileMapper.getPath(picturesPath.split(","));
                    }
                    map.put("picturesPath",path.toArray());
                    //??????????????????
                    Integer venuesId = (Integer) map.get("venuesId");
                    String staffJz = rmVenuesInfoMapper.getStaffJz(String.valueOf(venuesId));
                    map.put("staffs",staffJz);
                }
            }

            code= ResultCode.SUCCESS.getCode();
            message="?????????????????????????????????";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message = "?????????????????????????????????";
            e.printStackTrace();
        }
        return new AppResponse(code,message,mapList.toArray());
    }

    /**
     * ???????????????
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("?????????????????????????????????");
        }
        return loginNm;
    }
    /**
     * ??????
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
            throw new RuntimeException("????????????????????????????????????");
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
