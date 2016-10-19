package org.fs.sub.database;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.fs.sub.model.SrtEntity;

/**
 * Created by Fatih on
 * as org.fs.sub.net.OpenSubtitlesDb
 */
public interface IDatabaseHelper {

    /**
     *
     * @param hash
     */
    void withHashAndFirst(@NonNull String hash);

    /**
     *
     * @param hash HASH of Media File
     * @param lang
     */
    void withHashAndLangAndFirst(@NonNull String hash, @NonNull String lang);


    /**
     *
     * @param imdb IMDB_ID
     * @param lang
     */
    void withImdbAndLangAndFirst(@NonNull String imdb, @NonNull String lang);

    /**
     *
     * @param entity
     */
    void withUpdateOrSaveSrtEntity(@NonNull SrtEntity entity);

    /**
     * Release resources such as Dao objects created by OrmLite library.
     */
    void releaseAll();


    /**
     *
     * @return
     */
    RuntimeExceptionDao<SrtEntity, Long> getSrtEntityRuntimeDao();
}
