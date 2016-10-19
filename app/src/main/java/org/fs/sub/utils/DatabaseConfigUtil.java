package org.fs.sub.utils;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import org.fs.sub.model.SrtEntity;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Fatih on
 * as org.fs.sub.utils.DatabaseConfigUtil
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private final static Class<?>[] clazzes = new Class<?>[] { SrtEntity.class };

    /**
     * this method used for generating classes for related db system and sqlite database
     * @param args
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String... args) throws SQLException, IOException {
        writeConfigFile("ormlite_config.txt", clazzes);
    }
}
