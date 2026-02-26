package com.ibm.daytrader.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibm.daytrader.entity.AccountDataBean;

public interface AccountRepository extends JpaRepository<AccountDataBean, Integer> {

    @Query("SELECT a FROM AccountDataBean a WHERE a.profile.userID = :userID")
    Optional<AccountDataBean> findByProfileUserID(@Param("userID") String userID);
}
