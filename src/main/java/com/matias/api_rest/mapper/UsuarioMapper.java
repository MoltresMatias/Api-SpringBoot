package com.matias.api_rest.mapper;

import org.springframework.stereotype.Component;

import com.matias.api_rest.dto.request.CrearUsuarioRequest;
import com.matias.api_rest.dto.response.UsuarioResponse;
import com.matias.api_rest.models.Usuario;

@Component
public class UsuarioMapper {

    public Usuario toEntity(CrearUsuarioRequest request){
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setPassword(request.getPassword());
        return usuario;
    }

    public UsuarioResponse toResponse(Usuario usuario){
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getEmail(),
            usuario.getTelefono(),
            usuario.getRol()
        );
    }
}
