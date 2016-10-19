package org.fs.sub.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.fs.common.BusManager;
import org.fs.exception.AndroidException;
import org.fs.sub.SubServeApplication;
import org.fs.sub.R;
import org.fs.sub.database.IDatabaseHelper;
import org.fs.sub.events.SrtEntityFoundEvent;
import org.fs.sub.events.SrtEntityNotFoundEvent;
import org.fs.sub.events.SrtEntitySavedOrUpdatedEvent;
import org.fs.sub.model.SrtEntity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Fatih on
 * as org.fs.sub.common.DatabaseHelper
 */
public final class DatabaseHelper extends OrmLiteSqliteOpenHelper implements IDatabaseHelper {

    private final static String DB_NAME    = "subtitles.db";
    private final static int    DB_VERSION = 1;

    private Subscription    subscription   = null;
    private RuntimeExceptionDao<SrtEntity, Long> persist = null;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SrtEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, SrtEntity.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void withHashAndFirst(@NonNull String hash) {
        subscription = queryWithHashAndFirst(hash)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(actionWithSrtEntitySuccess());
    }

    @Override
    public void withHashAndLangAndFirst(@NonNull String hash, @NonNull String lang) {
        subscription = queryWithHashAndLangAndFirst(hash, lang)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(actionWithSrtEntitySuccess());
    }

    @Override
    public void withImdbAndLangAndFirst(@NonNull String imdb, @NonNull String lang) {
        subscription = queryWithImdbAndLangAndFirst(imdb, lang)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(actionWithSrtEntitySuccess());
    }

    public void ensureDaoCreated() {
        if(persist == null) {
            persist = getRuntimeExceptionDao(SrtEntity.class);
        }
    }

    @Override
    public void withUpdateOrSaveSrtEntity(@NonNull SrtEntity entity) {
        subscription = saveOrUpdateWithSrtEntity(entity)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(actionWithSrtEntity());
    }

    @Override
    public void releaseAll() {
        persist = null;
        close();
    }

    @Override
    public RuntimeExceptionDao<SrtEntity, Long> getSrtEntityRuntimeDao() {
        ensureDaoCreated();
        return persist;
    }

    protected String getClassTag() {
        return DatabaseHelper.class.getSimpleName();
    }

    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }

    protected void log(Exception error) {
        StringWriter srtWriter = new StringWriter();
        PrintWriter  prtWriter = new PrintWriter(srtWriter);
        error.printStackTrace(prtWriter);
        log(Log.ERROR, srtWriter.toString());
    }

    protected void log(final String str) {
        log(Log.DEBUG, str);
    }

    protected void log(final int lv, final String str) {
        if(isLogEnabled()) {
            Log.println(lv, getClassTag(), str);
        }
    }

    /*
      Querying Database with Hash String and Return first instance
    */
    private Observable<SrtEntity> queryWithHashAndFirst(String hash) {
        return Observable.just(hash)
                .flatMap(new Func1<String, Observable<SrtEntity>>() {
                    @Override
                    public Observable<SrtEntity> call(String hash) {
                        try {
                            return executeQueryWithHashAndFirst(hash);
                        } catch (SQLException sqlError) {
                            throw new AndroidException(sqlError);
                        }
                    }
                });
    }

    private Action1<SrtEntity> actionWithSrtEntitySuccess() {
        return new Action1<SrtEntity>() {
            @Override
            public void call(final SrtEntity srtEntity) {
                if(subscription != null) {
                    subscription.unsubscribe();
                }

                Handler handler = new Handler(Looper.getMainLooper());
                if(srtEntity != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            BusManager.MAIN.post(new SrtEntityFoundEvent(srtEntity, SrtEntityFoundEvent.LOCAL));
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            BusManager.MAIN.post(new SrtEntityNotFoundEvent(SrtEntityNotFoundEvent.LOCAL));
                        }
                    });
                }
            }
        };
    }

    private Observable<SrtEntity> executeQueryWithHashAndFirst(String hash) throws SQLException {
        ensureDaoCreated();
        PreparedQuery<SrtEntity> preparedQuery = persist.queryBuilder()
                .where().eq(SrtEntity.CLM_HASH, hash).prepare();
        SrtEntity srtEntity = persist.queryForFirst(preparedQuery);
        return Observable.just(srtEntity);
    }

    /*
      Querying Database with Hash and Language and Return first instance
     */
    private Observable<SrtEntity> queryWithHashAndLangAndFirst(String hash, String lang) {
        return Observable.just(new String[] { hash, lang })
                .flatMap(new Func1<String[], Observable<SrtEntity>>() {
                    @Override
                    public Observable<SrtEntity> call(String[] args) {
                        try {
                            return executeQueryWithHashAndLangAndFirst(args[0], args[1]);
                        } catch (SQLException sqlError) {
                            throw new AndroidException(sqlError);
                        }
                    }
                });

    }

    private Observable<SrtEntity> queryWithImdbAndLangAndFirst(String imdb, String lang) {
        return Observable.just(new String[] { imdb, lang })
                .flatMap(new Func1<String[], Observable<SrtEntity>>() {
                    @Override
                    public Observable<SrtEntity> call(String[] args) {
                        try {
                            return executeQueryWithImdbAndLangAndFirst(args[0], args[1]);
                        } catch (SQLException sqlError) {
                            throw new AndroidException(sqlError);
                        }
                    }
                });
    }

    private Observable<SrtEntity> executeQueryWithImdbAndLangAndFirst(String imdb, String lang) throws SQLException {
        ensureDaoCreated();
        PreparedQuery<SrtEntity> preparedQuery = persist.queryBuilder()
                .where().eq(SrtEntity.CLM_IMDB_ID, imdb).and().eq(SrtEntity.CLM_LANG, lang).prepare();
        SrtEntity srtEntity = persist.queryForFirst(preparedQuery);
        return Observable.just(srtEntity);
    }

    private Observable<SrtEntity> executeQueryWithHashAndLangAndFirst(String hash, String lang) throws SQLException {
        ensureDaoCreated();
        PreparedQuery<SrtEntity> preparedQuery = persist.queryBuilder()
                .where().eq(SrtEntity.CLM_HASH, hash).and().eq(SrtEntity.CLM_LANG, lang).prepare();
        SrtEntity srtEntity = persist.queryForFirst(preparedQuery);
        return Observable.just(srtEntity);
    }

    /*
        Query event
     */
    private Observable<Integer> saveOrUpdateWithSrtEntity(SrtEntity srtEntity) {
        return Observable.just(srtEntity)
                .flatMap(new Func1<SrtEntity, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(SrtEntity srtEntity) {
                        try {
                            return executeSaveOrUpdateWithSrtEntity(srtEntity);
                        } catch (SQLException sqlError) {
                            throw new AndroidException(sqlError);
                        }
                    }
                });
    }

    private Observable<Integer> executeSaveOrUpdateWithSrtEntity(SrtEntity srtEntity) throws SQLException {
        ensureDaoCreated();
        if(srtEntity.id() != null) {
            return Observable.just(persist.update(srtEntity));
        } else {
            return Observable.just(persist.create(srtEntity));
        }
    }

    private Action1<Integer> actionWithSrtEntity() {
        return new Action1<Integer>() {
            @Override
            public void call(final Integer integer) {
                if(subscription != null) {
                    subscription.unsubscribe();
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        BusManager.MAIN.post(new SrtEntitySavedOrUpdatedEvent(integer));
                    }
                });
            }
        };
    }
}
