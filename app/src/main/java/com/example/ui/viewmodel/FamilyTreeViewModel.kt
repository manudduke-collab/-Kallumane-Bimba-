package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.FamilyTreeDatabase
import com.example.data.model.CulturalHeritageEntity
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.FamilyStoryEntity
import com.example.data.model.Gender
import com.example.data.model.MediaType
import com.example.data.model.MemberMediaEntity
import com.example.util.ImageStorageHelper
import com.example.util.KannadaKinshipCalculator
import com.example.util.KinshipResult
import com.example.util.RelationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FamilyTreeUiState(
  val members: List<FamilyMemberEntity> = emptyList(),
  val allMedia: List<MemberMediaEntity> = emptyList(),
  val heritage: CulturalHeritageEntity? = null,
  val stories: List<FamilyStoryEntity> = emptyList(),
  val focusMember: FamilyMemberEntity? = null,
  val selectedMember: FamilyMemberEntity? = null,
  val highlightedMemberId: Long? = null,
  val selectedMediaForViewer: MemberMediaEntity? = null,
  val searchQuery: String = "",
  val selectedGeneration: Int? = null,
  val isKannada: Boolean = true,
  val isLoading: Boolean = false,
  val kinComparisonPersonA: FamilyMemberEntity? = null,
  val kinComparisonPersonB: FamilyMemberEntity? = null
)

class FamilyTreeViewModel(application: Application) : AndroidViewModel(application) {

  private val database = FamilyTreeDatabase.getDatabase(application, viewModelScope)
  private val memberDao = database.familyMemberDao()
  private val mediaDao = database.memberMediaDao()
  private val heritageDao = database.culturalHeritageDao()
  private val storyDao = database.familyStoryDao()

  private val _searchQuery = MutableStateFlow("")
  private val _selectedGeneration = MutableStateFlow<Int?>(null)
  private val _focusMemberId = MutableStateFlow<Long?>(11L) // Default Ananth Rao or first
  private val _selectedMember = MutableStateFlow<FamilyMemberEntity?>(null)
  private val _highlightedMemberId = MutableStateFlow<Long?>(null)
  private val _selectedMediaForViewer = MutableStateFlow<MemberMediaEntity?>(null)
  private val _isKannada = MutableStateFlow(true)
  private val _kinComparisonPersonA = MutableStateFlow<FamilyMemberEntity?>(null)
  private val _kinComparisonPersonB = MutableStateFlow<FamilyMemberEntity?>(null)

  val allMembers: StateFlow<List<FamilyMemberEntity>> = memberDao.getAllMembers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allMedia: StateFlow<List<MemberMediaEntity>> = mediaDao.getAllMedia()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val heritage: StateFlow<CulturalHeritageEntity?> = heritageDao.getHeritage()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val stories: StateFlow<List<FamilyStoryEntity>> = storyDao.getAllStories()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val uiState: StateFlow<FamilyTreeUiState> = combine(
    combine(allMembers, allMedia, heritage, stories) { members, media, h, st ->
      Quad(members, media, h, st)
    },
    combine(
      _focusMemberId,
      _selectedMember,
      _highlightedMemberId,
      _selectedMediaForViewer,
      _searchQuery,
      _selectedGeneration,
      _isKannada,
      _kinComparisonPersonA,
      _kinComparisonPersonB
    ) { params -> params }
  ) { quad, params ->
    val members = quad.first
    val media = quad.second
    val h = quad.third
    val st = quad.fourth

    val fId = params[0] as? Long
    val sel = params[1] as? FamilyMemberEntity
    val hlId = params[2] as? Long
    val selMedia = params[3] as? MemberMediaEntity
    val query = params[4] as String
    val gen = params[5] as? Int
    val kannada = params[6] as Boolean
    val kinA = params[7] as? FamilyMemberEntity
    val kinB = params[8] as? FamilyMemberEntity

    val focus = members.find { it.id == fId } ?: members.firstOrNull()
    val currentSelected = members.find { it.id == sel?.id } ?: sel

    FamilyTreeUiState(
      members = members,
      allMedia = media,
      heritage = h,
      stories = st,
      focusMember = focus,
      selectedMember = currentSelected,
      highlightedMemberId = hlId,
      selectedMediaForViewer = selMedia,
      searchQuery = query,
      selectedGeneration = gen,
      isKannada = kannada,
      isLoading = false,
      kinComparisonPersonA = kinA,
      kinComparisonPersonB = kinB
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    FamilyTreeUiState(isLoading = true)
  )

  init {
    viewModelScope.launch(Dispatchers.IO) {
      val existing = memberDao.getMemberById(1L)
      if (existing == null) {
        FamilyTreeDatabase.populateDatabase(database)
      }
    }
  }

  fun toggleLanguage() {
    _isKannada.value = !_isKannada.value
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setGenerationFilter(generation: Int?) {
    _selectedGeneration.value = generation
  }

  fun setFocusMember(member: FamilyMemberEntity) {
    _focusMemberId.value = member.id
  }

  fun selectMember(member: FamilyMemberEntity?) {
    _selectedMember.value = member
    _highlightedMemberId.value = member?.id
  }

  fun setHighlightedMember(memberId: Long?) {
    _highlightedMemberId.value = memberId
  }

  fun openMediaViewer(media: MemberMediaEntity?) {
    _selectedMediaForViewer.value = media
  }

  fun setKinshipComparisonA(member: FamilyMemberEntity?) {
    _kinComparisonPersonA.value = member
  }

  fun setKinshipComparisonB(member: FamilyMemberEntity?) {
    _kinComparisonPersonB.value = member
  }

  // Define and maintain robust relationships (parent-child, spouse, sibling)
  fun addRelationship(
    targetMemberId: Long,
    relativeMemberId: Long,
    relationType: RelationType,
    onComplete: () -> Unit = {}
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val target = memberDao.getMemberById(targetMemberId) ?: return@launch
      val relative = memberDao.getMemberById(relativeMemberId) ?: return@launch

      when (relationType) {
        RelationType.FATHER -> {
          // relative is father of target
          memberDao.updateMember(target.copy(fatherId = relative.id))
          // ensure father generation is higher (smaller number) if needed
          if (relative.generation >= target.generation) {
            memberDao.updateMember(relative.copy(generation = (target.generation - 1).coerceAtLeast(1)))
          }
        }

        RelationType.MOTHER -> {
          // relative is mother of target
          memberDao.updateMember(target.copy(motherId = relative.id))
          if (relative.generation >= target.generation) {
            memberDao.updateMember(relative.copy(generation = (target.generation - 1).coerceAtLeast(1)))
          }
        }

        RelationType.SPOUSE -> {
          // mutual marriage connection
          memberDao.updateMember(target.copy(spouseId = relative.id, generation = target.generation))
          memberDao.updateMember(relative.copy(spouseId = target.id, generation = target.generation))
        }

        RelationType.SIBLING -> {
          // Sibling relationship: connect via parents
          var commonFather = target.fatherId ?: relative.fatherId
          var commonMother = target.motherId ?: relative.motherId

          // If neither had parents, we can ensure they both share the same generation
          val updatedTarget = target.copy(
            fatherId = commonFather,
            motherId = commonMother
          )
          val updatedRelative = relative.copy(
            fatherId = commonFather,
            motherId = commonMother,
            generation = target.generation
          )
          memberDao.updateMember(updatedTarget)
          memberDao.updateMember(updatedRelative)
        }

        RelationType.SON, RelationType.DAUGHTER -> {
          // relative is child of target
          val updatedChild = if (target.gender == Gender.FEMALE) {
            relative.copy(
              motherId = target.id,
              fatherId = target.spouseId ?: relative.fatherId,
              generation = target.generation + 1
            )
          } else {
            relative.copy(
              fatherId = target.id,
              motherId = target.spouseId ?: relative.motherId,
              generation = target.generation + 1
            )
          }
          memberDao.updateMember(updatedChild)
        }
      }

      // Refresh current selected member if affected
      if (_selectedMember.value?.id == targetMemberId || _selectedMember.value?.id == relativeMemberId) {
        val updated = memberDao.getMemberById(_selectedMember.value!!.id)
        _selectedMember.value = updated
      }

      onComplete()
    }
  }

  fun removeRelationship(
    memberId: Long,
    relationType: RelationType,
    relativeId: Long? = null,
    onComplete: () -> Unit = {}
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val member = memberDao.getMemberById(memberId) ?: return@launch
      when (relationType) {
        RelationType.FATHER -> {
          memberDao.updateMember(member.copy(fatherId = null))
        }
        RelationType.MOTHER -> {
          memberDao.updateMember(member.copy(motherId = null))
        }
        RelationType.SPOUSE -> {
          val oldSpouseId = member.spouseId
          memberDao.updateMember(member.copy(spouseId = null))
          if (oldSpouseId != null) {
            val oldSpouse = memberDao.getMemberById(oldSpouseId)
            if (oldSpouse != null && oldSpouse.spouseId == member.id) {
              memberDao.updateMember(oldSpouse.copy(spouseId = null))
            }
          }
        }
        RelationType.SON, RelationType.DAUGHTER -> {
          if (relativeId != null) {
            val child = memberDao.getMemberById(relativeId)
            if (child != null) {
              val updated = if (child.fatherId == member.id) child.copy(fatherId = null)
              else if (child.motherId == member.id) child.copy(motherId = null)
              else child
              memberDao.updateMember(updated)
            }
          }
        }
        RelationType.SIBLING -> {
          // unlink from shared parent
          if (relativeId != null) {
            val sib = memberDao.getMemberById(relativeId)
            if (sib != null && (sib.fatherId == member.fatherId || sib.motherId == member.motherId)) {
              memberDao.updateMember(sib.copy(fatherId = null, motherId = null))
            }
          }
        }
      }

      if (_selectedMember.value?.id == memberId) {
        _selectedMember.value = memberDao.getMemberById(memberId)
      }
      onComplete()
    }
  }

  // Upload and associate media (photos & documents) with individual profile
  fun addMemberMedia(
    memberId: Long,
    uri: Uri,
    titleKannada: String,
    titleEnglish: String,
    mediaType: MediaType,
    description: String,
    onComplete: (Long) -> Unit = {}
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val saved = ImageStorageHelper.saveMediaFile(getApplication(), uri) ?: return@launch
      val entity = MemberMediaEntity(
        memberId = memberId,
        titleKannada = titleKannada.ifBlank { saved.fileName },
        titleEnglish = titleEnglish.ifBlank { saved.fileName },
        mediaType = mediaType,
        filePath = saved.filePath,
        fileName = saved.fileName,
        fileSizeBytes = saved.sizeBytes,
        uploadDate = "2026",
        description = description
      )
      val mediaId = mediaDao.insertMedia(entity)
      onComplete(mediaId)
    }
  }

  fun deleteMemberMedia(media: MemberMediaEntity, onComplete: () -> Unit = {}) {
    viewModelScope.launch(Dispatchers.IO) {
      ImageStorageHelper.deleteImage(media.filePath)
      mediaDao.deleteMedia(media)
      if (_selectedMediaForViewer.value?.id == media.id) {
        _selectedMediaForViewer.value = null
      }
      onComplete()
    }
  }

  fun saveMember(
    member: FamilyMemberEntity,
    newPhotoUri: Uri? = null,
    onComplete: (Long) -> Unit = {}
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      var photoPath = member.photoPath
      if (newPhotoUri != null) {
        val savedPath = ImageStorageHelper.saveImageToInternalStorage(getApplication(), newPhotoUri)
        if (savedPath != null) {
          photoPath = savedPath
        }
      }

      val updatedMember = member.copy(photoPath = photoPath)
      val memberId = if (updatedMember.id == 0L) {
        memberDao.insertMember(updatedMember)
      } else {
        memberDao.updateMember(updatedMember)
        updatedMember.id
      }

      // If spouse is set, reciprocate the spouse relation
      if (updatedMember.spouseId != null) {
        val spouse = memberDao.getMemberById(updatedMember.spouseId)
        if (spouse != null && spouse.spouseId != memberId) {
          memberDao.updateMember(spouse.copy(spouseId = memberId))
        }
      }

      if (_selectedMember.value?.id == memberId) {
        _selectedMember.value = memberDao.getMemberById(memberId)
      }

      onComplete(memberId)
    }
  }

  fun updateMemberPhoto(memberId: Long, uri: Uri) {
    viewModelScope.launch(Dispatchers.IO) {
      val member = memberDao.getMemberById(memberId) ?: return@launch
      val savedPath = ImageStorageHelper.saveImageToInternalStorage(getApplication(), uri)
      if (savedPath != null) {
        val updated = member.copy(photoPath = savedPath)
        memberDao.updateMember(updated)
        if (_selectedMember.value?.id == memberId) {
          _selectedMember.value = updated
        }
      }
    }
  }

  fun removeMemberPhoto(memberId: Long) {
    viewModelScope.launch(Dispatchers.IO) {
      val member = memberDao.getMemberById(memberId) ?: return@launch
      ImageStorageHelper.deleteImage(member.photoPath)
      val updated = member.copy(photoPath = null)
      memberDao.updateMember(updated)
      if (_selectedMember.value?.id == memberId) {
        _selectedMember.value = updated
      }
    }
  }

  fun deleteMember(member: FamilyMemberEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      ImageStorageHelper.deleteImage(member.photoPath)
      mediaDao.deleteMediaForMember(member.id)
      memberDao.deleteMember(member)
      if (_selectedMember.value?.id == member.id) {
        _selectedMember.value = null
      }
      if (_focusMemberId.value == member.id) {
        _focusMemberId.value = null
      }
    }
  }

  fun updateHeritage(heritage: CulturalHeritageEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      heritageDao.setHeritage(heritage)
    }
  }

  fun addStory(story: FamilyStoryEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      storyDao.insertStory(story)
    }
  }

  fun deleteStory(story: FamilyStoryEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      storyDao.deleteStory(story)
    }
  }

  fun resetToSampleData() {
    viewModelScope.launch(Dispatchers.IO) {
      memberDao.clearAll()
      mediaDao.clearAll()
      storyDao.clearAll()
      FamilyTreeDatabase.populateDatabase(database)
      _focusMemberId.value = 11L
      _selectedMember.value = null
      _highlightedMemberId.value = null
    }
  }

  fun calculateKinship(target: FamilyMemberEntity): KinshipResult {
    val focus = uiState.value.focusMember ?: target
    return KannadaKinshipCalculator.calculateKinship(
      focus = focus,
      target = target,
      allMembers = allMembers.value
    )
  }

  fun calculateKinshipBetween(personA: FamilyMemberEntity, personB: FamilyMemberEntity): KinshipResult {
    return KannadaKinshipCalculator.calculateKinship(
      focus = personA,
      target = personB,
      allMembers = allMembers.value
    )
  }
}

private data class Quad<A, B, C, D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)
