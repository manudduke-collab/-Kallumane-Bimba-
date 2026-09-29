package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.Gender
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.TempleOchre
import com.example.util.RelationType

@Composable
fun AddRelationshipDialog(
  currentMember: FamilyMemberEntity,
  allMembers: List<FamilyMemberEntity>,
  isKannada: Boolean,
  onDismiss: () -> Unit,
  onAddRelationship: (targetId: Long, relativeId: Long, relationType: RelationType) -> Unit
) {
  var selectedRelationType by remember { mutableStateOf(RelationType.SPOUSE) }
  var selectedRelative by remember { mutableStateOf<FamilyMemberEntity?>(null) }
  var showRelativeDropdown by remember { mutableStateOf(false) }

  // Eligible candidates based on relationship type
  val eligibleCandidates = remember(selectedRelationType, currentMember, allMembers) {
    allMembers.filter { candidate ->
      if (candidate.id == currentMember.id) return@filter false
      when (selectedRelationType) {
        RelationType.FATHER -> candidate.gender == Gender.MALE && candidate.id != currentMember.fatherId
        RelationType.MOTHER -> candidate.gender == Gender.FEMALE && candidate.id != currentMember.motherId
        RelationType.SPOUSE -> candidate.id != currentMember.spouseId
        RelationType.SIBLING -> candidate.id != currentMember.fatherId && candidate.id != currentMember.motherId
        RelationType.SON -> candidate.gender == Gender.MALE && candidate.fatherId != currentMember.id && candidate.motherId != currentMember.id
        RelationType.DAUGHTER -> candidate.gender == Gender.FEMALE && candidate.fatherId != currentMember.id && candidate.motherId != currentMember.id
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("add_relationship_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isKannada) "ಸಂಬಂಧ ಜೋಡಣೆ" else "Define Relationship",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
          Text(
            text = "${currentMember.kannadaName} (${currentMember.englishName})",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = if (isKannada) "ಯಾವ ಸಂಬಂಧವನ್ನು ವ್ಯಾಖ್ಯಾನಿಸಲು ಬಯಸುತ್ತೀರಿ?" else "Select relationship type to define:",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.SemiBold
        )

        // Relationship type chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            FilterChip(
              selected = selectedRelationType == RelationType.SPOUSE,
              onClick = {
                selectedRelationType = RelationType.SPOUSE
                selectedRelative = null
              },
              label = { Text(if (isKannada) "ಪತಿ/ಪತ್ನಿ (Spouse)" else "Spouse") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = KumkumaMaroon.copy(alpha = 0.2f)
              )
            )

            FilterChip(
              selected = selectedRelationType == RelationType.SIBLING,
              onClick = {
                selectedRelationType = RelationType.SIBLING
                selectedRelative = null
              },
              label = { Text(if (isKannada) "ಸಹೋದರ/ರಿ (Sibling)" else "Sibling") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = HeritageGold.copy(alpha = 0.25f)
              )
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            FilterChip(
              selected = selectedRelationType == RelationType.FATHER,
              onClick = {
                selectedRelationType = RelationType.FATHER
                selectedRelative = null
              },
              label = { Text(if (isKannada) "ತಂದೆ (Father)" else "Father") }
            )

            FilterChip(
              selected = selectedRelationType == RelationType.MOTHER,
              onClick = {
                selectedRelationType = RelationType.MOTHER
                selectedRelative = null
              },
              label = { Text(if (isKannada) "ತಾಯಿ (Mother)" else "Mother") }
            )

            FilterChip(
              selected = selectedRelationType == RelationType.SON,
              onClick = {
                selectedRelationType = RelationType.SON
                selectedRelative = null
              },
              label = { Text(if (isKannada) "ಮಗ (Son)" else "Son") }
            )

            FilterChip(
              selected = selectedRelationType == RelationType.DAUGHTER,
              onClick = {
                selectedRelationType = RelationType.DAUGHTER
                selectedRelative = null
              },
              label = { Text(if (isKannada) "ಮಗಳು (Dtr)" else "Dtr") }
            )
          }
        }

        // Relative selector card
        Column {
          Text(
            text = if (isKannada) "ಕುಟುಂಬದ ವ್ಯಕ್ತಿಯನ್ನು ಆಯ್ಕೆಮಾಡಿ:" else "Select Relative Member:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Box(modifier = Modifier.fillMaxWidth()) {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showRelativeDropdown = true }
                .testTag("select_relative_field"),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
              )
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (selectedRelative != null) {
                  MemberAvatar(member = selectedRelative!!, size = 38.dp)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = selectedRelative!!.kannadaName,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "${selectedRelative!!.englishName} • ತಲೆಮಾರು ${selectedRelative!!.generation}",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                } else {
                  Icon(
                    Icons.Default.FamilyRestroom,
                    contentDescription = null,
                    tint = TempleOchre,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = if (isKannada) "ಸದಸ್ಯರನ್ನು ಆಯ್ಕೆ ಮಾಡಿ..." else "Tap to choose family member...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                  )
                }

                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
              }
            }

            DropdownMenu(
              expanded = showRelativeDropdown,
              onDismissRequest = { showRelativeDropdown = false }
            ) {
              if (eligibleCandidates.isEmpty()) {
                DropdownMenuItem(
                  text = { Text(if (isKannada) "ಯಾವುದೇ ಸೂಕ್ತ ಸದಸ್ಯರಿಲ್ಲ" else "No eligible members found") },
                  onClick = { showRelativeDropdown = false }
                )
              } else {
                eligibleCandidates.forEach { candidate ->
                  DropdownMenuItem(
                    text = {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        MemberAvatar(member = candidate, size = 32.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                          Text(text = candidate.kannadaName, fontWeight = FontWeight.Bold)
                          Text(
                            text = "${candidate.englishName} (Gen ${candidate.generation})",
                            style = MaterialTheme.typography.labelSmall
                          )
                        }
                      }
                    },
                    onClick = {
                      selectedRelative = candidate
                      showRelativeDropdown = false
                    }
                  )
                }
              }
            }
          }
        }

        // Explanation text
        if (selectedRelative != null) {
          Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = if (isKannada) {
                "ಸಂಬಂಧ ಜೋಡಿಸಿದ ನಂತರ: '${currentMember.kannadaName}' ಮತ್ತು '${selectedRelative!!.kannadaName}' ಅವರ ನಡುವೆ ದ್ವಿಮುಖ ಸಂಬಂಧ ಮತ್ತು ವಂಶವೃಕ್ಷ ಕೊಂಡಿ ಸ್ಥಾಪನೆಯಾಗುತ್ತದೆ."
              } else {
                "After linking, a bidirectional genealogical connection will be established between ${currentMember.englishName} and ${selectedRelative!!.englishName}."
              },
              fontSize = 11.sp,
              color = Color(0xFF78350F),
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (selectedRelative != null) {
            onAddRelationship(currentMember.id, selectedRelative!!.id, selectedRelationType)
          }
        },
        enabled = selectedRelative != null,
        colors = ButtonDefaults.buttonColors(containerColor = KumkumaMaroon),
        modifier = Modifier.testTag("confirm_relationship_button")
      ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (isKannada) "ಸಂಬಂಧ ಜೋಡಿಸಿ" else "Link Relation")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isKannada) "ರದ್ದು" else "Cancel")
      }
    }
  )
}
