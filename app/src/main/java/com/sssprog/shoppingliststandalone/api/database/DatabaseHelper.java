package com.sssprog.shoppingliststandalone.api.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.sssprog.shoppingliststandalone.App;
import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.utils.LogHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = LogHelper.getTag(DatabaseHelper.class);
    private static final String DATABASE_NAME = "ShoppingList.db";
    private static final int DATABASE_VERSION = 4;

    private static final Set<Class<?>> DATA_CLASSES = new HashSet<>(Arrays.asList(new Class<?>[] {
            CategoryModel.class,
            ListModel.class,
            QuantityUnitModel.class,
            ItemModel.class
    }));

    private static DatabaseHelper instance;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper(App.getInstance());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        createDb();
    }

    private void createDb() {
        try {
            createTables();
            // Create default list
            ListModel list = new ListModel();
            list.setName(context.getString(R.string.default_list_name));
            getListDao().create(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables() throws SQLException {
        for (Class<?> dataClass : DATA_CLASSES) {
            TableUtils.createTable(connectionSource, dataClass);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        LogHelper.i(TAG, "onUpgrade " + oldVersion + " -> " + newVersion);

        if (oldVersion < 4) {
            for (Class<?> dataClass : DATA_CLASSES) {
                try {
                    TableUtils.dropTable(connectionSource, dataClass, true);
                } catch (SQLException e) {
                    LogHelper.printStackTrace(e);
                }
            }
            createDb();
        }

        autoUpgrade(db);
    }

    private void autoUpgrade(SQLiteDatabase db) {
        final Map<Class<?>, String> sqlTypes = new HashMap<Class<?>, String>();
        sqlTypes.put(byte.class, "INTEGER");
        sqlTypes.put(short.class, "INTEGER");
        sqlTypes.put(int.class, "INTEGER");
        sqlTypes.put(long.class, "INTEGER");
        sqlTypes.put(float.class, "REAL");
        sqlTypes.put(double.class, "REAL");
        sqlTypes.put(char.class, "TEXT");
        sqlTypes.put(boolean.class, "INTEGER");
        sqlTypes.put(Byte.class, "INTEGER");
        sqlTypes.put(Short.class, "INTEGER");
        sqlTypes.put(Integer.class, "INTEGER");
        sqlTypes.put(Long.class, "INTEGER");
        sqlTypes.put(Float.class, "REAL");
        sqlTypes.put(Double.class, "REAL");
        sqlTypes.put(Boolean.class, "INTEGER");
        sqlTypes.put(String.class, "TEXT");

        // Create new fields in existing tables
        for (Class<?> cls : DATA_CLASSES) {
            DatabaseTable a = cls.getAnnotation(DatabaseTable.class);
            String table = a.tableName();
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                if (fieldWithRightModifiers(field)) {
                    DatabaseField fa = field.getAnnotation(DatabaseField.class);
                    String name = fa.columnName();
                    String type = sqlTypes.get(field.getType());
                    String query = "ALTER TABLE " + table + " ADD COLUMN " + name + " " + type;
                    try {
                        db.execSQL(query);
                    } catch (Exception e) {
                        LogHelper.printStackTrace(e);
                    }
                }
            }
        }

        // Create new tables
        for (Class<?> dataClass : DATA_CLASSES) {
            try {
                TableUtils.createTable(connectionSource, dataClass);
            } catch (SQLException e) {
                LogHelper.printStackTrace(e);
            }
        }
    }

    private boolean fieldWithRightModifiers(Field field) {
        int m = field.getModifiers();
        return !Modifier.isFinal(m) &&
                !Modifier.isStatic(m);
    }

    public Dao<CategoryModel, Long> getCategoryDao() throws SQLException {
        return getDao(CategoryModel.class);
    }

    public Dao<ListModel, Long> getListDao() throws SQLException {
        return getDao(ListModel.class);
    }

    public Dao<QuantityUnitModel, Long> getQuantityUnitDao() throws SQLException {
        return getDao(QuantityUnitModel.class);
    }

    public Dao<ItemModel, Long> getItemDao() throws SQLException {
        return getDao(ItemModel.class);
    }

    @Override
    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        D dao = super.getDao(clazz);
        dao.setObjectCache(true);
        return dao;
    }

}
