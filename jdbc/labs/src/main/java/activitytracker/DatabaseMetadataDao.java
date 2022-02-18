package activitytracker;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMetadataDao {

    private DataSource dataSource;

    public DatabaseMetadataDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> getColumnsForTable(String table) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            return loadColumnNames(meta, table);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot get metadata from database.", sqle);
        }
    }

    private List<String> loadColumnNames(DatabaseMetaData meta, String table) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        try (ResultSet rs = meta.getColumns(null, null, table, null)) {
            while (rs.next()) {
                columnNames.add(rs.getString(4));
            }
        }
        return columnNames;
    }
}
