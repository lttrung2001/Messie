package vn.trunglt.messie.data.repositories.user

import vn.trunglt.messie.data.repositories.user.shared_preferences.UserStorage
import vn.trunglt.messie.domain.models.UserModel
import vn.trunglt.messie.domain.repositories.UserRepository

class UserRepoImpl(
    private val userStorage: UserStorage,
) : UserRepository {
    private var user: UserModel? = null

    override suspend fun findUser(): UserModel {
        return user ?: userStorage.findUser().also {
            user = it
        }
    }

    override suspend fun createUser(): UserModel {
        return userStorage.createUser().also {
            user = it
        }
    }
}