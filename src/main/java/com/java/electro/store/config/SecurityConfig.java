package com.java.electro.store.config;

import com.java.electro.store.security.JwtAuthenticationEntryPoint;
import com.java.electro.store.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;



    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // basic authentication
        http.csrf()
                 .disable()
                 .cors()
                 .disable()
                 .authorizeRequests()
                 .antMatchers("/auth/login")
                 .permitAll()
                 .antMatchers(HttpMethod.POST ,"/users")
                 .permitAll()
                 .antMatchers(HttpMethod.DELETE , "/users/**")
                 .hasRole("ADMIN")
                 .anyRequest()
                 .authenticated()
                 .and()
                 .exceptionHandling()
                 .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                 .and()
                 .sessionManagement()
                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationFilter , UsernamePasswordAuthenticationFilter.class);


       /*
        // form based authentication
       http.formLogin()
                .loginPage("login.html")
                .loginProcessingUrl("/login.do")
                .defaultSuccessUrl("/home.html")
                .failureForwardUrl("/error")
                .and()
                .logout()
                .logoutUrl("/logout")
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();
        */

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /*
    * interface UserDetails => it have the all methods related user so if we want to give ouer own user info then we need to convert it into UserDetails
    * interface UserDetailService => loadUserByUserName() => this authenticate user
    * */

   /* @Bean
    public UserDetailsService userDetailsService(){

        //  assigning value to org.springframework.security.core.userdetails.User class to create in memory authentication
        UserDetails normal = User.builder()
                .username("akash")
                .password(passwordEncoder().encode("akash"))
                .roles("NORMAL")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        // InMemoryUserDetailsManager => implementation class of userDetailService
        return new InMemoryUserDetailsManager(normal , admin);
    }
*/



}
