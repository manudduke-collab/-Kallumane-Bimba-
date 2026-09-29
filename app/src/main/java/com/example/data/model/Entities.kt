package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Gender {
  MALE,
  FEMALE,
  OTHER
}

enum class MediaType {
  PHOTO,
  DOCUMENT,
  KUNDALI,
  CERTIFICATE,
  LETTER
}

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val kannadaName: String,
  val englishName: String,
  val gender: Gender,
  val isAlive: Boolean = true,
  val birthYear: String = "",
  val deathYear: String? = null,
  val photoPath: String? = null,
  val maneHesaru: String = "", // e.g. "ದೊಡ್ಡಮನೆ" / "ಅರಳೀಮರ ಮನೆ" (Family house alias)
  val purveekaraOoru: String = "", // Ancestral village / Town e.g. "ತೀರ್ಥಹಳ್ಳಿ, ಶಿವಮೊಗ್ಗ"
  val gothra: String = "", // e.g. "ಕಾಶ್ಯಪ (Kashyapa)"
  val kulaDaiva: String = "", // Mane Devaru e.g. "ಶ್ರೀ ಕೊಲ್ಲೂರು ಮೂಕಾಂಬಿಕಾ"
  val kulaBranch: String = "", // Branch e.g. "ಸ್ಮಾರ್ತ" / "ವೀರಶೈವ" / "ಒಕ್ಕಲಿಗ"
  val mathaAffiliation: String = "", // e.g. "ಶ್ರೀ ಶೃಂಗೇರಿ ಮಠ"
  val fatherId: Long? = null,
  val motherId: Long? = null,
  val spouseId: Long? = null,
  val generation: Int = 3, // 1: Ancestors, 2: Grandparents, 3: Parents, 4: Self/Siblings, 5: Children
  val occupation: String = "",
  val contactNumber: String = "",
  val notes: String = "",
  val specialTradition: String = ""
)

@Entity(
  tableName = "member_media",
  foreignKeys = [
    ForeignKey(
      entity = FamilyMemberEntity::class,
      parentColumns = ["id"],
      childColumns = ["memberId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["memberId"])]
)
data class MemberMediaEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val memberId: Long,
  val titleKannada: String,
  val titleEnglish: String,
  val mediaType: MediaType,
  val filePath: String,
  val fileName: String = "",
  val fileSizeBytes: Long = 0L,
  val uploadDate: String = "",
  val description: String = ""
)

@Entity(tableName = "cultural_heritage")
data class CulturalHeritageEntity(
  @PrimaryKey
  val id: Long = 1L,
  val familyTitleKannada: String,
  val familyTitleEnglish: String,
  val primaryGothra: String,
  val kulaDaiva: String,
  val kulaDaivaTemplePlace: String,
  val mulaOoru: String,
  val mulaManeDescription: String,
  val mathaAffiliation: String,
  val mainFestivals: String,
  val ancestralHomePhotoPath: String? = null
)

@Entity(tableName = "family_stories")
data class FamilyStoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val titleKannada: String,
  val titleEnglish: String,
  val contentKannada: String,
  val contentEnglish: String,
  val eraOrYear: String,
  val narratorOrHero: String,
  val photoPath: String? = null
)
