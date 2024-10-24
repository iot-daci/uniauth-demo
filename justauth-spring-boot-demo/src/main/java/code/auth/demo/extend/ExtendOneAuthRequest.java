package code.auth.demo.extend;

import com.alibaba.fastjson.JSONObject;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;

/**
 * <p>
 * 测试用自定义扩展的第三方request
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019/10/9 14:19
 */
public class ExtendOneAuthRequest extends AuthDefaultRequest {

    public ExtendOneAuthRequest(AuthConfig config) {
        super(config, ExtendSource.ONEAUTH);
    }

    public ExtendOneAuthRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, ExtendSource.ONEAUTH, authStateCache);
    }

    /**
     * 获取access token
     *
     * @param authCallback 授权成功后的回调参数
     * @return token
     * @see AuthDefaultRequest#authorize()
     * @see AuthDefaultRequest#authorize(String)
     */
    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        String response = doPostAuthorizationCode(authCallback.getCode());
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        this.checkResponse(accessTokenObject);
        return AuthToken.builder()
                .accessToken(accessTokenObject.getString("access_token"))
                .refreshToken(accessTokenObject.getString("refresh_token"))
                .scope(accessTokenObject.getString("scope"))
                .tokenType(accessTokenObject.getString("token_type"))
                .expireIn(accessTokenObject.getIntValue("expires_in"))
                .build();
    }

    /**
     * 使用token换取用户信息
     *
     * @param authToken token信息
     * @return 用户信息
     * @see AuthDefaultRequest#getAccessToken(AuthCallback)
     */
    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String userInfo = doGetUserInfo(authToken);
        JSONObject object = JSONObject.parseObject(userInfo);
        this.checkResponse(object);
        return AuthUser.builder()
                .rawUserInfo(object)
                .uuid(object.getString("userId"))
                .username(object.getString("user"))
                .avatar(object.getString("avatarUrl"))
                .nickname(object.getString("displayName"))
                .company(object.getString("department"))
                .email(object.getString("email"))
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(source.toString())
                .build();
    }

    /**
     * 撤销授权
     *
     * @param authToken 登录成功后返回的Token信息
     * @return AuthResponse
     */
    @Override
    public AuthResponse revoke(AuthToken authToken) {
        return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).msg(AuthResponseStatus.SUCCESS.getMsg()).build();
    }

    /**
     * 刷新access token （续期）
     *
     * @param authToken 登录成功后返回的Token信息
     * @return AuthResponse
     */
    @Override
    public AuthResponse refresh(AuthToken authToken) {
        return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).data(AuthToken.builder().openId("openId").expireIn(1000).idToken("idToken").scope("scope").refreshToken("refreshToken").accessToken("accessToken").code("code").build()).build();
    }

    private void checkResponse(JSONObject object) {
        if (object.containsKey("error")) {
            throw new AuthException(object.getString("error_description"));
        }
    }
}
