package ca.yorku.eecs4314group12.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

/**
 * @Theme wires Vaadin 25 to the Lumo design system and loads our custom
 * styles from src/main/frontend/themes/absolute-cinema/styles.css
 */
@SpringBootApplication
@Theme("absolute-cinema")
public class UiServiceApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(UiServiceApplication.class, args);
    }

}