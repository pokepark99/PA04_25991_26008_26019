package com.example.myapplication.presentation.viewModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReduceCapacity
import androidx.compose.material.icons.filled.SensorOccupied
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainActivity
import com.example.myapplication.domain.model.Functionalities
import com.example.myapplication.domain.model.FunctionalityDetail
import com.example.myapplication.domain.model.Users
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MenuViewModel : ViewModel() {

    var listFunc by mutableStateOf<List<Functionalities>>(emptyList())
    var listFuncDetail by mutableStateOf<List<FunctionalityDetail>>(emptyList())
    var isGestor by mutableStateOf<Boolean>(false)
    var userData by mutableStateOf<Users?>(null)

    fun getFuncionalidades(mainActivity: MainActivity) {
        viewModelScope.launch {
            mainActivity.getFuncionalidades() { success ->
                listFunc = success
                updateFunctionalityDetails()
            }
        }
    }

    fun updateFunctionalityDetails() {
        viewModelScope.launch {
            val updatedDetails = mutableListOf(
                FunctionalityDetail("Visitas","visitasLoja", Icons.Default.Groups),
                FunctionalityDetail("Gerir Visitantes","visitantes", Icons.Default.ReduceCapacity),
                FunctionalityDetail("Gerir Agregado Familiar","agregado", Icons.Default.FamilyRestroom),
                FunctionalityDetail("Horários","horarios/${isGestor}", Icons.Default.CalendarMonth)
            )

            listFunc.filter { it.State }.forEach { functionality ->
                when (functionality.Id) {
                    "pedidos" -> {
                        updatedDetails.add(FunctionalityDetail("Pedidos","pedidos", Icons.Default.ShoppingBag))
                    }
                    "doacoes" -> {
                        updatedDetails.add(FunctionalityDetail("Doações","doacoes", Icons.Default.VolunteerActivism))
                    }
                    "stock" -> {
                        updatedDetails.add(FunctionalityDetail("Inventário","stock", Icons.Default.Inventory))
                    }
                    "entidades" -> {
                        updatedDetails.add(FunctionalityDetail("Gerir Entidades","entidades", Icons.Default.Badge))
                    }
                    "graficos" -> {
                        updatedDetails.add(FunctionalityDetail("Ver Gráficos","graficos", Icons.Default.PieChart))
                    }
                }
            }

            if (isGestor) {
                updatedDetails.add(FunctionalityDetail("Gerir Voluntários","voluntarios", Icons.Default.ManageAccounts))
            }

            listFuncDetail = updatedDetails
        }
    }

    // Suspende para obter dados corretos do utilizador antes de criar os itens no menu
    suspend fun getCurrentUser(mainActivity: MainActivity, uid: String) {
        suspendCoroutine<Unit> { continuation ->
            mainActivity.getCurrentUser(uid) { success ->
                isGestor = success?.admin ?: false
                userData = success
                continuation.resume(Unit)
            }
        }
    }

    fun getAllData() {
        Firebase.firestore.collection("Countries").get()
        Firebase.firestore.collection("Donations").get()
        Firebase.firestore.collection("Entities").get()
        Firebase.firestore.collection("Entries").get()
        Firebase.firestore.collection("Functionalities").get()
        Firebase.firestore.collection("Households").get()
        Firebase.firestore.collection("ItemType").get()
        Firebase.firestore.collection("Items").get()
        Firebase.firestore.collection("Positions").get()
        Firebase.firestore.collection("Requests").get()
        Firebase.firestore.collection("Schedules").get()
        Firebase.firestore.collection("Stores").get()
        Firebase.firestore.collection("Users").get()
        Firebase.firestore.collection("Visitors").get()
        Firebase.firestore.collection("VisitorHouseholds").get()
        Firebase.firestore.collection("Visits").get()
    }
}