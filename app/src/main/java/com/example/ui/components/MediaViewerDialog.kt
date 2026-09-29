package com.example.ui.components

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.MediaType
import com.example.data.model.MemberMediaEntity
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.TempleOchre
import com.example.util.ImageStorageHelper

@Composable
fun MediaViewerDialog(
  media: MemberMediaEntity,
  ownerMember: FamilyMemberEntity?,
  isKannada: Boolean,
  onDismiss: () -> Unit,
  onDelete: (MemberMediaEntity) -> Unit
) {
  val context = LocalContext.current
  var showDeleteConfirm by remember { mutableStateOf(false) }

  val typeIcon = when (media.mediaType) {
    MediaType.PHOTO -> Icons.Default.InsertPhoto
    MediaType.DOCUMENT -> Icons.Default.Description
    MediaType.KUNDALI -> Icons.Default.AutoAwesome
    MediaType.CERTIFICATE -> Icons.Default.WorkspacePremium
    MediaType.LETTER -> Icons.Default.HistoryEdu
  }

  val typeLabel = when (media.mediaType) {
    MediaType.PHOTO -> if (isKannada) "ಭಾವಚಿತ್ರ" else "Photo"
    MediaType.DOCUMENT -> if (isKannada) "ಪಾರಂಪರಿಕ ದಾಖಲೆ" else "Historical Document"
    MediaType.KUNDALI -> if (isKannada) "ಜಾತಕ ಕುಂಡಲಿ" else "Horoscope / Kundali"
    MediaType.CERTIFICATE -> if (isKannada) "ಪ್ರಶಸ್ತಿ / ಪ್ರಮಾಣ ಪತ್ರ" else "Certificate"
    MediaType.LETTER -> if (isKannada) "ಆಶೀರ್ವಾದ ಪತ್ರ" else "Letter"
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth(0.96f)
      .testTag("media_viewer_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Surface(
            color = Color(0xFFFEF3C7),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(bottom = 4.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(typeIcon, contentDescription = null, tint = TempleOchre, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = typeLabel,
                color = TempleOchre,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Text(
            text = if (isKannada) media.titleKannada else media.titleEnglish,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = KumkumaMaroon
          )
          if (ownerMember != null) {
            Text(
              text = "ಸದಸ್ಯರು: ${ownerMember.kannadaName} (${ownerMember.englishName})",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Media visual representation
        if (media.filePath.isNotBlank()) {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            AsyncImage(
              model = media.filePath,
              contentDescription = media.titleKannada,
              modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp)),
              contentScale = ContentScale.Fit
            )
          }
        } else {
          // Document Decorative Canvas Frame
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF8F1)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, HeritageGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(TempleOchre.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(typeIcon, contentDescription = null, tint = TempleOchre, modifier = Modifier.size(36.dp))
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = if (isKannada) media.titleKannada else media.titleEnglish,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = KumkumaMaroon
              )
              Text(
                text = "ಕಡತ: ${media.fileName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              if (media.fileSizeBytes > 0) {
                Text(
                  text = "ಗಾತ್ರ: ${ImageStorageHelper.formatFileSize(media.fileSizeBytes)}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        // Description & Metadata
        if (media.description.isNotBlank()) {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = if (isKannada) "ದಾಖಲೆಯ ವಿವರಣೆ ಮತ್ತು ಮಹತ್ವ" else "Document Description",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TempleOchre
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = media.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
          onClick = {
            val shareText = buildString {
              append("${if (isKannada) media.titleKannada else media.titleEnglish}\n")
              if (ownerMember != null) {
                append("ಕುಟುಂಬ ಸದಸ್ಯರು: ${ownerMember.kannadaName}\n")
              }
              if (media.description.isNotBlank()) {
                append("${media.description}\n")
              }
            }
            val intent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, shareText)
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(intent, "Share Document Details"))
          }
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (isKannada) "ಹಂಚಿಕೊಳ್ಳಿ" else "Share")
        }

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = KumkumaMaroon)
        ) {
          Text(if (isKannada) "ಮುಚ್ಚಿ" else "Close")
        }
      }
    },
    dismissButton = {
      IconButton(
        onClick = { showDeleteConfirm = true },
        modifier = Modifier.testTag("delete_media_button")
      ) {
        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
      }
    }
  )

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text(if (isKannada) "ದಾಖಲೆ ತೆಗೆದುಹಾಕುವುದೇ?" else "Delete Document?") },
      text = {
        Text(
          if (isKannada) "'${media.titleKannada}' ದಾಖಲೆಯನ್ನು ಖಚಿತವಾಗಿ ಅಳಿಸಬೇಕೇ?"
          else "Are you sure you want to delete '${media.titleEnglish}'?"
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirm = false
            onDelete(media)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text(if (isKannada) "ತೆಗೆದುಹಾಕಿ" else "Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text(if (isKannada) "ರದ್ದು" else "Cancel")
        }
      }
    )
  }
}
