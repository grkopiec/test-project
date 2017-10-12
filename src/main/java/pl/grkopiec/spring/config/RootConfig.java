package pl.grkopiec.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"pl.grkopiec.tcp", "pl.grkopiec.udp"})
public class RootConfig {
}