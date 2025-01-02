package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.ItemType
import com.example.myapplication.domain.model.Items
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StockViewModel : ViewModel(){
    private val firestore = Firebase.firestore

    // categorias para os produtos
    private val _itemTypes = MutableStateFlow<List<ItemType>>(emptyList())
    val itemTypes: StateFlow<List<ItemType>> = _itemTypes

    //lista de produtos
    var listProducts by mutableStateOf<List<Items>>(emptyList())

    private val _filteredProducts = MutableStateFlow<List<Items>>(emptyList())
    val filteredProducts: StateFlow<List<Items>> = _filteredProducts

    //busca as categorias de produtos e os produtos
    init {
        fetchItemTypes()
        fetchItems()

        _filteredProducts.value = listProducts
    }
    //fetch das categorias dos produtos
    private fun fetchItemTypes() {
        firestore.collection("ItemType").addSnapshotListener { snapshot, _ ->
            val items = snapshot?.documents?.map {
                ItemType(it.id, it.getString("Description") ?: "")
            } ?: emptyList()
            _itemTypes.value = items
        }
    }
    // busca os produtos
    private fun fetchItems() {
        firestore.collection("Items").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("StockViewModel", "Error fetching items: ", error)
                return@addSnapshotListener
            }
            val products = snapshot?.documents?.mapNotNull { document ->
                val id = document.id
                val itemTypeId = document.getString("ItemTypeId") ?: return@mapNotNull null
                val name = document.getString("Name") ?: return@mapNotNull null
                val stock = document.getLong("Stock") ?: return@mapNotNull null

                Items(
                    id = id,
                    itemTypeId = itemTypeId,
                    name = name,
                    stock = stock
                )
            } ?: emptyList()

            // atualiza a lista caso haja alteracoes no firestore
            listProducts = products
            _filteredProducts.value = products

            Log.d("StockViewModel", "Products updated: $products")
        }
    }
    //adicionar novo produto
    fun addProduct(name: String, itemTypeName: String, stock: Int, callback: (Boolean) -> Unit) {
        val itemType = _itemTypes.value.firstOrNull { it.description == itemTypeName }
        if (itemType != null) {
            val product = hashMapOf(
                "Name" to name,
                "ItemTypeId" to itemType.id,
                "Stock" to stock
            )
            firestore.collection("Items").add(product)
                .addOnSuccessListener {
                    Log.d("StockViewModel", "Product successfully added")
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.e("StockViewModel", "Error adding product", e)
                    callback(false)
                }
        }
    }
    //pesquisar pelo nome
    fun filterProducts(query: String) {
        _filteredProducts.value = if (query.isEmpty()) {
            listProducts
        } else {
            listProducts.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }
    //filtra pelo nome (ascendente ou descrecente) e por categorias
    fun sortProducts(sortOption: String, selectedCategories: List<String>){
        val filteredList = if (selectedCategories.isEmpty()) {
            listProducts //todos os produtos
        } else {
            listProducts.filter { item ->
                _itemTypes.value.any { it.id == item.itemTypeId && it.description in selectedCategories }
            }
        }

        _filteredProducts.value = when (sortOption) {
            "Nome: Crescente" -> filteredList.sortedBy { it.name.lowercase() }
            "Nome: Decrescente" -> filteredList.sortedByDescending { it.name.lowercase() }
            else -> filteredList
        }
    }
    fun updateProduct(id: String, name: String, stock: Int, callback: (Boolean) -> Unit) {
        val productRef = firestore.collection("Items").document(id)

        productRef.update(
            "Name", name,
            "Stock", stock
        )
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                callback(false)
                Log.e("Firestore", "Error updating product", exception)
            }
    }
    //apagar um produto no firestore
    fun deleteProduct(id: String) {
        val productDel = FirebaseFirestore.getInstance().collection("Items").document(id)
        Log.d("StockViewModel", "Attempting to delete document: ${productDel.path}")

        productDel.delete()
            .addOnSuccessListener {
                Log.d("StockViewModel", "Product deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("StockViewModel", "Error deleting product", exception)
            }
    }
}