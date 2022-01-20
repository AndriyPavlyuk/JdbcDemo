package com.bobocode.pool;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class CustomDataSource extends PGSimpleDataSource {
    private DataSource dataSource;
    private Queue<Connection> pool = new LinkedBlockingDeque<>();
    private int POOL_SIZE = 10;

    @SneakyThrows
    public CustomDataSource(String url, String username, String password)  {
        PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
        pgDataSource.setURL(url);
        pgDataSource.setUser(username);
        pgDataSource.setPassword(password);
        this.dataSource = pgDataSource;

        for (int i = 0; i < POOL_SIZE; i++) {
            Connection realConn = dataSource.getConnection();
            ConnectionProxy custom = new ConnectionProxy(realConn, pool);
            pool.add(custom);
        }
    }

    @Override
    public Connection getConnection() {
        return pool.poll();
    }
}
