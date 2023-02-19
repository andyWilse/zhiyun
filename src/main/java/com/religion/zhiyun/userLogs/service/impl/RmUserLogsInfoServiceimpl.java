package com.religion.zhiyun.userLogs.service.impl;

import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.userLogs.dao.RmUserLogsInfoMapper;
import com.religion.zhiyun.userLogs.entity.LogsEntity;
import com.religion.zhiyun.userLogs.service.RmUserLogsInfoService;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.base.HttpServletRequestReader;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@Service
public class RmUserLogsInfoServiceimpl implements RmUserLogsInfoService {
    @Autowired
    private RmUserLogsInfoMapper rmUserLogsInfoMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<LogsEntity> alllogs() {
        return rmUserLogsInfoMapper.alllogs();
    }

    @Override
    public void addlogs(LogsEntity logsEntity) {
        rmUserLogsInfoMapper.addlogs(logsEntity);
    }

    @Override
    public void updatelogs(LogsEntity logsEntity) {
        rmUserLogsInfoMapper.updatelogs(logsEntity);
    }

    @Override
    public void deletelogs(String logsId) {
        rmUserLogsInfoMapper.deletelogs(logsId);
    }

    @Override
    public RespPageBean getLogsByPage(Integer page, Integer size, String userName) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<VenuesEntity> dataList=rmUserLogsInfoMapper.getLogsByPage(page,size,userName);
        Object[] objects = dataList.toArray();
        Long total=rmUserLogsInfoMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }

/*    @Override
    public void savelogs(String response,String cnName) {
        LogsEntity logsEntity=new LogsEntity();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String nbr = request.getHeader("login-name");
        logsEntity.setUserNbr(nbr);
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        if(null!=sysUserEntity){
            logsEntity.setUserName(sysUserEntity.getLoginNm());
        }
        String servletPath = request.getServletPath();
        logsEntity.setInterfaceCode(servletPath);
        logsEntity.setInterfaceName(cnName);
        String body = HttpServletRequestReader.ReadAsChars(request);
        logsEntity.setRequestParam(body);
        logsEntity.setResponseResult(response);
        Timestamp timestamp = new Timestamp(new Date().getTime());
        logsEntity.setFkDate(timestamp);

        rmUserLogsInfoMapper.addlogs(logsEntity);
    }*/

    @Override
    public void savelogs(String response,String cnName) {//InfoEnums枚举，新增提示和操作
        //new一个日志对象
        LogsEntity logsEntity=new LogsEntity();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取该请求头的内容，为工号
        String nbr = request.getHeader("login-name");
        logsEntity.setUserNbr(nbr);
        //根据工号查询用户信息的对象
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        //对象不为空
        if(null!=sysUserEntity){
            //对象的登录名赋到日志对象的名字
            logsEntity.setUserName(sysUserEntity.getLoginNm());
        }
        //接口路径
        String servletPath = request.getServletPath();
        logsEntity.setInterfaceCode(servletPath);
        //接口名字
        logsEntity.setInterfaceName(cnName);
        //请求参数
        String body = HttpServletRequestReader.ReadAsChars(request);
        logsEntity.setRequestParam(body);
        //响应
        logsEntity.setResponseResult(response);
        //时间
        Timestamp timestamp = new Timestamp(new Date().getTime());
        logsEntity.setFkDate(timestamp);

        rmUserLogsInfoMapper.addlogs(logsEntity);
    }
}
