package com.radovan.spring.service.impl

import java.util.Optional

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.BillingAddressDto
import com.radovan.spring.repository.BillingAddressRepository
import com.radovan.spring.service.BillingAddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BillingAddressServiceImpl extends BillingAddressService {

  @Autowired
  private var billingAddressRepository:BillingAddressRepository = _

  @Autowired
  private var tempConverter:TempConverter = _

  override def addBillingAddress(billingAddress: BillingAddressDto): BillingAddressDto = {
    val billingAddressEntity = tempConverter.billingAddressDtoToEntity(billingAddress)
    val storedBillingAddress = billingAddressRepository.save(billingAddressEntity)
    val returnValue = tempConverter.billingAddressEntityToDto(storedBillingAddress)
    returnValue
  }

  override def updateBillingAddress(id: Integer, billingAddress: BillingAddressDto): BillingAddressDto = {
    val tempBillingAddress = billingAddressRepository.getById(id)
    val billingAddressEntity = tempConverter.billingAddressDtoToEntity(billingAddress)
    billingAddressEntity.setCustomer(tempBillingAddress.getCustomer)
    billingAddressEntity.setBillingAddressId(id)
    val updatedBillingAddress = billingAddressRepository.saveAndFlush(billingAddressEntity)
    val returnValue = tempConverter.billingAddressEntityToDto(updatedBillingAddress)
    returnValue
  }

  override def getBillingAddress(addressId: Integer): BillingAddressDto = {
    val addressEntity = Optional.ofNullable(billingAddressRepository.getById(addressId))
    var returnValue:BillingAddressDto = null
    if (addressEntity.isPresent) returnValue = tempConverter.billingAddressEntityToDto(addressEntity.get)
    returnValue
  }

  override def deleteBillingAddress(billingAddressId: Integer): Unit = {
    billingAddressRepository.deleteById(billingAddressId)
    billingAddressRepository.flush()
  }
}

