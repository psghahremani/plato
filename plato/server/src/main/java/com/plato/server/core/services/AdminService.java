package com.plato.server.core.services;

import com.plato.server.core.models.Admin;
import com.plato.server.repository.AdminRepository;

import java.util.UUID;

public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public boolean insertAdmin(Admin admin) {
        admin.setId(UUID.randomUUID().toString());
        return this.adminRepository.insertAdmin(admin);
    }

    public void editAdmin(Admin admin) {
        this.adminRepository.editAdmin(admin);
    }

    public Admin getAdminByUsername(String username) {
        return this.adminRepository.getAdminByUsername(username);
    }

    public Admin getAdminById(String id) {
        return this.adminRepository.getAdminByID(id);
    }
}
