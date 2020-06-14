package com.gestion.web.controller;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.commons.utils.ConstantesErrores;
import com.gestion.web.generic.ResultadoBean;
import com.gestion.web.generic.Usuario;
import com.gestion.web.services.IService;
import com.gestion.web.utilidades.Constantes;
import com.gestion.web.utilidades.Utils;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@RefreshScope
@Controller
public class UsuarioController {

	private static final Logger logger = Logger.getLogger(UsuarioController.class);
	
	@Autowired
	@Qualifier("serviceImpl")
	IService serviceImpl;
	
	@Autowired
	private MessageSource mensajes;
	
	/**
	 * Controller de la pantalla principal
	 * http://localhost:8080/index
	 * @param model
	 * @return
	 */
	@RequestMapping(path="/index")
	public String index (Model model) {
		logger.info("/index ");
		return Constantes.VIEW_INDEX;
	}
	
	/**
	 * Controller para cerrar sesion de la aplicacion
	 * @param model
	 * @return
	 */
	@RequestMapping(path="/logout")
	public String logout (Model model) {
		logger.info("/logout ");
		return Constantes.VIEW_INDEX;
	}
	
	/**
	 * Controller para acceder a la vista de Alta de Usuario
	 * @param model
	 * @return
	 */
	@RequestMapping(path="/altaUsuario")
	public String altaUsuario (Model model) {
		logger.info("/altaUsuario ");
		Usuario usuarioCA = new Usuario();
		model.addAttribute("usuarioCA", usuarioCA);
		return Constantes.VIEW_ALTA_USUARIO;
	}
	
	/**
	 * Controller para acceder a la vista de Actualizar Usuario
	 * @param model
	 * @param idUsuario
	 * @return
	 */
	@RequestMapping(path="/perfil")
	public String getPerfilUser (Model model, String idUsuario) {
		logger.info("/perfil ");
		
		ResultadoBean resultadoBean = serviceImpl.getUsuarioById(idUsuario);
		Usuario usuario = new Usuario();
		if(Constantes.CONS_RESULTADO_OK.equals(resultadoBean.getResultado())) {
			usuario = resultadoBean.getUsuario();
		}
		model.addAttribute("usuarioCA", usuario);
		return Constantes.VIEW_MODIFICAR_USUARIO;
	}
	
	/**
	 * Controller que accede al menu de la aplicacion
	 * @param model
	 * @param idUsuario
	 * @return
	 */
	@RequestMapping(path="/menu")
	public String menu (Model model, String idUsuario) {
		logger.info("/model ");
		
		ResultadoBean resultadoBean = serviceImpl.getUsuarioById(idUsuario);
		Usuario usuario = new Usuario();
		if(Constantes.CONS_RESULTADO_OK.equals(resultadoBean.getResultado())) {
			usuario = resultadoBean.getUsuario();
		}
		
		model.addAttribute("login", Boolean.valueOf(true));
		model.addAttribute("idUsuario", usuario.getIdUsuario());
		model.addAttribute("nombreUsuario", usuario.getNombre());
		return Constantes.VIEW_MENU;
	}
	
	/**
	 * Controller del login
	 * @param model
	 * @param user
	 * @param pwd
	 * @return
	 */
	@HystrixCommand(fallbackMethod = "exceptionLogin")
	@RequestMapping(path="/login", method = RequestMethod.POST)
	public String login (HttpSession session, Model model, String user, String pwd) {
		logger.info("/login ");

		ResultadoBean resuladoBean = serviceImpl.getToken(session, user, pwd);
		
		Usuario usuario = new Usuario();
		if(Constantes.CONS_RESULTADO_OK.equals(resuladoBean.getResultado())) {
			usuario = resuladoBean.getUsuario();
			model.addAttribute("login", Boolean.valueOf(true));
			model.addAttribute("idUsuario", usuario.getIdUsuario());
			model.addAttribute("nombreUsuario", usuario.getNombre());
			return Constantes.VIEW_MENU;
			
		//Error Usuario no encontrado
		} else {
			model.addAttribute("login", Boolean.valueOf(false));
			model.addAttribute("errorLogin", Boolean.valueOf(true));
			model.addAttribute("errorMsg", mensajes.getMessage(Utils.getCodErrorLiteral(resuladoBean.getCodError()), null, LocaleContextHolder.getLocale()));
			return Constantes.VIEW_INDEX;
		}
	}
	
	public String exceptionLogin (HttpSession session, Model model, String user, String pwd) {
		System.out.println("/exceptionLogin ");
		model.addAttribute("login", Boolean.valueOf(false));
		model.addAttribute("errorLogin", Boolean.valueOf(true));
		model.addAttribute("errorMsg", mensajes.getMessage(Utils.getCodErrorLiteral(ConstantesErrores.COD_ERROR_000), null, LocaleContextHolder.getLocale()));
		return Constantes.VIEW_INDEX;
	}
	
	/**
	 * Controller que gestiona el formulario de Actualizar Usuario
	 * @param usuario
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(path="/updateUsuarioForm", method = RequestMethod.POST)
	@HystrixCommand(fallbackMethod = "exceptionModificarUsuario")
	public String modificarUsuario (HttpSession session, @Valid @ModelAttribute("usuarioCA") com.gestion.web.generic.Usuario usuario, BindingResult result, Model model) {
		
		logger.info("/updateUsuarioForm ");
		logger.info("Parametros entrada: " + usuario.toString());
		
		 if (result.hasErrors()) {			
	        return Constantes.VIEW_MODIFICAR_USUARIO;
	    }
		
		//Se comprueba si existe usuario por DNI
		Boolean existeUsuario = Boolean.valueOf(false);
		String codError = "";
		ResultadoBean resultadoBean = serviceImpl.getUsuarioByDni(usuario.getDni());
		if(resultadoBean.getUsuario() != null && resultadoBean.getUsuario().getIdUsuario() != usuario.getIdUsuario()) {
			existeUsuario = Boolean.valueOf(true);
			codError = ConstantesErrores.COD_ERROR_106;
			logger.info("UsuarioModel.altaUsuarioForm -   Existe usuario con DNI ");
		//Se comprueba si existe usuario por EMAIL
		} else {
			resultadoBean = serviceImpl.getUsuarioByEmail(usuario.getEmail());
			if(resultadoBean.getUsuario() != null && resultadoBean.getUsuario().getIdUsuario() != usuario.getIdUsuario()) {
				existeUsuario = Boolean.valueOf(true);
				codError = ConstantesErrores.COD_ERROR_107;
				logger.info("UsuarioModel.altaUsuarioForm -   Existe usuario con Email ");
			}
		}
		
		if(!existeUsuario) {
			resultadoBean = serviceImpl.modificarUsuario(session, usuario, usuario.getIdUsuario());
			logger.info(" beanResultado: " + resultadoBean.getResultado());
			 
			if(Constantes.CONS_RESULTADO_OK.equals(resultadoBean.getResultado())) {	
				model.addAttribute("login", Boolean.valueOf(true));
				model.addAttribute("idUsuario", resultadoBean.getUsuario().getIdUsuario());
				model.addAttribute("nombreUsuario", resultadoBean.getUsuario().getNombre());
				model.addAttribute("successUpdate", Boolean.valueOf(true));
				model.addAttribute("successMsg", mensajes.getMessage(Constantes.LITERAL_SUCCESS_UPDATE, null, LocaleContextHolder.getLocale()));
				return Constantes.VIEW_MENU;
			} else {
				model.addAttribute("errorUpdate", Boolean.valueOf(true));
				model.addAttribute("errorMsg", mensajes.getMessage(Utils.getCodErrorLiteral(resultadoBean.getCodError()), null, LocaleContextHolder.getLocale()));
			}
		} else {
			model.addAttribute("errorUpdate", Boolean.valueOf(true));
			model.addAttribute("errorMsg", mensajes.getMessage(Utils.getCodErrorLiteral(codError), null, LocaleContextHolder.getLocale()));
		}
		
		return Constantes.VIEW_MODIFICAR_USUARIO;
	}
	
	public String exceptionModificarUsuario (HttpSession session, Usuario usuario, BindingResult result, Model model) {
		System.out.println("/exceptionModificarUsuario ");
		model.addAttribute("usuario", usuario);
		model.addAttribute("errorUpdate", Boolean.valueOf(true));
		model.addAttribute("errorMsg", mensajes.getMessage(Utils.getCodErrorLiteral(ConstantesErrores.COD_ERROR_000), null, LocaleContextHolder.getLocale()));
		return Constantes.VIEW_MODIFICAR_USUARIO;
	}
	
	/**
	 * Controller que controla el formulario del Alta de Usuario
	 * @param usuario
	 * @param result
	 * @param password2
	 * @param model
	 * @return
	 */
	@RequestMapping(path="/altaUsuarioForm", method = RequestMethod.POST)
	@HystrixCommand(fallbackMethod = "exceptionInsertarUsuario")
	public String insertarUsuario (@Valid @ModelAttribute("usuarioCA") com.gestion.web.generic.Usuario usuario, BindingResult result, String password2, Model model) {
		
		logger.info("/altaUsuarioForm ");
		logger.info("Parametros entrada: " + usuario.toString());
		
		 if (result.hasErrors()) {			
	        return Constantes.VIEW_ALTA_USUARIO;
	    }
		
		//Se comprueba si existe usuario por DNI
		Boolean existeUsuario = Boolean.valueOf(false);
		String codError = "";
		ResultadoBean resultadoBean = serviceImpl.getUsuarioByDni(usuario.getDni());
		if(resultadoBean.getUsuario() != null) {
			existeUsuario = Boolean.valueOf(true);
			codError = ConstantesErrores.COD_ERROR_106;
			logger.info("UsuarioModel.altaUsuarioForm -   Existe usuario con DNI ");
		//Se comprueba si existe usuario por EMAIL
		} else {
			resultadoBean = serviceImpl.getUsuarioByEmail(usuario.getEmail());
			if(resultadoBean.getUsuario() != null) {
				existeUsuario = Boolean.valueOf(true);
				codError = ConstantesErrores.COD_ERROR_107;
				logger.info("UsuarioModel.altaUsuarioForm -   Existe usuario con Email ");
			}
		}
		
		if(!existeUsuario) {
			resultadoBean = serviceImpl.insertarUsuario(usuario);
			logger.info(" beanResultado: " + resultadoBean.getResultado());
			 
			if(Constantes.CONS_RESULTADO_OK.equals(resultadoBean.getResultado())) {	
				model.addAttribute("login", Boolean.valueOf(true));
				model.addAttribute("idUsuario", resultadoBean.getUsuario().getIdUsuario());
				model.addAttribute("nombreUsuario", resultadoBean.getUsuario().getNombre());
				return Constantes.VIEW_MENU;
			} else {
				model.addAttribute("errorAlta", Boolean.valueOf(true));
				model.addAttribute("errorMsg", mensajes.getMessage(Utils.getCodErrorLiteral(resultadoBean.getCodError()), null, LocaleContextHolder.getLocale()));
				return Constantes.VIEW_ALTA_USUARIO;
			}
		} else {
			model.addAttribute("errorAlta", Boolean.valueOf(true));
			model.addAttribute("errorMsg", mensajes.getMessage(Utils.getCodErrorLiteral(codError), null, LocaleContextHolder.getLocale()));
			return Constantes.VIEW_ALTA_USUARIO;
		}
	}
	
	public String exceptionInsertarUsuario (Usuario usuario, BindingResult result, String password2, Model model) {
		System.out.println("/exceptionInsertarUsuario ");
		model.addAttribute("usuario", usuario);
		model.addAttribute("errorAlta", Boolean.valueOf(true));
		model.addAttribute("errorMsg", mensajes.getMessage(Utils.getCodErrorLiteral(ConstantesErrores.COD_ERROR_000), null, LocaleContextHolder.getLocale()));
		return Constantes.VIEW_ALTA_USUARIO;
	}
}