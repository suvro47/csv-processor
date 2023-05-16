package com.dsi.ieims.csv.processor;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CsvFileProcessor {

    @Autowired
    private SchoolCodeSchoolIdMapper schoolCodeSchoolIdMapper;

    @Async
    public void process(String sourceFilePath, String destinationFilePath, int chunkSize) throws IOException {
        try {
            CSVReader reader = new CSVReader(new FileReader(sourceFilePath));
            CSVWriter writer = new CSVWriter(new FileWriter(destinationFilePath));
            Map<String, String> codeIdMap = schoolCodeSchoolIdMapper.getSchoolCodeSchoolIdMap();

            String[] nextLine;
            int totalRowCount = 0;
            List<String[]> chunk = new ArrayList<>(chunkSize);

            while ((nextLine = reader.readNext()) != null) {
                log.info("WAIT ! PROCESSING");
                totalRowCount++;

                String[] modifiedLine = new String[4];

                if (nextLine.length > 2) {
                    String srcSchoolCode = nextLine[2];

                    if (codeIdMap.containsKey(srcSchoolCode)) {
                        modifiedLine[0] = codeIdMap.get(srcSchoolCode);
                        modifiedLine[1] = srcSchoolCode;
                        if(nextLine.length > 8 ) modifiedLine[2] = nextLine[8];
                        if(nextLine.length > 9 ) modifiedLine[3] = nextLine[9];
                        chunk.add(modifiedLine);
                    }
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

            log.info("----------------------------------------------------");
            log.info("TOTAL: " + totalRowCount + " PROCESSED SUCCESSFULLY.");
            log.info("----------------------------------------------------");

        } catch (IOException io) {
            io.printStackTrace();
            log.error("PROCESS TERMINATED WITH ERROR");
        }
    }

    @Async
    public void writeChunkAsync(List<String[]> chunk, CSVWriter writer) throws IOException {
        writer.writeAll(chunk);
    }

}
