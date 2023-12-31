package org.univaq.swa.auleweb.aulewebrest.result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CSVWriter {

    //Directory "relativa" del file in cui caricare la configurazione dei gruppi
    private static final String CSV_GRUPPI_FILE = "csv\\gruppi.csv";

    public void csv_gruppi(List<List<String>> confs, String path) throws IOException {
        //Unione della directory del contesto con la directory relativa --> PATH esatto del file
        String dir = path + CSV_GRUPPI_FILE;

        try ( BufferedWriter writer = Files.newBufferedWriter(Paths.get(dir));) {

            //Cambio del delimitatore
            CSVFormat csvFormat = CSVFormat.RFC4180.builder().
                    setDelimiter(';').setHeader(GruppiHeader.class).build();

            try ( CSVPrinter csvPrinter = new CSVPrinter(
                    writer, csvFormat
            );) {
                for (List<String> conf : confs) {
                    csvPrinter.printRecord(conf.get(0),
                            conf.get(1),
                            conf.get(2),
                            conf.get(3),
                            conf.get(4)
                    );
                }
                csvPrinter.flush();
            }

        }
    }

}
