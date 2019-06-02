package com.liyueze.demo.v2.service;

/**
 * 查询业务
 *
 */
public interface IQueryService {
	
	/**
	 * 查询
	 */
	public String query(String name);

	public String queryAll();

	public String queryError();
}
