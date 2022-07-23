package com.reggie.config;

import com.reggie.Interceptor.JwtTokenAdminInterceptor;
import com.reggie.Interceptor.JwtTokenUserInterceptor;
import com.reggie.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login", "/admin/employee/logout");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login");
    }

    /**
     * @return 配置swagger管理端接口文档对象
     */
    @Bean
    public Docket docket1() {
        log.info("准备生成接口文档...");

        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("瑞吉外卖项目接口文档")
                .version("2.0")
                .description("瑞吉外卖项目接口文档")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.reggie.controller.admin"))
                .paths(PathSelectors.any())
                .build();
    }


    /**
     * @return 配置swagger用户端接口文档对象
     */
    @Bean
    public Docket docket2() {
        log.info("准备生成接口文档...");

        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("瑞吉外卖项目接口文档")
                .version("2.0")
                .description("瑞吉外卖项目接口文档")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口")
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.reggie.controller.user"))
                .paths(PathSelectors.any())
                .build();
    }


    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展spring MVC的消息转换器
     *
     * @param converters 消息转换器
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("开启扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置消息转换器，将Java转化成JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将自己创建的消息转化器添加到容器中
        converters.add(0, messageConverter);
    }
}
