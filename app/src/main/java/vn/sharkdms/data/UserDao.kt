package vn.sharkdms.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("select * from user_table")
    fun getUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("delete from user_table")
    suspend fun deleteUserInfo()

    @Query("update user_table set avatar = :avatar")
    suspend fun updateAvatar(avatar: String)
}