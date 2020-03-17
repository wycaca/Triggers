package com.han.walktriggers.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.han.walktriggers.data.converters.Converters;
import com.han.walktriggers.data.online.WeatherDao;
import com.han.walktriggers.data.online.entity.Weather;

@Database(entities = {Weather.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {
    private static volatile AppDataBase INSTANCE;

    public abstract WeatherDao weatherDao();

    public static AppDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "triggers.db")
                            .allowMainThreadQueries()
//                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback sRoomDatabaseCallback = new Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final WeatherDao mDao;

        PopulateDbAsync(AppDataBase db) {
            mDao = db.weatherDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            return null;
        }
    }
}
