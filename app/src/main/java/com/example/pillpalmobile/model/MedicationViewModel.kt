package com.example.pillpalmobile.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillpalmobile.data.ApiClient
import com.example.pillpalmobile.data.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicationViewModel : ViewModel() {

    private val api: ApiService =
        ApiClient.instance.create(ApiService::class.java)

    private val _medications = MutableStateFlow<List<MedicationResponse>>(emptyList())
    val medications: StateFlow<List<MedicationResponse>> = _medications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError


    // ============================================
    // LOAD MEDICATIONS
    // ============================================
    fun loadMedications(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = api.getMedications(userId)
                if (response.isSuccessful) {
                    _medications.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }


    // ============================================
    // ADD MEDICATION — POST /medications
    // ============================================
    fun addMedication(request: AddMedicationRequest) {
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null

            try {
                val response = api.addMedication(request)
                if (response.isSuccessful) {
                    // Una vez creada → refrescamos lista
                    loadMedications(request.user_id)
                } else {
                    _saveError.value = "Save failed: ${response.code()}"
                }
            } catch (e: Exception) {
                _saveError.value = e.message ?: "Unknown error"
            } finally {
                _isSaving.value = false
            }
        }
    }
}
