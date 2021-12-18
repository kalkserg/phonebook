package dao;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;

public enum Sources {
    INSTANCE;

    private DataSource dataSource;

    public DataSource dataSource() {
        if (dataSource == null) {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser("mbus");
            dataSource.setPassword("mbus");
            dataSource.setURL("jdbc:mysql://localhost:3306/mydb");
            this.dataSource = dataSource;
        }
        return dataSource;
    }
}
