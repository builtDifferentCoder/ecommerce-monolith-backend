package com.monthly.ecommercemonolith.services;

import com.monthly.ecommercemonolith.entities.User;
import com.monthly.ecommercemonolith.payload.AddressDTO;


import java.util.List;


public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getAddressByUser(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDto);

    String deleteAddress(Long addressId);
}
