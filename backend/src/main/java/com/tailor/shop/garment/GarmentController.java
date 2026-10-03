package com.tailor.shop.garment;

import com.tailor.shop.common.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/garments")
public class GarmentController {

    private final GarmentService service;

    public GarmentController(GarmentService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Garment>> list(@RequestParam(required = false) String status) {
        return ApiResponse.success(status == null ? service.list() : service.listByStatus(status));
    }

    @PostMapping
    public ApiResponse<Garment> create(@RequestBody Garment input) {
        return ApiResponse.success(service.create(input));
    }

    @PutMapping("/{id}")
    public ApiResponse<Garment> update(@PathVariable Long id, @RequestBody Garment input) {
        return ApiResponse.success(service.update(id, input));
    }

    @PostMapping("/{id}/status")
    public ApiResponse<Garment> changeStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.success(service.changeStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null);
    }
}
