package com.dispassionproject.gutterball;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GutterballApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GutterballApplication.class);
        app.setBanner(new GutterballBanner());
        app.run(args);
    }

}
