package fi.metropolia.canopy.data.repository

import fi.metropolia.canopy.data.source.UserDao
import fi.metropolia.canopy.data.source.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(private val userDao: UserDao) {

    val userRole: Flow<String> =
        userDao.getUserRole().map { it ?: "student" }

    val customCo2: Flow<Double> =
        userDao.getCustomCo2().map { it ?: 0.0 }

    suspend fun changeRole(newRole: String) {
        val user = UserEntity(
            userRole = newRole
        )
        userDao.setUserRole(user)
    }

    suspend fun updateCustomCo2(value: Double) {
        userDao.setUserRole(
            UserEntity(
                id = 1,
                userRole = "student",
                customCo2 = value
            )
        )
    }
}