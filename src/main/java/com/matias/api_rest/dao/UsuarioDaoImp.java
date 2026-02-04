package com.matias.api_rest.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.matias.api_rest.models.Usuario;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository // Indica a Spring que esta clase se encarga del acceso a datos (Base de Datos)
@Transactional // Maneja las transacciones SQL automáticamente (commit/rollback)
public class UsuarioDaoImp implements UsuarioDao {

    
    @PersistenceContext // Inyecta el EntityManager, que es la herramienta principal de JPA para la BD
    private EntityManager entityManager;

    @Override
    @Transactional // Asegura que este método específico se ejecute dentro de una transacción
    public List<Usuario> getUsuarios() {
        String query = "SELECT u FROM Usuario u"; // Consulta en JPQL (Java Persistence Query Language)
        // Ejecuta la consulta y retorna la lista de resultados
        List<Usuario> resultado = entityManager.createQuery(query, Usuario.class).getResultList();
        return resultado;
    }

    @Override
    public void eliminar(long id){
        // Busca el usuario por su ID y luego lo elimina de la base de datos
        Usuario usuario = entityManager.find(Usuario.class, id);
        entityManager.remove(usuario);
    };

    @Override
    public Usuario agregar(Usuario usuario){
        // 'merge' guarda un nuevo usuario o actualiza uno existente si ya tiene ID
        return entityManager.merge(usuario);
    }

    @Override
    public Usuario verificar(Usuario usuario){
        // Consulta para buscar un usuario por su email
        String query = "SELECT u FROM Usuario u WHERE email = :email";
        List<Usuario> lista = entityManager.createQuery(query, Usuario.class)
        .setParameter("email", usuario.getEmail()) // Usa parámetros para evitar Inyección SQL
        .getResultList();

        // Si la lista está vacía, significa que el email no existe en la BD
        if(lista.isEmpty()){
            return null;
        }
        
        String passwordHashed = lista.get(0).getPassword();
        
        // Verifica si la contraseña ingresada coincide con el hash guardado usando Argon2
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        if (argon2.verify(passwordHashed, usuario.getPassword())){
            return lista.get(0);
        }
        return null;
    }
    
    @Override
    public Usuario getUsuario(long id) {
        // Busca y retorna un usuario específico por su ID (Primary Key)
        return entityManager.find(Usuario.class, id);
    }

    @Override
    public boolean existeEmail(String email) {
        String query = "SELECT COUNT(u) FROM Usuario u WHERE u.email = :email";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
