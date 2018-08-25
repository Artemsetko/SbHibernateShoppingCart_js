package ua.artem.sbshoppingcart.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Если вы хотите полностью контролировать Spring MVC, вы можете добавить свою собственную @Configuration, аннотированную с @EnableWebMvc.
//Если вы хотите сохранить функции Spring Boot MVC и хотите добавить дополнительную конфигурацию MVC (перехватчики, форматировщики, контроллеры представлений и другие функции), вы можете добавить свой собственный класс @Configuration типа WebMvcConfigurer, но без @EnableWebMvc.
//@Configuration — эта аннотация и говорит о том, что данный класс является Java Configuration
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Load file: validation.properties
        messageSource.setBasename("classpath:validation");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}