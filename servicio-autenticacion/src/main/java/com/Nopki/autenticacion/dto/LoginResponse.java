package com.Nopki.autenticacion.dto;

import com.Nopki.autenticacion.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo;
    private Long usuarioId;
    private String nombre;
    private String email;
    private Rol rol;
}