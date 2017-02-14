package org.dataarc.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class ClearContentLengthFilter extends OncePerRequestFilter {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response) {
            @Override
            public void setContentLength(int len) {
                logger.debug("pseudo setting length:" + len);
                // TODO Auto-generated method stub
//                super.setContentLength(len);
            }
            
        };
        filterChain.doFilter(request, wrapper);
    }
}