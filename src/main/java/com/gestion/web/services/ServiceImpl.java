package com.gestion.web.services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.commons.utils.ConstantesErrores;
import com.gestion.web.generic.ResultadoBean;
import com.gestion.web.generic.TokenBean;
import com.gestion.web.generic.Usuario;
import com.gestion.web.utilidades.Constantes;
import com.gestion.web.utilidades.Utils;

/**
 * Implementacion de la Interfaz IService
 * Se comunicara con otro microservicio mediante RestTemplate
 * @author Angel Ayuso
 *
 */
@RefreshScope
@Service("serviceImpl")
public class ServiceImpl implements IService {
	
	@Autowired
	Environment env;
	
	@Autowired
	RestTemplate clienteRestUsuario;
	
	private static final Logger logger = Logger.getLogger(ServiceImpl.class);
	private final static String CONS_PROPERTY_TOKEN = "CONS_PROPERTY_TOKEN";
	
	/**
	 * Metodo que realiza el login y crea el Token para el Usuario
	 * 1. Se comprueba que el usuario existe
	 * 2. Se comprueban sus credenciales con Spring Security y crea el Token
	 */
	@Override
	public ResultadoBean getToken(HttpSession session, String id, String pass) {
		
		logger.info("/getToken ");
		
		ResultadoBean resultado = new ResultadoBean();
		
		//Comprobamos si existe por DNI
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put("dni",id);
		resultado = clienteRestUsuario.getForObject("http://localhost:8090/api/usuario/getUsuarioByDni/{dni}", ResultadoBean.class, mapParams);
		if(resultado.getUsuario() == null) {
			mapParams = new HashMap<String, String>();
			mapParams.put("email",id);
			resultado = clienteRestUsuario.getForObject("http://localhost:8090/api/usuario/getUsuarioByEmail/{id}", ResultadoBean.class, mapParams);
			
			if(resultado.getUsuario() == null) {
				resultado.setResultado(Constantes.CONS_RESULTADO_KO);
				resultado.setCodError(ConstantesErrores.COD_ERROR_105);
				logger.error("  Error - " + ConstantesErrores.COD_ERROR_105);
			}
		}
				
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(getBodyToken(id, pass), getHttpHeadersToken());
		logger.info("  Invocacion oauthToken");
		try {
			ResponseEntity<Object> responseEntity = clienteRestUsuario.exchange("http://localhost:8090/api/security/oauth/token", HttpMethod.POST, entity, Object.class);
			logger.info("    StatusCode - " + responseEntity.getStatusCode().toString());
			if(responseEntity.getStatusCode().toString().startsWith(Constantes.HTTP_200)) {
				LinkedHashMap<String,String> map = (LinkedHashMap<String, String>) responseEntity.getBody();
				//Guardamos la informacion del Token en Memoria
				TokenBean token = new TokenBean();
				token.setToken(map.get("access_token"));
				token.setTokenType(map.get("token_type"));
				token.setRefreshToken(map.get("refresh_token"));
				session.setAttribute(CONS_PROPERTY_TOKEN, token);
				resultado.setResultado(Constantes.CONS_RESULTADO_OK);
				logger.info("    Token - " + token.getToken());
				logger.info("    TokenType - " + token.getTokenType());
				logger.info("    RefreshToken - " + token.getRefreshToken());
			} else {
				resultado.setResultado(Constantes.CONS_RESULTADO_KO);
				resultado.setCodError(Constantes.COD_ERROR_200);
				logger.error("    Error - " + ConstantesErrores.COD_ERROR_200);
			}
		} catch(HttpStatusCodeException ex) {
			resultado.setResultado(Constantes.CONS_RESULTADO_KO);
			resultado.setCodError(Constantes.COD_ERROR_200);
			logger.error("    HttpStatusCodeException - " + ex.getMessage());
		} catch(RestClientException  ex) {
			resultado.setResultado(Constantes.CONS_RESULTADO_KO);
			resultado.setCodError(Constantes.COD_ERROR_200);
			logger.error("    RestClientException - " + ex.getMessage());
		}
		
		logger.info("  Resultado - " + resultado.getResultado());
		logger.info("  CodError - " + (resultado.getCodError() == null ? "": resultado.getCodError()));
		return resultado;
	}
	
	/**
	 * Crea la cabecera con el id del Cliente y Password que invova al Microservicio
	 * @return
	 */
	private HttpHeaders getHttpHeadersToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(env.getProperty("config.security.oauth.client.id"), env.getProperty("config.security.oauth.client.secret"));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return headers;
	}
	
	/**
	 * Crea el Body para invocar al Token
	 * @return
	 */
	private MultiValueMap<String, String> getBodyToken(String id, String pass){
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("username", id);
		map.add("password", pass);
		map.add("grant_type", "password");
		return map;
	}
	
	
	@Override
	public ResultadoBean getUsuarioById(String id) {
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put("id",id);
		ResultadoBean resultado = clienteRestUsuario.getForObject("http://localhost:8090/api/usuario/getUsuarioById/{id}", ResultadoBean.class, mapParams);
		return resultado;
	}
	
	@Override
	public ResultadoBean getUsuarioByDni(String dni) {
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put("dni",dni);
		ResultadoBean resultado = clienteRestUsuario.getForObject("http://localhost:8090/api/usuario/getUsuarioByDni/{dni}", ResultadoBean.class, mapParams);
		return resultado;
	}

	@Override
	public ResultadoBean getUsuarioByEmail(String email) {
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put("email",email);
		ResultadoBean resultado = clienteRestUsuario.getForObject("http://localhost:8090/api/usuario/getUsuarioByEmail/{email}", ResultadoBean.class, mapParams);
		return resultado;
	}

	@Override
	public ResultadoBean insertarUsuario(Usuario usuario) {
		HttpEntity<Usuario> body = new HttpEntity<Usuario>(usuario);
		ResponseEntity<ResultadoBean> resultado = clienteRestUsuario.exchange("http://localhost:8090/api/usuario/crearUsuario", HttpMethod.POST, body, ResultadoBean.class);
		return resultado.getBody();
	}

	@Override
	public ResultadoBean modificarUsuario(HttpSession session, Usuario usuario, Integer id) {
		
		logger.info("/modificarUsuario ");
		
		TokenBean tokenBean = new TokenBean();
		if(session.getAttribute(CONS_PROPERTY_TOKEN) != null) {
			tokenBean = (TokenBean) session.getAttribute(CONS_PROPERTY_TOKEN);
		}
		
		HttpEntity<Object> entity = new HttpEntity<Object>(Utils.convertUsuarioToMap(usuario), getHttpHeadersAuthentication(tokenBean.getToken()));
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put("id",id.toString());
		ResponseEntity<ResultadoBean> resultado = clienteRestUsuario.exchange("http://localhost:8090/api/usuario/modificarUsuario/{id}", 
																				HttpMethod.PUT, entity, ResultadoBean.class, mapParams);
		
		logger.info("  StatusCode - " + resultado.getStatusCodeValue());
		logger.info("  Body - " + resultado.getBody().toString());
		
		return resultado.getBody();
	}

	@Override
	public ResultadoBean eliminarUsuario(HttpSession session, Integer id) {
		
		logger.info("/eliminarUsuario ");
		
		TokenBean tokenBean = new TokenBean();
		if(session.getAttribute(CONS_PROPERTY_TOKEN) != null) {
			tokenBean = (TokenBean) session.getAttribute(CONS_PROPERTY_TOKEN);
			logger.info("  Token - " + tokenBean.getToken());
		}
		
		HttpEntity<Object> entity = new HttpEntity<Object>(null, getHttpHeadersAuthentication(tokenBean.getToken()));
		
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put("id",id.toString());
		logger.info("  Invocacion eliminarUsuario");
		ResponseEntity<ResultadoBean> resultado = clienteRestUsuario.exchange("http://localhost:8090/api/usuario/eliminarUsuario/{id}", 
																				HttpMethod.DELETE, entity, ResultadoBean.class, mapParams);
		logger.info("    StatusCode - " + resultado.getStatusCode().toString());
		ResultadoBean resultadoBean = new ResultadoBean();
		if(resultado.getStatusCode().toString().contains(Constantes.HTTP_204)) {
			resultadoBean.setResultado(Constantes.CONS_RESULTADO_OK);
		} else {
			resultadoBean.setResultado(Constantes.CONS_RESULTADO_KO);
			resultadoBean.setCodError(ConstantesErrores.COD_ERROR_104);
		}
		
		return resultadoBean;
	}
	
	/**
	 * Crea la cabecera con el Token para las invocaciones a servicios que necesiten de autentificacion mediante Token
	 * @return
	 */
	private HttpHeaders getHttpHeadersAuthentication(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}