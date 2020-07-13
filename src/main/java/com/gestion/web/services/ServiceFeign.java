package com.gestion.web.services;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.web.clientes.UsuarioClienteRest;
import com.gestion.web.generic.ResultadoBean;
import com.gestion.web.generic.Usuario;

/**
 * Implementacion de la Interfaz IService
 * Se comunicara con otro microservicio mediante Feign
 * @author Angel Ayuso
 *
 */
@Service("serviceFeign")
public class ServiceFeign implements IService {
	
	@Autowired
	private UsuarioClienteRest uClienteRest;

	@Override
	public ResultadoBean getUsuarioById(String id) {
		return uClienteRest.getUsuarioById(Integer.parseInt(id));
	}
	
	@Override
	public ResultadoBean getUsuarioByDni(String dni) {
		return uClienteRest.getUsuarioByDni(dni);
	}

	@Override
	public ResultadoBean getUsuarioByEmail(String email) {
		return uClienteRest.getUsuarioByEmail(email);
	}

	@Override
	public ResultadoBean insertarUsuario(Usuario usuario) {
		return uClienteRest.insertarUsuario(usuario);
	}

	@Override
	public ResultadoBean modificarUsuario(HttpSession session, Usuario usuario, Integer id) {
		return uClienteRest.modificarUsuario(usuario, id);
	}

	@Override
	public ResultadoBean eliminarUsuario(HttpSession session, Integer id) {
		return uClienteRest.eliminarUsuario(id);
	}

	@Override
	public ResultadoBean getToken(HttpSession session, String id, String pass) {
		// TODO Auto-generated method stub
		return null;
	}
}
