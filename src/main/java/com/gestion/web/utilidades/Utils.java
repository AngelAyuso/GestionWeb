package com.gestion.web.utilidades;

import java.util.HashMap;

import com.gestion.web.generic.Usuario;

public class Utils {
	
	public static String getCodErrorLiteral(String codError) {
		return "cod.error." + codError;
	}
	
	public static HashMap convertUsuarioToMap(Usuario usuario){
		HashMap mapUsuario = new HashMap<>();
		mapUsuario.put("idUsuario", usuario.getIdUsuario());
		mapUsuario.put("nombre", usuario.getNombre());
		mapUsuario.put("primerApellido", usuario.getPrimerApellido());
		mapUsuario.put("segundoApellido", usuario.getSegundoApellido());
		mapUsuario.put("dni", usuario.getDni());
		mapUsuario.put("email", usuario.getEmail());
		mapUsuario.put("telefono", usuario.getTelefono());
		return mapUsuario;
	}
	
}
