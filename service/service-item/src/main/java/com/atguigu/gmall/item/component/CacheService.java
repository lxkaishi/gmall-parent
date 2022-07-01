package com.atguigu.gmall.item.component;

import java.util.concurrent.TimeUnit;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-30
 */
public interface CacheService {
    <T> T  getData(String cacheKey, Class<T> t);

    <T> void saveData(String cacheKey, T detail, Long time, TimeUnit unit);
}
