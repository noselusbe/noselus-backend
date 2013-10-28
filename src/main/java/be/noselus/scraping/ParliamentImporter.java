package be.noselus.scraping;

import be.noselus.NosElusModule;
import be.noselus.db.DatabaseHelper;
import be.noselus.db.SqlRequester;
import be.noselus.repository.AssemblyRegistry;
import be.noselus.repository.PoliticianRepository;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ParliamentImporter {

    private static final Logger logger = LoggerFactory.getLogger(ParliamentImporter.class);

    public static final int WAIT_INTERVAL = 1500;
    public static final int FROM_ID = 50450;
    public static final int TO_ID = 50800;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Injector injector = Guice.createInjector(new NosElusModule());
        PoliticianRepository deputyRepository = injector.getInstance(PoliticianRepository.class);
        AssemblyRegistry assemblyRegistry = injector.getInstance(AssemblyRegistry.class);
        QuestionParser parser = new QuestionParser(deputyRepository, assemblyRegistry);

        String url = "http://parlement.wallonie.be/content/print_container.php?print=quest_rep_voir.php&type=all&id_doc=";


        try (Connection db = DatabaseHelper.getInstance().getConnection(true, false);) {
            for (int id = FROM_ID; id < TO_ID; id++) {
                try {
                    logger.debug("importing question " + id);
                    SqlRequester.insertOrUpdateQuestion(db, parser.parse(id));
                    Thread.sleep(WAIT_INTERVAL);
                } catch (IOException | IllegalArgumentException | InterruptedException | IndexOutOfBoundsException e) {
                    logger.error(url + id, e);
                }
            }
        }
    }
}
