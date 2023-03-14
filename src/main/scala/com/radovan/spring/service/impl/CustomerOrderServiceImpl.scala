package com.radovan.spring.service.impl

import java.lang.Double
import java.util
import java.util.Optional

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.CustomerOrderDto
import com.radovan.spring.entity.{CustomerOrderEntity, OrderItemEntity, UserEntity}
import com.radovan.spring.exceptions.InsufficientStockException
import com.radovan.spring.repository.{CartItemRepository, CustomerOrderRepository, CustomerRepository, OrderAddressRepository, OrderItemRepository, ProductRepository}
import com.radovan.spring.service.{CartService, CustomerOrderService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Service
@Transactional
class CustomerOrderServiceImpl extends CustomerOrderService {

  @Autowired
  private var orderRepository:CustomerOrderRepository = _

  @Autowired
  private var customerRepository:CustomerRepository = _

  @Autowired
  private var tempConverter:TempConverter = _

  @Autowired
  private var productRepository:ProductRepository = _

  @Autowired
  private var orderItemRepository:OrderItemRepository = _

  @Autowired
  private var cartItemRepository:CartItemRepository = _

  @Autowired
  private var cartService:CartService = _

  @Autowired
  private var orderAddressRepository:OrderAddressRepository = _

  override def addCustomerOrder: CustomerOrderDto = {
    val authUser = SecurityContextHolder.getContext.getAuthentication.getPrincipal.asInstanceOf[UserEntity]
    val customerEntity = customerRepository.findByUserId(authUser.getId)
    val shippingAddress = customerEntity.getShippingAddress
    val cartEntity = customerEntity.getCart
    val orderEntity = new CustomerOrderEntity
    val orderedItems = new util.ArrayList[OrderItemEntity]
    val cartItems = Optional.ofNullable(cartEntity.getCartItems)
    if (!cartItems.isEmpty) {
      for (item <- cartItems.get.asScala) {
        val productEntity = Optional.ofNullable(item.getProduct)
        if (productEntity.isPresent) if (productEntity.get.getUnitStock < item.getQuantity) {
          val error = new Error("Not enough stock")
          throw new InsufficientStockException(error)
        }
        else {
          val tempProduct = productEntity.get
          val newStock = tempProduct.getUnitStock - item.getQuantity
          tempProduct.setUnitStock(newStock)
          productRepository.saveAndFlush(tempProduct)
        }
      }
    }
    if (!cartItems.isEmpty) {
      for (item <- cartItems.get.asScala) {
        val orderedItem = tempConverter.cartItemToOrderItemEntity(item)
        val storedOrderedItem = orderItemRepository.save(orderedItem)
        orderedItems.add(storedOrderedItem)
      }
    }
    val orderAddress = tempConverter.shippingAddressToOrderAddress(shippingAddress)
    val storedOrderAddress = orderAddressRepository.save(orderAddress)
    orderEntity.setCart(cartEntity)
    orderEntity.setCustomer(customerEntity)
    orderEntity.setOrderedItems(orderedItems)
    orderEntity.setAddress(storedOrderAddress)
    val storedOrder = orderRepository.save(orderEntity)
    storedOrderAddress.setOrder(storedOrder)
    orderAddressRepository.saveAndFlush(storedOrderAddress)
    val returnValue = tempConverter.orderEntityToDto(storedOrder)
    for (item <- storedOrder.getOrderedItems.asScala) {
      item.setOrder(storedOrder)
      orderItemRepository.saveAndFlush(item)
    }
    cartItemRepository.removeAllByCartId(cartEntity.getCartId)
    cartService.refreshCartState(cartEntity.getCartId)
    returnValue
  }

  override def listAll: util.List[CustomerOrderDto] = {
    val allOrders = orderRepository.findAll
    val returnValue = new util.ArrayList[CustomerOrderDto]
    for (order <- allOrders.asScala) {
      val orderDto = tempConverter.orderEntityToDto(order)
      returnValue.add(orderDto)
    }
    returnValue
  }

  override def calculateOrderTotal(orderId: Integer): Double = {
    val orderTotal = Optional.ofNullable(orderItemRepository.calculateGrandTotal(orderId))
    var returnValue = 0d
    if (orderTotal.isPresent) returnValue = orderTotal.get
    returnValue
  }

  override def getOrder(orderId: Integer): CustomerOrderDto = {
    val orderEntity = orderRepository.getById(orderId)
    val returnValue = tempConverter.orderEntityToDto(orderEntity)
    returnValue
  }

  override def deleteOrder(orderId: Integer): Unit = {
    orderRepository.deleteById(orderId)
    orderRepository.flush()
  }

  override def listAllByCustomerId(customerId: Integer): util.List[CustomerOrderDto] = {
    val returnValue = new util.ArrayList[CustomerOrderDto]
    val allOrders = Optional.ofNullable(orderRepository.findAllByCustomerId(customerId))
    if (!allOrders.isEmpty) {
      for (order <- allOrders.get.asScala) {
        val orderDto = tempConverter.orderEntityToDto(order)
        returnValue.add(orderDto)
      }
    }
    returnValue
  }
}

