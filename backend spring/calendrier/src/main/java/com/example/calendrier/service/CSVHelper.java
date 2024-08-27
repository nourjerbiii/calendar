package com.example.calendrier.service;

import com.example.calendrier.entity.MembreEntreprise;
import org.apache.commons.csv.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERs = { "matricule", "name" , "lastname","role"};

    public static boolean hasCSVFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static List<MembreEntreprise> csvToMOCs(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<MembreEntreprise> MOCs = new ArrayList<MembreEntreprise>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                MembreEntreprise MOC;

                MOC = new MembreEntreprise(csvRecord.get("matricule"),csvRecord.get("nom"),csvRecord.get("prenom"),csvRecord.get("role"));

                MOCs.add(MOC);
            }
            return MOCs;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream MOCsToCSV(List<MembreEntreprise> MOCs) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (MembreEntreprise MOC : MOCs) {
                List<String> data = Arrays.asList(
                        MOC.getMatricule(),
                        MOC.getName(),
                        MOC.getLastName(),
                        MOC.getRole()
                );

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

}
