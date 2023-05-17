package com.dsi.ieims.csv.processor;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CsvFileProcessor {

    @Autowired
    private ViewTypeMapper viewTypeMapper;

    private final String sourceDir = "/storage/srcdir";  // container dir
    private final String destinationDir = "/storage/desdir"; // container dir

    public void process(String sourceFilePath, String destinationFilePath, int chunkSize) throws IOException {
        try {
            CSVReader reader = new CSVReader(new FileReader(
                    sourceFilePath + File.separator + System.getenv("INPUT_CSV_FILE_NAME")));
            CSVWriter writer = new CSVWriter(new FileWriter(
                    destinationFilePath + File.separator + System.getenv("OUTPUT_CSV_FILE_NAME")));

            StopWatch stopwatch = new StopWatch();
            stopwatch.start();

            Iterator<String[]> iterator = reader.iterator();
            while (iterator.hasNext()) {
                List<String[]> chunk = getNextNRows(iterator, chunkSize);
                List<String[]> logs = copyItemsAndLogs(chunk);
                prepareCSV(logs, writer);
            }

            reader.close();
            writer.close();
            stopwatch.stop();

            log.info("Success! Total processing time : " + stopwatch.getTime());

        } catch (IOException io) {
            io.printStackTrace();
            log.error("CSV Read Write Process Terminated");
        }
    }

    public void prepareCSV(List<String[]> chunk, CSVWriter writer) throws IOException {
        writer.writeAll(chunk);
    }

    public List<String[]> copyItemsAndLogs(List<String[]> inputRows) {
        return inputRows.parallelStream().map(this::copySingleItemAndPrintLog)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public String[] copySingleItemAndPrintLog(String[] inputRows) {

        String schoolId = inputRows[0];
        String viewType = inputRows[2];
        String fileName = inputRows[3];

        Map<String, String> photoTypeMapper = viewTypeMapper.getViewTypeMapper();

        if (!photoTypeMapper.containsKey(viewType)) {
            log.info("Irrelevant View Type : " + fileName);
            return null;
        }

        String[] output = new String[3];
        String sourceAbsPath = sourceDir + File.separator + fileName;
        String destinationAbsPath = destinationDir + File.separator + schoolId;

        File sourceFile = new File(sourceAbsPath);
        if (sourceFile.exists()) {
            File destinationDir = new File(destinationAbsPath);
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }
            File destinationFile = new File(destinationAbsPath, fileName);
            try {
                Files.copy(sourceFile.toPath(), destinationFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                output[0] = schoolId;
                output[1] = photoTypeMapper.get(viewType);
                output[2] = fileName;

                log.info("Source file copied successfully : " + fileName);
                return output;

            } catch (IOException e) {
                log.error("Error occur while copying file : " + sourceAbsPath);
            }
        } else {
            log.info("Source file not found : " + fileName);
        }
        return null;
    }

    public List<String[]> getNextNRows(Iterator<String[]> iterator, int chunkSize) throws IOException {
        List<String[]> chunk = new ArrayList<>(chunkSize);

        while (iterator.hasNext() && chunk.size() < chunkSize) {
            String[] nextLine = iterator.next();
            chunk.add(nextLine);
        }
        return chunk;
    }
}
