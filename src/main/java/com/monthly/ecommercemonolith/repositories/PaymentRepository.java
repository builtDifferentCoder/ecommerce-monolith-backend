package com.monthly.ecommercemonolith.repositories;

import com.monthly.ecommercemonolith.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
