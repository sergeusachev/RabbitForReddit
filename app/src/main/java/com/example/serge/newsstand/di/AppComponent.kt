package com.example.serge.newsstand.di

import android.app.Application
import com.example.serge.newsstand.App
import com.example.serge.newsstand.di.scope.AppScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

@AppScope
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    ActivityProviderModule::class])
interface AppComponent {
    fun inject(app: App)

    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder
        fun build(): AppComponent
    }
}