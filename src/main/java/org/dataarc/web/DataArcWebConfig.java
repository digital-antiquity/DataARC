package org.dataarc.web;

import org.dataarc.config.DataArcConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = { DataArcConfiguration.class})
public class DataArcWebConfig {

}
