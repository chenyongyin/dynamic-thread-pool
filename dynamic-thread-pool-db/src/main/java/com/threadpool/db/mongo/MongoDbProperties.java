package com.threadpool.db.mongo;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author cyy
 * @date 2021/04/15 11:06
 **/
@Data
public class MongoDbProperties {

    private String host;

    private String port;

    private String authenticationDatabase;

    private String username;

    private String password;

    private String database;

    private String uri;

    public String getUri(){
        String authStr = "";
        if(!StringUtils.isEmpty(authenticationDatabase)){
            authStr = "?authSource=admin"+authenticationDatabase;
        }
        return StringUtils.isEmpty(this.uri) ? String.format("mongodb://%s:%s@%s:%s/%s%s",username,password,host,port,database,authStr) : this.uri;
    }
}
