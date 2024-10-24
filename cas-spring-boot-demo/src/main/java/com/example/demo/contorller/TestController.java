package com.example.demo.contorller;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class TestController {

    @GetMapping("test/index")
    public String index(HttpServletRequest request){
        String token =request.getParameter("token");
        System.out.println("token : "+token);
        Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);

        String username=     assertion.getPrincipal().getName();
        System.out.println(username);

        return "test1 index cas拦截正常,登录账号:"+username;
    }

    @GetMapping("test/index1")
    public String index1(HttpServletRequest request){
        String token =request.getParameter("token");
        System.out.println("token : "+token);
        Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);

        String username=     assertion.getPrincipal().getName();
        System.out.println(username);

        return "test index cas拦截正常,登录账号:"+username;
    }

    /**
     * 不走cas认证，无法获取登录信息
     * @param request
     * @return
     */
    @GetMapping("test/index2")
    public String index2(HttpServletRequest request){
//        String token =request.getParameter("token");
//        System.out.println("token : "+token);
//        Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
//
//        String username=     assertion.getPrincipal().getName();
//        System.out.println(username);

        return "cas 未拦截";
    }
}
