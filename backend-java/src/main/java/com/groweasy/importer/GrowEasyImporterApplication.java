package com.groweasy.importer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrowEasyImporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrowEasyImporterApplication.class, args);
        System.out.println("""
        ==========================================
        🚀 GrowEasy AI Importer Java Backend Ready
        ==========================================
        🌐 Server : http://localhost:5000
        ❤️ Health : http://localhost:5000/api/health
        ==========================================
        """);
    }
}
