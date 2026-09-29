package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CulturalHeritageDao
import com.example.data.dao.FamilyMemberDao
import com.example.data.dao.FamilyStoryDao
import com.example.data.dao.MemberMediaDao
import com.example.data.model.CulturalHeritageEntity
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.FamilyStoryEntity
import com.example.data.model.MemberMediaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    FamilyMemberEntity::class,
    MemberMediaEntity::class,
    CulturalHeritageEntity::class,
    FamilyStoryEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class FamilyTreeDatabase : RoomDatabase() {
  abstract fun familyMemberDao(): FamilyMemberDao
  abstract fun memberMediaDao(): MemberMediaDao
  abstract fun culturalHeritageDao(): CulturalHeritageDao
  abstract fun familyStoryDao(): FamilyStoryDao

  companion object {
    @Volatile
    private var INSTANCE: FamilyTreeDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): FamilyTreeDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          FamilyTreeDatabase::class.java,
          "vamsha_vriksha_database"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateDatabase(database)
          }
        }
      }
    }

    suspend fun populateDatabase(database: FamilyTreeDatabase) {
      val memberDao = database.familyMemberDao()
      val mediaDao = database.memberMediaDao()
      val heritageDao = database.culturalHeritageDao()
      val storyDao = database.familyStoryDao()

      memberDao.insertAll(SampleDataGenerator.getSampleMembers())
      mediaDao.insertAll(SampleDataGenerator.getSampleMedia())
      heritageDao.setHeritage(SampleDataGenerator.getSampleHeritage())
      storyDao.insertAll(SampleDataGenerator.getSampleStories())
    }
  }
}
