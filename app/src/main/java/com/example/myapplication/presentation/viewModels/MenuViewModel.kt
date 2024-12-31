package com.example.myapplication.presentation.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.domain.model.Functionalities
import com.example.myapplication.domain.model.FunctionalityDetail
import com.example.myapplication.domain.model.Users
import kotlinx.coroutines.launch

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
        val updatedDetails = mutableListOf(
            FunctionalityDetail("Visitas","visitasLoja",R.drawable.groups_100dp_434343),
            FunctionalityDetail("Gerir Visitantes","visitantes",R.drawable.reduce_capacity_100dp_434343),
            FunctionalityDetail("Gerir Agregado Familiar","agregado",R.drawable.family_restroom_100dp_434343),
            FunctionalityDetail("Horários","horarios/${isGestor}",R.drawable.calendar_month_100dp_434343)
        )

        listFunc.filter { it.State }.forEach { functionality ->
            when (functionality.Id) {
                "pedidos" -> {
                    updatedDetails.add(FunctionalityDetail("Pedidos","pedidos",R.drawable.shopping_bag_100dp_434343))
                }
                "doacoes" -> {
                    updatedDetails.add(FunctionalityDetail("Doações","doacoes",R.drawable.volunteer_activism_100dp_434343))
                    updatedDetails.add(FunctionalityDetail("Gerir Doadores","doadores",R.drawable.sensor_occupied_100dp_434343))
                }
                "stock" -> {
                    updatedDetails.add(FunctionalityDetail("Inventário","stock",R.drawable.inventory_100dp_434343))
                }
                "entidades" -> {
                    updatedDetails.add(FunctionalityDetail("Gerir Entidades","entidades",R.drawable.badge_100dp_434343))
                }
                "graficos" -> {
                    updatedDetails.add(FunctionalityDetail("Ver Gráficos","graficos",R.drawable.pie_chart_100dp_434343))
                }
            }
        }

        if (isGestor) {
            updatedDetails.add(FunctionalityDetail("Registar Voluntário","voluntarios",R.drawable.manage_accounts_100dp_434343))
        }

        listFuncDetail = updatedDetails
    }

    fun getCurrentUser(mainActivity: MainActivity, uid: String) {
        viewModelScope.launch {
            mainActivity.getCurrentUser(uid) { success ->
                userData = success
                isGestor = success?.admin ?: false
            }
        }
    }
}