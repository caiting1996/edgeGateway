package com.example.edge;

import com.example.edge.client.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;

@SpringBootApplication
public class EdgeApplication {

    public static void main(String[] args) {

        SpringApplication.run(EdgeApplication.class, args);
    }


}
