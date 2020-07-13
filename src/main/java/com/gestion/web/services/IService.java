package com.gestion.web.services;

import javax.servlet.http.HttpSession;

import com.gestion.web.generic.ResultadoBean;
import com.gestion.web.generic.Usuario;

public interface IService {
	
	public ResultadoBean getToken(HttpSession session, String id, String pass);
	public ResultadoBean getUsuarioById(String id);
	public ResultadoBean getUsuarioByDni(String dni);
	public ResultadoBean getUsuarioByEmail(String email);
	
	public ResultadoBean insertarUsuario(Usuario usuario);
	public ResultadoBean modificarUsuario(HttpSession session, Usuario usuario, Integer id);
	public ResultadoBean eliminarUsuario(HttpSession session, Integer id);
}
