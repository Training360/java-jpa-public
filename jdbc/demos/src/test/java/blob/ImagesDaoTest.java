package blob;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ImagesDaoTest {

    ImagesDao imagesDao;

    @BeforeEach
    void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        flyway.clean();
        flyway.migrate();

        imagesDao = new ImagesDao(dataSource);
    }

    @Test
    void saveImage() throws IOException {
        imagesDao.saveImage("training360.gif",
                ImagesDaoTest.class.getResourceAsStream("/training360.gif"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (InputStream is = imagesDao.getImageByName("training360.gif")) {
            is.transferTo(baos);
        }

        assertTrue(baos.size() > 3000, "File size");
    }
}
