package com.radovan.spring.controller

import com.radovan.spring.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RequestParam}

@Controller
@RequestMapping (value = Array("/products"))
class ProductController {

  @Autowired
  private var productService:ProductService = _

  @RequestMapping(value = Array("/allProducts"), method = Array(RequestMethod.GET))
  def getAllProducts(map: ModelMap): String = {
    val allProducts = productService.listAll
    map.put("allProducts", allProducts)
    map.put("recordsPerPage", 5.asInstanceOf[Integer])
    "fragments/productList :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/getProduct/{productId}"), method = Array(RequestMethod.GET))
  def getProductDetails(@PathVariable("productId") productId: Integer, map: ModelMap): String = {
    val currentProduct = productService.getProduct(productId)
    map.put("currentProduct", currentProduct)
    "fragments/productDetails :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/searchProducts"), method = Array(RequestMethod.GET))
  def searchProducts(@RequestParam("keyword") keyword: String, map: ModelMap): String = {
    val searchResult = productService.listAllByKeyword(keyword)
    map.put("searchResult", searchResult)
    map.put("recordsPerPage", 5.asInstanceOf[Integer])
    "fragments/searchList :: ajaxLoadedContent"
  }
}

