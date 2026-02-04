package com.matias.api_rest.dao;

import java.util.List;

import com.matias.api_rest.models.Usuario;

// Interfaz que define el contrato (métodos obligatorios) para el acceso a datos
public interface UsuarioDao {

    List<Usuario> getUsuarios();

    void eliminar(long id);

    Usuario agregar(Usuario usuario);

    Usuario verificar(Usuario usuario);

    Usuario getUsuario(long id);

    boolean existeEmail(String email);
}
