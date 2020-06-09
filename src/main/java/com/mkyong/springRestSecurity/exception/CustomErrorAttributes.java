package com.mkyong.springRestSecurity.exception;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
//To override the default JSON error response for all exceptions,
// create a bean and extends DefaultErrorAttributes
public class CustomErrorAttributes extends DefaultErrorAttributes {
    private static final DateFormat dataFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {

        // Let Spring handle the error first, we will modify later :)
        Map<String , Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

        //format & update timestamp
        Object timestamp = errorAttributes.get("timestamp");
        if (timestamp == null){
            errorAttributes.put("timestamp", dataFormat.format(new Date()));
        } else {
            errorAttributes.put("timestamp", dataFormat.format((Date) timestamp));
        }

        // insert new key
        errorAttributes.put("version", "1.2");

        return errorAttributes;
    }
}
