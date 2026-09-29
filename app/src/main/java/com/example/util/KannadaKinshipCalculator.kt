package com.example.util

import com.example.data.model.FamilyMemberEntity
import com.example.data.model.Gender

data class KinshipResult(
  val kannadaTerm: String,
  val englishTerm: String,
  val explanationKannada: String,
  val explanationEnglish: String
)

object KannadaKinshipCalculator {

  fun calculateKinship(
    focus: FamilyMemberEntity,
    target: FamilyMemberEntity,
    allMembers: List<FamilyMemberEntity>
  ): KinshipResult {
    if (focus.id == target.id) {
      return KinshipResult(
        kannadaTerm = "ತಾನು (ನನ್ನ ಸ್ಥಾನ)",
        englishTerm = "Self",
        explanationKannada = "ಆಯ್ಕೆ ಮಾಡಿದ ವ್ಯಕ್ತಿ ನೀವೇ ಆಗಿದ್ದೀರಿ",
        explanationEnglish = "You are looking at yourself"
      )
    }

    val memberMap = allMembers.associateBy { it.id }

    // Direct Spouse
    if (focus.spouseId == target.id || target.spouseId == focus.id) {
      return if (target.gender == Gender.MALE) {
        KinshipResult(
          kannadaTerm = "ಪತಿ / ಗಂಡ (Pati / Ganda)",
          englishTerm = "Husband",
          explanationKannada = "ನಿಮ್ಮ ಪತಿ",
          explanationEnglish = "Your husband"
        )
      } else {
        KinshipResult(
          kannadaTerm = "ಪತ್ನಿ / ಹೆಂಡತಿ (Patni / Hendathi)",
          englishTerm = "Wife",
          explanationKannada = "ನಿಮ್ಮ ಧರ್ಮಪತ್ನಿ",
          explanationEnglish = "Your wife"
        )
      }
    }

    // Direct Parents
    if (focus.fatherId == target.id) {
      return KinshipResult(
        kannadaTerm = "ತಂದೆ / ಅಪ್ಪ (Thande / Appa)",
        englishTerm = "Father",
        explanationKannada = "ನಿಮ್ಮ ಜನಕ ತಂದೆ",
        explanationEnglish = "Your biological father"
      )
    }
    if (focus.motherId == target.id) {
      return KinshipResult(
        kannadaTerm = "ತಾಯಿ / ಅಮ್ಮ (Thayi / Amma)",
        englishTerm = "Mother",
        explanationKannada = "ನಿಮ್ಮ ಹೆತ್ತ ತಾಯಿ",
        explanationEnglish = "Your biological mother"
      )
    }

    // Direct Children
    if (target.fatherId == focus.id || target.motherId == focus.id) {
      return if (target.gender == Gender.MALE) {
        KinshipResult(
          kannadaTerm = "ಮಗ (Maga)",
          englishTerm = "Son",
          explanationKannada = "ನಿಮ್ಮ ಪುತ್ರ",
          explanationEnglish = "Your son"
        )
      } else {
        KinshipResult(
          kannadaTerm = "ಮಗಳು (Magalu)",
          englishTerm = "Daughter",
          explanationKannada = "ನಿಮ್ಮ ಪುತ್ರಿ",
          explanationEnglish = "Your daughter"
        )
      }
    }

    // Siblings (Share at least one parent)
    val hasSameFather = focus.fatherId != null && focus.fatherId == target.fatherId
    val hasSameMother = focus.motherId != null && focus.motherId == target.motherId
    if (hasSameFather || hasSameMother) {
      val isOlder = try {
        (target.birthYear.toIntOrNull() ?: 0) < (focus.birthYear.toIntOrNull() ?: 0)
      } catch (_: Exception) {
        false
      }
      return if (target.gender == Gender.MALE) {
        if (isOlder) {
          KinshipResult("ಅಣ್ಣ (Anna)", "Elder Brother", "ನಿಮ್ಮ ಹಿರಿಯ ಸಹೋದರ", "Your elder brother")
        } else {
          KinshipResult("ತಮ್ಮ (Thamma)", "Younger Brother", "ನಿಮ್ಮ ಕಿರಿಯ ಸಹೋದರ", "Your younger brother")
        }
      } else {
        if (isOlder) {
          KinshipResult("ಅಕ್ಕ (Akka)", "Elder Sister", "ನಿಮ್ಮ ಹಿರಿಯ ಸಹೋದರಿ", "Your elder sister")
        } else {
          KinshipResult("ತಂಗಿ (Thangi)", "Younger Sister", "ನಿಮ್ಮ ಕಿರಿಯ ಸಹೋದರಿ", "Your younger sister")
        }
      }
    }

    // Grandparents
    val focusFather = focus.fatherId?.let { memberMap[it] }
    val focusMother = focus.motherId?.let { memberMap[it] }

    if (focusFather?.fatherId == target.id) {
      return KinshipResult("ಅಜ್ಜ / ತಾತ (Paternal Grandfather)", "Grandfather (Father's Father)", "ತಂದೆಯವರ ತಂದೆ", "Your paternal grandfather")
    }
    if (focusFather?.motherId == target.id) {
      return KinshipResult("ಅಜ್ಜಿ (Paternal Grandmother)", "Grandmother (Father's Mother)", "ತಂದೆಯವರ ತಾಯಿ", "Your paternal grandmother")
    }
    if (focusMother?.fatherId == target.id) {
      return KinshipResult("ಅಜ್ಜ / ತಾತ (Maternal Grandfather)", "Grandfather (Mother's Father)", "ತಾಯಿಯವರ ತಂದೆ", "Your maternal grandfather")
    }
    if (focusMother?.motherId == target.id) {
      return KinshipResult("ಅಜ್ಜಿ (Maternal Grandmother)", "Grandmother (Mother's Mother)", "ತಾಯಿಯವರ ತಾಯಿ", "Your maternal grandmother")
    }

    // Great-Grandparents
    val grandfather = focusFather?.fatherId?.let { memberMap[it] }
    val grandmother = focusFather?.motherId?.let { memberMap[it] }
    if (grandfather?.fatherId == target.id || grandmother?.fatherId == target.id) {
      return KinshipResult("ಮುತ್ತಜ್ಜ (Muttajja)", "Great-Grandfather", "ನಿಮ್ಮ ಪೂರ್ವಿಕ ಮುತ್ತಜ್ಜ", "Your great-grandfather")
    }
    if (grandfather?.motherId == target.id || grandmother?.motherId == target.id) {
      return KinshipResult("ಮುತ್ತಜ್ಜಿ (Muttajji)", "Great-Grandmother", "ನಿಮ್ಮ ಪೂರ್ವಿಕ ಮುತ್ತಜ್ಜಿ", "Your great-grandmother")
    }

    // Grandchildren
    val targetFather = target.fatherId?.let { memberMap[it] }
    val targetMother = target.motherId?.let { memberMap[it] }
    if ((targetFather != null && (targetFather.fatherId == focus.id || targetFather.motherId == focus.id)) ||
        (targetMother != null && (targetMother.fatherId == focus.id || targetMother.motherId == focus.id))) {
      return if (target.gender == Gender.MALE) {
        KinshipResult("ಮೊಮ್ಮಗ (Mommaga)", "Grandson", "ನಿಮ್ಮ ಮೊಮ್ಮಗ", "Your grandson")
      } else {
        KinshipResult("ಮೊಮ್ಮಗಳು (Mommagalu)", "Granddaughter", "ನಿಮ್ಮ ಮೊಮ್ಮಗಳು", "Your granddaughter")
      }
    }

    // Paternal Aunts and Uncles (Father's siblings)
    if (focusFather != null && (target.fatherId == focusFather.fatherId || target.motherId == focusFather.motherId)) {
      val isOlderThanFather = try {
        (target.birthYear.toIntOrNull() ?: 0) < (focusFather.birthYear.toIntOrNull() ?: 0)
      } catch (_: Exception) {
        false
      }
      return if (target.gender == Gender.MALE) {
        if (isOlderThanFather) {
          KinshipResult("ದೊಡ್ಡಪ್ಪ (Doddappa)", "Paternal Elder Uncle", "ತಂದೆಯವರ ಹಿರಿಯ ಅಣ್ಣ", "Father's elder brother")
        } else {
          KinshipResult("ಚಿಕ್ಕಪ್ಪ (Chikkappa)", "Paternal Younger Uncle", "ತಂದೆಯವರ ಕಿರಿಯ ತಮ್ಮ", "Father's younger brother")
        }
      } else {
        KinshipResult("ಅತ್ತೆ (Athe)", "Paternal Aunt", "ತಂದೆಯವರ ಸಹೋದರಿ", "Father's sister")
      }
    }

    // Maternal Aunts and Uncles (Mother's siblings)
    if (focusMother != null && (target.fatherId == focusMother.fatherId || target.motherId == focusMother.motherId)) {
      val isOlderThanMother = try {
        (target.birthYear.toIntOrNull() ?: 0) < (focusMother.birthYear.toIntOrNull() ?: 0)
      } catch (_: Exception) {
        false
      }
      return if (target.gender == Gender.MALE) {
        KinshipResult("ಮಾವ (Maava)", "Maternal Uncle", "ತಾಯಿಯವರ ಸಹೋದರ", "Mother's brother")
      } else {
        if (isOlderThanMother) {
          KinshipResult("ದೊಡ್ಡಮ್ಮ (Doddamma)", "Maternal Elder Aunt", "ತಾಯಿಯವರ ಹಿರಿಯ ಅಕ್ಕ", "Mother's elder sister")
        } else {
          KinshipResult("ಚಿಕ್ಕಮ್ಮ (Chikkamma)", "Maternal Younger Aunt", "ತಾಯಿಯವರ ಕಿರಿಯ ತಂಗಿ", "Mother's younger sister")
        }
      }
    }

    // In-Laws: Spouse's Parents
    val focusSpouse = focus.spouseId?.let { memberMap[it] }
    if (focusSpouse != null) {
      if (focusSpouse.fatherId == target.id) {
        return KinshipResult("ಮಾವ (Maava)", "Father-in-law", "ನಿಮ್ಮ ಪತಿ/ಪತ್ನಿಯವರ ತಂದೆ", "Spouse's father")
      }
      if (focusSpouse.motherId == target.id) {
        return KinshipResult("ಅತ್ತೆ (Athe)", "Mother-in-law", "ನಿಮ್ಮ ಪತಿ/ಪತ್ನಿಯವರ ತಾಯಿ", "Spouse's mother")
      }
    }

    // Nephew / Niece (Children of siblings)
    val targetParent = targetFather ?: targetMother
    if (targetParent != null && (targetParent.fatherId == focus.fatherId || targetParent.motherId == focus.motherId)) {
      return if (targetParent.gender == Gender.FEMALE) {
        // Sister's children
        if (target.gender == Gender.MALE) {
          KinshipResult("ಅಳಿಯ (Aliyandru)", "Nephew (Sister's Son)", "ಸಹೋದರಿಯ ಮಗ", "Sister's son")
        } else {
          KinshipResult("ಸೊಸೆ (Sose)", "Niece (Sister's Daughter)", "ಸಹೋದರಿಯ ಮಗಳು", "Sister's daughter")
        }
      } else {
        // Brother's children
        if (target.gender == Gender.MALE) {
          KinshipResult("ಮಗ ಸಮಾನ / ಅಣ್ಣ-ತಮ್ಮನ ಮಗ", "Nephew (Brother's Son)", "ಸಹೋದರನ ಮಗ", "Brother's son")
        } else {
          KinshipResult("ಮಗಳು ಸಮಾನ / ಅಣ್ಣ-ತಮ್ಮನ ಮಗಳು", "Niece (Brother's Daughter)", "ಸಹೋದರನ ಮಗಳು", "Brother's daughter")
        }
      }
    }

    // Cousins (Children of aunts/uncles)
    if (targetParent != null && focusFather != null && targetParent.fatherId == focusFather.fatherId) {
      return if (target.gender == Gender.MALE) {
        KinshipResult("ದಾಯಾದಿ ಸಹೋದರ (Dayadi / Cousin Brother)", "Cousin Brother", "ಚಿಕ್ಕಪ್ಪ / ದೊಡ್ಡಪ್ಪನ ಮಗ", "Paternal uncle's son")
      } else {
        KinshipResult("ದಾಯಾದಿ ಸಹೋದರಿ (Dayadi / Cousin Sister)", "Cousin Sister", "ಚಿಕ್ಕಪ್ಪ / ದೊಡ್ಡಪ್ಪನ ಮಗಳು", "Paternal uncle's daughter")
      }
    }

    // Cross-cousin via Maternal Uncle or Paternal Aunt
    if (targetParent != null && focusMother != null && targetParent.fatherId == focusMother.fatherId) {
      return if (target.gender == Gender.MALE) {
        KinshipResult("ಬಾವ / ಮೈದುನ (Bava / Cross-Cousin)", "Cousin / Potential Brother-in-law", "ಮಾವನ ಮಗ", "Maternal uncle's son")
      } else {
        KinshipResult("ಅತ್ತಿಗೆ / ನಾದಿನಿ (Cross-Cousin Sister)", "Cross-Cousin Sister", "ಮಾವನ ಮಗಳು", "Maternal uncle's daughter")
      }
    }

    // Generation-based relative fallback
    val genDiff = target.generation - focus.generation
    return when {
      genDiff == -2 -> KinshipResult("ಅಜ್ಜಂದಿರ ತಲೆಮಾರು", "Grandparent's Generation", "ಕುಟುಂಬದ ಹಿರಿಯ ತಲೆಮಾರು", "Ancestor of grandparents' generation")
      genDiff == -1 -> KinshipResult("ಹಿರಿಯರು / ತಂದೆ ಸಮಾನರು", "Parental Generation Elder", "ಕುಟುಂಬದ ಪೋಷಕ ತಲೆಮಾರು", "Elder of parental generation")
      genDiff == 0 -> KinshipResult("ಸಮಕಾಲೀನರು / ಸಹೋದರ ಸಮಾನರು", "Peer / Cousin Relation", "ನಿಮ್ಮದೇ ತಲೆಮಾರಿನ ಸಂಬಂಧಿ", "Relative of same generation")
      genDiff == 1 -> KinshipResult("ಕಿರಿಯರು / ಪುತ್ರ ಸಮಾನರು", "Next Generation Relation", "ಮುಂದಿನ ತಲೆಮಾರಿನ ಮಕ್ಕಳು", "Relative of children's generation")
      genDiff >= 2 -> KinshipResult("ಮೊಮ್ಮಕ್ಕಳ ತಲೆಮಾರು", "Grandchild Generation", "ಕುಟುಂಬದ ಕುಡಿಗಳು", "Relative of grandchildren generation")
      else -> KinshipResult("ವಂಶಸ್ಥರು (Vamshastha)", "Family Kinsman", "ಕುಟುಂಬದ ಸಂಬಂಧಿ", "Family member in lineage")
    }
  }
}
