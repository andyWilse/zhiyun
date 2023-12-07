package com.religion.zhiyun.login.shiro;


import com.religion.zhiyun.login.token.AuthFilter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Filter;


@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean=new ShiroFilterFactoryBean();

        //关联 DefaultWebSecurityManager
        shiroFilterFactoryBean.setSecurityManager(this.defaultWebSecurityManager(this.authRealm()));
        //shiroFilterFactoryBean.setLoginUrl("/loginIndex");
        //添加shiro内置的过滤器
        /*
        * anon: 无需认证就可以访问
        * authc: 必须认证才能访问
        * user: 必须拥有记住我功能才能用
        * perms: 拥有对某个资源的权限才能访问
        * role: 拥有某个角色权限才能访问
        * */

        //要拦截的路径放在map里面
        Map<String,String> filterMap=new LinkedHashMap<String,String>();
        filterMap.put("/login","anon");  //放行login接口
        filterMap.put("/logout","anon");    //放行logout接口
        filterMap.put("/app/sendVerifyCode","anon");  //放行sendVerifyCode
        filterMap.put("/app/loginIn","anon");  //放行loginIn
        filterMap.put("/app/updatePassword","anon");  //放行updatePassword

        filterMap.put("/file/images/upload","anon");
        filterMap.put("/file/uploadVideo","anon");

        filterMap.put("/event/addEvent","anon");  //放行AI预警
        filterMap.put("/event/addEventByNB","anon");  //放行烟感预警
        filterMap.put("/huawei/callStatus","anon");//华为通话状态接收
        filterMap.put("/huawei/callFee","anon");//华为通话状态接收

        filterMap.put("/event/daPing/day","anon");
        filterMap.put("/event/daPing/gather","anon");
        filterMap.put("/monitor/getMoDaPing","anon");  //放行大屏监控
        filterMap.put("/venues/daPing/score","anon");
        filterMap.put("/play/sync","anon");
        //filterMap.put("/venues/find","perms[venues:get]");

        filterMap.put("/**","auth");    //拦截所有路径, 它自动会跑到 AuthFilter这个自定义的过滤器里面
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

        //添加过滤器
        Map<String,Filter> filters=new HashMap<>();
        filters.put("auth",new AuthFilter());
        shiroFilterFactoryBean.setFilters(filters);  //添加自定义的认证过滤器

        return shiroFilterFactoryBean;
    }

    //配置securityManager的实现类，变向的配置了securityManager
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(AuthRealm authRealm){
        DefaultWebSecurityManager defaultWebSecurityManager=new DefaultWebSecurityManager();

        //关联realm
        defaultWebSecurityManager.setRealm(authRealm);

        /*
         * 关闭shiro自带的session
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        defaultWebSecurityManager.setSubjectDAO(subjectDAO);
        return defaultWebSecurityManager;
    }

    //将自定义realm注入到 DefaultWebSecurityManager
    @Bean
    public AuthRealm authRealm(){
        return new AuthRealm();
    }


    //通过调用Initializable.init()和Destroyable.destroy()方法,从而去管理shiro bean生命周期
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    //开启shiro权限注解
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager defaultWebSecurityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(defaultWebSecurityManager);
        return advisor;
    }
}
