package com.radovan.spring.service.impl

import java.io.IOException
import java.util
import java.util.Optional

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ProductDto
import com.radovan.spring.repository.{CartItemRepository, CartRepository, ProductRepository}
import com.radovan.spring.service.{CartService, ProductService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Service
@Transactional
class ProductServiceImpl extends ProductService {

  @Autowired
  private var productRepository:ProductRepository = _

  @Autowired
  private var tempConverter:TempConverter = _

  @Autowired
  private var cartItemRepository:CartItemRepository = _

  @Autowired
  private var cartRepository:CartRepository = _

  @Autowired
  private var cartService:CartService = _

  override def listAll: util.List[ProductDto] = {
    val allProducts = productRepository.findAll
    val returnValue = new util.ArrayList[ProductDto]
    for (productEntity <- allProducts.asScala) {
      val productDto = tempConverter.productEntityToDto(productEntity)
      returnValue.add(productDto)
    }
    returnValue
  }

  override def getProduct(id: Integer): ProductDto = {
    val productEntity = productRepository.getById(id)
    val returnValue = tempConverter.productEntityToDto(productEntity)
    returnValue
  }

  override def deleteProduct(id: Integer): Unit = {
    productRepository.deleteById(id)
    productRepository.flush()
  }

  override def addProduct(product: ProductDto): ProductDto = {
    val productId = Optional.ofNullable(product.getProductId)
    val productEntity = tempConverter.productDtoToEntity(product)
    val storedProduct = productRepository.save(productEntity)
    val returnValue = tempConverter.productEntityToDto(storedProduct)
    if (productId.isPresent) {
      val allCartItems = Optional.ofNullable(cartItemRepository.findAllByProductId(productId.get))
      if (!allCartItems.isEmpty) {
        for (itemEntity <- allCartItems.get.asScala) {
          var price = returnValue.getProductPrice
          price = (price - ((price / 100) * returnValue.getDiscount)) * itemEntity.getQuantity
          itemEntity.setPrice(price)
          cartItemRepository.saveAndFlush(itemEntity)
        }
        val allCarts = Optional.ofNullable(cartRepository.findAll)
        if (!allCarts.isEmpty) {
          for (cartEntity <- allCarts.get.asScala) {
            cartService.refreshCartState(cartEntity.getCartId)
          }
        }
      }
    }
    returnValue
  }

  @throws[IOException]
  override def updateProduct(id: Integer, product: ProductDto): ProductDto = {
    val tempProduct = productRepository.getById(id)
    val productEntity = tempConverter.productDtoToEntity(product)
    productEntity.setProductId(tempProduct.getProductId)
    val updatedProduct = productRepository.saveAndFlush(productEntity)
    val returnValue = tempConverter.productEntityToDto(updatedProduct)
    returnValue
  }

  override def listAllByKeyword(keyword: String): util.List[ProductDto] = {
    val listResult = productRepository.findAllByKeyword(keyword)
    val returnValue = new util.ArrayList[ProductDto]
    for (productEnt <- listResult.asScala) {
      val productDto = tempConverter.productEntityToDto(productEnt)
      returnValue.add(productDto)
    }
    returnValue
  }
}

