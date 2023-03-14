package com.radovan.spring.repository

import java.util
import com.radovan.spring.entity.CustomerOrderEntity
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait CustomerOrderRepository extends JpaRepository[CustomerOrderEntity, Integer] {

  @Query(value = "select * from customer_orders where customer_id = :customerId", nativeQuery = true)
  def findAllByCustomerId(@Param("customerId") customerId: Integer): util.List[CustomerOrderEntity]
}
