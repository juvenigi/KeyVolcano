package foo.bar.keyvolcano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;

@SpringBootApplication
public class KeyVolcanoMvnApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {});
        SpringApplication.run(KeyVolcanoMvnApplication.class, args);
    }
}
