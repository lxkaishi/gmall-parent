package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
public class JSONs {

    static ObjectMapper mapper = new ObjectMapper();

    public static String toStr(Object o){
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
