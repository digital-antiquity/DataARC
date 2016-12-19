package org.dataarc.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.hibernate.FlushMode;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;

import net.sf.ehcache.constructs.web.ShutdownListener;

public class ServletInitializer implements WebApplicationInitializer {

    public static final String ALL_PATHS = "/*";
    EnumSet<DispatcherType> allDispacherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);

    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(DataArcWebConfig.class);
        ctx.getEnvironment().setActiveProfiles("mongo");
        ctx.setServletContext(container);
        container.addListener(new ContextLoaderListener(ctx));
        container.addListener(RequestContextListener.class);

        container.addListener(ShutdownListener.class);

        addSitemeshFilterToServletContext(container);
        ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(ctx));

        Dynamic addFilter = container.addFilter("openEntityMangerInView", OpenEntityManagerInViewFilter.class);
        addFilter.setInitParameter("flushMode", FlushMode.MANUAL.name());
        addFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, ALL_PATHS);
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }

    private void addSitemeshFilterToServletContext(ServletContext servletContext) {
        FilterRegistration.Dynamic sitemesh = servletContext.addFilter("sitemesh", new SiteMeshFilter());
        EnumSet<DispatcherType> sitemeshDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        sitemesh.addMappingForUrlPatterns(sitemeshDispatcherTypes, true, "/*");
    }

    // @Override
    // protected Class<?>[] getServletConfigClasses() {
    // // GolfingWebConfig defines beans that would be in golfing-servlet.xml
    // return new Class[] { DataArcWebConfig.class };
    // }
    //
    // @Override
    // protected Class<?>[] getRootConfigClasses() {
    // return new Class[] {DataArcConfiguration.class };
    // }
    //
    // @Override
    // protected String[] getServletMappings() {
    // return new String[] {"/*"};
    // }
}
