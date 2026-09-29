package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.Gender
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.KumkumaMaroon
import com.example.ui.theme.TempleOchre

@Composable
fun MemberAvatar(
  member: FamilyMemberEntity,
  size: Dp = 56.dp,
  borderWidth: Dp = 2.dp,
  modifier: Modifier = Modifier
) {
  val borderColor = if (!member.isAlive) {
    Color(0xFFE2D9C8) // Dignified silver/ivory aura for ancestors
  } else if (member.gender == Gender.FEMALE) {
    KumkumaMaroon
  } else {
    HeritageGold
  }

  val backgroundBrush = if (member.gender == Gender.FEMALE) {
    Brush.radialGradient(
      colors = listOf(Color(0xFFFFF1F2), Color(0xFFFFE4E6), Color(0xFFFBCFE8))
    )
  } else {
    Brush.radialGradient(
      colors = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7), Color(0xFFFDE68A))
    )
  }

  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(backgroundBrush)
      .border(borderWidth, borderColor, CircleShape),
    contentAlignment = Alignment.Center
  ) {
    if (!member.photoPath.isNullOrEmpty()) {
      AsyncImage(
        model = member.photoPath,
        contentDescription = member.kannadaName,
        modifier = Modifier
          .size(size)
          .clip(CircleShape),
        contentScale = ContentScale.Crop
      )
    } else {
      // Kannada/English Initial Avatar
      val initial = member.kannadaName.firstOrNull()?.toString()
        ?: member.englishName.firstOrNull()?.toString()
        ?: "?"

      Text(
        text = initial,
        fontSize = (size.value * 0.42f).sp,
        fontWeight = FontWeight.Bold,
        color = if (member.gender == Gender.FEMALE) KumkumaMaroon else TempleOchre
      )
    }

    // Small ribbon badge for deceased / ancestors
    if (!member.isAlive) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .background(Color(0xCC000000), CircleShape)
      )
    }
  }
}
