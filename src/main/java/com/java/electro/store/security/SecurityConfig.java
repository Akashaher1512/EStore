package com.java.electro.store.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserDetailsService userDetailsService;


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
                 .anyRequest()
                 .authenticated()
                 .and()
                 .httpBasic();

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
