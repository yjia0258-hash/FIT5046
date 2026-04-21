package com.example.dailywell.data.local

import androidx.room.*
import com.example.dailywell.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    //  Register new user
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    // Update user information (for saving on the Profile page)
    @Update
    suspend fun updateUser(user: User)

    // Search for users by email address (for login verification)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // Login verification via email and password
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    // Search for users by ID (for display on the Profile page)
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): Flow<User?>

    // Check if the email address is already registered (for verification on the Sign-up page)
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun isEmailRegistered(email: String): Int

    // Delete User (for logging out/deleting account)
    @Delete
    suspend fun deleteUser(user: User)
}