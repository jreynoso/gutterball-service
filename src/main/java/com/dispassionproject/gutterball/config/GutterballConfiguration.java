package com.dispassionproject.gutterball.config;

import io.jsondb.JsonDBTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class GutterballConfiguration {

    @Value("${json.db.file.dir}")
    private String jsonDbFilesDir;

    @Bean
    public JsonDBTemplate jsonDBTemplate() {
        return new JsonDBTemplate(jsonDbFilesDir, "com.dispassionproject.gutterball.api");
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        return filter;
    }

}
