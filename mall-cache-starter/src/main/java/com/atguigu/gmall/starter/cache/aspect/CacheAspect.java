package com.atguigu.gmall.starter.cache.aspect;

import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-07-01
 */
@Component
@Aspect
@Slf4j
public class CacheAspect {

    @Autowired
    CacheService cacheService;
    @Autowired
    RedissonClient redissonClient;

    SpelExpressionParser parser = new SpelExpressionParser(); //表达式解析器是线程安全的


    @Around("@annotation(com.atguigu.gmall.starter.cache.annotation.Cache)")  //执行哪些方法才切入
    public Object cacheapsectAround(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();  //当时目标方法被调用时传入的值
        Object retVal = null;
        try {
        String cacheKey = calculateCacheKey(pjp);
            Object cacheData = cacheService.getData(cacheKey, new TypeReference<Object>() {
                @Override
                public Type getType() {
                    MethodSignature signature = (MethodSignature) pjp.getSignature();
                    //当前方法带泛型的返回值类型
                    return signature.getMethod().getGenericReturnType();
                }
            });
        if (cacheData!=null){
            return cacheData;
        }

        //缓存没有
            Cache cache = getCacheAnnotation(pjp, Cache.class);
            if(StringUtils.isEmpty(cache.bloomName())){
                //不使用布隆
                return getDataWithLock(pjp, args, cacheKey);
            }else {
//使用布隆
                //4.1、拿到布隆，问有没有
                RBloomFilter<Object> filter = redissonClient.getBloomFilter(cache.bloomName());
                //4.2、获取布隆判定用的值。
                Object bloomIfValue = getBloomIfValue(pjp);
                if (filter.contains(bloomIfValue)) {
                    //4.2、布隆说有
                    //4.2.1、防止击穿，加锁
                    return getDataWithLock(pjp, args, cacheKey);
                }else {
                    //4.3、布隆说没有
                    return null;
                }
            }
    } catch (Throwable e) {
        //异常通知
        log.info("切面炸了...{}",e);
        throw new RuntimeException(e); //异常继续抛出去
    }finally {
        //后置通知
    }
    }

    private Object getBloomIfValue(ProceedingJoinPoint pjp) {

        Cache cache = getCacheAnnotation(pjp, Cache.class);
        //得到布隆判定表达式
        String bloomIfExpr = cache.bloomIf();

        //得到最终的值
        Object expression = calculateExpression(bloomIfExpr, pjp);
        return expression;
    }

    private Object getDataWithLock(ProceedingJoinPoint pjp, Object[] args, String cacheKey) throws Throwable {
        Object retVal;
        String lockKey = RedisConst.LOCK_PREFIX + cacheKey;
        //cacheKey: lock:sku:info:49   lock:categorys
        RLock lock = redissonClient.getLock(lockKey);
        //4.2.2、 加锁
        boolean tryLock = lock.tryLock();  //有自动解锁逻辑
        if(tryLock){
            //4.2.3、加锁成功，回源查数据
            retVal = pjp.proceed(args);//执行目标方法，还能自动续期
            //4.2.4、放缓存
            Cache cache = getCacheAnnotation(pjp, Cache.class);
            cacheService.saveData(cacheKey,retVal,cache.ttl(),TimeUnit.MILLISECONDS);
            //4.2.5、解锁&返回
            lock.unlock();
            return retVal;
        }

        //4.2.6 没得到锁
        TimeUnit.MILLISECONDS.sleep(500);
        //4.2.7 直接查缓存结束即可
        return cacheService.getData(cacheKey, SkuDetailVo.class);
    }

    /**
     * 计算缓存用的key；
     * @param pjp
     * @return
     */
    private String calculateCacheKey(ProceedingJoinPoint pjp) {
        //1、拿到目标方法上的 @Cache
        Cache cache = getCacheAnnotation(pjp,Cache.class);
        //4、得到key值。
        String key = cache.key();  //未来这个是一个表达式。可以被动态计算的。

        //5、计算表达式，得到最终的值
        String spElValue = calculateExpression(key,pjp).toString();

        return spElValue;

    }

    //获取一个注解
    private <T extends Annotation> T getCacheAnnotation(ProceedingJoinPoint pjp, Class<T> tClass) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        //2、拿到目标方法
        Method method = signature.getMethod();
        //3、获取方法上标注的注解
        T cache = method.getDeclaredAnnotation(tClass);
        return cache;
    }


    /**
     * 计算SpEL
     * @param expressionStr
     * @param pjp
     * @return
     */
    private Object calculateExpression(String expressionStr, ProceedingJoinPoint pjp) {

        //1、得到一个表达式
        Expression expression = parser.parseExpression(expressionStr, new TemplateParserContext());

        //2、准备一个计算上下文
        EvaluationContext context = new StandardEvaluationContext();
        //支持的所有语法【动态扩展所有支持的属性】
        context.setVariable("params",pjp.getArgs()); //所有的参数列表
        context.setVariable("currentDate", DateUtil.formatDate(new Date()));
        context.setVariable("redisson",redissonClient);  //指向一个组件，可以无限调方法
        //redis  #{#redis.get('hello')}
        //3、获取表达式的值
        Object expressionValue = expression.getValue(context, Object.class);

        return expressionValue;
    }
}
