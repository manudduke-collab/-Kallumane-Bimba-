package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.FamilyMemberEntity
import com.example.ui.components.AddEditMemberDialog
import com.example.ui.components.AddRelationshipDialog
import com.example.ui.components.MediaViewerDialog
import com.example.ui.components.MemberDetailSheet
import com.example.ui.components.UploadMediaDialog
import com.example.ui.screens.CulturalHeritageScreen
import com.example.ui.screens.MemberListScreen
import com.example.ui.screens.TreeVisualizerScreen
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TempleOchre
import com.example.ui.viewmodel.FamilyTreeViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        FamilyTreeApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyTreeApp(
  viewModel: FamilyTreeViewModel = viewModel()
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) }
  var showAddDialog by remember { mutableStateOf(false) }
  var editingMember by remember { mutableStateOf<FamilyMemberEntity?>(null) }
  var memberForRelationship by remember { mutableStateOf<FamilyMemberEntity?>(null) }
  var memberForUploadMedia by remember { mutableStateOf<FamilyMemberEntity?>(null) }
  var showMenu by remember { mutableStateOf(false) }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          androidx.compose.foundation.layout.Column {
            Text(
              text = if (uiState.isKannada) "ವಂಶವೃಕ್ಷ" else "Vamsha Vriksha",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = KumkumaMaroon
            )
            Text(
              text = if (uiState.isKannada) "ಕರ್ನಾಟಕದ ಕುಟುಂಬ ಪರಂಪರೆ ಮತ್ತು ಬೇರುಗಳು" else "Karnataka Family Heritage & Ancestral Roots",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          // Language Toggle Button
          IconButton(
            onClick = { viewModel.toggleLanguage() },
            modifier = Modifier.testTag("language_toggle_button")
          ) {
            Icon(
              Icons.Default.Language,
              contentDescription = "Toggle Language",
              tint = TempleOchre
            )
          }

          // Overflow Menu
          IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.testTag("overflow_menu_button")
          ) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
          }

          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              text = {
                Text(if (uiState.isKannada) "ವಂಶ ಸಾರಾಂಶ ಹಂಚಿಕೊಳ್ಳಿ" else "Share Family Summary")
              },
              leadingIcon = {
                Icon(Icons.Default.Share, contentDescription = null, tint = TempleOchre)
              },
              onClick = {
                showMenu = false
                val summary = buildString {
                  append("ವಂಶವೃಕ್ಷ - ಕುಟುಂಬ ಪರಂಪರೆ (Family Heritage)\n\n")
                  uiState.heritage?.let { h ->
                    append("ಕುಟುಂಬ: ${h.familyTitleKannada}\n")
                    append("ಗೋತ್ರ: ${h.primaryGothra}\n")
                    append("ಕುಲದೈವ: ${h.kulaDaiva}\n")
                    append("ಮೂಲ ಊರು: ${h.mulaOoru}\n\n")
                  }
                  append("ಒಟ್ಟು ಸದಸ್ಯರು: ${uiState.members.size}\n")
                  uiState.members.take(15).forEach { m ->
                    append("• ${m.kannadaName} (${m.englishName}) - ${m.gothra} [${m.purveekaraOoru}]\n")
                  }
                }
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, summary)
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Vamsha Vriksha"))
              }
            )

            DropdownMenuItem(
              text = {
                Text(if (uiState.isKannada) "ಮಾದರಿ ಡೇಟಾ ಮರುಸ್ಥಾಪಿಸಿ" else "Reset Sample Family Data")
              },
              leadingIcon = {
                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = KumkumaMaroon)
              },
              onClick = {
                showMenu = false
                viewModel.resetToSampleData()
                coroutineScope.launch {
                  snackbarHostState.showSnackbar(
                    if (uiState.isKannada) "ಮಾದರಿ ಕುಟುಂಬ ಡೇಟಾ ಮರುಸ್ಥಾಪಿಸಲಾಗಿದೆ" else "Sample family data restored"
                  )
                }
              }
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = KumkumaMaroon
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = { Icon(Icons.Default.AccountTree, contentDescription = "Family Tree") },
          label = {
            Text(
              if (uiState.isKannada) "ವೃಕ್ಷ" else "Tree",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KumkumaMaroon,
            indicatorColor = HeritageGold.copy(alpha = 0.2f)
          ),
          modifier = Modifier.testTag("nav_tree_tab")
        )

        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = { Icon(Icons.Default.People, contentDescription = "Members") },
          label = {
            Text(
              if (uiState.isKannada) "ಸದಸ್ಯರು" else "Members",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KumkumaMaroon,
            indicatorColor = HeritageGold.copy(alpha = 0.2f)
          ),
          modifier = Modifier.testTag("nav_members_tab")
        )

        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = { Icon(Icons.Default.TempleHindu, contentDescription = "Heritage") },
          label = {
            Text(
              if (uiState.isKannada) "ಪರಂಪರೆ" else "Heritage",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KumkumaMaroon,
            indicatorColor = HeritageGold.copy(alpha = 0.2f)
          ),
          modifier = Modifier.testTag("nav_heritage_tab")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = TempleOchre)
        }
      } else {
        when (selectedTab) {
          0 -> TreeVisualizerScreen(
            members = uiState.members,
            allMedia = uiState.allMedia,
            focusMember = uiState.focusMember,
            highlightedMemberId = uiState.highlightedMemberId,
            isKannada = uiState.isKannada,
            onMemberClick = { member ->
              viewModel.selectMember(member)
            },
            onAddMemberClick = {
              editingMember = null
              showAddDialog = true
            },
            onAddRelationshipClick = { member ->
              memberForRelationship = member
            },
            onMediaBadgeClick = { media ->
              viewModel.openMediaViewer(media)
            },
            getKinshipForMember = { target ->
              viewModel.calculateKinship(target)
            }
          )

          1 -> MemberListScreen(
            members = uiState.members,
            focusMember = uiState.focusMember,
            isKannada = uiState.isKannada,
            onMemberClick = { member ->
              viewModel.selectMember(member)
            },
            onAddMemberClick = {
              editingMember = null
              showAddDialog = true
            },
            getKinshipForMember = { target ->
              viewModel.calculateKinship(target)
            }
          )

          2 -> CulturalHeritageScreen(
            heritage = uiState.heritage,
            stories = uiState.stories,
            members = uiState.members,
            focusMember = uiState.focusMember,
            isKannada = uiState.isKannada,
            onUpdateHeritage = { updated ->
              viewModel.updateHeritage(updated)
            },
            onAddStory = { story ->
              viewModel.addStory(story)
            },
            onSelectMember = { member ->
              viewModel.selectMember(member)
            }
          )
        }
      }
    }
  }

  // Member Detail Sheet
  if (uiState.selectedMember != null) {
    val selected = uiState.selectedMember!!
    val kinship = viewModel.calculateKinship(selected)
    val isFocus = uiState.focusMember?.id == selected.id
    val memberMediaList = uiState.allMedia.filter { it.memberId == selected.id }

    MemberDetailSheet(
      member = selected,
      allMembers = uiState.members,
      memberMedia = memberMediaList,
      kinshipToFocus = kinship,
      isFocusMember = isFocus,
      isKannada = uiState.isKannada,
      sheetState = sheetState,
      onDismiss = { viewModel.selectMember(null) },
      onSetAsFocus = { member ->
        viewModel.setFocusMember(member)
        coroutineScope.launch {
          snackbarHostState.showSnackbar(
            if (uiState.isKannada) "${member.kannadaName} ಅವರನ್ನು ಕೇಂದ್ರ ಸ್ಥಾನವಾಗಿ ನಿಗದಿಪಡಿಸಲಾಗಿದೆ" else "Reference set to ${member.englishName}"
          )
        }
      },
      onEdit = { member ->
        viewModel.selectMember(null)
        editingMember = member
        showAddDialog = true
      },
      onDelete = { member ->
        viewModel.deleteMember(member)
        coroutineScope.launch {
          snackbarHostState.showSnackbar(
            if (uiState.isKannada) "${member.kannadaName} ಅವರನ್ನು ತೆಗೆದುಹಾಕಲಾಗಿದೆ" else "${member.englishName} deleted"
          )
        }
      },
      onSelectMember = { nextMember ->
        viewModel.selectMember(nextMember)
      },
      onPhotoUpdated = { memberId, uri ->
        viewModel.updateMemberPhoto(memberId, uri)
      },
      onAddRelationshipClick = {
        memberForRelationship = selected
      },
      onRemoveRelationship = { relationType, relativeId ->
        viewModel.removeRelationship(selected.id, relationType, relativeId) {
          coroutineScope.launch {
            snackbarHostState.showSnackbar(
              if (uiState.isKannada) "ಸಂಬಂಧವನ್ನು ತೆಗೆದುಹಾಕಲಾಗಿದೆ" else "Relationship unlinked"
            )
          }
        }
      },
      onUploadMediaClick = {
        memberForUploadMedia = selected
      },
      onMediaClick = { media ->
        viewModel.openMediaViewer(media)
      }
    )
  }

  // Add / Edit Member Dialog
  if (showAddDialog) {
    AddEditMemberDialog(
      member = editingMember,
      allMembers = uiState.members,
      isKannada = uiState.isKannada,
      onDismiss = {
        showAddDialog = false
        editingMember = null
      },
      onSave = { entity, photoUri ->
        viewModel.saveMember(entity, photoUri) {
          showAddDialog = false
          editingMember = null
          coroutineScope.launch {
            snackbarHostState.showSnackbar(
              if (uiState.isKannada) "${entity.kannadaName} ವಿವರ ಉಳಿಸಲಾಗಿದೆ" else "${entity.englishName} saved successfully"
            )
          }
        }
      }
    )
  }

  // Add Relationship Dialog
  if (memberForRelationship != null) {
    AddRelationshipDialog(
      currentMember = memberForRelationship!!,
      allMembers = uiState.members,
      isKannada = uiState.isKannada,
      onDismiss = { memberForRelationship = null },
      onAddRelationship = { targetId, relativeId, relationType ->
        viewModel.addRelationship(targetId, relativeId, relationType) {
          memberForRelationship = null
          coroutineScope.launch {
            snackbarHostState.showSnackbar(
              if (uiState.isKannada) "ಹೊಸ ಸಂಬಂಧ ಜೋಡಿಸಲಾಗಿದೆ" else "Relationship defined successfully"
            )
          }
        }
      }
    )
  }

  // Upload Media Dialog
  if (memberForUploadMedia != null) {
    UploadMediaDialog(
      member = memberForUploadMedia!!,
      isKannada = uiState.isKannada,
      onDismiss = { memberForUploadMedia = null },
      onUpload = { uri, titleKannada, titleEnglish, mediaType, description ->
        viewModel.addMemberMedia(
          memberId = memberForUploadMedia!!.id,
          uri = uri,
          titleKannada = titleKannada,
          titleEnglish = titleEnglish,
          mediaType = mediaType,
          description = description
        ) {
          memberForUploadMedia = null
          coroutineScope.launch {
            snackbarHostState.showSnackbar(
              if (uiState.isKannada) "ಮಾಧ್ಯಮ ದಾಖಲೆಯನ್ನು ಯಶಸ್ವಿಯಾಗಿ ಅಪ್ಲೋಡ್ ಮಾಡಲಾಗಿದೆ" else "Media uploaded successfully"
            )
          }
        }
      }
    )
  }

  // Media Viewer Dialog
  if (uiState.selectedMediaForViewer != null) {
    val media = uiState.selectedMediaForViewer!!
    val owner = uiState.members.find { it.id == media.memberId }

    MediaViewerDialog(
      media = media,
      ownerMember = owner,
      isKannada = uiState.isKannada,
      onDismiss = { viewModel.openMediaViewer(null) },
      onDelete = { toDelete ->
        viewModel.deleteMemberMedia(toDelete) {
          coroutineScope.launch {
            snackbarHostState.showSnackbar(
              if (uiState.isKannada) "ದಾಖಲೆ ತೆಗೆದುಹಾಕಲಾಗಿದೆ" else "Document deleted"
            )
          }
        }
      }
    )
  }
}
