package com.radovan.spring.repository

import com.radovan.spring.entity.ReviewMessageEntity
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait ReviewMessageRepository extends JpaRepository[ReviewMessageEntity, Integer] {

  @Modifying
  @Query(value = "delete from reviews where customer_id = :customerId", nativeQuery = true)
  def deleteAllByCustomerId(@Param("customerId") customerId: Integer): Unit
}
