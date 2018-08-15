package com.fjc.springmvc.dao.impl;

import com.fjc.springmvc.annotation.Repository;
import com.fjc.springmvc.dao.IUserDao;

@Repository("userDaoImpl")
public class userDaoImpl implements IUserDao {
    public String getUser(String id) {
        if("8888".equals(id))
            return "冯佳财";
        else
            return "其他人";
    }
}
