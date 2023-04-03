package shop.mtcoding.hiberpc;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Map;

@SpringBootApplication
public class HiberpcApplication {

    public static void main(String[] args) {


        ConfigurableApplicationContext context = SpringApplication.run(HiberpcApplication.class, args);
        Map<String, Object> repositories = context.getBeansWithAnnotation(Repository.class);
        for (String key : repositories.keySet()) {
            System.out.println(key + " : " + repositories.get(key));
        }

    }

}
