/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo;

import com.bitpay.demo.invoice.domain.InvoiceRepository;
import com.bitpay.demo.shared.logger.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.config.location=classpath:application-integrationtest.yaml"})
@SpringBootTest(classes = BitPayDemoApplication.class)
public abstract class AbstractIntegrationTest implements UnitTest {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected InvoiceRepository invoiceRepository;

    @MockitoBean
    private Logger logger;

    @BeforeEach
    public void cleanup() throws SQLException {

        final Connection connection = this.dataSource.getConnection();
        final Statement statement = connection.createStatement();

        disableFk(statement);
        truncateTables(statement);
        restartSequence(statement);
        enableFb(statement);

        statement.close();
        connection.close();
    }

    private void enableFb(final Statement statement) throws SQLException {
        statement.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void restartSequence(final Statement statement) throws SQLException {
        final ResultSet resultSet = statement.executeQuery(
            "SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'"
        );

        final Set<String> sequences = new HashSet<>();
        while (resultSet.next()) {
            sequences.add(resultSet.getString(1));
        }
        resultSet.close();
        for (final String seq : sequences) {
            statement.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
        }
    }

    private void truncateTables(final Statement statement) throws SQLException {
        final ResultSet resultSet = statement.executeQuery(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'"
        );
        final Set<String> tables = new HashSet<>();

        while (resultSet.next()) {
            tables.add(resultSet.getString(1));
        }

        resultSet.close();
        for (final String table : tables) {
            if ("DATABASECHANGELOG".equals(table) || "DATABASECHANGELOGLOCK".equals(table)) {
                continue;
            }

            statement.executeUpdate("TRUNCATE TABLE " + table);
        }
    }

    private void disableFk(final Statement statement) throws SQLException {
        statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
    }
}
