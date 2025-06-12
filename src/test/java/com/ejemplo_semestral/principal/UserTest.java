package com.ejemplo_semestral.principal;

import com.ejemplo_semestral.principal.models.Usuario;
import com.ejemplo_semestral.principal.models.dto.UsuarioDto;
import com.ejemplo_semestral.principal.models.entity.UsuarioEntity;
import com.ejemplo_semestral.principal.repository.UsuarioRepository;
import com.ejemplo_semestral.principal.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserService userService;

    private Usuario usuario;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    public void setUp() {
        
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario(1, "Juan", "Pérez", "juan@mail.com");
        usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId(1);
        usuarioEntity.setNombre("Juan");
        usuarioEntity.setApellido("Pérez");
        usuarioEntity.setCorreo("juan@mail.com");
    }

    @Test
    public void testAgregarUsuario_Nuevo() {
        when(usuarioRepository.existsByCorreo(usuario.getCorreo())).thenReturn(false);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);

        String result = userService.agregarUsuario(usuario);
        assertEquals("Usuario agregado correctamente", result);
    }

    @Test
    public void testAgregarUsuario_Existente() {
        when(usuarioRepository.existsByCorreo(usuario.getCorreo())).thenReturn(true);

        String result = userService.agregarUsuario(usuario);
        assertEquals("El usuario ya existe", result);
    }

    @Test
    public void testObtenerUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuarioEntity));
        List<Usuario> usuarios = userService.obtenerUsuarios();
        assertEquals(1, usuarios.size());
        assertEquals("Juan", usuarios.get(0).getNombre());
    }

    @Test
    public void testTraerUsuario_Existe() {
        when(usuarioRepository.findByCorreo("juan@mail.com")).thenReturn(usuarioEntity);
        Usuario result = userService.traerUsuario("juan@mail.com");
        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
    }

    @Test
    public void testTraerUsuario_NoExiste() {
        when(usuarioRepository.findByCorreo("noexiste@mail.com")).thenReturn(null);
        Usuario result = userService.traerUsuario("noexiste@mail.com");
        assertNull(result);
    }

    @Test
    public void testObtenerUsuarioId_Existe() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioEntity));
        UsuarioDto dto = userService.obtenerUsuarioId(1);
        assertNotNull(dto);
        assertEquals("Juan", dto.getNombre());
    }

    @Test
    public void testObtenerUsuarioId_NoExiste() {
        when(usuarioRepository.findById(2)).thenReturn(Optional.empty());
        UsuarioDto dto = userService.obtenerUsuarioId(2);
        assertNull(dto);
    }

    @Test
    public void testActualizarUsuario_Existe() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);

        Usuario nuevo = new Usuario(1, "Pedro", "Gómez", "pedro@mail.com");
        String result = userService.actualizarUsuario(1, nuevo);
        assertEquals("Usuario actualizado correctamente", result);
    }

    @Test
    public void testActualizarUsuario_NoExiste() {
        when(usuarioRepository.findById(2)).thenReturn(Optional.empty());
        Usuario nuevo = new Usuario(2, "Pedro", "Gómez", "pedro@mail.com");
        String result = userService.actualizarUsuario(2, nuevo);
        assertEquals("Usuario no encontrado", result);
    }

    @Test
    public void testBorrarUsuario_Existe() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1);

        String result = userService.borrarUsuario(1);
        assertEquals("Usuario borrado correctamente", result);
    }

    @Test
    public void testBorrarUsuario_NoExiste() {
        when(usuarioRepository.existsById(2)).thenReturn(false);

        String result = userService.borrarUsuario(2);
        assertEquals("Usuario no encontrado", result);
    }

    @Test
    public void testBorrarUsuarioPorCorreo_Existe() {
        when(usuarioRepository.existsByCorreo("juan@mail.com")).thenReturn(true);
        doNothing().when(usuarioRepository).deleteByCorreo("juan@mail.com");

        String result = userService.borrarUsuarioPorCorreo("juan@mail.com");
        assertEquals("Usuario borrado correctamente", result);
    }

    @Test
    public void testBorrarUsuarioPorCorreo_NoExiste() {
        when(usuarioRepository.existsByCorreo("noexiste@mail.com")).thenReturn(false);

        String result = userService.borrarUsuarioPorCorreo("noexiste@mail.com");
        assertEquals("Usuario no encontrado", result);
    }

    @Test
    public void testObtenerUserDto_Existe() {
        when(usuarioRepository.findByCorreo("juan@mail.com")).thenReturn(usuarioEntity);
        ResponseEntity<UsuarioDto> response = userService.obtenerUserDto("juan@mail.com");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Juan", response.getBody().getNombre());
    }

    @Test
    public void testObtenerUserDto_NoExiste() {
        when(usuarioRepository.findByCorreo("noexiste@mail.com")).thenReturn(null);
        ResponseEntity<UsuarioDto> response = userService.obtenerUserDto("noexiste@mail.com");
        assertEquals(404, response.getStatusCodeValue());
    }
}