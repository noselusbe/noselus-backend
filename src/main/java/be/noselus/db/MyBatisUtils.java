package be.noselus.db;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyBatisUtils {

    private static SqlSessionFactory sqlSessionFactory;

    private static void setup(){
        DbConfig dbConfig = new DbConfig().invoke();
        String resource = "mybatis-config.xml";

        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Properties properties = new Properties();
        if (dbConfig.getUser() != null){
            properties.setProperty("username", dbConfig.getUser());
        }
        if (dbConfig.getPassword() != null){
            properties.setProperty("password", dbConfig.getPassword());
        }
        properties.setProperty("url", dbConfig.getUrl());

        String environment;
        if (dbConfig.getUser() == null ){
            environment = "development";
        } else {
            environment = "heroku";
        }

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, environment, properties);
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory == null){
            setup();
        }
        return sqlSessionFactory;
    }
}
