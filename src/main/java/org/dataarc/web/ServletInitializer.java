package org.dataarc.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.hibernate.FlushMode;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import net.sf.ehcache.constructs.web.ShutdownListener;

public class ServletInitializer implements WebApplicationInitializer {

    public static final String ALL_PATHS = "/*";
    EnumSet<DispatcherType> allDispacherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);

    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext ctx = setupContext(container);
        container.addListener(RequestContextListener.class);

        container.addListener(ShutdownListener.class);

        addSitemeshFilterToServletContext(container);
        ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(ctx));
        setupOpenSessionInView(container, servlet);
    }

    private void setupOpenSessionInView(ServletContext container, ServletRegistration.Dynamic servlet) {
        Dynamic addFilter = container.addFilter("openEntityMangerInView", OpenEntityManagerInViewFilter.class);
        addFilter.setInitParameter("flushMode", FlushMode.MANUAL.name());
        addFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, ALL_PATHS);
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }

    private AnnotationConfigWebApplicationContext setupContext(ServletContext container) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(DataArcWebConfig.class);
        ctx.getEnvironment().setActiveProfiles("mongo");
        ctx.setServletContext(container);
        container.addListener(new ContextLoaderListener(ctx));
        return ctx;
    }

    private void addSitemeshFilterToServletContext(ServletContext servletContext) {
        FilterRegistration.Dynamic sitemesh = servletContext.addFilter("sitemesh", new ConfigurableSiteMeshFilter());
        EnumSet<DispatcherType> sitemeshDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        sitemesh.addMappingForUrlPatterns(sitemeshDispatcherTypes, true, "/*");
    }

}
