package code.auth.demo.extend;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 根据bean name 获取bean
     * @param beanName
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName) {
        if(applicationContext.containsBean(beanName)){
            return (T) applicationContext.getBean(beanName);
        }else{
            return null;
        }
    }

    /**
     * 根据bean type 获取bean
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T> T getBeanOfType(Class<T> beanType) {
        return applicationContext.getBean(beanType);
    }

    /**
     * 根据bean type 获取bean map
     *
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> beanType) {
        return applicationContext.getBeansOfType(beanType);
    }
}