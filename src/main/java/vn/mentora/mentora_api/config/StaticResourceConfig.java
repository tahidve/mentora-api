package vn.mentora.mentora_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload-file.base-uri}")
    private String baseUri;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map tất cả request bắt đầu bằng /uploads/vào thư mục chứa ảnh
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(baseUri);
    }
}
