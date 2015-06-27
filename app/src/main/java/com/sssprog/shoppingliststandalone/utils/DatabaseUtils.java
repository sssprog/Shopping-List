package com.sssprog.shoppingliststandalone.utils;

import com.j256.ormlite.misc.TransactionManager;
import com.sssprog.shoppingliststandalone.api.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.concurrent.Callable;

public class DatabaseUtils {

    public static <T> T callInTransaction(Callable<T> task) {
        try {
            return TransactionManager.callInTransaction(DatabaseHelper.getInstance().getConnectionSource(), task);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeWithRuntimeException(DatabaseTask task) {
        try {
            task.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface DatabaseTask {
        void execute() throws Exception;
    }

}
