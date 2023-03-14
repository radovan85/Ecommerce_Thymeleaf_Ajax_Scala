package com.radovan.spring.converter

import java.util
import java.util.Optional

import com.radovan.spring.dto.{BillingAddressDto, CartDto, CartItemDto, CustomerDto, CustomerOrderDto, OrderAddressDto, OrderItemDto, ProductDto, ReviewMessageDto, RoleDto, ShippingAddressDto, UserDto}
import com.radovan.spring.entity.{BillingAddressEntity, CartEntity, CartItemEntity, CustomerEntity, CustomerOrderEntity, OrderAddressEntity, OrderItemEntity, ProductEntity, ReviewMessageEntity, RoleEntity, ShippingAddressEntity, UserEntity}
import com.radovan.spring.repository.{BillingAddressRepository, CartItemRepository, CartRepository, CustomerOrderRepository, CustomerRepository, OrderAddressRepository, OrderItemRepository, ProductRepository, RoleRepository, ShippingAddressRepository, UserRepository}
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._

class TempConverter {

  @Autowired
  private var mapper:ModelMapper = _

  @Autowired
  private var customerRepository:CustomerRepository = _

  @Autowired
  private var cartRepository:CartRepository = _

  @Autowired
  private var userRepository:UserRepository = _

  @Autowired
  private var billingAddressRepository:BillingAddressRepository = _

  @Autowired
  private var shippingAddressRepository:ShippingAddressRepository = _

  @Autowired
  private var productRepository:ProductRepository = _

  @Autowired
  private var cartItemRepository:CartItemRepository = _

  @Autowired
  private var roleRepository:RoleRepository = _

  @Autowired
  private var orderItemRepository:OrderItemRepository = _

  @Autowired
  private var orderRepository:CustomerOrderRepository = _

  @Autowired
  private var orderAddressRepository:OrderAddressRepository = _

  def cartEntityToDto(cartEntity: CartEntity): CartDto = {
    val returnValue = mapper.map(cartEntity, classOf[CartDto])
    val cartPrice = Optional.ofNullable(cartEntity.getCartPrice)
    if (!cartPrice.isPresent) returnValue.setCartPrice(0d)
    val customerEntity = Optional.ofNullable(cartEntity.getCustomer)
    if (customerEntity.isPresent) returnValue.setCustomerId(customerEntity.get.getCustomerId)
    val itemsIds = new util.ArrayList[Integer]
    val cartItems = Optional.ofNullable(cartEntity.getCartItems)
    if (!cartItems.isEmpty) {
      for (itemEntity <- cartItems.get.asScala) {
        val itemId = itemEntity.getCartItemId
        itemsIds.add(itemId)
      }
    }
    returnValue.setCartItemsIds(itemsIds)
    returnValue
  }

  def cartDtoToEntity(cartDto: CartDto): CartEntity = {
    val returnValue = mapper.map(cartDto, classOf[CartEntity])
    val cartPrice = Optional.ofNullable(cartDto.getCartPrice)
    if (!cartPrice.isPresent) returnValue.setCartPrice(0d)
    val customerId = Optional.ofNullable(cartDto.getCustomerId)
    if (customerId.isPresent) {
      val customerEntity = customerRepository.getById(customerId.get)
      returnValue.setCustomer(customerEntity)
    }
    val cartItems = new util.ArrayList[CartItemEntity]
    val itemIds = Optional.ofNullable(cartDto.getCartItemsIds)
    if (!itemIds.isEmpty) {
      for (itemId <- itemIds.get.asScala) {
        val itemEntity = cartItemRepository.getById(itemId)
        cartItems.add(itemEntity)
      }
    }
    returnValue.setCartItems(cartItems)
    returnValue
  }

  def cartItemEntityToDto(cartItemEntity: CartItemEntity): CartItemDto = {
    val returnValue = mapper.map(cartItemEntity, classOf[CartItemDto])
    val product = Optional.ofNullable(cartItemEntity.getProduct)
    if (product.isPresent) {
      returnValue.setProductId(product.get.getProductId)
      var price = product.get.getProductPrice
      val discount = product.get.getDiscount
      val quantity = returnValue.getQuantity
      price = (price - ((price / 100) * discount)) * quantity
      returnValue.setPrice(price)
    }
    val cart = Optional.ofNullable(cartItemEntity.getCart)
    if (cart.isPresent) returnValue.setCartId(cart.get.getCartId)
    returnValue
  }

  def cartItemDtoToEntity(cartItemDto: CartItemDto): CartItemEntity = {
    val returnValue = mapper.map(cartItemDto, classOf[CartItemEntity])
    val cartId = Optional.ofNullable(cartItemDto.getCartId)
    if (cartId.isPresent) {
      val cartEntity = cartRepository.getById(cartId.get)
      returnValue.setCart(cartEntity)
    }
    val productId = Optional.ofNullable(cartItemDto.getProductId)
    if (productId.isPresent) {
      val productEntity = productRepository.getById(productId.get)
      returnValue.setProduct(productEntity)
      var price = productEntity.getProductPrice
      val discount = productEntity.getDiscount
      val quantity = returnValue.getQuantity
      price = (price - ((price / 100) * discount)) * quantity
      returnValue.setPrice(price)
    }
    returnValue
  }

  def productEntityToDto(productEntity: ProductEntity): ProductDto = {
    val returnValue = mapper.map(productEntity, classOf[ProductDto])
    returnValue
  }

  def productDtoToEntity(productDto: ProductDto): ProductEntity = {
    val returnValue = mapper.map(productDto, classOf[ProductEntity])
    returnValue
  }

  def customerEntityToDto(customerEntity: CustomerEntity): CustomerDto = {
    val returnValue = mapper.map(customerEntity, classOf[CustomerDto])
    val billingAddressEntity = Optional.ofNullable(customerEntity.getBillingAddress)
    if (billingAddressEntity.isPresent) returnValue.setBillingAddressId(billingAddressEntity.get.getBillingAddressId)
    val shippingAddressEntity = Optional.ofNullable(customerEntity.getShippingAddress)
    if (shippingAddressEntity.isPresent) returnValue.setShippingAddressId(shippingAddressEntity.get.getShippingAddressId)
    val cartEntity = Optional.ofNullable(customerEntity.getCart)
    if (cartEntity.isPresent) returnValue.setCartId(cartEntity.get.getCartId)
    val userEntity = Optional.ofNullable(customerEntity.getUser)
    if (userEntity.isPresent) returnValue.setUserId(userEntity.get.getId)
    returnValue
  }

  def customerDtoToEntity(customerDto: CustomerDto): CustomerEntity = {
    val returnValue = mapper.map(customerDto, classOf[CustomerEntity])
    val billingAddressId = Optional.ofNullable(customerDto.getBillingAddressId)
    if (billingAddressId.isPresent) {
      val billingAddressEntity = billingAddressRepository.getById(billingAddressId.get)
      returnValue.setBillingAddress(billingAddressEntity)
    }
    val shippingAddressId = Optional.ofNullable(customerDto.getShippingAddressId)
    if (shippingAddressId.isPresent) {
      val shippingAddressEntity = shippingAddressRepository.getById(shippingAddressId.get)
      returnValue.setShippingAddress(shippingAddressEntity)
    }
    val cartId = Optional.ofNullable(customerDto.getCartId)
    if (cartId.isPresent) {
      val cartEntity = cartRepository.getById(cartId.get)
      returnValue.setCart(cartEntity)
    }
    val userId = Optional.ofNullable(customerDto.getUserId)
    if (userId.isPresent) {
      val userEntity = userRepository.getById(userId.get)
      returnValue.setUser(userEntity)
    }
    returnValue
  }

  def billingAddressEntityToDto(addressEntity: BillingAddressEntity): BillingAddressDto = {
    val returnValue = mapper.map(addressEntity, classOf[BillingAddressDto])
    val customerEntity = Optional.ofNullable(addressEntity.getCustomer)
    if (customerEntity.isPresent) returnValue.setCustomerId(customerEntity.get.getCustomerId)
    returnValue
  }

  def billingAddressDtoToEntity(addressDto: BillingAddressDto): BillingAddressEntity = {
    val returnValue = mapper.map(addressDto, classOf[BillingAddressEntity])
    val customerId = Optional.ofNullable(addressDto.getCustomerId)
    if (customerId.isPresent) {
      val customerEntity = customerRepository.getById(customerId.get)
      returnValue.setCustomer(customerEntity)
    }
    returnValue
  }

  def shippingAddressEntityToDto(addressEntity: ShippingAddressEntity): ShippingAddressDto = {
    val returnValue = mapper.map(addressEntity, classOf[ShippingAddressDto])
    val customerEntity = Optional.ofNullable(addressEntity.getCustomer)
    if (customerEntity.isPresent) returnValue.setCustomerId(customerEntity.get.getCustomerId)
    returnValue
  }

  def shippingAddressDtoToEntity(addressDto: ShippingAddressDto): ShippingAddressEntity = {
    val returnValue = mapper.map(addressDto, classOf[ShippingAddressEntity])
    val customerId = Optional.ofNullable(addressDto.getCustomerId)
    if (customerId.isPresent) {
      val customerEntity = customerRepository.getById(customerId.get)
      returnValue.setCustomer(customerEntity)
    }
    returnValue
  }

  def orderEntityToDto(orderEntity: CustomerOrderEntity): CustomerOrderDto = {
    val returnValue = mapper.map(orderEntity, classOf[CustomerOrderDto])
    val addressEntity = Optional.ofNullable(orderEntity.getAddress)
    if (addressEntity.isPresent) returnValue.setAddressId(addressEntity.get.getOrderAddressId)
    val customerEntity = Optional.ofNullable(orderEntity.getCustomer)
    if (customerEntity.isPresent) returnValue.setCustomerId(customerEntity.get.getCustomerId)
    val cartEntity = Optional.ofNullable(orderEntity.getCart)
    if (cartEntity.isPresent) returnValue.setCartId(cartEntity.get.getCartId)
    val orderedItemsIds = new util.ArrayList[Integer]
    val orderedItems = Optional.ofNullable(orderEntity.getOrderedItems)
    if (!orderedItems.isEmpty) {
      for (item <- orderedItems.get.asScala) {
        val itemId = item.getOrderItemId
        orderedItemsIds.add(itemId)
      }
    }
    returnValue.setOrderedItemsIds(orderedItemsIds)
    returnValue
  }

  def orderDtoToEntity(orderDto: CustomerOrderDto): CustomerOrderEntity = {
    val returnValue = mapper.map(orderDto, classOf[CustomerOrderEntity])
    val addressId = Optional.ofNullable(orderDto.getAddressId)
    if (addressId.isPresent) {
      val address = orderAddressRepository.getById(addressId.get)
      returnValue.setAddress(address)
    }
    val customerId = Optional.ofNullable(orderDto.getCustomerId)
    if (customerId.isPresent) {
      val customerEntity = customerRepository.getById(customerId.get)
      returnValue.setCustomer(customerEntity)
    }
    val cartId = Optional.ofNullable(orderDto.getCartId)
    if (cartId.isPresent) {
      val cartEntity = cartRepository.getById(cartId.get)
      returnValue.setCart(cartEntity)
    }
    val orderedItems = new util.ArrayList[OrderItemEntity]
    val orderedItemsIds = Optional.ofNullable(orderDto.getOrderedItemsIds)
    if (!orderedItemsIds.isEmpty) {
      for (itemId <- orderedItemsIds.get.asScala) {
        val itemEntity = orderItemRepository.getById(itemId)
        orderedItems.add(itemEntity)
      }
    }
    returnValue.setOrderedItems(orderedItems)
    returnValue
  }

  def userEntityToDto(userEntity: UserEntity): UserDto = {
    val returnValue = mapper.map(userEntity, classOf[UserDto])
    returnValue.setEnabled(userEntity.getEnabled)
    val roles = Optional.ofNullable(userEntity.getRoles)
    val rolesIds = new util.ArrayList[Integer]
    if (!roles.isEmpty) {
      for (roleEntity <- roles.get.asScala) {
        rolesIds.add(roleEntity.getId)
      }
    }
    returnValue.setRolesIds(rolesIds)
    returnValue
  }

  def userDtoToEntity(userDto: UserDto): UserEntity = {
    val returnValue = mapper.map(userDto, classOf[UserEntity])
    val roles = new util.ArrayList[RoleEntity]
    val rolesIds = Optional.ofNullable(userDto.getRolesIds)
    if (!rolesIds.isEmpty) {
      for (roleId <- rolesIds.get.asScala) {
        val role = roleRepository.getById(roleId)
        roles.add(role)
      }
    }
    returnValue.setRoles(roles)
    returnValue
  }

  def roleEntityToDto(roleEntity: RoleEntity): RoleDto = {
    val returnValue = mapper.map(roleEntity, classOf[RoleDto])
    val users = Optional.ofNullable(roleEntity.getUsers)
    val userIds = new util.ArrayList[Integer]
    if (!users.isEmpty) {
      for (user <- users.get.asScala) {
        userIds.add(user.getId)
      }
    }
    returnValue.setUsersIds(userIds)
    returnValue
  }

  def roleDtoToEntity(roleDto: RoleDto): RoleEntity = {
    val returnValue = mapper.map(roleDto, classOf[RoleEntity])
    val usersIds = Optional.ofNullable(roleDto.getUsersIds)
    val users = new util.ArrayList[UserEntity]
    if (!usersIds.isEmpty) {
      for (userId <- usersIds.get.asScala) {
        val userEntity = userRepository.getById(userId)
        users.add(userEntity)
      }
    }
    returnValue.setUsers(users)
    returnValue
  }

  def orderItemEntityToDto(itemEntity: OrderItemEntity): OrderItemDto = {
    val returnValue = mapper.map(itemEntity, classOf[OrderItemDto])
    val orderEntity = Optional.ofNullable(itemEntity.getOrder)
    if (orderEntity.isPresent) returnValue.setOrderId(orderEntity.get.getCustomerOrderId)
    returnValue
  }

  def orderItemDtoToEntity(itemDto: OrderItemDto): OrderItemEntity = {
    val returnValue = mapper.map(itemDto, classOf[OrderItemEntity])
    val orderId = Optional.ofNullable(itemDto.getOrderId)
    if (orderId.isPresent) {
      val orderEntity = orderRepository.getById(orderId.get)
      returnValue.setOrder(orderEntity)
    }
    returnValue
  }

  def cartItemToOrderItemEntity(cartItemEntity: CartItemEntity): OrderItemEntity = {
    val returnValue = mapper.map(cartItemEntity, classOf[OrderItemEntity])
    val product = Optional.ofNullable(cartItemEntity.getProduct)
    if (product.isPresent) {
      returnValue.setProductName(product.get.getProductName)
      returnValue.setProductPrice(product.get.getProductPrice)
      returnValue.setDiscount(product.get.getDiscount)
    }
    returnValue
  }

  def reviewMessageEntityToDto(reviewEntity: ReviewMessageEntity): ReviewMessageDto = {
    val returnValue = mapper.map(reviewEntity, classOf[ReviewMessageDto])
    val customerEntity = Optional.ofNullable(reviewEntity.getCustomer)
    if (customerEntity.isPresent) returnValue.setCustomerId(customerEntity.get.getCustomerId)
    returnValue
  }

  def reviewMessageDtoToEntity(reviewDto: ReviewMessageDto): ReviewMessageEntity = {
    val returnValue = mapper.map(reviewDto, classOf[ReviewMessageEntity])
    val customerId = Optional.ofNullable(reviewDto.getCustomerId)
    if (customerId.isPresent) {
      val customerEntity = customerRepository.getById(customerId.get)
      returnValue.setCustomer(customerEntity)
    }
    returnValue
  }

  def orderAddressEntityToDto(address: OrderAddressEntity): OrderAddressDto = {
    val returnValue = mapper.map(address, classOf[OrderAddressDto])
    val orderEntity = Optional.ofNullable(address.getOrder)
    if (orderEntity.isPresent) returnValue.setOrderId(orderEntity.get.getCustomerOrderId)
    returnValue
  }

  def orderAddressDtoToEntity(address: OrderAddressDto): OrderAddressEntity = {
    val returnValue = mapper.map(address, classOf[OrderAddressEntity])
    val orderId = Optional.ofNullable(address.getOrderId)
    if (orderId.isPresent) {
      val orderEntity = orderRepository.getById(orderId.get)
      returnValue.setOrder(orderEntity)
    }
    returnValue
  }

  def shippingAddressToOrderAddress(address: ShippingAddressEntity): OrderAddressEntity = {
    val returnValue = mapper.map(address, classOf[OrderAddressEntity])
    returnValue
  }
}
