package org.univaq.swa.auleweb.aulewebrest.result;

import jakarta.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CSVReader {

    //Directory "relativa" del file di appoggio dove copiare le configurazioni dei gruppi fornite
    private static final String CSV_GRUPPI_FILE = "csv\\gruppi.csv";

    public List<List<String>> csv_gruppi(Part part, String path) throws IOException {

        List<List<String>> confGruppi = new ArrayList();

        //Unione della directory del contesto con la directory relativa --> PATH esatto del file
        String dir = path + CSV_GRUPPI_FILE;

        //Copio il contenuto del file di configurazione fornito dall'admin in gruppi.csv
        try ( InputStream is = part.getInputStream();  OutputStream os = new FileOutputStream(dir)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
        }

        try ( Reader reader = Files.newBufferedReader(Paths.get(dir));) {
            //Cambio del delimitatore
            CSVFormat csvFormat = CSVFormat.RFC4180.builder()
                    .setHeader(GruppiHeader.class).setDelimiter(';').build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            int i = 0;

            for (CSVRecord csvRecord : records) {
                confGruppi.add(new ArrayList());
                confGruppi.get(i).add(csvRecord.get("Edificio"));
                confGruppi.get(i).add(csvRecord.get("Luogo"));
                confGruppi.get(i).add(csvRecord.get("Piano"));
                confGruppi.get(i).add(csvRecord.get("Aula"));
                confGruppi.get(i).add(csvRecord.get("Gruppo"));
                i++;
            }
        }

        return confGruppi;
    }

}
