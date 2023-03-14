package com.radovan.spring.service

import java.util
import java.lang.Double
import com.radovan.spring.dto.CustomerOrderDto

trait CustomerOrderService {

  def addCustomerOrder: CustomerOrderDto

  def listAll: util.List[CustomerOrderDto]

  def calculateOrderTotal(orderId: Integer): Double

  def getOrder(orderId: Integer): CustomerOrderDto

  def deleteOrder(orderId: Integer): Unit

  def listAllByCustomerId(customerId: Integer): util.List[CustomerOrderDto]
}

