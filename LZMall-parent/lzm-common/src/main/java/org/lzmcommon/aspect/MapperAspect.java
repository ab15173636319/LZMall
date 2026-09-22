package org.lzmcommon.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.lzmcommon.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


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
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = pjp.proceed();
            long cost = System.currentTimeMillis() - startTime;
            logger.info("方法 {} 执行成功，耗时 {} ms", methodName, cost);
        } catch (Throwable e) {
            long cost = System.currentTimeMillis() - startTime;
            if (e instanceof BusinessException) {
                logger.warn("方法 {} 执行业务错误，耗时 {} ms：{}", methodName, cost, e.getMessage());
            } else {
                logger.error("方法 {} 执行异常，耗时 {} ms", methodName, cost, e);
            }
            throw e;
        }
        return result;
    }

}
