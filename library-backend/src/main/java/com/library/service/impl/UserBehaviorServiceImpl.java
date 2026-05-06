package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.UserBehavior;
import com.library.mapper.UserBehaviorMapper;
import com.library.service.UserBehaviorService;
import org.springframework.stereotype.Service;

@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements UserBehaviorService {

}
