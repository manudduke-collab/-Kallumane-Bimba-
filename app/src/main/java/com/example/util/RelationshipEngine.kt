package com.example.util

import com.example.data.model.FamilyMemberEntity
import com.example.data.model.Gender

enum class RelationType {
  FATHER,
  MOTHER,
  SPOUSE,
  SIBLING,
  SON,
  DAUGHTER
}

data class DirectRelatives(
  val father: FamilyMemberEntity? = null,
  val mother: FamilyMemberEntity? = null,
  val spouse: FamilyMemberEntity? = null,
  val siblings: List<FamilyMemberEntity> = emptyList(),
  val children: List<FamilyMemberEntity> = emptyList()
)

data class VisualTreeConnection(
  val fromMemberId: Long,
  val toMemberId: Long,
  val connectionType: ConnectionType
)

enum class ConnectionType {
  MARRIAGE,
  PARENT_TO_CHILD,
  SIBLING
}

object RelationshipEngine {

  fun getDirectRelatives(
    member: FamilyMemberEntity,
    allMembers: List<FamilyMemberEntity>
  ): DirectRelatives {
    val map = allMembers.associateBy { it.id }

    val father = member.fatherId?.let { map[it] }
    val mother = member.motherId?.let { map[it] }
    val spouse = member.spouseId?.let { map[it] }

    val siblings = allMembers.filter { other ->
      other.id != member.id &&
          ((member.fatherId != null && other.fatherId == member.fatherId) ||
              (member.motherId != null && other.motherId == member.motherId))
    }

    val children = allMembers.filter { other ->
      other.fatherId == member.id || other.motherId == member.id
    }

    return DirectRelatives(
      father = father,
      mother = mother,
      spouse = spouse,
      siblings = siblings,
      children = children
    )
  }

  fun getVisualConnections(allMembers: List<FamilyMemberEntity>): List<VisualTreeConnection> {
    val connections = mutableListOf<VisualTreeConnection>()
    val visitedMarriagePairs = mutableSetOf<Pair<Long, Long>>()

    allMembers.forEach { member ->
      // 1. Spouses (Marriage)
      member.spouseId?.let { spouseId ->
        val pairKey = if (member.id < spouseId) Pair(member.id, spouseId) else Pair(spouseId, member.id)
        if (!visitedMarriagePairs.contains(pairKey)) {
          visitedMarriagePairs.add(pairKey)
          connections.add(
            VisualTreeConnection(
              fromMemberId = pairKey.first,
              toMemberId = pairKey.second,
              connectionType = ConnectionType.MARRIAGE
            )
          )
        }
      }

      // 2. Parents to Child
      member.fatherId?.let { fatherId ->
        connections.add(
          VisualTreeConnection(
            fromMemberId = fatherId,
            toMemberId = member.id,
            connectionType = ConnectionType.PARENT_TO_CHILD
          )
        )
      }
      member.motherId?.let { motherId ->
        connections.add(
          VisualTreeConnection(
            fromMemberId = motherId,
            toMemberId = member.id,
            connectionType = ConnectionType.PARENT_TO_CHILD
          )
        )
      }
    }

    return connections
  }
}
