package com.monthly.ecommercemonolith.controllers;

import com.monthly.ecommercemonolith.entities.User;
import com.monthly.ecommercemonolith.payload.AddressDTO;
import com.monthly.ecommercemonolith.services.AddressService;
import com.monthly.ecommercemonolith.utils.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private AuthUtils authUtil;
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO>createAddress(@Valid @RequestBody AddressDTO addressDTO){
        User user=authUtil.loggedInUser();
        AddressDTO savedAddress=addressService.createAddress(addressDTO,user);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>>getAddresses(){
        List<AddressDTO>addressDTOs=addressService.getAddresses();
        return new ResponseEntity<>(addressDTOs,HttpStatus.OK);
    }

   @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO>getAddressById(@PathVariable Long addressId){
        AddressDTO addressDTO=addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }
   @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>>getAddressByUser(){
        User user=authUtil.loggedInUser();
        List<AddressDTO> addressDTOs=addressService.getAddressByUser(user);
        return new ResponseEntity<>(addressDTOs,HttpStatus.OK);
    }
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO>updateAddress(@PathVariable Long addressId,
                                                   @RequestBody AddressDTO addressDto){
        AddressDTO updatedAddress=addressService.updateAddress(addressId,addressDto);
        return new ResponseEntity<>(updatedAddress,HttpStatus.OK);
    }
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status=addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status,HttpStatus.NO_CONTENT);
    }
}
