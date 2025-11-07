package me.sebastianlizcano.unabshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val productos: List<Producto> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.obtenerProductosFlow().collect { lista ->
                _ui.update { it.copy(productos = lista) }
            }
        }
    }

    fun onNombreChange(v: String) = _ui.update { it.copy(nombre = v) }
    fun onDescripcionChange(v: String) = _ui.update { it.copy(descripcion = v) }
    fun onPrecioChange(v: String) = _ui.update { it.copy(precio = v) }

    fun guardarProducto() {
        val nombre = _ui.value.nombre.trim()
        val desc = _ui.value.descripcion.trim()
        val precioNum = _ui.value.precio.toDoubleOrNull()

        if (nombre.isEmpty() || desc.isEmpty() || precioNum == null) {
            _ui.update { it.copy(error = "Completa todos los campos correctamente") }
            return
        }

        viewModelScope.launch {
            try {
                repo.agregarProducto(Producto(nombre = nombre, descripcion = desc, precio = precioNum))
                _ui.update { it.copy(nombre = "", descripcion = "", precio = "", error = null) }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Error al guardar") }
            }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            try {
                repo.eliminarProducto(id)
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Error al eliminar") }
            }
        }
    }
}
