package ua.artem.sbshoppingcart.config;

import ua.artem.sbshoppingcart.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@ComponentScan(basePackages = {
        "ua.artem.sbshoppingcart"
})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public UserDetailsServiceImpl userDetailsService;

    //Реализация PasswordEncoder, который использует сильную хеширующую функцию BCrypt.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    //AuthenticationManagerBuilder Позволяет легко создавать аутентификацию в памяти
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        // Setting Service to find User in the database.
        // And Setting PassswordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

    }

    //Override to configure how requests are
    //secured by interceptors.
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable(); //отключаем защиту от Межсайтовой подделки запроса (CSRF)

        // Requires login with role ROLE_EMPLOYEE or ROLE_MANAGER.
        // If not, it will redirect to /admin/login.
        http.authorizeRequests().antMatchers("/admin/orderList", "/admin/order", "/admin/accountInfo")//
                .access("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_MANAGER')");

        // Pages only for MANAGER
        http.authorizeRequests().antMatchers("/admin/product").access("hasRole('ROLE_MANAGER')");

        // When user login, role XX.
        // But access to the page requires the YY role,
        // An AccessDeniedException will be thrown.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

        // Configuration for Login Form.
        http.authorizeRequests().and().formLogin()//

                //
                .loginProcessingUrl("/j_spring_security_check") // Submit URL, указывает URL-адрес, с помощью которого проверяется имя пользователя и пароль.
                .loginPage("/admin/login")//страница логирования
                .defaultSuccessUrl("/admin/accountInfo")// если залогинились успешно, переходим сюда
                .failureUrl("/admin/login?error=true")//
                .usernameParameter("userName")//
                .passwordParameter("password")

                // Configuration for the Logout page.
                // (After logout, go to home page)
                .and().logout().logoutUrl("/admin/logout").logoutSuccessUrl("/");

    }
}