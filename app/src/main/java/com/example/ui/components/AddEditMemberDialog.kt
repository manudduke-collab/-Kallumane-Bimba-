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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.Gender
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.HeritageGoldLight
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.KumkumaMaroonLight
import com.example.ui.theme.TempleOchre

val KarnatakaGothras = listOf(
  "ಕಾಶ್ಯಪ (Kashyapa)",
  "ಭಾರದ್ವಾಜ (Bharadwaja)",
  "ವಿಶ್ವಾಮಿತ್ರ (Vishwamitra)",
  "ವಸಿಷ್ಠ (Vashistha)",
  "ಗೌತಮ (Gautama)",
  "ಆತ್ರೇಯ (Atreya)",
  "ಜಮದಗ್ನಿ (Jamadagni)",
  "ಕೌಂಡಿನ್ಯ (Kaundinya)",
  "ಹಾರೀತ (Haritsa)",
  "ಕೌಶಿಕ (Kaushika)",
  "ಶಾಂಡಿಲ್ಯ (Shandilya)",
  "ಶ್ರೀವತ್ಸ (Srivatsa)"
)

val CommonManeDevaru = listOf(
  "ಶ್ರೀ ಕೊಲ್ಲೂರು ಮೂಕಾಂಬಿಕಾ ದೇವಿ",
  "ಶ್ರೀ ಮಲೆ ಮಹದೇಶ್ವರ ಸ್ವಾಮಿ",
  "ಶ್ರೀ ಮಂಜುನಾಥ ಸ್ವಾಮಿ (ಧರ್ಮಸ್ಥಳ)",
  "ಶ್ರೀ ಚಾಮುಂಡೇಶ್ವರಿ ದೇವಿ (ಮೈಸೂರು)",
  "ಶ್ರೀ ಕಳಸೇಶ್ವರ ಸ್ವಾಮಿ (ಕಳಸ)",
  "ಶ್ರೀ ಸಿಗಂದೂರು ಚೌಡೇಶ್ವರಿ ದೇವಿ",
  "ಶ್ರೀ ಕುಕ್ಕೆ ಸುಬ್ರಹ್ಮಣ್ಯ ಸ್ವಾಮಿ",
  "ಶ್ರೀ ಶಾರದಾಂಬೆ (ಶೃಂಗೇರಿ)",
  "ಶ್ರೀ ಬನಶಂಕರಿ ದೇವಿ",
  "ಶ್ರೀ ಹಾಸನಾಂಬ ದೇವಿ (ಹಾಸನ)",
  "ಶ್ರೀ ಚೆನ್ನಕೇಶವ ಸ್ವಾಮಿ (ಬೇಲೂರು)",
  "ಶ್ರೀ ತಿರುಪತಿ ವೆಂಕಟೇಶ್ವರ ಸ್ವಾಮಿ"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemberDialog(
  member: FamilyMemberEntity? = null,
  allMembers: List<FamilyMemberEntity>,
  isKannada: Boolean,
  onDismiss: () -> Unit,
  onSave: (FamilyMemberEntity, Uri?) -> Unit
) {
  var kannadaName by remember { mutableStateOf(member?.kannadaName ?: "") }
  var englishName by remember { mutableStateOf(member?.englishName ?: "") }
  var gender by remember { mutableStateOf(member?.gender ?: Gender.MALE) }
  var isAlive by remember { mutableStateOf(member?.isAlive ?: true) }
  var birthYear by remember { mutableStateOf(member?.birthYear ?: "") }
  var deathYear by remember { mutableStateOf(member?.deathYear ?: "") }
  var maneHesaru by remember { mutableStateOf(member?.maneHesaru ?: "") }
  var purveekaraOoru by remember { mutableStateOf(member?.purveekaraOoru ?: "") }
  var gothra by remember { mutableStateOf(member?.gothra ?: "") }
  var kulaDaiva by remember { mutableStateOf(member?.kulaDaiva ?: "") }
  var kulaBranch by remember { mutableStateOf(member?.kulaBranch ?: "") }
  var mathaAffiliation by remember { mutableStateOf(member?.mathaAffiliation ?: "") }
  var occupation by remember { mutableStateOf(member?.occupation ?: "") }
  var contactNumber by remember { mutableStateOf(member?.contactNumber ?: "") }
  var notes by remember { mutableStateOf(member?.notes ?: "") }
  var generation by remember { mutableIntStateOf(member?.generation ?: 3) }

  var fatherId by remember { mutableStateOf(member?.fatherId) }
  var motherId by remember { mutableStateOf(member?.motherId) }
  var spouseId by remember { mutableStateOf(member?.spouseId) }

  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var currentPhotoPath by remember { mutableStateOf(member?.photoPath) }

  // Photo picker launcher
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedImageUri = uri
      currentPhotoPath = null
    }
  }

  // Dropdown states
  var showGothraDropdown by remember { mutableStateOf(false) }
  var showDevaruDropdown by remember { mutableStateOf(false) }
  var showFatherDropdown by remember { mutableStateOf(false) }
  var showMotherDropdown by remember { mutableStateOf(false) }
  var showSpouseDropdown by remember { mutableStateOf(false) }

  val candidateFathers = allMembers.filter { it.gender == Gender.MALE && it.id != member?.id }
  val candidateMothers = allMembers.filter { it.gender == Gender.FEMALE && it.id != member?.id }
  val candidateSpouses = allMembers.filter { it.id != member?.id }

  AlertDialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth(0.96f)
      .testTag("add_edit_member_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (member == null) {
              if (isKannada) "ಹೊಸ ಕುಟುಂಬ ಸದಸ್ಯರನ್ನು ಸೇರಿಸಿ" else "Add Family Member"
            } else {
              if (isKannada) "ಸದಸ್ಯರ ವಿವರ ತಿದ್ದುಪಡಿ" else "Edit Member Details"
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
          Text(
            text = if (isKannada) "ಕರ್ನಾಟಕ ಸಂಸ್ಕೃತಿ ಮತ್ತು ವಂಶ ದಾಖಲೆ" else "Karnataka Heritage & Family Record",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_dialog_button")) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Photo Selection Banner
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, HeritageGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              if (selectedImageUri != null) {
                AsyncImage(
                  model = selectedImageUri,
                  contentDescription = "Selected Photo",
                  modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape),
                  contentScale = ContentScale.Crop
                )
              } else if (!currentPhotoPath.isNullOrEmpty()) {
                AsyncImage(
                  model = currentPhotoPath,
                  contentDescription = "Member Photo",
                  modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape),
                  contentScale = ContentScale.Crop
                )
              } else {
                Icon(
                  Icons.Default.Person,
                  contentDescription = "No Photo",
                  modifier = Modifier.size(42.dp),
                  tint = TempleOchre
                )
              }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
              Text(
                text = if (isKannada) "ವ್ಯಕ್ತಿಯ ಭಾವಚಿತ್ರ" else "Member Photo",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = if (isKannada) "ಪೂರ್ವಿಕರ ಅಥವಾ ಕುಟುಂಬದ ಫೋಟೋ" else "Upload ancestral or recent photo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = {
                    photoPickerLauncher.launch(
                      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TempleOchre),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.testTag("choose_photo_button")
                ) {
                  Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = if (isKannada) "ಫೋಟೋ ಆಯ್ಕೆ" else "Choose", fontSize = 12.sp)
                }

                if (selectedImageUri != null || !currentPhotoPath.isNullOrEmpty()) {
                  OutlinedButton(
                    onClick = {
                      selectedImageUri = null
                      currentPhotoPath = null
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("remove_photo_button")
                  ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                  }
                }
              }
            }
          }
        }

        // Names
        OutlinedTextField(
          value = kannadaName,
          onValueChange = { kannadaName = it },
          label = { Text(if (isKannada) "ಕನ್ನಡ ಹೆಸರು (ಉದಾ: ಶ್ರೀ ರಾಮಚಂದ್ರ ರಾವ್)*" else "Kannada Name*") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("kannada_name_input"),
          singleLine = true
        )

        OutlinedTextField(
          value = englishName,
          onValueChange = { englishName = it },
          label = { Text(if (isKannada) "ಆಂಗ್ಲ ಹೆಸರು (English Name)*" else "English Name*") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("english_name_input"),
          singleLine = true,
          keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        // Gender & Life Status
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isKannada) "ಲಿಂಗ (Gender)" else "Gender",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Medium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              FilterChip(
                selected = gender == Gender.MALE,
                onClick = { gender = Gender.MALE },
                label = { Text(if (isKannada) "ಗಂಡು" else "Male") },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = HeritageGoldLight.copy(alpha = 0.5f)
                )
              )
              FilterChip(
                selected = gender == Gender.FEMALE,
                onClick = { gender = Gender.FEMALE },
                label = { Text(if (isKannada) "ಹೆಣ್ಣು" else "Female") },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = KumkumaMaroonLight.copy(alpha = 0.25f)
                )
              )
            }
          }

          // Living / Deceased
          Card(
            colors = CardDefaults.cardColors(
              containerColor = if (isAlive) Color(0xFFF0FDF4) else Color(0xFFF3F4F6)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = if (isAlive) {
                  if (isKannada) "ಜೀವಂತರಾಗಿದ್ದಾರೆ" else "Living"
                } else {
                  if (isKannada) "ಸ್ವರ್ಗಸ್ಥರು / ದಿವಂಗತ" else "In Memory"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAlive) Color(0xFF166534) else Color(0xFF4B5563)
              )
              Switch(
                checked = isAlive,
                onCheckedChange = { isAlive = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF166534))
              )
            }
          }
        }

        // Years
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = birthYear,
            onValueChange = { birthYear = it },
            label = { Text(if (isKannada) "ಜನನ ವರ್ಷ (Birth)" else "Birth Year") },
            placeholder = { Text("1960") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )

          if (!isAlive) {
            OutlinedTextField(
              value = deathYear,
              onValueChange = { deathYear = it },
              label = { Text(if (isKannada) "ಪುಣ್ಯತಿಥಿ (Death)" else "Passing Year") },
              placeholder = { Text("2015") },
              modifier = Modifier.weight(1f),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true
            )
          }
        }

        // Generation
        Column {
          Text(
            text = if (isKannada) "ತಲೆಮಾರು / ಹಂತ (Generation Level)" else "Generation Level",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            val genLabels = listOf(
              1 to if (isKannada) "೧:ಮುತ್ತಜ್ಜ" else "1:Great-Grand",
              2 to if (isKannada) "೨:ಅಜ್ಜ" else "2:Grand",
              3 to if (isKannada) "೩:ಅಪ್ಪ/ಅಮ್ಮ" else "3:Parents",
              4 to if (isKannada) "೪:ನಾವು" else "4:Self/Sib",
              5 to if (isKannada) "೫:ಮಕ್ಕಳು" else "5:Children"
            )
            genLabels.forEach { (g, label) ->
              FilterChip(
                selected = generation == g,
                onClick = { generation = g },
                label = { Text(label, fontSize = 11.sp) }
              )
            }
          }
        }

        // Heritage Fields (Mane Hesaru & Native Place)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = maneHesaru,
            onValueChange = { maneHesaru = it },
            label = { Text(if (isKannada) "ಮನೆ ಹೆಸರು (Mane Hesaru)" else "House Alias") },
            placeholder = { Text("ದೊಡ್ಡಮನೆ / ಅರಳೀಮರ ಮನೆ") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )

          OutlinedTextField(
            value = purveekaraOoru,
            onValueChange = { purveekaraOoru = it },
            label = { Text(if (isKannada) "ಮೂಲ ಊರು (Native Village)" else "Ancestral Place") },
            placeholder = { Text("ತೀರ್ಥಹಳ್ಳಿ / ಮೈಸೂರು") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        // Gothra with auto-suggest dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = gothra,
            onValueChange = { gothra = it },
            label = { Text(if (isKannada) "ಗೋತ್ರ (Gothra)" else "Gothra") },
            placeholder = { Text("ಕಾಶ್ಯಪ / ಭಾರದ್ವಾಜ") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
              IconButton(onClick = { showGothraDropdown = !showGothraDropdown }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Gothra")
              }
            }
          )
          DropdownMenu(
            expanded = showGothraDropdown,
            onDismissRequest = { showGothraDropdown = false }
          ) {
            KarnatakaGothras.forEach { item ->
              DropdownMenuItem(
                text = { Text(item) },
                onClick = {
                  gothra = item
                  showGothraDropdown = false
                }
              )
            }
          }
        }

        // Mane Devaru (Kula Daiva)
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = kulaDaiva,
            onValueChange = { kulaDaiva = it },
            label = { Text(if (isKannada) "ಕುಲದೈವ / ಮನೆ ದೇವರು (Mane Devaru)" else "Kula Daiva") },
            placeholder = { Text("ಶ್ರೀ ಕೊಲ್ಲೂರು ಮೂಕಾಂಬಿಕಾ / ಮಲೆ ಮಹದೇಶ್ವರ") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
              IconButton(onClick = { showDevaruDropdown = !showDevaruDropdown }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Deity")
              }
            }
          )
          DropdownMenu(
            expanded = showDevaruDropdown,
            onDismissRequest = { showDevaruDropdown = false }
          ) {
            CommonManeDevaru.forEach { item ->
              DropdownMenuItem(
                text = { Text(item) },
                onClick = {
                  kulaDaiva = item
                  showDevaruDropdown = false
                }
              )
            }
          }
        }

        // Family Branch & Matha
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = kulaBranch,
            onValueChange = { kulaBranch = it },
            label = { Text(if (isKannada) "ಕುಲ / ಶಾಖೆ" else "Branch / Community") },
            placeholder = { Text("ಸ್ಮಾರ್ತ / ಲಿಂಗಾಯತ / ಒಕ್ಕಲಿಗ") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )

          OutlinedTextField(
            value = mathaAffiliation,
            onValueChange = { mathaAffiliation = it },
            label = { Text(if (isKannada) "ಮಠ / ಗುರುಪೀಠ" else "Matha Affiliation") },
            placeholder = { Text("ಶೃಂಗೇರಿ / ಸುತ್ತೂರು") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        // Relationships: Father, Mother, Spouse
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = if (isKannada) "ಸಂಬಂಧ ಜೋಡಣೆ (Family Connections)" else "Family Relationships",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = KumkumaMaroon
            )

            // Father
            Box(modifier = Modifier.fillMaxWidth()) {
              val fatherName = candidateFathers.find { it.id == fatherId }?.let {
                if (isKannada) it.kannadaName else it.englishName
              } ?: (if (isKannada) "ಆಯ್ಕೆ ಮಾಡಿಲ್ಲ (None)" else "None")

              OutlinedTextField(
                value = fatherName,
                onValueChange = {},
                readOnly = true,
                label = { Text(if (isKannada) "ತಂದೆ (Father)" else "Father") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                  IconButton(onClick = { showFatherDropdown = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Father")
                  }
                }
              )
              DropdownMenu(
                expanded = showFatherDropdown,
                onDismissRequest = { showFatherDropdown = false }
              ) {
                DropdownMenuItem(
                  text = { Text(if (isKannada) "ಯಾರೂ ಇಲ್ಲ (None)" else "None") },
                  onClick = {
                    fatherId = null
                    showFatherDropdown = false
                  }
                )
                candidateFathers.forEach { candidate ->
                  DropdownMenuItem(
                    text = { Text("${candidate.kannadaName} (${candidate.englishName})") },
                    onClick = {
                      fatherId = candidate.id
                      showFatherDropdown = false
                    }
                  )
                }
              }
            }

            // Mother
            Box(modifier = Modifier.fillMaxWidth()) {
              val motherName = candidateMothers.find { it.id == motherId }?.let {
                if (isKannada) it.kannadaName else it.englishName
              } ?: (if (isKannada) "ಆಯ್ಕೆ ಮಾಡಿಲ್ಲ (None)" else "None")

              OutlinedTextField(
                value = motherName,
                onValueChange = {},
                readOnly = true,
                label = { Text(if (isKannada) "ತಾಯಿ (Mother)" else "Mother") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                  IconButton(onClick = { showMotherDropdown = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Mother")
                  }
                }
              )
              DropdownMenu(
                expanded = showMotherDropdown,
                onDismissRequest = { showMotherDropdown = false }
              ) {
                DropdownMenuItem(
                  text = { Text(if (isKannada) "ಯಾರೂ ಇಲ್ಲ (None)" else "None") },
                  onClick = {
                    motherId = null
                    showMotherDropdown = false
                  }
                )
                candidateMothers.forEach { candidate ->
                  DropdownMenuItem(
                    text = { Text("${candidate.kannadaName} (${candidate.englishName})") },
                    onClick = {
                      motherId = candidate.id
                      showMotherDropdown = false
                    }
                  )
                }
              }
            }

            // Spouse
            Box(modifier = Modifier.fillMaxWidth()) {
              val spouseName = candidateSpouses.find { it.id == spouseId }?.let {
                if (isKannada) it.kannadaName else it.englishName
              } ?: (if (isKannada) "ಆಯ್ಕೆ ಮಾಡಿಲ್ಲ (None)" else "None")

              OutlinedTextField(
                value = spouseName,
                onValueChange = {},
                readOnly = true,
                label = { Text(if (isKannada) "ಪತಿ / ಪತ್ನಿ (Spouse)" else "Spouse") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                  IconButton(onClick = { showSpouseDropdown = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Spouse")
                  }
                }
              )
              DropdownMenu(
                expanded = showSpouseDropdown,
                onDismissRequest = { showSpouseDropdown = false }
              ) {
                DropdownMenuItem(
                  text = { Text(if (isKannada) "ಯಾರೂ ಇಲ್ಲ (None)" else "None") },
                  onClick = {
                    spouseId = null
                    showSpouseDropdown = false
                  }
                )
                candidateSpouses.forEach { candidate ->
                  DropdownMenuItem(
                    text = { Text("${candidate.kannadaName} (${candidate.englishName})") },
                    onClick = {
                      spouseId = candidate.id
                      showSpouseDropdown = false
                    }
                  )
                }
              }
            }
          }
        }

        // Occupation & Contact
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = occupation,
            onValueChange = { occupation = it },
            label = { Text(if (isKannada) "ಉದ್ಯೋಗ / ವೃತ್ತಿ" else "Occupation") },
            placeholder = { Text("ಕೃಷಿಕರು / ಸಾಫ್ಟ್‌ವೇರ್") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )

          OutlinedTextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = { Text(if (isKannada) "ದೂರವಾಣಿ ಸಂಖ್ಯೆ" else "Phone") },
            placeholder = { Text("+91 98...") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
          )
        }

        // Memories & Cultural Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text(if (isKannada) "ಪೂರ್ವಿಕರ ನೆನಪುಗಳು ಮತ್ತು ಸಾಧನೆಗಳು (Notes & Memories)" else "Memories & Cultural Notes") },
          placeholder = { Text("ವಿಶೇಷ ಗುಣಗಳು, ಜೀವನ ಶೈಲಿ, ಪ್ರಮುಖ ಘಟನೆಗಳು...") },
          modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
          maxLines = 4
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (kannadaName.isBlank() && englishName.isBlank()) return@Button
          val finalKannada = kannadaName.ifBlank { englishName }
          val finalEnglish = englishName.ifBlank { kannadaName }

          val entity = FamilyMemberEntity(
            id = member?.id ?: 0L,
            kannadaName = finalKannada,
            englishName = finalEnglish,
            gender = gender,
            isAlive = isAlive,
            birthYear = birthYear,
            deathYear = if (!isAlive) deathYear.ifBlank { null } else null,
            photoPath = currentPhotoPath,
            maneHesaru = maneHesaru,
            purveekaraOoru = purveekaraOoru,
            gothra = gothra,
            kulaDaiva = kulaDaiva,
            kulaBranch = kulaBranch,
            mathaAffiliation = mathaAffiliation,
            fatherId = fatherId,
            motherId = motherId,
            spouseId = spouseId,
            generation = generation,
            occupation = occupation,
            contactNumber = contactNumber,
            notes = notes
          )
          onSave(entity, selectedImageUri)
        },
        colors = ButtonDefaults.buttonColors(containerColor = KumkumaMaroon),
        modifier = Modifier.testTag("save_member_button")
      ) {
        Icon(Icons.Default.Check, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = if (isKannada) "ಉಳಿಸಿ (Save)" else "Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, modifier = Modifier.testTag("cancel_button")) {
        Text(text = if (isKannada) "ರದ್ದು (Cancel)" else "Cancel")
      }
    }
  )
}
