package com.macrotel.zippyworld_test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:///${user.home}/env/zippyworld_application_properties.properties")
public class ApplicationProperties {
}
