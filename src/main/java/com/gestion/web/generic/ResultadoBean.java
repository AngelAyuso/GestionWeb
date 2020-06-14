package com.gestion.web.generic;

import com.gestion.web.generic.Usuario;

/**
 * Clase Generica con el resultado de las operaciones de la capa de Servicios
 *  - resultado: Resultado de la operacion
 *  - codError: Codigo de error, en caso de haberlo
 *  - Usuario: Usuario recuperado, en caso de que sea necesario devolverlo
 * @author User
 *
 */
public class ResultadoBean {
	
	private String resultado;
	
	private String codError;
	
	private Usuario usuario;

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public String getCodError() {
		return codError;
	}

	public void setCodError(String codError) {
		this.codError = codError;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}