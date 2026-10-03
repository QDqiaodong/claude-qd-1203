package com.tailor.shop.garment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarmentRepository extends JpaRepository<Garment, Long> {

    List<Garment> findByStatus(String status);

    Optional<Garment> findByCode(String code);
}
