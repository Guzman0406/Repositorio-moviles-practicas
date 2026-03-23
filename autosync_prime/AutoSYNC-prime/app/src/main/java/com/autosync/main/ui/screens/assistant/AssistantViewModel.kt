package com.autosync.main.ui.screens.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.repository.AssistantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean
)

data class AssistantState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(text = "¡Hola! Soy tu mecánico virtual. ¿En qué puedo ayudarte con tu auto hoy?", isFromUser = false)
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val assistantRepository: AssistantRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AssistantState())
    val state: StateFlow<AssistantState> = _state.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(text = text, isFromUser = true)
        
        _state.update { 
            it.copy(
                messages = it.messages + userMessage,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                assistantRepository.sendMessage(text).collect { responseText ->
                    val aiMessage = ChatMessage(text = responseText, isFromUser = false)
                    _state.update { 
                        it.copy(
                            messages = it.messages + aiMessage,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error al conectar con el mecánico."
                    )
                }
            }
        }
    }
}
