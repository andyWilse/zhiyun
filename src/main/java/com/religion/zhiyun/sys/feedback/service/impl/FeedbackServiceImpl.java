package com.religion.zhiyun.sys.feedback.service.impl;

import com.religion.zhiyun.sys.feedback.dao.FeedbackMapper;
import com.religion.zhiyun.sys.feedback.entity.FeedbackEntity;
import com.religion.zhiyun.sys.feedback.entity.FeedbackEnums;
import com.religion.zhiyun.sys.feedback.service.FeedbackService;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.ResultCode;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private FeedbackMapper feedbackMapper;
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;

    @Override
    public AppResponse feedbackUse(Map<String, Object> map, String token) {
        long code= ResultCode.ERROR.code();
        String message="使用反馈失败！";

        try {
            String feedbackContent = (String) map.get("feedbackContent");
            String feedbackPicture = (String) map.get("feedbackPicture");
            if(feedbackContent.isEmpty()){
                throw new RuntimeException("使用反馈:反馈内容不能为空！");
            }
            //保存
            FeedbackEntity vo=new FeedbackEntity();
            vo.setFeedbackContent(feedbackContent);
            vo.setFeedbackPicture(feedbackPicture);
            vo.setFeedbackType(FeedbackEnums.FEEDBACK_MY.getCode());
            vo.setFeedbackOperator(this.getLogin(token));
            vo.setFeedbackTime(TimeTool.getYmdHms());

            feedbackMapper.add(vo);

            code=ResultCode.SUCCESS.code();
            message="使用反馈成功!";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }

        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse axisUpdate(Map<String, Object> map, String token) {
        long code= ResultCode.ERROR.code();
        String message="坐标修改失败！";
        String latitudeLongitudeOrigin="";

        try {
            String feedbackContent = (String) map.get("feedbackContent");
            String feedbackPicture = (String) map.get("feedbackPicture");
            if(feedbackContent.isEmpty()){
                throw new RuntimeException("使用反馈:反馈内容不能为空！");
            }
            String relVenuesId = (String) map.get("relVenuesId");
            if(relVenuesId.isEmpty()){
                throw new RuntimeException("请选择场所！");
            }
            String mapPicture = (String) map.get("mapPicture");
            String latitudeLongitudeNew = (String) map.get("latitudeLongitudeNew");
            /*if(latitudeLongitudeNew.isEmpty()){
                throw new RuntimeException("请输入新的经纬度！");
            }*/
            //1.修改场所经纬度
            if(!GeneTool.isEmpty(latitudeLongitudeNew)){
                VenuesEntity venue = rmVenuesInfoMapper.getVenueByID(relVenuesId);
                if(null==venue){
                    throw new RuntimeException("场所信息丢失，请联系管理员！");
                }
                String longitude = venue.getLongitude();
                String latitude = venue.getLatitude();
                latitudeLongitudeOrigin=longitude+","+latitude;
                String[] split = latitudeLongitudeNew.split(",");
                if(split.length>1&& split!=null){
                    if(!split[0].isEmpty()){
                        longitude=split[0];
                    }else{
                        throw new RuntimeException("经度数据丢失，请确认数据后重新提交！");
                    }
                    if(!split[1].isEmpty()){
                        latitude=split[1];
                    }else{
                        throw new RuntimeException("纬度数据丢失，请确认数据后重新提交！");
                    }
                }else{
                    throw new RuntimeException("请按照格式（120.630091,27.999600）正确输入经纬度！");
                }
                rmVenuesInfoMapper.updateLngLat(longitude,latitude,Integer.parseInt(relVenuesId));
            }


            //2.保存反馈信息
            FeedbackEntity vo=new FeedbackEntity();
            vo.setFeedbackContent(feedbackContent);
            vo.setFeedbackPicture(feedbackPicture);
            vo.setRelVenuesId(Integer.parseInt(relVenuesId));
            vo.setMapPicture(mapPicture);
            vo.setLatitudeLongitudeNew(latitudeLongitudeNew);
            vo.setLatitudeLongitudeOrigin(latitudeLongitudeOrigin);
            vo.setFeedbackType(FeedbackEnums.AXIS_UPDATE.getCode());
            vo.setFeedbackOperator(this.getLogin(token));
            vo.setFeedbackTime(TimeTool.getYmdHms());

            feedbackMapper.add(vo);

            code=ResultCode.SUCCESS.code();
            message="坐标修改成功!";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }

        return new AppResponse(code,message);
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
}
