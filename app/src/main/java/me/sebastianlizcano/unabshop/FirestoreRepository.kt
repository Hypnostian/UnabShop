package me.sebastianlizcano.unabshop

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val collection = db.collection("productos")

    suspend fun agregarProducto(producto: Producto) {
        collection.add(producto).await()
    }

    suspend fun eliminarProducto(id: String) {
        collection.document(id).delete().await()
    }

    fun obtenerProductosFlow(): Flow<List<Producto>> = callbackFlow {
        val listener = collection.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val items = snap?.documents?.mapNotNull {
                it.toObject(Producto::class.java)?.copy(id = it.id)
            }.orEmpty()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }
}
