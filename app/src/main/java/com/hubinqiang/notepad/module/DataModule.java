package com.hubinqiang.notepad.module;

import android.content.Context;

import com.hubinqiang.notepad.ui.EditNoteTypeActivity;
import com.hubinqiang.notepad.ui.MainActivity;
import com.hubinqiang.notepad.ui.NoteActivity;
import com.hubinqiang.notepad.ui.SettingActivity;
import com.hubinqiang.notepad.ui.fragments.SettingFragment;

import net.tsz.afinal.FinalDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(
        injects = {
                MainActivity.class,
                NoteActivity.class,
                SettingActivity.class,
                SettingFragment.class,
                EditNoteTypeActivity.class
        },
        addsTo = AppModule.class,
        library = true
)

public class DataModule {

    @Provides @Singleton
    FinalDb.DaoConfig provideDaoConfig(Context context) {
        FinalDb.DaoConfig config = new FinalDb.DaoConfig();
        config.setDbName("notes.db");
        config.setDbVersion(1);
        config.setDebug(true);
        config.setContext(context);
        return config;
    }

    @Provides @Singleton
    FinalDb provideFinalDb(FinalDb.DaoConfig config) {
        return FinalDb.create(config);
    }
}
