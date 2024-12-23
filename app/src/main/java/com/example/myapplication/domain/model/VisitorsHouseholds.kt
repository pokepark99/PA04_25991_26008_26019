package com.example.myapplication.domain.model

data class VisitorsHouseholds (
    val householdsId: String,
    val visitorsId: String
) {
    companion object {
        fun addVisitorHousehold(
            list: MutableList<VisitorsHouseholds>,
            householdsId: String,
            visitorsId: String
        ) {
            val visitorHousehold = VisitorsHouseholds(householdsId, visitorsId)
            list.add(visitorHousehold)
        }
    }
}
