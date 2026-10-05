package com.stylemirror.controller;

import com.stylemirror.dto.ApiResponse;
import com.stylemirror.dto.ReportDto;
import com.stylemirror.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
@RequiredArgsConstructor
public class ReportController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<ApiResponse<ReportDto>> getReports() {
        ReportDto report = adminService.getSystemReport();
        return ResponseEntity.ok(ApiResponse.ok(report));
    }
}
