/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.auto.billiardnote.fao.AppDatabase
import com.auto.billiardnote.fao.Note
import com.auto.billiardnote.ui.home.draw.StraightLine
import com.google.gson.GsonBuilder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Test the implementation of [NoteDao]
 */
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before fun initDb() {
        // using an in-memory database because the information stored here disappears after test
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java)
            // allowing main thread queries, just for testing
            .allowMainThreadQueries()
            .build()
    }

    @After fun closeDb() {
        database.close()
    }

    @Test fun getUsersWhenNoUserInserted() {
        database.noteDao().insert(noteInfo)
            .test()
            .assertNoValues()
    }

    @Test fun insertAndGetUser() {
        // When inserting a new user in the data source
        database.noteDao().insert(noteInfo).blockingAwait()

        // When subscribing to the emissions of the user
        database.noteDao().loadById(noteInfo.id)
            .test()
            // assertValue asserts that there was only one emission of the user
            .assertValue { it.id == noteInfo.id && it.name == noteInfo.name }
    }

    @Test fun updateAndGetUser() {
        // Given that we have a user in the data source
        database.noteDao().insert(noteInfo).blockingAwait()

        // When we are updating the name of the user
        val line = StraightLine()
        val ball = noteInfo.balls
//        val updatedNote = ball.let { Note(line, it, "update", "test") }
//        database.noteDao().insert(updatedNote).blockingAwait()

        // When subscribing to the emissions of the user
        database.noteDao().loadById(noteInfo.id)
            .test()
            // assertValue asserts that there was only one emission of the user
            .assertValue { it.id == noteInfo.id && it.name == "new username" }
    }

    @Test fun deleteAndGetUser() {
        // Given that we have a user in the data source
        database.noteDao().insert(noteInfo).blockingAwait()

        //When we are deleting all users
        database.noteDao().delete()
        // When subscribing to the emissions of the user
        database.noteDao().loadById(noteInfo.id)
            .test()
            // check that there's no user emitted
            .assertNoValues()
    }

    companion object {
        private val noteInfo = GsonBuilder().create().fromJson("", Note::class.java)
    }
}