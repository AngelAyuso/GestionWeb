package com.gestion.web.clientes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gestion.web.generic.ResultadoBean;
import com.gestion.web.generic.Usuario;
/**
 * Interfaz cliente Feign
 * @author Angel Ayuso
 *
 */
@FeignClient (name = "gestion-usuarios", url = "localhost:8090")
public interface UsuarioClienteRest {
	
	@GetMapping("/api/usuario/login/{id}/{pass}")
	public ResultadoBean login(@PathVariable String id, @PathVariable String pass);
	
	@GetMapping("/api/usuario/getUsuarioById/{id}")
	public ResultadoBean getUsuarioById(@PathVariable Integer id);
	
	@GetMapping("/api/usuario/getUsuarioByDni/{dni}")
	public ResultadoBean getUsuarioByDni(@PathVariable String dni);
	
	@GetMapping("/api/usuario/getUsuarioByEmail/{email}")
	public ResultadoBean getUsuarioByEmail(@PathVariable String email);
	
	@PostMapping("/api/usuario/crearUsuario")
	public ResultadoBean insertarUsuario(@RequestBody Usuario usuario);
	
	@PutMapping("/api/usuario/modificarUsuario/{id}")
	public ResultadoBean modificarUsuario(@RequestBody Usuario usuario, @PathVariable Integer id);
	
	@DeleteMapping("/api/usuario/eliminarUsuario")
	public void eliminarUsuario(@PathVariable Integer id);
}
