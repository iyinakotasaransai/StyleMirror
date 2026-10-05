package com.stylemirror.service;

import com.stylemirror.dto.AdminDashboardDto;
import com.stylemirror.dto.ReportDto;
import com.stylemirror.dto.UserDto;

import java.util.List;

public interface AdminService {
    AdminDashboardDto getDashboard();
    List<UserDto> getAllUsers();
    UserDto toggleUserStatus(Long userId, boolean active);
    void approveSeller(Long sellerId);
    void blockSeller(Long sellerId);
    ReportDto getSystemReport();
}
