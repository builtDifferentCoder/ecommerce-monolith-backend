package com.monthly.ecommercemonolith.repositories;

import com.monthly.ecommercemonolith.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {
}
