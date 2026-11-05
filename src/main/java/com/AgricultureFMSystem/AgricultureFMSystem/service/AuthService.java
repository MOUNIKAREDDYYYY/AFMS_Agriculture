package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.LoginRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.RegisterRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO requestDTO);

    AuthResponseDTO register(RegisterRequestDTO requestDTO);

    AuthResponseDTO getCurrentUser(String username);
}
