package com.java.electro.store.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserDetailsService userDetailsService;
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        logger.info("Header : {} " ,requestHeader );
        // Bearer 38825cc429414434....
        String userName = null;
        String token = null;

        if(requestHeader != null && requestHeader.startsWith("Bearer")){
            token = requestHeader.substring(7);
            try{
                userName = jwtHelper.getUsernameFromToken(token);
            }catch (IllegalArgumentException e){
                logger.info("IllegalArgument Exception while fetching userName");
                e.printStackTrace();
            }catch (ExpiredJwtException e){
                logger.info("given Jwt token is expired ..!!");
                e.printStackTrace();
            }catch (MalformedJwtException e){
                logger.info("Some change has done in token !! Invalid token");
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            logger.info("Invalid header value !!");
        }

        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // fetch user detail from user name
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            Boolean validateToken = jwtHelper.validateToken(token, userDetails);

            if(validateToken){
                // set authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else {
                // validation failed
                logger.info("Validation fails");
            }
        }
        filterChain.doFilter(request ,response);
    }
}
