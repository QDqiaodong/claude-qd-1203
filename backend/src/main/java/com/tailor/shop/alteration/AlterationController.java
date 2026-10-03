package com.tailor.shop.alteration;

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
@RequestMapping("/api/alterations")
public class AlterationController {

    private final AlterationService service;

    public AlterationController(AlterationService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Alteration>> list(@RequestParam(required = false) Long garmentId) {
        return ApiResponse.success(garmentId == null ? service.list() : service.listByGarment(garmentId));
    }

    @PostMapping
    public ApiResponse<Alteration> create(@RequestBody Alteration input) {
        return ApiResponse.success(service.create(input));
    }

    @PutMapping("/{id}")
    public ApiResponse<Alteration> update(@PathVariable Long id, @RequestBody Alteration input) {
        return ApiResponse.success(service.update(id, input));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<Alteration> complete(@PathVariable Long id) {
        return ApiResponse.success(service.complete(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null);
    }
}
