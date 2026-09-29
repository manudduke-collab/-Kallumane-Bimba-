package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CulturalHeritageEntity
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.FamilyStoryEntity
import com.example.data.model.MemberMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMemberDao {
  @Query("SELECT * FROM family_members ORDER BY generation ASC, id ASC")
  fun getAllMembers(): Flow<List<FamilyMemberEntity>>

  @Query("SELECT * FROM family_members WHERE id = :id")
  suspend fun getMemberById(id: Long): FamilyMemberEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: FamilyMemberEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(members: List<FamilyMemberEntity>)

  @Update
  suspend fun updateMember(member: FamilyMemberEntity)

  @Delete
  suspend fun deleteMember(member: FamilyMemberEntity)

  @Query("DELETE FROM family_members")
  suspend fun clearAll()
}

@Dao
interface MemberMediaDao {
  @Query("SELECT * FROM member_media WHERE memberId = :memberId ORDER BY id DESC")
  fun getMediaForMember(memberId: Long): Flow<List<MemberMediaEntity>>

  @Query("SELECT * FROM member_media ORDER BY id DESC")
  fun getAllMedia(): Flow<List<MemberMediaEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMedia(media: MemberMediaEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(mediaList: List<MemberMediaEntity>)

  @Delete
  suspend fun deleteMedia(media: MemberMediaEntity)

  @Query("DELETE FROM member_media WHERE memberId = :memberId")
  suspend fun deleteMediaForMember(memberId: Long)

  @Query("DELETE FROM member_media")
  suspend fun clearAll()
}

@Dao
interface CulturalHeritageDao {
  @Query("SELECT * FROM cultural_heritage WHERE id = 1 LIMIT 1")
  fun getHeritage(): Flow<CulturalHeritageEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setHeritage(heritage: CulturalHeritageEntity)
}

@Dao
interface FamilyStoryDao {
  @Query("SELECT * FROM family_stories ORDER BY id DESC")
  fun getAllStories(): Flow<List<FamilyStoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStory(story: FamilyStoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(stories: List<FamilyStoryEntity>)

  @Delete
  suspend fun deleteStory(story: FamilyStoryEntity)

  @Query("DELETE FROM family_stories")
  suspend fun clearAll()
}
