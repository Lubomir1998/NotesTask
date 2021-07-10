package com.example.notes.di

import android.content.Context
import androidx.room.Room
import com.example.notes.db.DbHelper
import com.example.notes.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        DbHelper::class.java,
        "note_db"
    ).build()


    @Singleton
    @Provides
    fun provideDao(db: DbHelper) = db.getDao()


    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return object : DispatcherProvider {

            override val main: CoroutineDispatcher
                get() = Dispatchers.Main

            override val io: CoroutineDispatcher
                get() = Dispatchers.IO

            override val default: CoroutineDispatcher
                get() = Dispatchers.Default
        }
    }


}