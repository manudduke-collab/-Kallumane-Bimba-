package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.Gender
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.TempleOchre
import com.example.util.KinshipResult

@Composable
fun MemberListScreen(
  members: List<FamilyMemberEntity>,
  focusMember: FamilyMemberEntity?,
  isKannada: Boolean,
  onMemberClick: (FamilyMemberEntity) -> Unit,
  onAddMemberClick: () -> Unit,
  getKinshipForMember: (FamilyMemberEntity) -> KinshipResult
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedGenFilter by remember { mutableStateOf<Int?>(null) }
  var onlyLivingFilter by remember { mutableStateOf(false) }

  val filteredMembers = remember(members, searchQuery, selectedGenFilter, onlyLivingFilter) {
    members.filter { member ->
      val matchesSearch = if (searchQuery.isBlank()) true else {
        val q = searchQuery.lowercase()
        member.kannadaName.lowercase().contains(q) ||
            member.englishName.lowercase().contains(q) ||
            member.purveekaraOoru.lowercase().contains(q) ||
            member.gothra.lowercase().contains(q) ||
            member.maneHesaru.lowercase().contains(q) ||
            member.kulaDaiva.lowercase().contains(q)
      }
      val matchesGen = selectedGenFilter == null || member.generation == selectedGenFilter
      val matchesLiving = !onlyLivingFilter || member.isAlive
      matchesSearch && matchesGen && matchesLiving
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("member_directory_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Search Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              if (isKannada) "ಹೆಸರು, ಗೋತ್ರ, ಊರು, ಕುಲದೈವ ಹುಡುಕಿ..." else "Search by name, gothra, village...",
              fontSize = 13.sp
            )
          },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TempleOchre) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("directory_search_input"),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )
      }

      // Generation & Status Filter Chips
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedGenFilter == null && !onlyLivingFilter,
            onClick = {
              selectedGenFilter = null
              onlyLivingFilter = false
            },
            label = { Text(if (isKannada) "ಎಲ್ಲರೂ (${members.size})" else "All (${members.size})") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = HeritageGold.copy(alpha = 0.2f)
            )
          )

          val genOptions = listOf(
            1 to if (isKannada) "ಮುತ್ತಜ್ಜಂದಿರು (Gen 1)" else "Gen 1 (Ancestors)",
            2 to if (isKannada) "ಅಜ್ಜಂದಿರು (Gen 2)" else "Gen 2 (Grandparents)",
            3 to if (isKannada) "ಪೋಷಕರು (Gen 3)" else "Gen 3 (Parents)",
            4 to if (isKannada) "ನಾವು/ಸಹೋದರರು (Gen 4)" else "Gen 4 (Self/Sibs)",
            5 to if (isKannada) "ಮಕ್ಕಳು (Gen 5)" else "Gen 5 (Children)"
          )

          genOptions.forEach { (gen, label) ->
            FilterChip(
              selected = selectedGenFilter == gen,
              onClick = { selectedGenFilter = if (selectedGenFilter == gen) null else gen },
              label = { Text(label, fontSize = 11.sp) }
            )
          }

          FilterChip(
            selected = onlyLivingFilter,
            onClick = { onlyLivingFilter = !onlyLivingFilter },
            label = { Text(if (isKannada) "ಜೀವಂತ ಸದಸ್ಯರು" else "Living Only") }
          )
        }
      }

      // Results Count Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isKannada) "ಒಟ್ಟು ಸದಸ್ಯರು: ${filteredMembers.size}" else "Total Members: ${filteredMembers.size}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
        }
      }

      // Member Cards
      items(filteredMembers, key = { it.id }) { member ->
        val isSelf = focusMember?.id == member.id
        val kinship = getKinshipForMember(member)

        DirectoryMemberCard(
          member = member,
          isSelf = isSelf,
          kinship = kinship,
          isKannada = isKannada,
          onClick = { onMemberClick(member) }
        )
      }
    }

    // Add Member FAB
    FloatingActionButton(
      onClick = onAddMemberClick,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("directory_add_member_fab"),
      containerColor = KumkumaMaroon,
      contentColor = Color.White
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Member")
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isKannada) "ಸದಸ್ಯರನ್ನು ಸೇರಿಸಿ" else "Add Member",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
  }
}

@Composable
fun DirectoryMemberCard(
  member: FamilyMemberEntity,
  isSelf: Boolean,
  kinship: KinshipResult,
  isKannada: Boolean,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("directory_card_${member.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelf) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isSelf) TempleOchre else MaterialTheme.colorScheme.outlineVariant
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      MemberAvatar(member = member, size = 52.dp)

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = member.kannadaName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (member.gender == Gender.FEMALE) KumkumaMaroon else MaterialTheme.colorScheme.onSurface
          )
          if (isSelf) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = TempleOchre,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = if (isKannada) "ನನ್ನ ಸ್ಥಾನ" else "Self",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }

        Text(
          text = member.englishName,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Kinship relationship badge to self
        if (!isSelf) {
          Text(
            text = "ನಿಮಗೆ: ${if (isKannada) kinship.kannadaTerm else kinship.englishTerm}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TempleOchre
          )
        }

        // Details Row: Generation & Gothra & Ooru
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "ತಲೆಮಾರು ${member.generation}",
              fontSize = 9.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }

          if (member.gothra.isNotBlank()) {
            Surface(
              color = Color(0xFFFEF3C7),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = member.gothra.split("(").firstOrNull()?.trim() ?: member.gothra,
                fontSize = 9.sp,
                color = TempleOchre,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }

          if (member.purveekaraOoru.isNotBlank()) {
            Text(
              text = member.purveekaraOoru.split(",").firstOrNull()?.trim() ?: member.purveekaraOoru,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Icon(
        Icons.Default.ChevronRight,
        contentDescription = "View Details",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
