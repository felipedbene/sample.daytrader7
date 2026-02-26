package com.ibm.daytrader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibm.daytrader.entity.HoldingDataBean;

public interface HoldingRepository extends JpaRepository<HoldingDataBean, Integer> {

    @Query("SELECT h FROM HoldingDataBean h JOIN FETCH h.quote WHERE h.account.profile.userID = :userID")
    List<HoldingDataBean> findByAccountProfileUserID(@Param("userID") String userID);
}
