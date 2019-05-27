package com.liyueze.demo.v2.action;

import com.liyueze.demo.v2.service.IModifyService;
import com.liyueze.demo.v2.service.IQueryService;
import com.liyueze.mvcFrameworkCode.v2.annotation.AutowiredV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ControllerV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.RequestMappingV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.RequestParamV2;
import com.liyueze.mvcFrameworkCode.v2.springmvn.servlet.Model;
import com.liyueze.mvcFrameworkCode.v2.springmvn.servlet.ModelAndView;
import javafx.scene.input.DataFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * 公布接口url
 *
 *  测试访问：http://localhost:8080/springExplore_war_exploded/web/first.asdf?name=liyueze
 */
@ControllerV2
@RequestMappingV2("/web")
public class MyAction {

	@AutowiredV2
	private IQueryService queryService;
	@AutowiredV2
	private IModifyService modifyService;

	@RequestMappingV2("/query.json")
	public void query(HttpServletRequest request, HttpServletResponse response,
								@RequestParamV2("name") String name){
		String result = queryService.query(name);
		out(response,result);
	}
	
	@RequestMappingV2("/add*.json")
	public void add(HttpServletRequest request,HttpServletResponse response,
			   @RequestParamV2("name") String name,@RequestParamV2("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@RequestMappingV2("/remove.json")
	public void remove(HttpServletRequest request,HttpServletResponse response,
		   @RequestParamV2("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@RequestMappingV2("/edit.json")
	public void edit(HttpServletRequest request,HttpServletResponse response,
			@RequestParamV2("id") Integer id,
			@RequestParamV2("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}

	@RequestMappingV2("/test.json")
	public void test(@RequestParamV2("id") Integer id, @RequestParamV2("name") String name){
		String result = modifyService.edit(id,name);
		System.out.println(result);
	}
	// 访问：http://localhost:8080/springExplore_war_exploded/web/first.asdf?name=liyueze
	@RequestMappingV2("/first*")
	public ModelAndView first(@RequestParamV2("name") String name, Model model, HttpSession httpSession){
		SimpleDateFormat dataFormat=new SimpleDateFormat( " yyyy年MM月dd日 " );
		String result = queryService.query(name);
		model.put("name",name);
		model.put("data",dataFormat.format(new Date()));
		model.put("token",httpSession);
		ModelAndView modelAndView=new ModelAndView("first");
		modelAndView.setModel(model);
		return modelAndView;
	}
	
	
	
	private void out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
