package com.hubinqiang.notepad.module;

import android.app.Application;
import android.content.Context;

import com.hubinqiang.notepad.App;

import dagger.Module;
import dagger.Provides;


@Module(
        injects = {
                App.class
        },
        library = true
)

public class AppModule {
    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    Application provideApplication() {
        return app;
    }

    @Provides
    Context provideContext() {
        return app.getApplicationContext();
    }
}
