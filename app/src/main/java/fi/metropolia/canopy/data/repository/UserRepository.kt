package fi.metropolia.canopy.data.repository

import fi.metropolia.canopy.data.source.UserDao
import fi.metropolia.canopy.data.source.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(private val userDao: UserDao) {

    // Expose as a Flow so the UI can observe changes in real-time
    val userRole: Flow<String> = userDao.getUserRole().map { it ?: "student" }

    suspend fun changeRole(newRole: String) {
        val user = UserEntity(userRole = newRole)
        userDao.setUserRole(user)
    }
}