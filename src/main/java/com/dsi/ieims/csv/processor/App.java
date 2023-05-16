package com.dsi.ieims.csv.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class App implements CommandLineRunner {

    @Autowired
    private CsvFileProcessor csvFileProcessor;

    String source = System.getenv("INPUT_CSV_PATH");
    String destination = System.getenv("OUTPUT_CSV_PATH");

    public static void main(String[] args) {

        SpringApplication.run(App.class, args);

        log.info("**********************************");
        log.info("CSV processor started successfully");
        log.info("**********************************");
    }

    @Override public void run(String... args) throws Exception {
        csvFileProcessor.process(source, destination, 5);
    }
}
