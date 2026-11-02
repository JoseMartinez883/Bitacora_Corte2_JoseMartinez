package com.restaurante.service;

import com.restaurante.model.dto.RegisterRequestDTO;

public interface UsuarioService {
    void registrarCliente(RegisterRequestDTO request);
    void registrarEmpleado(RegisterRequestDTO request);
}

