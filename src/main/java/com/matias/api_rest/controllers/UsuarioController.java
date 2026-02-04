package com.matias.api_rest.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matias.api_rest.dao.UsuarioDao;
import com.matias.api_rest.models.Usuario;
import com.matias.api_rest.models.Rol;
import com.matias.api_rest.utils.JWTUtil;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.matias.api_rest.dto.request.CrearUsuarioRequest;
import com.matias.api_rest.dto.response.UsuarioResponse;
import com.matias.api_rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;


@RestController // Indica que esta clase es un controlador REST que devuelve JSON
@RequestMapping("/usuario") // Define la ruta base para todos los endpoints de esta clase
public class UsuarioController {

    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired // Inyección de dependencias: Spring instancia el DAO automáticamente
    private UsuarioDao usuarioDao;
    @Autowired // Inyección de dependencias: Spring instancia la utilidad de JWT
    private JWTUtil jwtUtil;

    // Método auxiliar para verificar si el token recibido es válido
    private boolean validarToken(String token){
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        // Extrae el ID del usuario desde el token; si falla devuelve null
        String usuarioId = jwtUtil.getKey(token.substring(7));
        return usuarioId != null;
    }

    @GetMapping("/{id}") // Endpoint para obtener un usuario específico (GET /usuario/1)
    public ResponseEntity<UsuarioResponse> getUsuario(@RequestHeader(value = "Authorization") String token,@PathVariable Long id){
        if(!validarToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String jwt = token.substring(7);
        String rol = jwtUtil.getRol(jwt);
        String usuarioId = jwtUtil.getKey(jwt);

        if (rol == null || (!rol.equals(Rol.ADMIN.toString()) && !usuarioId.equals(String.valueOf(id)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Usuario usuario = usuarioDao.getUsuario(id);
        if (usuario != null){
            return ResponseEntity.ok(usuarioMapper.toResponse(usuario));
        }else{
            return ResponseEntity.notFound().build();
        }
        
    }

    @GetMapping // Endpoint para listar todos los usuarios (GET /usuario)
    public ResponseEntity<List<UsuarioResponse>> getUsuarios(@RequestHeader(value = "Authorization") String token){
        if(!validarToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String jwt = token.substring(7);
        String rol = jwtUtil.getRol(jwt);

        if (rol == null || !rol.equals(Rol.ADMIN.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UsuarioResponse> usuarios = usuarioDao.getUsuarios()
            .stream()
            .map(usuarioMapper::toResponse)
            .toList();
        
        return ResponseEntity.ok(usuarios);
        }
    
    @DeleteMapping("/{id}") // Endpoint para eliminar un usuario (DELETE /usuario/1)
    public ResponseEntity<Void> eliminar(@RequestHeader(value = "Authorization") String token,@PathVariable Long id){
        if(!validarToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();      
        }
        String jwt = token.substring(7);
        String rol = jwtUtil.getRol(jwt);
        String usuarioId = jwtUtil.getKey(jwt);

        if (rol == null || (!rol.equals(Rol.ADMIN.toString()) && !usuarioId.equals(String.valueOf(id)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (usuarioDao.getUsuario(id) == null){
            return ResponseEntity.notFound().build();
        }

        usuarioDao.eliminar(id);
        return ResponseEntity.noContent().build();
        
    }

    @PostMapping // Endpoint para registrar un nuevo usuario (POST /usuario)
    public ResponseEntity<?> agregar(@Valid @RequestBody CrearUsuarioRequest request) {

        if (usuarioDao.existeEmail(request.getEmail())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("message", "El email ya está registrado"));
        }
        
        Usuario usuario = usuarioMapper.toEntity(request);

        // Configuración de Argon2 para el hashing seguro de contraseñas
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        // Genera el hash de la contraseña recibida
        String hash = argon2.hash(4, 65536, 1, usuario.getPassword());
        usuario.setPassword(hash);

        usuario.setRol(Rol.USER);

        // Guarda el usuario en la base de datos a través del DAO
        Usuario nuevoUsuario = usuarioDao.agregar(usuario); 
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toResponse(nuevoUsuario));                        

    }
    
}
