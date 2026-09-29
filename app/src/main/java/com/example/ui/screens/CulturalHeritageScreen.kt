package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CulturalHeritageEntity
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.FamilyStoryEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.HeritageGoldLight
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.TempleOchre
import com.example.util.KannadaKinshipCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CulturalHeritageScreen(
  heritage: CulturalHeritageEntity?,
  stories: List<FamilyStoryEntity>,
  members: List<FamilyMemberEntity>,
  focusMember: FamilyMemberEntity?,
  isKannada: Boolean,
  onUpdateHeritage: (CulturalHeritageEntity) -> Unit,
  onAddStory: (FamilyStoryEntity) -> Unit,
  onSelectMember: (FamilyMemberEntity) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var showEditHeritageDialog by remember { mutableStateOf(false) }
  var showAddStoryDialog by remember { mutableStateOf(false) }

  // Kinship explorer states
  var personA by remember(focusMember, members) {
    mutableStateOf(focusMember ?: members.firstOrNull())
  }
  var personB by remember(members) {
    mutableStateOf(members.getOrNull(2) ?: members.lastOrNull())
  }

  Column(modifier = Modifier.fillMaxSize()) {
    PrimaryTabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = KumkumaMaroon
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            if (isKannada) "ವಂಶ ಪರಂಪರೆ" else "Heritage",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        },
        icon = { Icon(Icons.Default.TempleHindu, contentDescription = null, modifier = Modifier.size(18.dp)) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            if (isKannada) "ಸಂಬಂಧ ಕೋಶ" else "Kinship",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        },
        icon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null, modifier = Modifier.size(18.dp)) }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Text(
            if (isKannada) "ಕಥೆಗಳು & ನೆನಪು" else "Stories",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        },
        icon = { Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(18.dp)) }
      )
    }

    when (selectedTab) {
      0 -> HeritageTabContent(
        heritage = heritage,
        isKannada = isKannada,
        onEditClick = { showEditHeritageDialog = true }
      )
      1 -> KinshipCalculatorTabContent(
        members = members,
        personA = personA,
        personB = personB,
        onSelectPersonA = { personA = it },
        onSelectPersonB = { personB = it },
        isKannada = isKannada,
        onMemberClick = onSelectMember
      )
      2 -> StoriesTabContent(
        stories = stories,
        isKannada = isKannada,
        onAddStoryClick = { showAddStoryDialog = true }
      )
    }
  }

  // Edit Heritage Dialog
  if (showEditHeritageDialog && heritage != null) {
    EditHeritageDialog(
      heritage = heritage,
      isKannada = isKannada,
      onDismiss = { showEditHeritageDialog = false },
      onSave = { updated ->
        onUpdateHeritage(updated)
        showEditHeritageDialog = false
      }
    )
  }

  // Add Story Dialog
  if (showAddStoryDialog) {
    AddStoryDialog(
      isKannada = isKannada,
      onDismiss = { showAddStoryDialog = false },
      onSave = { newStory ->
        onAddStory(newStory)
        showAddStoryDialog = false
      }
    )
  }
}

@Composable
fun HeritageTabContent(
  heritage: CulturalHeritageEntity?,
  isKannada: Boolean,
  onEditClick: () -> Unit
) {
  if (heritage == null) return

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("heritage_tab_content"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Banner
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = KumkumaMaroon),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isKannada) "ಕರ್ನಾಟಕದ ವಂಶ ಪರಂಪರೆ" else "Ancestral Roots of Karnataka",
              color = Color.White.copy(alpha = 0.8f),
              style = MaterialTheme.typography.labelMedium
            )
            IconButton(onClick = onEditClick) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Heritage", tint = Color.White)
            }
          }
          Text(
            text = if (isKannada) heritage.familyTitleKannada else heritage.familyTitleEnglish,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "ಮೂಲ: ${heritage.mulaOoru}",
            style = MaterialTheme.typography.bodyMedium,
            color = HeritageGoldLight
          )
        }
      }
    }

    // 1. Kula Daiva / Mane Devaru
    item {
      HeritageFeatureCard(
        icon = Icons.Default.TempleHindu,
        iconTint = TempleOchre,
        title = if (isKannada) "ಕುಲದೈವ / ಮನೆ ದೇವರು (Kula Daiva)" else "Family Deity (Kula Daiva)",
        mainValue = heritage.kulaDaiva,
        subtitle = "ದೇವಾಲಯದ ಸ್ಥಳ: ${heritage.kulaDaivaTemplePlace}",
        description = if (isKannada) {
          "ಪ್ರತಿ ಮಂಗಳಕಾರ್ಯ, ವಿವಾಹ ಮತ್ತು ವಾರ್ಷಿಕ ಜಾತ್ರೆಗಳಲ್ಲಿ ಮನೆ ದೇವರಿಗೆ ವಿಶೇಷ ಸೇವೆ, ಎಣ್ಣೆ ಶಾಸ್ತ್ರ ಹಾಗೂ ದೇವಸ್ಥಾನದ ಪ್ರಸಾದ ತರುವುದು ಪದ್ಧತಿ."
        } else {
          "Special prayers, annual pilgrimage, and offerings are traditionally dedicated to the family deity during milestones and festivals."
        }
      )
    }

    // 2. Gothra & Pravara
    item {
      HeritageFeatureCard(
        icon = Icons.Default.AutoAwesome,
        iconTint = HeritageGold,
        title = if (isKannada) "ಗೋತ್ರ ಮತ್ತು ಋಷಿ ಪರಂಪರೆ (Gothra)" else "Gothra & Sage Lineage",
        mainValue = heritage.primaryGothra,
        subtitle = if (isKannada) "ಸಪ್ತರ್ಷಿ ಪರಂಪರೆ" else "Lineage of Vedic Sages",
        description = if (isKannada) {
          "ಕುಟುಂಬವು ಪ್ರಾಚೀನ ಋಷಿ ಪರಂಪರೆಯಿಂದ ಬಂದ ಪವಿತ್ರ ಗೋತ್ರವನ್ನು ಹೊಂದಿದೆ. ಗೋತ್ರ ಪ್ರವರ ಮತ್ತು ಸಂಧ್ಯಾವಂದನೆ ಕಾಲದಲ್ಲಿ ಪೂರ್ವಿಕರ ಸ್ಮರಣೆ ಮಾಡಲಾಗುತ್ತದೆ."
        } else {
          "The sacred lineage originating from ancient sages, defining ritual purity and spiritual roots."
        }
      )
    }

    // 3. Mula Mane & Purveekara Ooru
    item {
      HeritageFeatureCard(
        icon = Icons.Default.Home,
        iconTint = Color(0xFF15803D),
        title = if (isKannada) "ಮೂಲ ಮನೆ ಮತ್ತು ಪೂರ್ವಿಕರ ಊರು" else "Ancestral House & Origin",
        mainValue = heritage.mulaOoru,
        subtitle = if (isKannada) "ಪೂರ್ವಜರ ನೆಲೆಬೀಡು" else "Ancestral Hearth",
        description = heritage.mulaManeDescription
      )
    }

    // 4. Guru Parampara & Matha Affiliation
    item {
      HeritageFeatureCard(
        icon = Icons.Default.MenuBook,
        iconTint = Color(0xFF9333EA),
        title = if (isKannada) "ಗುರು ಪರಂಪರೆ ಮತ್ತು ಮಠ" else "Matha & Spiritual Affiliation",
        mainValue = heritage.mathaAffiliation,
        subtitle = if (isKannada) "ಧಾರ್ಮಿಕ ಪೀಠ" else "Spiritual Guidance",
        description = if (isKannada) {
          "ಕುಟುಂಬದ ಗುರುಪೀಠಕ್ಕೆ ಪರಂಪರಾಗತ ನಿಷ್ಠೆ ಮತ್ತು ಜಗದ್ಗುರುಗಳ ಆಶೀರ್ವಾದದೊಂದಿಗೆ ಕುಟುಂಬವು ಮುನ್ನಡೆದಿದೆ."
        } else {
          "Traditional alignment and revered guidance from the spiritual center."
        }
      )
    }

    // 5. Main Festivals Celebrated (Mane Habba)
    item {
      HeritageFeatureCard(
        icon = Icons.Default.Celebration,
        iconTint = KumkumaMaroon,
        title = if (isKannada) "ಮನೆ ಹಬ್ಬಗಳು & ಸಂಪ್ರದಾಯಗಳು (Habba)" else "Family Festivals & Rituals",
        mainValue = heritage.mainFestivals,
        subtitle = if (isKannada) "ವಾರ್ಷಿಕ ಆಚರಣೆಗಳು" else "Annual Festivities",
        description = if (isKannada) {
          "ವರ್ಷದ ಪ್ರಮುಖ ಹಬ್ಬಗಳಲ್ಲಿ ಕುಟುಂಬದ ಎಲ್ಲಾ ಸದಸ್ಯರು ಒಟ್ಟಾಗಿ ಸೇರಿ ಹಿರಿಯರಿಗೆ ನಮಸ್ಕರಿಸಿ ಪ್ರಸಾದ ಸ್ವೀಕರಿಸುವುದು ಮಲೆನಾಡು ಹಾಗೂ ಕರ್ನಾಟಕದ ಸದ್ಗುಣ."
        } else {
          "Traditional festivals bring all generations together in devotion, culinary feasts, and honoring family elders."
        }
      )
    }
  }
}

@Composable
fun HeritageFeatureCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  title: String,
  mainValue: String,
  subtitle: String,
  description: String
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(iconTint.copy(alpha = 0.14f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = TempleOchre,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = mainValue,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
fun KinshipCalculatorTabContent(
  members: List<FamilyMemberEntity>,
  personA: FamilyMemberEntity?,
  personB: FamilyMemberEntity?,
  onSelectPersonA: (FamilyMemberEntity) -> Unit,
  onSelectPersonB: (FamilyMemberEntity) -> Unit,
  isKannada: Boolean,
  onMemberClick: (FamilyMemberEntity) -> Unit
) {
  var showDropdownA by remember { mutableStateOf(false) }
  var showDropdownB by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("kinship_calculator_tab"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CompareArrows, contentDescription = null, tint = TempleOchre)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isKannada) "ಕನ್ನಡ ಸಂಬಂಧ ನಿರ್ಣಯ (Kinship Calculator)" else "Kannada Kinship Calculator",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = KumkumaMaroon
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isKannada) {
              "ಕುಟುಂಬದ ಯಾವುದೇ ಇಬ್ಬರು ಸದಸ್ಯರನ್ನು ಆಯ್ಕೆ ಮಾಡಿ. ಕನ್ನಡ ಪರಂಪರೆಯ ಪ್ರಕಾರ ಅವರ ಪರಸ್ಪರ ಸಂಬಂಧವನ್ನು ಕ್ಷಣಮಾತ್ರದಲ್ಲಿ ತಿಳಿದುಕೊಳ್ಳಿ!"
            } else {
              "Select any two family members to calculate their exact authentic Kannada kinship term and relationship path."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Person A Selector
    item {
      Column {
        Text(
          text = if (isKannada) "ಮೊದಲ ವ್ಯಕ್ತಿ (Person 1 - Reference):" else "Person 1 (Reference):",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showDropdownA = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (personA != null) {
                MemberAvatar(member = personA, size = 42.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = personA.kannadaName, fontWeight = FontWeight.Bold)
                  Text(text = personA.englishName, style = MaterialTheme.typography.bodySmall)
                }
              } else {
                Text(text = "ವ್ಯಕ್ತಿಯನ್ನು ಆಯ್ಕೆಮಾಡಿ")
              }
              Text(
                text = if (isKannada) "ಬದಲಾಯಿಸಿ ▾" else "Change ▾",
                color = TempleOchre,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          DropdownMenu(
            expanded = showDropdownA,
            onDismissRequest = { showDropdownA = false }
          ) {
            members.forEach { m ->
              DropdownMenuItem(
                text = { Text("${m.kannadaName} (${m.englishName})") },
                onClick = {
                  onSelectPersonA(m)
                  showDropdownA = false
                }
              )
            }
          }
        }
      }
    }

    // Person B Selector
    item {
      Column {
        Text(
          text = if (isKannada) "ಎರಡನೇ ವ್ಯಕ್ತಿ (Person 2 - Target):" else "Person 2 (Target):",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showDropdownB = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (personB != null) {
                MemberAvatar(member = personB, size = 42.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = personB.kannadaName, fontWeight = FontWeight.Bold)
                  Text(text = personB.englishName, style = MaterialTheme.typography.bodySmall)
                }
              } else {
                Text(text = "ವ್ಯಕ್ತಿಯನ್ನು ಆಯ್ಕೆಮಾಡಿ")
              }
              Text(
                text = if (isKannada) "ಬದಲಾಯಿಸಿ ▾" else "Change ▾",
                color = TempleOchre,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          DropdownMenu(
            expanded = showDropdownB,
            onDismissRequest = { showDropdownB = false }
          ) {
            members.forEach { m ->
              DropdownMenuItem(
                text = { Text("${m.kannadaName} (${m.englishName})") },
                onClick = {
                  onSelectPersonB(m)
                  showDropdownB = false
                }
              )
            }
          }
        }
      }
    }

    // Calculated Relationship Result
    if (personA != null && personB != null) {
      item {
        val result = remember(personA, personB, members) {
          KannadaKinshipCalculator.calculateKinship(personA, personB, members)
        }

        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, KumkumaMaroon.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (isKannada) "${personA.kannadaName} ಅವರಿಗೆ ${personB.kannadaName} ಅವರು:" else "Relationship of ${personB.englishName} to ${personA.englishName}:",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = result.kannadaTerm,
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = KumkumaMaroon
            )
            Text(
              text = "(${result.englishTerm})",
              style = MaterialTheme.typography.titleSmall,
              color = TempleOchre,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              color = Color.White,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "ವಿವರಣೆ: ${result.explanationKannada}",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = "Meaning: ${result.explanationEnglish}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun StoriesTabContent(
  stories: List<FamilyStoryEntity>,
  isKannada: Boolean,
  onAddStoryClick: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("stories_tab_content"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isKannada) "ಪೂರ್ವಿಕರ ಕಥೆಗಳು & ನೆನಪುಗಳು" else "Ancestral Stories & Legends",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
          Text(
            text = if (isKannada) "ತಲೆಮಾರುಗಳಿಂದ ದಾಟಿದ ಐತಿಹ್ಯಗಳು" else "Folklore passed down through generations",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = onAddStoryClick,
          colors = ButtonDefaults.buttonColors(containerColor = KumkumaMaroon),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (isKannada) "ಕಥೆ ಸೇರಿಸಿ" else "Add Story", fontSize = 12.sp)
        }
      }
    }

    items(stories, key = { it.id }) { story ->
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = Color(0xFFFEF3C7),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "ಕಾಲ: ${story.eraOrYear}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TempleOchre,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "ನಾಯಕರು: ${story.narratorOrHero}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = if (isKannada) story.titleKannada else story.titleEnglish,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = if (isKannada) story.contentKannada else story.contentEnglish,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
fun EditHeritageDialog(
  heritage: CulturalHeritageEntity,
  isKannada: Boolean,
  onDismiss: () -> Unit,
  onSave: (CulturalHeritageEntity) -> Unit
) {
  var familyTitleKannada by remember { mutableStateOf(heritage.familyTitleKannada) }
  var familyTitleEnglish by remember { mutableStateOf(heritage.familyTitleEnglish) }
  var primaryGothra by remember { mutableStateOf(heritage.primaryGothra) }
  var kulaDaiva by remember { mutableStateOf(heritage.kulaDaiva) }
  var kulaDaivaPlace by remember { mutableStateOf(heritage.kulaDaivaTemplePlace) }
  var mulaOoru by remember { mutableStateOf(heritage.mulaOoru) }
  var mulaManeDescription by remember { mutableStateOf(heritage.mulaManeDescription) }
  var mathaAffiliation by remember { mutableStateOf(heritage.mathaAffiliation) }
  var mainFestivals by remember { mutableStateOf(heritage.mainFestivals) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isKannada) "ವಂಶ ಪರಂಪರೆಯ ವಿವರ ತಿದ್ದುಪಡಿ" else "Edit Family Heritage Profile",
        fontWeight = FontWeight.Bold,
        color = KumkumaMaroon
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = familyTitleKannada,
          onValueChange = { familyTitleKannada = it },
          label = { Text("ಕುಟುಂಬದ ಶೀರ್ಷಿಕೆ (ಕನ್ನಡ)") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = primaryGothra,
          onValueChange = { primaryGothra = it },
          label = { Text("ಪ್ರಮುಖ ಗೋತ್ರ") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = kulaDaiva,
          onValueChange = { kulaDaiva = it },
          label = { Text("ಕುಲದೈವ / ಮನೆ ದೇವರು") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = mulaOoru,
          onValueChange = { mulaOoru = it },
          label = { Text("ಮೂಲ ಊರು (Ancestral Village)") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = mulaManeDescription,
          onValueChange = { mulaManeDescription = it },
          label = { Text("ಮೂಲ ಮನೆಯ ಇತಿಹಾಸ ಮತ್ತು ವಿವರ") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(
            heritage.copy(
              familyTitleKannada = familyTitleKannada,
              familyTitleEnglish = familyTitleEnglish,
              primaryGothra = primaryGothra,
              kulaDaiva = kulaDaiva,
              kulaDaivaTemplePlace = kulaDaivaPlace,
              mulaOoru = mulaOoru,
              mulaManeDescription = mulaManeDescription,
              mathaAffiliation = mathaAffiliation,
              mainFestivals = mainFestivals
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = KumkumaMaroon)
      ) {
        Text("ಉಳಿಸಿ")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("ರದ್ದು") }
    }
  )
}

@Composable
fun AddStoryDialog(
  isKannada: Boolean,
  onDismiss: () -> Unit,
  onSave: (FamilyStoryEntity) -> Unit
) {
  var titleKannada by remember { mutableStateOf("") }
  var contentKannada by remember { mutableStateOf("") }
  var eraOrYear by remember { mutableStateOf("") }
  var narratorOrHero by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isKannada) "ಹೊಸ ಪೂರ್ವಿಕರ ಕಥೆ ಸೇರಿಸಿ" else "Add Family Story",
        fontWeight = FontWeight.Bold,
        color = KumkumaMaroon
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = titleKannada,
          onValueChange = { titleKannada = it },
          label = { Text("ಕಥೆಯ ಶೀರ್ಷಿಕೆ (Title)") },
          placeholder = { Text("ಉದಾ: ದೊಡ್ಡಮನೆಯ ಐತಿಹ್ಯ") },
          modifier = Modifier.fillMaxWidth()
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = eraOrYear,
            onValueChange = { eraOrYear = it },
            label = { Text("ಕಾಲ / ಇಸವಿ (Year)") },
            placeholder = { Text("1950") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = narratorOrHero,
            onValueChange = { narratorOrHero = it },
            label = { Text("ಹಿರಿಯರು / ನಾಯಕರು") },
            placeholder = { Text("ಅಜ್ಜ / ಮುತ್ತಜ್ಜ") },
            modifier = Modifier.weight(1f)
          )
        }
        OutlinedTextField(
          value = contentKannada,
          onValueChange = { contentKannada = it },
          label = { Text("ಕಥೆಯ ಪೂರ್ಣ ವಿವರ (Story Content)") },
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
          maxLines = 4
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (titleKannada.isNotBlank()) {
            onSave(
              FamilyStoryEntity(
                titleKannada = titleKannada,
                titleEnglish = titleKannada,
                contentKannada = contentKannada,
                contentEnglish = contentKannada,
                eraOrYear = eraOrYear,
                narratorOrHero = narratorOrHero
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = KumkumaMaroon)
      ) {
        Text("ಸೇರಿಸಿ")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("ರದ್ದು") }
    }
  )
}
