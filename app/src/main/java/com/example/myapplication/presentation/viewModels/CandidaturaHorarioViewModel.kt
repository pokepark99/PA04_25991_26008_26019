package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Entries
import com.example.myapplication.domain.model.EntriesDetail
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class CandidaturaHorarioViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    var listEntriesDetail by mutableStateOf<List<EntriesDetail>>(emptyList())
    val uid = FirebaseAuth.getInstance().currentUser?.uid!!

    fun getCandidaturas() {
        firestore.collection("Entries")
            .whereEqualTo("UsersId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val entriesList = snapshot.documents.mapNotNull { document ->
                        val schedulesId = document.getString("SchedulesId") ?: ""
                        val positionsId = document.getString("PositionsId") ?: ""
                        val usersId = document.getString("UsersId") ?: ""
                        val state = document.getLong("State") ?: 0

                        Entries(
                            id = document.id,
                            schedulesId = schedulesId,
                            positionsId = positionsId,
                            usersId = usersId,
                            state = state.toInt()
                        )
                    }
                    fetchDetalhes(entriesList)
                } else {
                    Log.d("CandidaturaHorarioViewModel", "Não foram encontradas candidaturas")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CandidaturaHorarioViewModel", "Erro a obter candidaturas: ", e)
            }
    }

    private fun fetchDetalhes(entries: List<Entries>) {
        val positionsMap = mutableMapOf<String, String>()
        val schedulesMap = mutableMapOf<String, String>()

        // Carrega posições e horários únicos
        val positionIds = entries.map { it.positionsId }.distinct()
        val scheduleIds = entries.map { it.schedulesId }.distinct()

        val positionsTask = firestore.collection("Positions")
            .whereIn(FieldPath.documentId(), positionIds)
            .get()

        val schedulesTask = firestore.collection("Schedules")
            .whereIn(FieldPath.documentId(), scheduleIds)
            .get()

        // Obtem todos os valores antes de concluir
        Tasks.whenAllComplete(positionsTask, schedulesTask).addOnSuccessListener {
            val positionsResult = positionsTask.result
            val schedulesResult = schedulesTask.result

            positionsResult?.documents?.forEach { doc ->
                positionsMap[doc.id] = doc.getString("Name") ?: "Desconhecido"
            }

            schedulesResult?.documents?.forEach { doc ->
                val timestamp = doc.getTimestamp("DateStart")
                val data = timestamp?.toDate()?.let { date ->
                    val formatar = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formatar.format(date)
                } ?: "Desconhecido"
                schedulesMap[doc.id] = data
            }

            // Converte para a classe de detalhes
            val entriesDetailList = entries.map { entry ->
                EntriesDetail(
                    id = entry.id,
                    userId = entry.usersId,
                    userName = "",
                    state = entry.state,
                    positionsId = entry.positionsId,
                    positionName = positionsMap[entry.positionsId] ?: "Desconhecido",
                    schedulesId = entry.schedulesId,
                    scheduleDate = schedulesMap[entry.schedulesId] ?: "Desconhecido"
                )
            }

            listEntriesDetail = entriesDetailList
        }.addOnFailureListener { e ->
            Log.e("CandidaturaHorarioViewModel", "Erro a obter detalhes das candidaturas: ", e)
        }
    }

    fun getCandidaturasHorario(scheduleId: String) {
        firestore.collection("Entries")
            .whereEqualTo("SchedulesId", scheduleId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val entriesList = snapshot.documents.mapNotNull { document ->
                        val positionsId = document.getString("PositionsId") ?: ""
                        val usersId = document.getString("UsersId") ?: ""
                        val state = document.getLong("State") ?: 0

                        Entries(
                            id = document.id,
                            schedulesId = scheduleId,
                            positionsId = positionsId,
                            usersId = usersId,
                            state = state.toInt()
                        )
                    }
                    fetchDetalhesHorario(entriesList)
                } else {
                    Log.d("CandidaturaHorarioViewModel", "Não foram encontradas candidaturas")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CandidaturaHorarioViewModel", "Erro a obter candidaturas: ", e)
            }
    }

    private fun fetchDetalhesHorario(entries: List<Entries>) {
        val positionsMap = mutableMapOf<String, String>()
        val usersMap = mutableMapOf<String, String>()

        // Carrega posições e voluntários únicos
        val positionIds = entries.map { it.positionsId }.distinct()
        val userIds = entries.map { it.usersId }.distinct()

        val positionsTask = firestore.collection("Positions")
            .whereIn(FieldPath.documentId(), positionIds)
            .get()

        val usersTask = firestore.collection("Users")
            .whereIn(FieldPath.documentId(), userIds)
            .get()

        // Obtem todos os valores antes de concluir
        Tasks.whenAllComplete(positionsTask, usersTask).addOnSuccessListener {
            val positionsResult = positionsTask.result
            val usersResult = usersTask.result

            positionsResult?.documents?.forEach { doc ->
                positionsMap[doc.id] = doc.getString("Name") ?: "Desconhecido"
            }

            usersResult?.documents?.forEach { doc ->
                usersMap[doc.id] = doc.getString("Name") ?: "Desconhecido"
            }

            // Converte para a classe de detalhes
            val entriesDetailList = entries.map { entry ->
                EntriesDetail(
                    id = entry.id,
                    userId = entry.usersId,
                    state = entry.state,
                    positionsId = entry.positionsId,
                    positionName = positionsMap[entry.positionsId] ?: "Desconhecido",
                    schedulesId = entry.schedulesId,
                    scheduleDate = "",
                    userName = usersMap[entry.usersId] ?: "Desconhecido"
                )
            }

            listEntriesDetail = entriesDetailList
        }.addOnFailureListener { e ->
            Log.e("CandidaturaHorarioViewModel", "Erro a obter detalhes das candidaturas: ", e)
        }
    }


    fun updateCandidaturaHorario(updatedEntry: EntriesDetail) {

        val entryMap = mapOf(
            "SchedulesId" to updatedEntry.schedulesId,
            "PositionsId" to updatedEntry.positionsId,
            "UsersId" to updatedEntry.userId,
            "State" to updatedEntry.state
        )

        firestore.collection("Entries").document(updatedEntry.id).set(entryMap)
            .addOnSuccessListener { documentReference ->
                Log.d("CandidaturaHorarioViewModel", "Candidatura atualizada")
                getCandidaturasHorario(updatedEntry.schedulesId)
            }
            .addOnFailureListener { e ->
                Log.w("CandidaturaHorarioViewModel", "Erro a atualizar candidatura", e)
            }
    }

}