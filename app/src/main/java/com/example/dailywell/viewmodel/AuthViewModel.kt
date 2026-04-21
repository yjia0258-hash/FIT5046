package com.example.dailywell.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailywell.data.local.AppDatabase
import com.example.dailywell.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Login/Register Status
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    //Currently logged-in user ID (saved after successful login)
    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId

    //  Login status
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    // Registration status
    private val _signUpState = MutableStateFlow<AuthState>(AuthState.Idle)
    val signUpState: StateFlow<AuthState> = _signUpState

    //  Login
    fun login(email: String, password: String) {
        // Verification information
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Please fill in all fields.")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = AuthState.Error("Please enter a valid email address.")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val user = userDao.login(email.trim(), password)
            if (user != null) {
                _currentUserId.value = user.id
                _loginState.value = AuthState.Success
            } else {
                _loginState.value = AuthState.Error("Incorrect email or password.")
            }
        }
    }

    // register
    fun signUp(name: String, email: String, password: String, confirmPassword: String) {
        // Verification information
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _signUpState.value = AuthState.Error("Please fill in all fields.")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _signUpState.value = AuthState.Error("Please enter a valid email address.")
            return
        }
        if (password.length < 8) {
            _signUpState.value = AuthState.Error("Password must be at least 8 characters.")
            return
        }
        if (!password.any { it.isLetter() } || !password.any { it.isDigit() }) {
            _signUpState.value = AuthState.Error("Password must include letters and numbers.")
            return
        }
        if (password != confirmPassword) {
            _signUpState.value = AuthState.Error("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _signUpState.value = AuthState.Loading

            //Check if the email address is already registered.
            val exists = userDao.isEmailRegistered(email.trim())
            if (exists > 0) {
                _signUpState.value = AuthState.Error("This email is already registered.")
                return@launch
            }

            // Create new user
            val newUser = User(
                name = name.trim(),
                email = email.trim(),
                password = password
            )
            userDao.insertUser(newUser)

            // Login successful, user ID retrieved
            val user = userDao.getUserByEmail(email.trim())
            _currentUserId.value = user?.id
            _signUpState.value = AuthState.Success
        }
    }

    // Sign out
    fun logout() {
        _currentUserId.value = null
        _loginState.value = AuthState.Idle
        _signUpState.value = AuthState.Idle
    }

    // reset state
    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun resetSignUpState() {
        _signUpState.value = AuthState.Idle
    }
}
