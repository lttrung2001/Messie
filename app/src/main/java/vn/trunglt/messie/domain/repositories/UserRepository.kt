package vn.trunglt.messie.domain.repositories

import vn.trunglt.messie.domain.models.UserModel

interface UserRepository {
    suspend fun findUser(): UserModel
    suspend fun createUser(): UserModel
}