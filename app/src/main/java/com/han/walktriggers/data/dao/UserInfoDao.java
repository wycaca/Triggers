package com.han.walktriggers.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.han.walktriggers.entity.UserInfo;


@Dao
public interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUserInfo(UserInfo userInfo);

    @Delete
    void deleteUserInfo(UserInfo userInfo);

    @Update
    void updateUserInfo(UserInfo ... userInfo);

    @Query("select * from user_info limit 1")
    UserInfo getUserInfo();
}
