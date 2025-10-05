package com.monthly.ecommercemonolith.serviceImplementations;

import com.monthly.ecommercemonolith.entities.Address;
import com.monthly.ecommercemonolith.entities.User;
import com.monthly.ecommercemonolith.exceptions.ResourceNotFoundException;
import com.monthly.ecommercemonolith.payload.AddressDTO;
import com.monthly.ecommercemonolith.repositories.AddressRepository;
import com.monthly.ecommercemonolith.repositories.UserRepository;
import com.monthly.ecommercemonolith.services.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(address -> modelMapper.map(address,
                AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address =
                addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("AddressId", addressId, "Address"));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressByUser(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDto) {
        Address address =
                addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("AddressId", addressId, "Address"));
        address.setCity(addressDto.getCity());
        address.setZipCode(addressDto.getZipCode());
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        address.setStreet(addressDto.getStreet());
        address.setBuildingName(address.getBuildingName());
        Address updatedAddress = addressRepository.save(address);
        User user = address.getUser();
        user.getAddresses().removeIf(a -> a.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("AddressId", addressId, "Address"));
        User user = address.getUser();
        user.getAddresses().removeIf(a -> a.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(address);
        return "Address deleted successfully with addressId:" + addressId;
    }
}
