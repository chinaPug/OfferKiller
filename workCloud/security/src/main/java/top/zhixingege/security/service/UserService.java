package top.zhixingege.security.service;

import top.zhixingege.common.entity.UserEntity;

public interface UserService {
    UserEntity selectUserByEmail(String email);
}
