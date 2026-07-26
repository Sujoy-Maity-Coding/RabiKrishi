package com.sujoy.smartfarm.Domain.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sujoy.smartfarm.Data.Repo.RepoImpl
import com.sujoy.smartfarm.Domain.repo.Repo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provideRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
        @ApplicationContext context: Context
    ): Repo {
        return RepoImpl(firebaseAuth, firebaseFirestore, firebaseStorage, context)
    }

}