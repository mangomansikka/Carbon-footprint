package fi.metropolia.canopy.data.repository

import fi.metropolia.canopy.data.source.UserDao
import fi.metropolia.canopy.data.source.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun changeRole(newRole: String) {
        val user = UserEntity(userRole = newRole)
        userDao.setUserRole(user)
    }
}