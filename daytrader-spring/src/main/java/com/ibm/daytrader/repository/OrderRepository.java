package com.ibm.daytrader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibm.daytrader.entity.OrderDataBean;

public interface OrderRepository extends JpaRepository<OrderDataBean, Integer> {

    @Query("SELECT o FROM OrderDataBean o WHERE o.account.profile.userID = :userID ORDER BY o.orderID DESC")
    List<OrderDataBean> findByAccountProfileUserID(@Param("userID") String userID);

    @Query("SELECT o FROM OrderDataBean o WHERE o.orderStatus = 'closed' AND o.account.profile.userID = :userID")
    List<OrderDataBean> findClosedOrdersByUserID(@Param("userID") String userID);

    @Modifying
    @Query("UPDATE OrderDataBean o SET o.orderStatus = 'completed' WHERE o.orderStatus = 'closed' AND o.account.profile.userID = :userID")
    int markClosedOrdersCompleted(@Param("userID") String userID);
}
