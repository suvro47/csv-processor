package com.dsi.ieims.csv.processor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {

    @Autowired
    private CsvFileProcessor csvFileProcessor;

    String source = "/storage/inputcsv"; // container path
    String destination = "/storage/outputcsv"; // container path

    int chunkSize = Integer.parseInt(System.getenv("CHUNK_SIZE_INTEND_TO_PROCESS"));

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override public void run(String... args) throws Exception {
        csvFileProcessor.process(source, destination, chunkSize);
    }
}
