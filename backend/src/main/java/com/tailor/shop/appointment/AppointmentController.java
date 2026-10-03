package com.tailor.shop.appointment;

import com.tailor.shop.common.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Appointment>> list() {
        return ApiResponse.success(service.list());
    }

    @PostMapping
    public ApiResponse<Appointment> create(@RequestBody Appointment input) {
        return ApiResponse.success(service.create(input));
    }

    @PutMapping("/{id}")
    public ApiResponse<Appointment> update(@PathVariable Long id, @RequestBody Appointment input) {
        return ApiResponse.success(service.update(id, input));
    }

    @PostMapping("/{id}/pickup")
    public ApiResponse<Appointment> pickup(@PathVariable Long id) {
        return ApiResponse.success(service.pickup(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null);
    }
}
