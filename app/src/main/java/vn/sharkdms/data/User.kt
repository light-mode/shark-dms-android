package vn.sharkdms.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(val token: String, val id: Int, val name: String, val phone: String,
    val avatar: String, val createdAt: String, val email: String, val position: String,
    val company: String, val roleId: Int, val roleName: String, @PrimaryKey val key: Int)
