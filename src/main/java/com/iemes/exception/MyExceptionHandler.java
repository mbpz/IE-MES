package com.iemes.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 统一异常处理，有效地针对异步和非异步请求
 * 不同异常会到不同页面
 * throw new ParameterException("XXXX")  －－－－ >  error-parameter.jsp
 * throw new SystemException("XXXX")  －－－－ >  error-System.jsp
 * throw new Exception("XXXX")  －－－－ >  error.jsp
 * 参数异常返回 HTTP 400，其他异常返回 HTTP 500。
 * @author lanyuan
 * @date 2014-12-10
 * @Email：mmm333zzz520@163.com
 */
public class MyExceptionHandler implements HandlerExceptionResolver {
	private static final Logger logger = Logger.getLogger(MyExceptionHandler.class);

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		logger.error("Request failed: " + request.getRequestURI(), ex);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("ex", ex);
		//是否异步请求
		String accept = request.getHeader("Accept");
		String requestedWith = request.getHeader("X-Requested-With");
		boolean ajax = (accept != null && accept.contains("application/json"))
				|| (requestedWith != null && requestedWith.contains("XMLHttpRequest"));
		response.setStatus(ex instanceof ParameterException ? HttpServletResponse.SC_BAD_REQUEST
				: HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		if (!ajax) {
			// 根据不同错误转向不同页面
				if(ex instanceof SystemException) {
					return new ModelAndView("WEB-INF/jsp/exception/error-system", model);
				}else if(ex instanceof ParameterException) {
					return new ModelAndView("WEB-INF/jsp/exception/error-parameter", model);
				} else {
					return new ModelAndView("WEB-INF/jsp/exception/error", model);
				}
		 }else{
			 try {  
				 response.setContentType("text/plain;charset=UTF-8");
                 PrintWriter writer = response.getWriter();  
				 writer.write(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
                 writer.flush();  
             } catch (IOException e) {  
            	 model.put("ex", e);
			 return new ModelAndView("WEB-INF/jsp/exception/error", model);
             }  
			 return new ModelAndView();
		 }
	}
}
