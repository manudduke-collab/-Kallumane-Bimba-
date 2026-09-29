package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.MediaType
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.TempleOchre

@Composable
fun UploadMediaDialog(
  member: FamilyMemberEntity,
  isKannada: Boolean,
  onDismiss: () -> Unit,
  onUpload: (uri: Uri, titleKannada: String, titleEnglish: String, mediaType: MediaType, description: String) -> Unit
) {
  var selectedUri by remember { mutableStateOf<Uri?>(null) }
  var selectedType by remember { mutableStateOf(MediaType.PHOTO) }
  var titleKannada by remember { mutableStateOf("") }
  var titleEnglish by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var pickedFileName by remember { mutableStateOf("") }

  // Photo Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedUri = uri
      pickedFileName = uri.lastPathSegment ?: "photo.jpg"
      if (titleKannada.isBlank()) {
        titleKannada = when (selectedType) {
          MediaType.PHOTO -> "${member.kannadaName} ಅವರ ಭಾವಚಿತ್ರ"
          MediaType.KUNDALI -> "ಜಾತಕ ಕುಂಡಲಿ ಪತ್ರಿಕೆ"
          MediaType.DOCUMENT -> "ಪಾರಂಪರಿಕ ದಾಖಲೆ"
          MediaType.CERTIFICATE -> "ಪ್ರಶಸ್ತಿ / ಪ್ರಮಾಣ ಪತ್ರ"
          MediaType.LETTER -> "ಆಶೀರ್ವಾದ ಪತ್ರ"
        }
      }
    }
  }

  // Document File Picker (PDFs, Images, Docs)
  val docPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedUri = uri
      pickedFileName = uri.lastPathSegment ?: "document.pdf"
      if (titleKannada.isBlank()) {
        titleKannada = when (selectedType) {
          MediaType.KUNDALI -> "ಜಾತಕ ಕುಂಡಲಿ ಪತ್ರಿಕೆ"
          MediaType.DOCUMENT -> "ಲಗ್ನ ಪತ್ರಿಕೆ / ಪಾರಂಪರಿಕ ದಾಖಲೆ"
          MediaType.CERTIFICATE -> "ಪ್ರಶಸ್ತಿ ಪತ್ರ"
          MediaType.LETTER -> "ಪಾರಂಪರಿಕ ಪತ್ರ"
          MediaType.PHOTO -> "ಭಾವಚಿತ್ರ"
        }
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("upload_media_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isKannada) "ಫೋಟೋ & ದಾಖಲೆ ಅಪ್ಲೋಡ್" else "Upload Photo & Document",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
          Text(
            text = "${member.kannadaName} ಅವರ ಪ್ರೊಫೈಲ್‌ಗೆ ಸೇರಿಸಲು",
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
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Media Type Selector
        Text(
          text = if (isKannada) "ಮಾಧ್ಯಮದ ಪ್ರಕಾರವನ್ನು ಆಯ್ಕೆಮಾಡಿ:" else "Select Media Category:",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          FilterChip(
            selected = selectedType == MediaType.PHOTO,
            onClick = { selectedType = MediaType.PHOTO },
            label = { Text(if (isKannada) "ಭಾವಚಿತ್ರ (Photo)" else "Photo") },
            leadingIcon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp)) },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = HeritageGold.copy(alpha = 0.2f))
          )
          FilterChip(
            selected = selectedType == MediaType.DOCUMENT,
            onClick = { selectedType = MediaType.DOCUMENT },
            label = { Text(if (isKannada) "ದಾಖಲೆ (Doc)" else "Doc") },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          FilterChip(
            selected = selectedType == MediaType.KUNDALI,
            onClick = { selectedType = MediaType.KUNDALI },
            label = { Text(if (isKannada) "ಜಾತಕ ಕುಂಡಲಿ" else "Horoscope") },
            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
          )
          FilterChip(
            selected = selectedType == MediaType.CERTIFICATE,
            onClick = { selectedType = MediaType.CERTIFICATE },
            label = { Text(if (isKannada) "ಪ್ರಶಸ್ತಿ ಪತ್ರ" else "Certificate") },
            leadingIcon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp)) }
          )
          FilterChip(
            selected = selectedType == MediaType.LETTER,
            onClick = { selectedType = MediaType.LETTER },
            label = { Text(if (isKannada) "ಪತ್ರ" else "Letter") },
            leadingIcon = { Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(16.dp)) }
          )
        }

        // File selection buttons
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (selectedUri != null) {
              Box(
                modifier = Modifier
                  .size(100.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.White)
                  .border(1.dp, HeritageGold, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                if (selectedType == MediaType.PHOTO) {
                  AsyncImage(
                    model = selectedUri,
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                  )
                } else {
                  Icon(
                    Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = TempleOchre,
                    modifier = Modifier.size(48.dp)
                  )
                }
              }
              Text(
                text = "ಆಯ್ಕೆ ಮಾಡಿದ ಕಡತ: $pickedFileName",
                style = MaterialTheme.typography.labelSmall,
                color = KumkumaMaroon,
                fontWeight = FontWeight.Bold
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                colors = ButtonDefaults.buttonColors(containerColor = TempleOchre),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("pick_photo_button")
              ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isKannada) "ಫೋಟೋ ಗ್ಯಾಲರಿ" else "Choose Photo", fontSize = 12.sp)
              }

              OutlinedButton(
                onClick = {
                  docPickerLauncher.launch(arrayOf("*/*", "application/pdf", "image/*"))
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("pick_document_button")
              ) {
                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isKannada) "ದಾಖಲೆ / PDF" else "Choose Document", fontSize = 12.sp)
              }
            }
          }
        }

        // Title and notes
        OutlinedTextField(
          value = titleKannada,
          onValueChange = { titleKannada = it },
          label = { Text(if (isKannada) "ದಾಖಲೆ / ಫೋಟೋ ಶೀರ್ಷಿಕೆ (ಕನ್ನಡ)*" else "Title (Kannada)*") },
          placeholder = { Text("ಉದಾ: ೧೯೬೦ ರ ಜಾತಕ ಪತ್ರಿಕೆ / ಲಗ್ನ ಪತ್ರಿಕೆ") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("media_title_input"),
          singleLine = true
        )

        OutlinedTextField(
          value = titleEnglish,
          onValueChange = { titleEnglish = it },
          label = { Text(if (isKannada) "ಆಂಗ್ಲ ಶೀರ್ಷಿಕೆ (Title in English)" else "Title (English)") },
          placeholder = { Text("e.g. 1960 Horoscope / Wedding Card") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text(if (isKannada) "ವಿವರ / ಮಹತ್ವ (Description & Notes)" else "Description & Notes") },
          placeholder = { Text("ದಾಖಲೆಯ ಹಿನ್ನೆಲೆ, ಸಂರಕ್ಷಿತ ಮೂಲ, ಇಸವಿ...") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (selectedUri != null) {
            val finalKannada = titleKannada.ifBlank { pickedFileName }
            val finalEnglish = titleEnglish.ifBlank { finalKannada }
            onUpload(selectedUri!!, finalKannada, finalEnglish, selectedType, description)
          }
        },
        enabled = selectedUri != null,
        colors = ButtonDefaults.buttonColors(containerColor = KumkumaMaroon),
        modifier = Modifier.testTag("save_media_button")
      ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (isKannada) "ಅಪ್ಲೋಡ್ ಮಾಡಿ" else "Upload")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isKannada) "ರದ್ದು" else "Cancel")
      }
    }
  )
}
