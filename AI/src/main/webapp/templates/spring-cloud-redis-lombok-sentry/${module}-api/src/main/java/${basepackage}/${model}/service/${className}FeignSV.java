package ${basePackage}.user.service;

import ${basePackage}.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import ${basePackage}.user.service.UserInfoFeignSV.UserInfoFeignServiceImpl;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "pps-cloud-provider", fallback = UserInfoFeignServiceImpl.class)
public interface UserInfoFeignSV {

    @RequestMapping(value = "/userinfo/getById", method = RequestMethod.GET)
    UserInfo getById(@RequestParam("id") Integer id);

    @RequestMapping(value = "/userinfo/getByName", method = RequestMethod.GET)
    List<UserInfo> getByName(@RequestParam("userName") String userName);

    @RequestMapping(value = "/userinfo/getByRoleId", method = RequestMethod.GET)
    List<UserInfo> getByRoleId(@RequestParam("roleId") Integer roleId);

    @RequestMapping(value = "/userinfo/getByRoleName", method = RequestMethod.GET)
    List<UserInfo> getByRoleName(@RequestParam("roleName") String roleName);


    @Component
    class UserInfoFeignServiceImpl implements UserInfoFeignSV {

        public UserInfo getById(Integer id) {
            System.out.print("getById()-----------------------------");
            return null;
        }

        public List<UserInfo> getByName(String userName) {
            return null;
        }

        public List<UserInfo> getByRoleId(Integer roleId) {
            return null;
        }

        public List<UserInfo> getByRoleName(String roleName) {
            return null;
        }

    }


}
