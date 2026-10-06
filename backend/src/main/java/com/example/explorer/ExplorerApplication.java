package com.example.explorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
// En ASP.NET Core sería Program.cs con builder.Build().Run()
// En Express sería app.listen(port)
@SpringBootApplication
@EnableAsync // Habilita @Async para el listener de eventos (sin esto, @Async se ignora silenciosamente)
public class ExplorerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExplorerApplication.class, args);
    }
}
