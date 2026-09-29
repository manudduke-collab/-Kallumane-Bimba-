package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.Gender
import com.example.data.model.MediaType
import com.example.data.model.MemberMediaEntity
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.TempleOchre
import com.example.util.ImageStorageHelper
import com.example.util.KinshipResult
import com.example.util.RelationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailSheet(
  member: FamilyMemberEntity,
  allMembers: List<FamilyMemberEntity>,
  memberMedia: List<MemberMediaEntity>,
  kinshipToFocus: KinshipResult,
  isFocusMember: Boolean,
  isKannada: Boolean,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSetAsFocus: (FamilyMemberEntity) -> Unit,
  onEdit: (FamilyMemberEntity) -> Unit,
  onDelete: (FamilyMemberEntity) -> Unit,
  onSelectMember: (FamilyMemberEntity) -> Unit,
  onPhotoUpdated: (Long, Uri) -> Unit,
  onAddRelationshipClick: () -> Unit,
  onRemoveRelationship: (RelationType, Long?) -> Unit,
  onUploadMediaClick: () -> Unit,
  onMediaClick: (MemberMediaEntity) -> Unit
) {
  var showDeleteConfirm by remember { mutableStateOf(false) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      onPhotoUpdated(member.id, uri)
    }
  }

  val memberMap = remember(allMembers) { allMembers.associateBy { it.id } }
  val father = member.fatherId?.let { memberMap[it] }
  val mother = member.motherId?.let { memberMap[it] }
  val spouse = member.spouseId?.let { memberMap[it] }
  val siblings = remember(member, allMembers) {
    allMembers.filter { other ->
      other.id != member.id &&
          ((member.fatherId != null && other.fatherId == member.fatherId) ||
              (member.motherId != null && other.motherId == member.motherId))
    }
  }
  val children = remember(member.id, allMembers) {
    allMembers.filter { it.fatherId == member.id || it.motherId == member.id }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("member_detail_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top Row: Avatar & Primary Info
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(92.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A))
              )
            )
            .border(2.5.dp, if (!member.isAlive) Color(0xFFC0A080) else HeritageGold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          if (!member.photoPath.isNullOrEmpty()) {
            AsyncImage(
              model = member.photoPath,
              contentDescription = member.kannadaName,
              modifier = Modifier
                .size(92.dp)
                .clip(CircleShape),
              contentScale = ContentScale.Crop
            )
          } else {
            val initial = member.kannadaName.firstOrNull()?.toString()
              ?: member.englishName.firstOrNull()?.toString()
              ?: "?"
            Text(
              text = initial,
              fontSize = 38.sp,
              fontWeight = FontWeight.Bold,
              color = if (member.gender == Gender.FEMALE) KumkumaMaroon else TempleOchre
            )
          }

          // Camera icon overlay to change photo
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .size(28.dp)
              .clip(CircleShape)
              .background(TempleOchre)
              .clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
              .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.CameraAlt,
              contentDescription = "Update photo",
              modifier = Modifier.size(15.dp),
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = member.kannadaName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
          Text(
            text = member.englishName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          val yearsText = buildString {
            if (member.birthYear.isNotBlank()) append(member.birthYear)
            if (!member.isAlive) {
              append(" - ")
              append(member.deathYear ?: (if (isKannada) "ಸ್ವರ್ಗಸ್ಥರು" else "Late"))
            }
          }
          if (yearsText.isNotBlank()) {
            Text(
              text = yearsText,
              style = MaterialTheme.typography.labelMedium,
              color = if (member.isAlive) Color(0xFF166534) else Color(0xFF4B5563)
            )
          }

          if (member.maneHesaru.isNotBlank()) {
            Text(
              text = "ಮನೆ: ${member.maneHesaru}",
              style = MaterialTheme.typography.labelSmall,
              color = TempleOchre,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // Kinship to Focus Person Highlight Card
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              Icons.Default.FamilyRestroom,
              contentDescription = null,
              tint = TempleOchre,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = if (isKannada) "ನಿಮಗೆ ಇವರ ಸಂಬಂಧ (Kinship to You)" else "Relationship to You",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = TempleOchre
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isKannada) kinshipToFocus.kannadaTerm else kinshipToFocus.englishTerm,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
          Text(
            text = if (isKannada) kinshipToFocus.explanationKannada else kinshipToFocus.explanationEnglish,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Quick Actions Row (Set as Center, Edit, Delete)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (!isFocusMember) {
          FilledTonalButton(
            onClick = { onSetAsFocus(member) },
            modifier = Modifier
              .weight(1f)
              .testTag("set_as_self_button"),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = HeritageGold.copy(alpha = 0.15f),
              contentColor = HeritageGold
            )
          ) {
            Icon(Icons.Default.CenterFocusStrong, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = if (isKannada) "ನನ್ನ ಸ್ಥಾನ" else "Set as Self", fontSize = 12.sp)
          }
        }

        OutlinedButton(
          onClick = { onEdit(member) },
          modifier = Modifier
            .weight(1f)
            .testTag("edit_member_button")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = if (isKannada) "ತಿದ್ದುಪಡಿ" else "Edit", fontSize = 12.sp)
        }

        IconButton(
          onClick = { showDeleteConfirm = true },
          modifier = Modifier.testTag("delete_member_icon_button")
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
        }
      }

      // --- SECTION 1: PHOTOS & DOCUMENTS (ಮಾಧ್ಯಮ ಮತ್ತು ಪಾರಂಪರಿಕ ದಾಖಲೆಗಳು) ---
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (isKannada) "ಭಾವಚಿತ್ರಗಳು ಮತ್ತು ದಾಖಲೆಗಳು" else "Photos & Documents",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = KumkumaMaroon
              )
              Text(
                text = "ಜಾತಕ, ಲಗ್ನ ಪತ್ರಿಕೆ, ಪ್ರಶಸ್ತಿ ಪತ್ರಗಳು (${memberMedia.size})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Button(
              onClick = onUploadMediaClick,
              colors = ButtonDefaults.buttonColors(containerColor = TempleOchre),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("add_media_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (isKannada) "ದಾಖಲೆ ಸೇರಿಸಿ" else "Add Media", fontSize = 12.sp)
            }
          }

          if (memberMedia.isEmpty()) {
            Card(
              colors = CardDefaults.cardColors(containerColor = Color.White),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = if (isKannada) {
                    "ಇನ್ನೂ ದಾಖಲೆಗಳು ಅಥವಾ ಫೋಟೋಗಳು ಸೇರಿಸಲಾಗಿಲ್ಲ. ಜಾತಕ ಕುಂಡಲಿ, ಲಗ್ನ ಪತ್ರಿಕೆ ಅಥವಾ ಹಳೆಯ ಪತ್ರಗಳನ್ನು ಅಪ್ಲೋಡ್ ಮಾಡಿ."
                  } else {
                    "No media uploaded yet. Add horoscopes, wedding cards, or ancestral photos."
                  },
                  fontSize = 12.sp,
                  color = Color.Gray
                )
              }
            }
          } else {
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              contentPadding = PaddingValues(vertical = 4.dp)
            ) {
              items(memberMedia, key = { it.id }) { item ->
                MediaThumbnailCard(
                  media = item,
                  isKannada = isKannada,
                  onClick = { onMediaClick(item) }
                )
              }
            }
          }
        }
      }

      // --- SECTION 2: RELATIONSHIP MAPPING (ಸಂಬಂಧ ಜೋಡಣೆ & ನಿರ್ವಹಣೆ) ---
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, HeritageGold.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (isKannada) "ವ್ಯಾಖ್ಯಾನಿಸಿದ ಸಂಬಂಧಗಳು" else "Defined Relationships",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = KumkumaMaroon
              )
              Text(
                text = if (isKannada) "ಪೋಷಕರು, ದಂಪತಿ, ಸಹೋದರರು & ಮಕ್ಕಳು" else "Parents, Spouse, Siblings & Children",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Button(
              onClick = onAddRelationshipClick,
              colors = ButtonDefaults.buttonColors(containerColor = KumkumaMaroon),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("add_relationship_button")
            ) {
              Icon(Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (isKannada) "ಸಂಬಂಧ ಜೋಡಿಸಿ" else "Link Relation", fontSize = 12.sp)
            }
          }

          // Parents (Father & Mother)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (father != null) {
              ManageableRelationCard(
                relationLabel = if (isKannada) "ತಂದೆ (Father)" else "Father",
                relative = father,
                modifier = Modifier.weight(1f),
                onSelect = onSelectMember,
                onUnlink = { onRemoveRelationship(RelationType.FATHER, null) }
              )
            }
            if (mother != null) {
              ManageableRelationCard(
                relationLabel = if (isKannada) "ತಾಯಿ (Mother)" else "Mother",
                relative = mother,
                modifier = Modifier.weight(1f),
                onSelect = onSelectMember,
                onUnlink = { onRemoveRelationship(RelationType.MOTHER, null) }
              )
            }
          }

          // Spouse
          if (spouse != null) {
            ManageableRelationCard(
              relationLabel = if (isKannada) "ಪತಿ / ಪತ್ನಿ (Spouse)" else "Spouse",
              relative = spouse,
              modifier = Modifier.fillMaxWidth(),
              onSelect = onSelectMember,
              onUnlink = { onRemoveRelationship(RelationType.SPOUSE, null) }
            )
          }

          // Siblings
          if (siblings.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = if (isKannada) "ಸಹೋದರರು / ಸಹೋದರಿಯರು (${siblings.size})" else "Siblings (${siblings.size})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TempleOchre
              )
              siblings.forEach { sib ->
                ManageableRelationCard(
                  relationLabel = if (sib.gender == Gender.MALE) {
                    if (isKannada) "ಸಹೋದರ (Brother)" else "Brother"
                  } else {
                    if (isKannada) "ಸಹೋದರಿ (Sister)" else "Sister"
                  },
                  relative = sib,
                  modifier = Modifier.fillMaxWidth(),
                  onSelect = onSelectMember,
                  onUnlink = { onRemoveRelationship(RelationType.SIBLING, sib.id) }
                )
              }
            }
          }

          // Children
          if (children.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = if (isKannada) "ಮಕ್ಕಳು (${children.size})" else "Children (${children.size})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TempleOchre
              )
              children.forEach { child ->
                ManageableRelationCard(
                  relationLabel = if (child.gender == Gender.MALE) {
                    if (isKannada) "ಮಗ (Son)" else "Son"
                  } else {
                    if (isKannada) "ಮಗಳು (Daughter)" else "Daughter"
                  },
                  relative = child,
                  modifier = Modifier.fillMaxWidth(),
                  onSelect = onSelectMember,
                  onUnlink = { onRemoveRelationship(if (child.gender == Gender.MALE) RelationType.SON else RelationType.DAUGHTER, child.id) }
                )
              }
            }
          }
        }
      }

      // --- SECTION 3: CULTURAL HERITAGE DETAILS ---
      Text(
        text = if (isKannada) "ಸಾಂಸ್ಕೃತಿಕ ಹಿನ್ನೆಲೆ & ಬೇರುಗಳು" else "Cultural Heritage & Roots",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = KumkumaMaroon
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (member.gothra.isNotBlank()) {
          HeritageInfoRow(
            icon = Icons.Default.TempleHindu,
            label = if (isKannada) "ಗೋತ್ರ (Gothra)" else "Gothra",
            value = member.gothra
          )
        }
        if (member.kulaDaiva.isNotBlank()) {
          HeritageInfoRow(
            icon = Icons.Default.TempleHindu,
            label = if (isKannada) "ಕುಲದೈವ / ಮನೆ ದೇವರು" else "Kula Daiva",
            value = member.kulaDaiva
          )
        }
        if (member.purveekaraOoru.isNotBlank()) {
          HeritageInfoRow(
            icon = Icons.Default.Place,
            label = if (isKannada) "ಮೂಲ ಊರು / ಪೂರ್ವಿಕರ ಸ್ಥಳ" else "Ancestral Village",
            value = member.purveekaraOoru
          )
        }
        if (member.kulaBranch.isNotBlank()) {
          HeritageInfoRow(
            icon = Icons.Default.Home,
            label = if (isKannada) "ಶಾಖೆ / ಪಂಗಡ" else "Branch / Community",
            value = member.kulaBranch
          )
        }
        if (member.mathaAffiliation.isNotBlank()) {
          HeritageInfoRow(
            icon = Icons.Default.TempleHindu,
            label = if (isKannada) "ಮಠ / ಗುರುಪೀಠ" else "Matha Affiliation",
            value = member.mathaAffiliation
          )
        }
        if (member.occupation.isNotBlank()) {
          HeritageInfoRow(
            icon = Icons.Default.Work,
            label = if (isKannada) "ವೃತ್ತಿ / ಸಾಧನೆ" else "Occupation",
            value = member.occupation
          )
        }
        if (member.contactNumber.isNotBlank()) {
          HeritageInfoRow(
            icon = Icons.Default.Phone,
            label = if (isKannada) "ಸಂಪರ್ಕ ಸಂಖ್ಯೆ" else "Phone",
            value = member.contactNumber
          )
        }
      }

      // Notes / Memories
      if (member.notes.isNotBlank()) {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = if (isKannada) "ನೆನಪುಗಳು & ಕಥೆಗಳು" else "Memories & Cultural Notes",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = KumkumaMaroon
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = member.notes,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }
  }

  // Delete Confirmation Dialog
  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text(if (isKannada) "ಸದಸ್ಯರನ್ನು ತೆಗೆದುಹಾಕುವುದೇ?" else "Delete Member?") },
      text = {
        Text(
          if (isKannada) "${member.kannadaName} ಅವರನ್ನು ವಂಶವೃಕ್ಷದಿಂದ ತೆಗೆದುಹಾಕಲು ಖಚಿತವೇ?"
          else "Are you sure you want to delete ${member.englishName} from the family tree?"
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirm = false
            onDelete(member)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text(if (isKannada) "ತೆಗೆದುಹಾಕಿ (Delete)" else "Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text(if (isKannada) "ರದ್ದು (Cancel)" else "Cancel")
        }
      }
    )
  }
}

@Composable
fun MediaThumbnailCard(
  media: MemberMediaEntity,
  isKannada: Boolean,
  onClick: () -> Unit
) {
  val typeIcon = when (media.mediaType) {
    MediaType.PHOTO -> Icons.Default.InsertPhoto
    MediaType.DOCUMENT -> Icons.Default.Description
    MediaType.KUNDALI -> Icons.Default.AutoAwesome
    MediaType.CERTIFICATE -> Icons.Default.WorkspacePremium
    MediaType.LETTER -> Icons.Default.HistoryEdu
  }

  Card(
    modifier = Modifier
      .width(130.dp)
      .clickable { onClick() }
      .testTag("media_card_${media.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (media.filePath.isNotBlank()) {
        AsyncImage(
          model = media.filePath,
          contentDescription = media.titleKannada,
          modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(6.dp)),
          contentScale = ContentScale.Crop
        )
      } else {
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFFEF3C7)),
          contentAlignment = Alignment.Center
        ) {
          Icon(typeIcon, contentDescription = null, tint = TempleOchre, modifier = Modifier.size(32.dp))
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (isKannada) media.titleKannada else media.titleEnglish,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      if (media.fileSizeBytes > 0) {
        Text(
          text = ImageStorageHelper.formatFileSize(media.fileSizeBytes),
          fontSize = 9.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun ManageableRelationCard(
  relationLabel: String,
  relative: FamilyMemberEntity,
  modifier: Modifier = Modifier,
  onSelect: (FamilyMemberEntity) -> Unit,
  onUnlink: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = Color.White),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(modifier = Modifier.clickable { onSelect(relative) }) {
        MemberAvatar(member = relative, size = 36.dp)
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column(
        modifier = Modifier
          .weight(1f)
          .clickable { onSelect(relative) }
      ) {
        Text(
          text = relationLabel,
          style = MaterialTheme.typography.labelSmall,
          color = TempleOchre,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = relative.kannadaName,
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      IconButton(onClick = onUnlink, modifier = Modifier.size(28.dp)) {
        Icon(
          Icons.Default.LinkOff,
          contentDescription = "Unlink",
          tint = Color.Gray,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
fun HeritageInfoRow(
  icon: ImageVector,
  label: String,
  value: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(TempleOchre.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = TempleOchre, modifier = Modifier.size(18.dp))
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
  }
}

