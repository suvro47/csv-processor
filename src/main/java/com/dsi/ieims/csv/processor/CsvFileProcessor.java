package com.dsi.ieims.csv.processor;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CsvFileProcessor {

    @Autowired
    private ViewTypeMapper viewTypeMapper;

    private String sourceDir = "/home/dsi/sourceDir";
    private String destinationDir = "/home/dsi/destinationDir";

    @Async
    public void process(String sourceFilePath, String destinationFilePath, int chunkSize) throws IOException {
        try {
            CSVReader reader = new CSVReader(new FileReader(sourceFilePath));
            CSVWriter writer = new CSVWriter(new FileWriter(destinationFilePath));
            Map<String, String> photoTypeMapper = viewTypeMapper.getViewTypeMapper();

            String[] nextLine;
            int totalRowCount = 0;
            List<String[]> chunk = new ArrayList<>(chunkSize);

            while ((nextLine = reader.readNext()) != null) {
                log.info("WAIT ! PROCESSING");
                totalRowCount++;

                String schoolId = nextLine[0];
                String srcViewType = nextLine[2];
                String fileName = nextLine[3];

                if (photoTypeMapper.containsKey(srcViewType)
                        && checkFileExistenceInSourceAndCopyToDestination(fileName, schoolId)) {
                    String[] output = new String[4];
                    output[0] = schoolId;
                    output[1] = photoTypeMapper.get(srcViewType);
                    output[2] = fileName;
                    chunk.add(output);
                }

                if (chunk.size() >= chunkSize) {
                    writeChunkAsync(chunk, writer);
                    chunk.clear();
                }
            }

            if (!chunk.isEmpty()) {
                writeChunkAsync(chunk, writer);
            }
            reader.close();
            writer.close();

            log.info("--------------------------------------------");
            log.info(totalRowCount + " ROW PROCESSED SUCCESSFULLY.");
            log.info("--------------------------------------------");

        } catch (IOException io) {
            io.printStackTrace();
            log.error("PROCESS TERMINATED WITH ERROR");
        }
    }

    private boolean checkFileExistenceInSourceAndCopyToDestination(String fileName, String schoolId) {
        String sourceAbsPath = sourceDir + File.separator + fileName;
        String destinationAbsPath = destinationDir + File.separator + schoolId;

        File sourceFile = new File(sourceAbsPath);
        if (sourceFile.exists()) {
            log.info("SOURCE FILE FOUND " + fileName);

            File destinationDir = new File(destinationAbsPath);
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }
            File destinationFile = new File(destinationAbsPath, fileName);
            try {
                Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("SOURCE FILE COPIED SUCCESSFULLY : " + fileName);

                return true;
            } catch (IOException e) {
                log.error("ERROR COPYING FILE : " + sourceAbsPath);
                e.printStackTrace();
                return false;
            }
        }
        log.info("SOURCE FILE NOT FOUND : " + fileName);
        return false;
    }

    @Async
    public void writeChunkAsync(List<String[]> chunk, CSVWriter writer) throws IOException {
        writer.writeAll(chunk);
    }

}
