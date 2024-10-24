package code.auth.demo;

import com.alibaba.fastjson.JSON;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 测试 Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-07-22 11:17
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class Oauth2Controller {
    private final AuthRequestFactory factory;

    @Resource
    private LocalUserDetailProvider localUserDetailProvider;

    @GetMapping
    public List<String> list() {
        return factory.oauthList();
    }

    /**
     * oauth2登录入口
     *
     * @param type
     * @param response
     * @throws IOException
     */
    @GetMapping("/login/{type}")
    public void login(@PathVariable String type, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get(type);
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    /**
     * 授权码回调地址
     *
     * @param type
     * @param callback
     * @param servletResponse
     */
    @RequestMapping("/{type}/callback")
    public void login(@PathVariable String type, AuthCallback callback, HttpServletResponse servletResponse) {
        AuthRequest authRequest = factory.get(type);
        AuthResponse<AuthUser> response = authRequest.login(callback);

        log.info("【response】= {}", JSON.toJSONString(response));

        // response 返回了用户信息
        /**
         * "uuid": "1",
         * "username": "admin",
         * "nickname": "系统管理员",
         * "avatar": null,
         * "blog": null,
         * "company": "科技部",
         * "location": null,
         * "email": "shimingxy@qq.com",
         * "remark": null,
         * "gender": "UNKNOWN",
         * "source": "ONEAUTH",
         */


        // 统一登录平台用户和本地用户关联
        // 例如，根据username查询本地是否有同名的用户，如果没有，就自动创建一个同名的用户
        String userName = response.getData().getUsername();
        if (!localUserDetailProvider.isUserExist(userName)) {
            localUserDetailProvider.saveUser(response.getData());
        }

        // 生成token
        String token = localUserDetailProvider.buildToken(userName);

        // 把token返回给页面，例如可以通过cookie方式返回给前端
        // 前端页面从cookie里面获取token，后续的请求带上token，就可以了
        Cookie accessToken = new Cookie("ACCESS_TOKEN", token);
        accessToken.setPath("/");
        accessToken.setMaxAge(3600);

        servletResponse.addCookie(accessToken);
    }


}
