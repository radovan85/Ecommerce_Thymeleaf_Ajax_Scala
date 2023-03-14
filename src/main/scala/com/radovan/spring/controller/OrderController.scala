package com.radovan.spring.controller

import com.radovan.spring.dto.{BillingAddressDto, CustomerDto, CustomerOrderDto, ShippingAddressDto}
import com.radovan.spring.service.{BillingAddressService, CartItemService, CartService, CustomerOrderService, CustomerService, ProductService, ShippingAddressService, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{ModelAttribute, RequestMapping, RequestMethod}

@Controller
@RequestMapping(value = Array("/order"))
class OrderController {

  @Autowired
  private var cartService:CartService = _

  @Autowired
  private var customerService:CustomerService = _

  @Autowired
  private var billingAddressService:BillingAddressService = _

  @Autowired
  private var shippingAddressService:ShippingAddressService = _

  @Autowired
  private var userService:UserService = _

  @Autowired
  private var cartItemService:CartItemService = _

  @Autowired
  private var productService:ProductService = _

  @Autowired
  private var orderService:CustomerOrderService = _

  @RequestMapping(value = Array("/cancel"), method = Array(RequestMethod.GET))
  def cancelOrder = "fragments/checkout/checkOutCancelled :: ajaxLoadedContent"

  @RequestMapping(value = Array("/cartError"), method = Array(RequestMethod.GET))
  def cartError = "fragments/checkout/invalidCartWarning :: ajaxLoadedContent"

  @RequestMapping(value = Array("/billingConfirmation"), method = Array(RequestMethod.GET))
  def checkout(map: ModelMap): String = {
    val authUser = userService.getCurrentUser
    val customer = customerService.getCustomerByUserId(authUser.getId)
    cartService.validateCart(customer.getCartId)
    val billingAddress = new BillingAddressDto
    val currentBillingAddress = billingAddressService.getBillingAddress(customer.getBillingAddressId)
    map.put("billingAddress", billingAddress)
    map.put("currentBillingAddress", currentBillingAddress)
    "fragments/checkout/confirm_billing_details :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/storeBillingAddress"), method = Array(RequestMethod.POST))
  def storeBilling(@ModelAttribute("billingAddress") billingAddress: BillingAddressDto): String = {
    billingAddressService.addBillingAddress(billingAddress)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/shippingConfirmation"), method = Array(RequestMethod.GET))
  def confirmShipping(map: ModelMap): String = {
    val shippingAddress = new ShippingAddressDto
    val authUser = userService.getCurrentUser
    val customer = customerService.getCustomerByUserId(authUser.getId)
    val currentShippingAddress = shippingAddressService.getShippingAddress(customer.getShippingAddressId)
    map.put("shippingAddress", shippingAddress)
    map.put("currentShippingAddress", currentShippingAddress)
    "fragments/checkout/confirm_shipping_details :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/storeShippingAddress"), method = Array(RequestMethod.POST))
  def storeShipping(@ModelAttribute("shippingAddress") shippingAddress: ShippingAddressDto): String = {
    shippingAddressService.addShippingAddress(shippingAddress)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/phoneConfirmation"), method = Array(RequestMethod.GET))
  def confirmPhone(map: ModelMap): String = {
    val authUser = userService.getCurrentUser
    val customer = new CustomerDto
    val currentCustomer = customerService.getCustomerByUserId(authUser.getId)
    map.put("customer", customer)
    map.put("currentCustomer", currentCustomer)
    "fragments/checkout/confirm_customer_phone :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/storeCustomer"), method = Array(RequestMethod.POST))
  def storeCustomer(@ModelAttribute("customer") customer: CustomerDto): String = {
    customerService.addCustomer(customer)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/orderConfirmation"), method = Array(RequestMethod.GET))
  def confirmOrder(map: ModelMap): String = {
    val authUser = userService.getCurrentUser
    val customer = customerService.getCustomerByUserId(authUser.getId)
    val cart = cartService.getCartByCartId(customer.getCartId)
    val billingAddress = billingAddressService.getBillingAddress(customer.getBillingAddressId)
    val shippingAddress = shippingAddressService.getShippingAddress(customer.getShippingAddressId)
    val order = new CustomerOrderDto
    val allCartItems = cartItemService.listAllByCartId(cart.getCartId)
    val allProducts = productService.listAll
    map.put("cart", cart)
    map.put("billingAddress", billingAddress)
    map.put("shippingAddress", shippingAddress)
    map.put("order", order)
    map.put("allCartItems", allCartItems)
    map.put("allProducts", allProducts)
    "fragments/checkout/orderConfirmation :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/createOrder"), method = Array(RequestMethod.POST))
  def createOrder: String = {
    orderService.addCustomerOrder
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/stockProblem"), method = Array(RequestMethod.GET))
  def stockProblem = "fragments/checkout/stock_exception :: ajaxLoadedContent"

  @RequestMapping(value = Array("/orderExecuted"), method = Array(RequestMethod.GET))
  def orderCompleted = "fragments/checkout/thankCustomer :: ajaxLoadedContent"
}

