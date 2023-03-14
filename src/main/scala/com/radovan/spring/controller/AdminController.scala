package com.radovan.spring.controller

import java.nio.file.{Files, Paths}
import java.time.format.DateTimeFormatter

import com.radovan.spring.dto.ProductDto
import com.radovan.spring.exceptions.ImagePathException
import com.radovan.spring.service.{BillingAddressService, CartItemService, CartService, CustomerOrderService, CustomerService, OrderAddressService, OrderItemService, ProductService, ReviewMessageService, ShippingAddressService, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{ModelAttribute, PathVariable, RequestMapping, RequestMethod, RequestParam}
import org.springframework.web.multipart.MultipartFile

import scala.collection.JavaConverters._

@Controller
@RequestMapping(value = Array("/admin"))
class AdminController {

  @Autowired
  private var productService:ProductService = _

  @Autowired
  private var customerService:CustomerService = _

  @Autowired
  private var userService:UserService = _

  @Autowired
  private var billingAddressService:BillingAddressService = _

  @Autowired
  private var shippingAddressService:ShippingAddressService = _

  @Autowired
  private var reviewService:ReviewMessageService = _

  @Autowired
  private var orderService:CustomerOrderService = _

  @Autowired
  private var orderItemService:OrderItemService = _

  @Autowired
  private var orderAddressService:OrderAddressService = _

  @Autowired
  private var cartItemService:CartItemService = _

  @Autowired
  private var cartService:CartService = _

  @RequestMapping(value = Array("/"), method = Array(RequestMethod.GET))
  def adminHome = "fragments/admin :: ajaxLoadedContent"

  @RequestMapping(value = Array("/addNewProduct"), method = Array(RequestMethod.GET))
  def renderProductForm(map: ModelMap): String = {
    val product = new ProductDto
    map.put("product", product)
    "fragments/addProduct :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/createProduct"), method = Array(RequestMethod.POST))
  @throws[Throwable]
  def createProduct(@ModelAttribute("product") product: ProductDto, map: ModelMap, @RequestParam("productImage") file: MultipartFile, @RequestParam("imgName") imgName: String): String = {
    val fileLocation = "C:\\Users\\Radovan\\IdeaProjects\\Ecommerce_Thymeleaf_Ajax_Scala\\src\\main\\resources\\static\\images\\productImages\\"
    var imageUUID:String = null
    val locationPath = Paths.get(fileLocation)
    if (!Files.exists(locationPath)) {
      val error = new Error("Invalid file path!")
      throw new ImagePathException(error)
    }
    imageUUID = file.getOriginalFilename
    val fileNameAndPath = Paths.get(fileLocation, imageUUID)
    if (file != null && !file.isEmpty) {
      Files.write(fileNameAndPath, file.getBytes)
      System.out.println("IMage Save at:" + fileNameAndPath.toString)
    }
    else imageUUID = imgName
    product.setImageName(imageUUID)
    productService.addProduct(product)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/deleteProduct/{productId}"), method = Array(RequestMethod.GET))
  @throws[Throwable]
  def deleteProduct(@PathVariable("productId") productId: Integer): String = {
    val product = productService.getProduct(productId)
    val path = Paths.get("C:\\Users\\Radovan\\IdeaProjects\\Ecommerce_Thymeleaf_Ajax_Scala\\src\\main\\resources\\static\\images\\productImages\\" + product.getImageName)
    if (Files.exists(path)) Files.delete(path)
    else {
      val error = new Error("Invalid file path!")
      throw new ImagePathException(error)
    }
    cartItemService.eraseAllByProductId(productId)
    productService.deleteProduct(productId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/updateProduct/{productId}"), method = Array(RequestMethod.GET))
  def renderUpdateForm(@PathVariable("productId") productId: Integer, map: ModelMap): String = {
    val product = new ProductDto
    val currentProduct = productService.getProduct(productId)
    map.put("product", product)
    map.put("currentProduct", currentProduct)
    "fragments/updateProduct :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/allCustomers"), method = Array(RequestMethod.GET))
  def customerList(map: ModelMap): String = {
    val allCustomers = customerService.getAllCustomers
    val allUsers = userService.listAllUsers
    var recordsPerPage:Integer = 8
    map.put("allCustomers", allCustomers)
    map.put("allUsers", allUsers)
    map.put("recordsPerPage", recordsPerPage)
    "fragments/customerList :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/getCustomer/{customerId}"), method = Array(RequestMethod.GET))
  def getCustomer(@PathVariable("customerId") customerId: Integer, map: ModelMap): String = {
    val customer = customerService.getCustomer(customerId)
    val tempUser = userService.getUserById(customer.getUserId)
    val shippingAddress = shippingAddressService.getShippingAddress(customer.getShippingAddressId)
    val billingAddress = billingAddressService.getBillingAddress(customer.getBillingAddressId)
    map.put("tempCustomer", customer)
    map.put("tempUser", tempUser)
    map.put("billingAddress", billingAddress)
    map.put("shippingAddress", shippingAddress)
    "fragments/customerDetails :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/deleteCustomer/{customerId}"), method = Array(RequestMethod.GET))
  def removeCustomer(@PathVariable("customerId") customerId: Integer): String = {
    val customer = customerService.getCustomer(customerId)
    val cart = cartService.getCartByCartId(customer.getCartId)
    val billingAddress = billingAddressService.getBillingAddress(customer.getBillingAddressId)
    val shippingAddress = shippingAddressService.getShippingAddress(customer.getShippingAddressId)
    val user = userService.getUserById(customer.getUserId)
    val allOrders = orderService.listAllByCustomerId(customerId)
    for (order <- allOrders.asScala) {
      orderItemService.eraseAllByOrderId(order.getCustomerOrderId)
      orderService.deleteOrder(order.getCustomerOrderId)
    }
    cartItemService.eraseAllCartItems(cart.getCartId)
    reviewService.deleteAllByCustomerId(customerId)
    customerService.resetCustomer(customerId)
    billingAddressService.deleteBillingAddress(billingAddress.getBillingAddressId)
    shippingAddressService.deleteShippingAddress(shippingAddress.getShippingAddressId)
    cartService.deleteCart(cart.getCartId)
    customerService.deleteCustomer(customerId)
    userService.deleteUser(user.getId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/allReviews"), method = Array(RequestMethod.GET))
  def listAllReviews(map: ModelMap): String = {
    val allReviews = reviewService.allReviews
    val allCustomers = customerService.getAllCustomers
    val allUsers = userService.listAllUsers
    map.put("allCustomers", allCustomers)
    map.put("allUsers", allUsers)
    map.put("allReviews", allReviews)
    map.put("recordsPerPage", 5.asInstanceOf[Integer])
    "fragments/reviewList :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/reviewDetails/{reviewId}"), method = Array(RequestMethod.GET))
  def getReview(@PathVariable("reviewId") reviewId: Integer, map: ModelMap): String = {
    val review = reviewService.getReview(reviewId)
    val customer = customerService.getCustomer(review.getCustomerId)
    val user = userService.getUserById(customer.getUserId)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val dateTime = review.getDate.toLocalDateTime
    val createdAtStr = dateTime.format(formatter)
    map.put("review", review)
    map.put("user", user)
    map.put("createdAtStr", createdAtStr)
    "fragments/reviewDetails :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/deleteReview/{reviewId}"), method = Array(RequestMethod.GET))
  def deleteReview(@PathVariable("reviewId") reviewId: Integer): String = {
    reviewService.deleteReview(reviewId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/suspendUser/{userId}"), method = Array(RequestMethod.POST))
  def suspendUser(@PathVariable("userId") userId: Integer): String = {
    userService.suspendUser(userId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/allOrders"), method = Array(RequestMethod.GET))
  def listAllOrders(map: ModelMap): String = {
    val allOrders = orderService.listAll
    val allCustomers = customerService.getAllCustomers
    val allUsers = userService.listAllUsers
    map.put("allOrders", allOrders)
    map.put("allCustomers", allCustomers)
    map.put("allUsers", allUsers)
    map.put("recordsPerPage", 10.asInstanceOf[Integer])
    "fragments/orderList :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/deleteOrder/{orderId}"), method = Array(RequestMethod.GET))
  def deleteOrder(@PathVariable("orderId") orderId: Integer): String = {
    orderItemService.eraseAllByOrderId(orderId)
    orderService.deleteOrder(orderId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/getOrder/{orderId}"), method = Array(RequestMethod.GET))
  def orderDetails(@PathVariable("orderId") orderId: Integer, map: ModelMap): String = {
    val order = orderService.getOrder(orderId)
    val address = orderAddressService.getAddressById(order.getAddressId)
    val allProducts = productService.listAll
    val orderPrice = orderService.calculateOrderTotal(orderId)
    val orderedItems = orderItemService.listAllByOrderId(orderId)
    map.put("order", order)
    map.put("address", address)
    map.put("allProducts", allProducts)
    map.put("orderPrice", orderPrice)
    map.put("orderedItems", orderedItems)
    "fragments/orderDetails :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/invalidPath"), method = Array(RequestMethod.GET))
  def invalidImagePath = "fragments/invalidImagePath :: ajaxLoadedContent"
}

