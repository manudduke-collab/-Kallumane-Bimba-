package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.Gender
import com.example.data.model.MediaType
import com.example.data.model.MemberMediaEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.TempleOchre
import com.example.util.KinshipResult
import com.example.util.RelationshipEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeVisualizerScreen(
  members: List<FamilyMemberEntity>,
  allMedia: List<MemberMediaEntity>,
  focusMember: FamilyMemberEntity?,
  highlightedMemberId: Long?,
  isKannada: Boolean,
  onMemberClick: (FamilyMemberEntity) -> Unit,
  onAddMemberClick: () -> Unit,
  onAddRelationshipClick: (FamilyMemberEntity) -> Unit,
  onMediaBadgeClick: (MemberMediaEntity) -> Unit,
  getKinshipForMember: (FamilyMemberEntity) -> KinshipResult
) {
  var selectedLineageFilter by remember { mutableStateOf("ALL") } // ALL, PATERNAL, MATERNAL
  var searchQuery by remember { mutableStateOf("") }

  // Map media by memberId
  val mediaMap = remember(allMedia) {
    allMedia.groupBy { it.memberId }
  }

  // Identify direct network for highlighted member (if any)
  val highlightedNetwork = remember(highlightedMemberId, members) {
    val target = members.find { it.id == highlightedMemberId }
    if (target != null) {
      RelationshipEngine.getDirectRelatives(target, members)
    } else null
  }

  val filteredMembers = remember(members, selectedLineageFilter, searchQuery) {
    var list = members
    if (searchQuery.isNotBlank()) {
      val q = searchQuery.lowercase()
      list = list.filter {
        it.kannadaName.lowercase().contains(q) ||
            it.englishName.lowercase().contains(q) ||
            it.purveekaraOoru.lowercase().contains(q) ||
            it.gothra.lowercase().contains(q) ||
            it.maneHesaru.lowercase().contains(q)
      }
    }
    when (selectedLineageFilter) {
      "PATERNAL" -> list.filter { it.gothra.contains("ಕಾಶ್ಯಪ") || it.gothra.contains("Kashyapa") || it.generation <= 2 }
      "MATERNAL" -> list.filter { it.purveekaraOoru.contains("ಉಡುಪಿ") || it.gothra.contains("ಕೌಂಡಿನ್ಯ") || it.gender == Gender.FEMALE }
      else -> list
    }
  }

  val gen1Members = filteredMembers.filter { it.generation == 1 }
  val gen2Members = filteredMembers.filter { it.generation == 2 }
  val gen3Members = filteredMembers.filter { it.generation == 3 }
  val gen4Members = filteredMembers.filter { it.generation == 4 }
  val gen5Members = filteredMembers.filter { it.generation == 5 }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("tree_visualizer_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Search and Filter Bar
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
              Text(
                if (isKannada) "ಕುಟುಂಬದ ಸದಸ್ಯರು, ಊರು, ಗೋತ್ರ ಹುಡುಕಿ..." else "Search member, village, gothra...",
                fontSize = 13.sp
              )
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TempleOchre) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("tree_search_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          // Lineage Branch Filter Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = selectedLineageFilter == "ALL",
              onClick = { selectedLineageFilter = "ALL" },
              label = { Text(if (isKannada) "ಸಂಪೂರ್ಣ ವಂಶ (All)" else "All Lineages") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = HeritageGold.copy(alpha = 0.2f)
              )
            )
            FilterChip(
              selected = selectedLineageFilter == "PATERNAL",
              onClick = { selectedLineageFilter = "PATERNAL" },
              label = { Text(if (isKannada) "ಪಿತೃ ವಂಶ (Paternal)" else "Paternal Branch") }
            )
            FilterChip(
              selected = selectedLineageFilter == "MATERNAL",
              onClick = { selectedLineageFilter = "MATERNAL" },
              label = { Text(if (isKannada) "ಮಾತೃ ವಂಶ (Maternal)" else "Maternal Branch") }
            )
          }

          // Active Focus & Connection Legend Banner
          Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.CenterFocusStrong,
                  contentDescription = null,
                  tint = TempleOchre,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (focusMember != null) {
                    if (isKannada) "ಕೇಂದ್ರ ಸ್ಥಾನ: ${focusMember.kannadaName}" else "Self Reference: ${focusMember.englishName}"
                  } else {
                    if (isKannada) "ಸಂಬಂಧಗಳ ದೃಶ್ಯ ನಕ್ಷೆ" else "Visual Relationship Map"
                  },
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = KumkumaMaroon
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              // Visual Connection Indicators Legend
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                LegendBadge(color = Color(0xFF15803D), label = if (isKannada) "ತಂದೆ-ತಾಯಿ" else "Parents")
                LegendBadge(color = KumkumaMaroon, label = if (isKannada) "ದಂಪತಿ (ಪತಿ/ಪತ್ನಿ)" else "Spouse")
                LegendBadge(color = Color(0xFF1D4ED8), label = if (isKannada) "ಸಹೋದರರು" else "Siblings")
                LegendBadge(color = TempleOchre, label = if (isKannada) "ಮಕ್ಕಳು" else "Children")
              }
            }
          }
        }
      }

      // Generation 1: Ancestors / Great-Grandparents
      if (gen1Members.isNotEmpty()) {
        item {
          GenerationVisualSection(
            levelNumber = 1,
            titleKannada = "೧. ಮೂಲ ಪೂರ್ವಿಕರು / ಮುತ್ತಜ್ಜಂದಿರು",
            titleEnglish = "1. Great-Grandparents & Lineage Founders",
            subtitle = if (isKannada) "ವಂಶದ ಹಿರಿಯ ಬೇರುಗಳು (Ancestral Roots)" else "Ancestral Roots",
            members = gen1Members,
            allMembers = members,
            mediaMap = mediaMap,
            focusMember = focusMember,
            highlightedMemberId = highlightedMemberId,
            highlightedNetwork = highlightedNetwork,
            isKannada = isKannada,
            onMemberClick = onMemberClick,
            onAddRelationshipClick = onAddRelationshipClick,
            onMediaBadgeClick = onMediaBadgeClick,
            getKinship = getKinshipForMember
          )
        }
      }

      // Generation 2: Grandparents
      if (gen2Members.isNotEmpty()) {
        item {
          GenerationVisualSection(
            levelNumber = 2,
            titleKannada = "೨. ಅಜ್ಜ-ಅಜ್ಜಿಯಂದಿರ ತಲೆಮಾರು",
            titleEnglish = "2. Grandparents & Elders",
            subtitle = if (isKannada) "ಮಾರ್ಗದರ್ಶಿ ಹಿರಿಯರು (Patriarchs & Matriarchs)" else "Patriarchs & Matriarchs",
            members = gen2Members,
            allMembers = members,
            mediaMap = mediaMap,
            focusMember = focusMember,
            highlightedMemberId = highlightedMemberId,
            highlightedNetwork = highlightedNetwork,
            isKannada = isKannada,
            onMemberClick = onMemberClick,
            onAddRelationshipClick = onAddRelationshipClick,
            onMediaBadgeClick = onMediaBadgeClick,
            getKinship = getKinshipForMember
          )
        }
      }

      // Generation 3: Parents & Uncles/Aunts
      if (gen3Members.isNotEmpty()) {
        item {
          GenerationVisualSection(
            levelNumber = 3,
            titleKannada = "೩. ಪೋಷಕರ ತಲೆಮಾರು (ತಂದೆ-ತಾಯಿ, ದೊಡ್ಡಪ್ಪ, ಚಿಕ್ಕಪ್ಪ)",
            titleEnglish = "3. Parental Generation (Parents & Aunts/Uncles)",
            subtitle = if (isKannada) "ಕುಟುಂಬದ ಪಾಲಕರು (Guardians)" else "Guardians",
            members = gen3Members,
            allMembers = members,
            mediaMap = mediaMap,
            focusMember = focusMember,
            highlightedMemberId = highlightedMemberId,
            highlightedNetwork = highlightedNetwork,
            isKannada = isKannada,
            onMemberClick = onMemberClick,
            onAddRelationshipClick = onAddRelationshipClick,
            onMediaBadgeClick = onMediaBadgeClick,
            getKinship = getKinshipForMember
          )
        }
      }

      // Generation 4: Self, Siblings & Cousins
      if (gen4Members.isNotEmpty()) {
        item {
          GenerationVisualSection(
            levelNumber = 4,
            titleKannada = "೪. ನಾವು - ಸಹೋದರರು ಮತ್ತು ದಂಪತಿಗಳು",
            titleEnglish = "4. Present Generation (Self, Siblings & Cousins)",
            subtitle = if (isKannada) "ಸಮಕಾಲೀನ ತಲೆಮಾರು (Current Generation)" else "Current Generation",
            members = gen4Members,
            allMembers = members,
            mediaMap = mediaMap,
            focusMember = focusMember,
            highlightedMemberId = highlightedMemberId,
            highlightedNetwork = highlightedNetwork,
            isKannada = isKannada,
            onMemberClick = onMemberClick,
            onAddRelationshipClick = onAddRelationshipClick,
            onMediaBadgeClick = onMediaBadgeClick,
            getKinship = getKinshipForMember
          )
        }
      }

      // Generation 5: Children & Nieces/Nephews
      if (gen5Members.isNotEmpty()) {
        item {
          GenerationVisualSection(
            levelNumber = 5,
            titleKannada = "೫. ಮುಂದಿನ ತಲೆಮಾರು - ಮಕ್ಕಳು ಮತ್ತು ಕುಡಿಗಳು",
            titleEnglish = "5. Next Generation (Children & Descendants)",
            subtitle = if (isKannada) "ಕುಟುಂಬದ ಮುಂದಿನ ಭವಿಷ್ಯ (Next Gen Heirs)" else "Future Heirs",
            members = gen5Members,
            allMembers = members,
            mediaMap = mediaMap,
            focusMember = focusMember,
            highlightedMemberId = highlightedMemberId,
            highlightedNetwork = highlightedNetwork,
            isKannada = isKannada,
            onMemberClick = onMemberClick,
            onAddRelationshipClick = onAddRelationshipClick,
            onMediaBadgeClick = onMediaBadgeClick,
            getKinship = getKinshipForMember
          )
        }
      }
    }

    // Floating Action Button to Add Family Member
    FloatingActionButton(
      onClick = onAddMemberClick,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("add_member_fab"),
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
fun LegendBadge(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
  }
}

@Composable
fun GenerationVisualSection(
  levelNumber: Int,
  titleKannada: String,
  titleEnglish: String,
  subtitle: String,
  members: List<FamilyMemberEntity>,
  allMembers: List<FamilyMemberEntity>,
  mediaMap: Map<Long, List<MemberMediaEntity>>,
  focusMember: FamilyMemberEntity?,
  highlightedMemberId: Long?,
  highlightedNetwork: com.example.util.DirectRelatives?,
  isKannada: Boolean,
  onMemberClick: (FamilyMemberEntity) -> Unit,
  onAddRelationshipClick: (FamilyMemberEntity) -> Unit,
  onMediaBadgeClick: (MemberMediaEntity) -> Unit,
  getKinship: (FamilyMemberEntity) -> KinshipResult
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Header Banner for Generation
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(26.dp)
          .clip(CircleShape)
          .background(TempleOchre),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$levelNumber",
          color = Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = if (isKannada) titleKannada else titleEnglish,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = KumkumaMaroon
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Horizontal Scroll Row of Connected Family Nodes
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
    ) {
      items(members, key = { it.id }) { member ->
        val isSelf = focusMember?.id == member.id
        val kinship = getKinship(member)
        val memberMediaList = mediaMap[member.id] ?: emptyList()

        // Relationship role relative to highlighted member
        val relationRole = if (highlightedMemberId != null && highlightedNetwork != null) {
          when {
            member.id == highlightedMemberId -> HighlightRole.SELECTED
            member.id == highlightedNetwork.father?.id || member.id == highlightedNetwork.mother?.id -> HighlightRole.PARENT
            member.id == highlightedNetwork.spouse?.id -> HighlightRole.SPOUSE
            highlightedNetwork.siblings.any { it.id == member.id } -> HighlightRole.SIBLING
            highlightedNetwork.children.any { it.id == member.id } -> HighlightRole.CHILD
            else -> HighlightRole.NONE
          }
        } else HighlightRole.NONE

        VisualTreeNodeCard(
          member = member,
          isSelf = isSelf,
          relationRole = relationRole,
          kinship = kinship,
          mediaList = memberMediaList,
          isKannada = isKannada,
          onClick = { onMemberClick(member) },
          onAddRelation = { onAddRelationshipClick(member) },
          onMediaBadgeClick = { media -> onMediaBadgeClick(media) }
        )
      }
    }

    Divider(
      color = HeritageGold.copy(alpha = 0.25f),
      thickness = 1.dp,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}

enum class HighlightRole {
  SELECTED,
  PARENT,
  SPOUSE,
  SIBLING,
  CHILD,
  NONE
}

@Composable
fun VisualTreeNodeCard(
  member: FamilyMemberEntity,
  isSelf: Boolean,
  relationRole: HighlightRole,
  kinship: KinshipResult,
  mediaList: List<MemberMediaEntity>,
  isKannada: Boolean,
  onClick: () -> Unit,
  onAddRelation: () -> Unit,
  onMediaBadgeClick: (MemberMediaEntity) -> Unit
) {
  // Border and halo colors based on relationship role
  val (cardBorderColor, borderWidth, roleLabel, roleColor) = when (relationRole) {
    HighlightRole.SELECTED -> Quadruple(TempleOchre, 2.5.dp, if (isKannada) "ಆಯ್ಕೆ" else "Selected", TempleOchre)
    HighlightRole.PARENT -> Quadruple(Color(0xFF15803D), 2.5.dp, if (isKannada) "ಪೋಷಕರು" else "Parent", Color(0xFF15803D))
    HighlightRole.SPOUSE -> Quadruple(KumkumaMaroon, 2.5.dp, if (isKannada) "ದಂಪತಿ" else "Spouse", KumkumaMaroon)
    HighlightRole.SIBLING -> Quadruple(Color(0xFF1D4ED8), 2.5.dp, if (isKannada) "ಸಹೋದರ/ರಿ" else "Sibling", Color(0xFF1D4ED8))
    HighlightRole.CHILD -> Quadruple(HeritageGold, 2.5.dp, if (isKannada) "ಮಕ್ಕಳು" else "Child", HeritageGold)
    HighlightRole.NONE -> {
      if (isSelf) Quadruple(TempleOchre, 2.dp, null, TempleOchre)
      else if (!member.isAlive) Quadruple(Color(0xFFD1C7B7), 1.dp, null, Color.Gray)
      else if (member.gender == Gender.FEMALE) Quadruple(KumkumaMaroon.copy(alpha = 0.5f), 1.dp, null, KumkumaMaroon)
      else Quadruple(HeritageGold.copy(alpha = 0.5f), 1.dp, null, HeritageGold)
    }
  }

  val backgroundColor = when {
    isSelf -> Color(0xFFFEF3C7)
    relationRole == HighlightRole.PARENT -> Color(0xFFF0FDF4)
    relationRole == HighlightRole.SPOUSE -> Color(0xFFFFF1F2)
    relationRole == HighlightRole.SIBLING -> Color(0xFFEFF6FF)
    relationRole == HighlightRole.CHILD -> Color(0xFFFFFBEB)
    !member.isAlive -> Color(0xFFF9F7F4)
    member.gender == Gender.FEMALE -> Color(0xFFFFF7F8)
    else -> Color(0xFFFDFCF9)
  }

  val photoCount = mediaList.count { it.mediaType == MediaType.PHOTO }
  val docCount = mediaList.count { it.mediaType != MediaType.PHOTO }

  Card(
    modifier = Modifier
      .width(174.dp)
      .shadow(elevation = if (isSelf || relationRole != HighlightRole.NONE) 4.dp else 1.5.dp, shape = RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .testTag("tree_node_${member.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    border = androidx.compose.foundation.BorderStroke(borderWidth, cardBorderColor)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Role Banner or Status
      if (roleLabel != null) {
        Surface(
          color = roleColor,
          shape = RoundedCornerShape(4.dp),
          modifier = Modifier.padding(bottom = 4.dp)
        ) {
          Text(
            text = roleLabel,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      } else if (isSelf) {
        Surface(
          color = TempleOchre,
          shape = RoundedCornerShape(4.dp),
          modifier = Modifier.padding(bottom = 4.dp)
        ) {
          Text(
            text = if (isKannada) "★ ನನ್ನ ಸ್ಥಾನ (Self)" else "★ You (Self)",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      } else if (!member.isAlive) {
        Surface(
          color = Color(0xFF6B7280),
          shape = RoundedCornerShape(4.dp),
          modifier = Modifier.padding(bottom = 4.dp)
        ) {
          Text(
            text = if (isKannada) "ದಿವಂಗತ" else "In Memory",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
          )
        }
      } else {
        Spacer(modifier = Modifier.height(2.dp))
      }

      // Avatar
      MemberAvatar(member = member, size = 52.dp)

      Spacer(modifier = Modifier.height(4.dp))

      // Name
      Text(
        text = if (isKannada) member.kannadaName else member.englishName,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = if (member.gender == Gender.FEMALE) KumkumaMaroon else MaterialTheme.colorScheme.onSurface
      )

      // Kinship to Self / Focus
      if (!isSelf) {
        Text(
          text = if (isKannada) kinship.kannadaTerm else kinship.englishTerm,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = TempleOchre,
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Connection Indicators: Spouse Knot & Parents Tag
      if (member.spouseId != null) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Icon(Icons.Default.Favorite, contentDescription = "Spouse", tint = KumkumaMaroon, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = if (isKannada) "ದಂಪತಿ ಕೊಂಡಿ" else "Married",
            fontSize = 9.sp,
            color = KumkumaMaroon,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Media Badges (Photos and Documents uploaded for this person)
      if (mediaList.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(6.dp))
            .border(0.5.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .clickable {
              val firstMedia = mediaList.firstOrNull()
              if (firstMedia != null) onMediaBadgeClick(firstMedia)
            }
        ) {
          if (photoCount > 0) {
            Icon(Icons.Default.InsertPhoto, contentDescription = null, tint = TempleOchre, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "$photoCount", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TempleOchre)
            Spacer(modifier = Modifier.width(6.dp))
          }
          if (docCount > 0) {
            Icon(Icons.Default.Description, contentDescription = null, tint = KumkumaMaroon, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "$docCount", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KumkumaMaroon)
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isKannada) "ದಾಖಲೆ" else "Media",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Quick Action to Define / Add Relationship
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onAddRelation() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.AddLink, contentDescription = null, tint = TempleOchre, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = if (isKannada) "+ ಸಂಬಂಧ" else "+ Relation",
          fontSize = 10.sp,
          color = TempleOchre,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

private data class Quadruple<A, B, C, D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)
