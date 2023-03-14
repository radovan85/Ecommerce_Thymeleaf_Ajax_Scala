package com.radovan.spring.service

import java.util
import java.io.IOException

import com.radovan.spring.dto.ProductDto

trait ProductService {

  def listAll: util.List[ProductDto]

  def getProduct(id: Integer): ProductDto

  def deleteProduct(id: Integer): Unit

  @throws[IOException]
  def addProduct(product: ProductDto): ProductDto

  @throws[IOException]
  def updateProduct(id: Integer, product: ProductDto): ProductDto

  def listAllByKeyword(keyword: String): util.List[ProductDto]
}
