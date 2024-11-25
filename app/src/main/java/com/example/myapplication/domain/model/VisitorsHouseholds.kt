package com.example.myapplication.domain.model

data class VisitorsHouseholds (
    val householdsId: Int,
    val visitorsId: Int
) {
    companion object {
        fun addVisitorHousehold(
            list: MutableList<VisitorsHouseholds>,
            householdsId: Int,
            visitorsId: Int
        ) {
            val visitorHousehold = VisitorsHouseholds(householdsId, visitorsId)
            list.add(visitorHousehold)
        }
    }
}
