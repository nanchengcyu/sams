package com.as.service;

import java.util.List;

public interface DeptService<T> {
	
	public abstract List<T> findAllDept() throws Exception;
	
	public abstract List<T> findDeptByPid(T t) throws Exception;

	public abstract int countDeptByPid(T t) throws Exception;
	

	public T findOneDeptById(int id) throws Exception;
	
	public void updateDept(T t) throws Exception;
	
	public void addDept(T t) throws Exception;
	
	public void deleteDept(T t) throws Exception;

}
