package com.gestion.web.generic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Clase Usuario para validar los datos de Entrada de los Formularios de Modificar y Alta Usuario
 * @author User
 *
 */
@Entity
public class Usuario {
	@Id
	private int idUsuario;
	
	@NotEmpty(message="Campo obligatorio")
	@Size(max = 45)
	private String nombre;
	
	@NotEmpty(message="Campo obligatorio")
	@Size(max = 45)
	private String primerApellido;
	
	@NotEmpty(message="Campo obligatorio")
	@Size(max = 45)
	private String segundoApellido;
	
	@NotEmpty(message="Campo obligatorio")
	@Email(message="Formato email incorrecto")
	private String email;
	
	@NotNull(message="Campo obligatorio")
	@Digits(fraction = 9, integer = 10, message="Formato de Telefono incorrecto")
	private Number telefono;
	
	@NotNull(message="Campo obligatorio")
	@Pattern(regexp = "[0-9]{7,8}[A-Za-z]", message="Formato de DNI incorrecto")
	private String dni;
	
	@Size(max = 100)
	@Pattern(regexp = "[a-zA-Z0-9]+", message="La contraseña debe tener letras mayúsculas, minúsculas y números")
	private String password;
	
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPrimerApellido() {
		return primerApellido;
	}
	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = primerApellido;
	}
	public String getSegundoApellido() {
		return segundoApellido;
	}
	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Number getTelefono() {
		return telefono;
	}
	public void setTelefono(Number telefono) {
		this.telefono = telefono;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	
	public String toString() {
        return String.format("idUsuario:"+idUsuario+";nombre:"+nombre+";primerApellido:"+primerApellido+";segundoApellido:"+segundoApellido+
        		";email:"+email+";telefono:"+telefono+";dni:"+dni);
    }
}
