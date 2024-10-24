package code.auth.demo;

import com.alibaba.fastjson.JSON;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.stereotype.Component;

/**
 * 自己实现
 *
 * @author : qihang.liu
 * @date 2023-04-07
 */
@Component
public class LocalUserDetailProvider {
    public boolean isUserExist(String userName) {
        // TODO： 判断用户是否存在
        return true;
    }

    public void saveUser(AuthUser user) {
        // TODO: 保存用户到本地
        System.out.println("save user " + JSON.toJSONString(user));
    }

    public String buildToken(String userName) {
        // 生成本地账户的token
        return "token";
    }
}
