package com.tailor.shop.fabric;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FabricRepository extends JpaRepository<Fabric, Long> {

    List<Fabric> findByCategory(String category);
}
