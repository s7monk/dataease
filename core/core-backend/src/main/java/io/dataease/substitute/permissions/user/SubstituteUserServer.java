package io.dataease.substitute.permissions.user;


import cn.dev33.satoken.stp.StpUtil;
import io.dataease.api.permissions.user.vo.UserFormVO;
import io.dataease.data.vo.UserVO;
import io.dataease.service.UserService;
import io.dataease.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnMissingBean(name = "loginServer")
@RestController
@RequestMapping("/user")
public class SubstituteUserServer {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        int loginIdAsInt = StpUtil.getLoginIdAsInt();
        UserVO user = userService.getUserById(loginIdAsInt);
        result.put("id", loginIdAsInt);
        result.put("name", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("oid", "1");
        result.put("language", "zh-CN");
        return result;
    }
    @GetMapping("/personInfo")
    public UserFormVO personInfo() {
        UserFormVO userFormVO = new UserFormVO();
        userFormVO.setId(1L);
        userFormVO.setAccount("admin");
        userFormVO.setName("管理员");
        userFormVO.setIp(IPUtils.get());
        // 当前模式为无XPack
        userFormVO.setModel("lose");
        return userFormVO;
    }
}
