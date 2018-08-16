package com.fjc.springmvc.service.impl;

import com.fjc.springmvc.annotation.Qualifier;
import com.fjc.springmvc.annotation.Service;
import com.fjc.springmvc.dao.IUserDao;
import com.fjc.springmvc.service.IUserService;

@Service("userServiceImpl")
public class UserServiceImpl implements IUserService {

    @Qualifier("userDaoImpl")
    private IUserDao userDaoImpl;

    public String getUser(String id) {
        return userDaoImpl.getUser(id);
    }

}
