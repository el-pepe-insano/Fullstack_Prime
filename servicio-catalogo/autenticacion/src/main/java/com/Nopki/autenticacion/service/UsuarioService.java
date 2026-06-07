package com.Nopki.autenticacion.service;

import com.Nopki.autenticacion.dto.*;
import com.Nopki.autenticacion.model.Rol;
import com.Nopki.autenticacion.model.Usuario;
import java.util.List;

public interface UsuarioService {
    LoginResponse login(LoginRequest request);
    UsuarioResponse registrar(RegistroRequest request);
    UsuarioResponse obtenerPorId(Long id);
    UsuarioResponse obtenerPorEmail(String email);
    List<UsuarioResponse> listarTodos();
    List<UsuarioResponse> listarPorRol(Rol rol);
    UsuarioResponse actualizarEstado(Long id, boolean activo);
    boolean validarToken(String token);
    boolean existeUsuario(Long id);
}