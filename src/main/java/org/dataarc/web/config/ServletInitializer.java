package org.dataarc.web.config;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.dataarc.web.filter.ClearContentLengthFilter;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected void registerContextLoaderListener(ServletContext container) {
        super.registerContextLoaderListener(container);
        container.addListener(RequestContextListener.class);
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] { new OpenEntityManagerInViewFilter()};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { DataArcWebConfig.class, SecurityConfig.class };
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {

        // File uploadDirectory = ServiceConfiguration.CRM_STORAGE_UPLOADS_DIRECTORY;
        //
        // MultipartConfigElement multipartConfigElement =
        // new MultipartConfigElement(uploadDirectory.getAbsolutePath(),
        // maxUploadSizeInMb, maxUploadSizeInMb * 2, maxUploadSizeInMb / 2);
        //
        // registration.setMultipartConfig(multipartConfigElement);

    }

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        WebApplicationContext context = (WebApplicationContext) super.createRootApplicationContext();
        ((ConfigurableEnvironment) context.getEnvironment()).setActiveProfiles("mongo");
        return context;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/*" };
    }

}
