package com.radovan.spring.controller

import com.radovan.spring.dto.CartItemDto
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.service.{CartItemService, CartService, CustomerService, ProductService, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod}

@Controller
@RequestMapping(value = Array("/cart"))
class CartController {

  @Autowired
  private var userService:UserService = _

  @Autowired
  private var customerService:CustomerService = _

  @Autowired
  private var cartService:CartService = _

  @Autowired
  private var cartItemService:CartItemService = _

  @Autowired
  private var productService:ProductService = _

  @RequestMapping(value = Array("/viewCart"), method = Array(RequestMethod.GET))
  def getCart(map: ModelMap): String = {
    val authUser = userService.getCurrentUser
    val customer = customerService.getCustomerByUserId(authUser.getId)
    val cart = cartService.getCartByCartId(customer.getCartId)
    val allCartItems = cartItemService.listAllByCartId(cart.getCartId)
    val allProducts = productService.listAll
    val fullPrice = cartService.calculateFullPrice(cart.getCartId)
    map.put("allCartItems", allCartItems)
    map.put("allProducts", allProducts)
    map.put("fullPrice", fullPrice)
    map.put("cart", cart)
    "fragments/cart :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/add/{productId}"), method = Array(RequestMethod.GET))
  def addCartItem(@PathVariable(value = "productId") productId: Integer): String = {
    val user = SecurityContextHolder.getContext.getAuthentication.getPrincipal.asInstanceOf[UserEntity]
    val customer = customerService.getCustomerByUserId(user.getId)
    val cart = cartService.getCartByCartId(customer.getCartId)
    val cartItemIds = cart.getCartItemsIds
    val product = productService.getProduct(productId)
    var i = 0
    while ( {
      i < cartItemIds.size
    }) {
      val itemId = cartItemIds.get(i)
      val cartItem = cartItemService.getCartItem(itemId)
      if (product.getProductId == cartItem.getProductId) {
        cartItem.setQuantity(cartItem.getQuantity + 1)
        val tempProduct = productService.getProduct(cartItem.getProductId)
        cartItem.setPrice(cartItem.getQuantity * tempProduct.getProductPrice)
        cartItemService.addCartItem(cartItem)
        cartService.refreshCartState(cart.getCartId)
        return "fragments/homePage :: ajaxContentLoaded"
      }

      {
        i += 1; i - 1
      }
    }
    val cartItem = new CartItemDto
    cartItem.setQuantity(1)
    cartItem.setProductId(productId)
    cartItem.setPrice(product.getProductPrice * 1)
    cartItem.setCartId(cart.getCartId)
    cartItemService.addCartItem(cartItem)
    cartService.refreshCartState(cart.getCartId)
    "fragments/homePage :: ajaxContentLoaded"
  }

  @RequestMapping(value = Array("/removeCartItem/{cartId}/{itemId}"), method = Array(RequestMethod.GET))
  def removeCartItem(@PathVariable(value = "cartId") cartId: Integer, @PathVariable(value = "itemId") itemId: Integer): String = {
    cartItemService.removeCartItem(cartId, itemId)
    "fragments/homePage :: ajaxContentLoaded"
  }

  @RequestMapping(value = Array("/removeAllItems/{cartId}"), method = Array(RequestMethod.GET))
  def removeAllCartItems(@PathVariable(value = "cartId") cartId: Integer): String = {
    cartItemService.eraseAllCartItems(cartId)
    "fragments/homePage :: ajaxContentLoaded"
  }
}

