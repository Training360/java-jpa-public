package metadata;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class MetaDataDaoTest {

    private MetaDataDao metaDataDao;

    @Before
    public void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);

        flyway.clean();
        flyway.migrate();

        metaDataDao = new MetaDataDao(dataSource);
    }

    @Test
    public void testTableNames() {
        List<String> names = metaDataDao.getTableNames();
        System.out.println(names);

        assertTrue("Contains employees table", names.contains("employees"));
    }
}
