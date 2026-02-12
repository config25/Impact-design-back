package qtedu.Impact_design.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import qtedu.Impact_design.api.util.converter.StringToFileCategoryConverter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;

import java.nio.file.Paths;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserArgumentResolver userArgumentResolver;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    public WebConfig(UserArgumentResolver userArgumentResolver) {
        this.userArgumentResolver = userArgumentResolver;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToFileCategoryConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/static/docs/");

        String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        System.out.println(">> UserArgumentResolver registered");
        resolvers.add(userArgumentResolver); // ✅ 반드시 빈으로 주입된 resolver 사용
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // API 경로를 대상으로
                .allowedOrigins("http://localhost:3000")  // 허용할 출처 (프론트엔드 주소) -> 나중에 변동가능
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);  // 쿠키, 인증 헤더 허용 시 필요
    }

}