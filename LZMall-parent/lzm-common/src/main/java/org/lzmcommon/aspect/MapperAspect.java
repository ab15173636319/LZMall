package org.lzmcommon.aspect;

import cn.hutool.json.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.lzmcommon.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
public class MapperAspect {
    private static final Logger logger = LoggerFactory.getLogger(MapperAspect.class);

    // 切面：项目所有模块*impl实现包
    // * org表示项目所有模块的impl实现包下的所有类
    // lzm*表示项目所有模块的impl实现包下的所有类
    @Pointcut("execution(* org.lzm*..impl.*.*(..))")
    public void mapperImplLogger() {
    }

    @Around("mapperImplLogger()")
    public Object objectMapper(ProceedingJoinPoint pjp) throws Throwable {
        // 获取当前方法签名
        Signature signature = pjp.getSignature();
        // 转换为方法签名
        MethodSignature ms = (MethodSignature) signature;
        // 获取当前方法
        Method method = ms.getMethod();
        Object result = null;
        try {
            result = pjp.proceed();
            logger.info("方法 {} 执行成功", method.getName());
        } catch (Throwable e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            logger.error("方法 {} 执行异常：{}", method.getName(), e.getMessage());
        }
        return result;
    }

}
