package com.matias.api_rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matias.api_rest.dao.UsuarioDao;
import com.matias.api_rest.models.Usuario;
import com.matias.api_rest.models.Rol;
import com.matias.api_rest.utils.JWTUtil;
import com.matias.api_rest.dto.request.LoginRequest;
import jakarta.validation.Valid;


@RestController // Controlador para manejar la autenticación
@RequestMapping("/login") // Ruta base: /login
public class AuthController {

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private JWTUtil jwtUtil;
    
    @PostMapping // Endpoint para iniciar sesión (POST /login)
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());

        Usuario usuarioLogeado = usuarioDao.verificar(usuario);


        
        if (usuarioLogeado != null){
            
            // Si el usuario no tiene rol asignado, asignarle USER por defecto
            String rolUsuario = usuarioLogeado.getRol() != null ? usuarioLogeado.getRol().toString() : Rol.USER.toString();

            // Si las credenciales son correctas, crea y devuelve un token JWT
            String token = jwtUtil.create(String.valueOf(usuarioLogeado.getId()), usuarioLogeado.getEmail(), rolUsuario);

            return ResponseEntity.ok(token);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FAIL");
        }
    }

}
