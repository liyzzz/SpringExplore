package com.liyueze.demo.v2.action;

import com.liyueze.demo.v2.service.IModifyService;
import com.liyueze.demo.v2.service.IQueryService;
import com.liyueze.mvcFrameworkCode.v2.annotation.AutowiredV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ControllerV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.RequestMappingV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.RequestParamV2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口url
 * @author Tom
 *
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
	
	
	
	private void out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
