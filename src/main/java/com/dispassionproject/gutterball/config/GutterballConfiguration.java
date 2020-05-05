package com.dispassionproject.gutterball.config;

import io.jsondb.JsonDBTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GutterballConfiguration {

    @Value("${json.db.file.dir}")
    private String jsonDbFilesDir;

    @Bean
    public JsonDBTemplate jsonDBTemplate() {
        return new JsonDBTemplate(jsonDbFilesDir, "com.dispassionproject.gutterball.api");
    }

}
