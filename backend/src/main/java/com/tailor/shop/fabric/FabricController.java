package com.tailor.shop.fabric;

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
@RequestMapping("/api/fabrics")
public class FabricController {

    private final FabricService service;

    public FabricController(FabricService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Fabric>> list(@RequestParam(required = false) String category) {
        return ApiResponse.success(category == null ? service.list() : service.listByCategory(category));
    }

    @PostMapping
    public ApiResponse<Fabric> create(@RequestBody Fabric input) {
        return ApiResponse.success(service.create(input));
    }

    @PutMapping("/{id}")
    public ApiResponse<Fabric> update(@PathVariable Long id, @RequestBody Fabric input) {
        return ApiResponse.success(service.update(id, input));
    }

    @PostMapping("/{id}/consume")
    public ApiResponse<Fabric> consume(@PathVariable Long id, @RequestParam int qty) {
        return ApiResponse.success(service.consume(id, qty));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null);
    }
}
