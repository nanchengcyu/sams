package com.as.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.as.entity.Menu;
import com.as.entity.Role;
import com.as.entity.User;
import com.as.service.MenuService;
import com.as.service.RoleService;
import com.as.service.UserService;
import com.as.util.StringUtil;
import com.as.util.WriterUtil;
import com.mysql.jdbc.TimeUtil;
import com.sun.media.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



@Controller
@SuppressWarnings("unchecked")
public class LoginController {

	private User user;
	private User currentUser;
	@Autowired
	private UserService<User> userService;
	@Autowired
	private MenuService<Menu> menuService;
	private Role role;
	@Autowired
	private RoleService<Role> roleService;
	private Map map;
	
	
	@SuppressWarnings("static-access")
	@RequestMapping("login")
	public void login(HttpServletRequest request,HttpServletResponse response){
		try {
			HttpSession session = request.getSession();
			String userName=request.getParameter("userName");
			String password=request.getParameter("password");
			String imageCode=request.getParameter("imageCode");
			System.out.println(userName);
			System.out.println(password);
			System.out.println(imageCode);
			request.setAttribute("userName", userName);
			request.setAttribute("password", password);
			request.setAttribute("imageCode", imageCode);
			if(StringUtil.isEmpty(userName)||StringUtil.isEmpty(password)){
				request.setAttribute("error", "账户或密码为空");
				request.getRequestDispatcher("login.jsp").forward(request, response);
				return;
			}
			/**
			if(StringUtil.isEmpty(imageCode)){
				request.setAttribute("error", "验证码为空");
				request.getRequestDispatcher("login.jsp").forward(request, response);
				return;
			}
			if(!imageCode.equals(session.getAttribute("sRand"))){
				request.setAttribute("error", "验证码错误");
				request.getRequestDispatcher("login.jsp").forward(request, response);
				return;
			}*/
			map = new HashMap<String, String>();
			map.put("userName", userName);
			map.put("password", password);
			currentUser = userService.loginUser(map);
			if(currentUser==null){
				request.setAttribute("error", "用户名或密码错误");
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}else{
				
				// 登录信息存入session
				role = roleService.findOneRole(currentUser.getRoleId());
				String roleName=role.getRoleName();
				currentUser.setRoleName(roleName);
				session.setAttribute("currentUser", currentUser);  // 当前用户信息
				session.setAttribute("currentOperationIds", role.getOperationIds());  // 当前用户所拥有的按钮权限
				
				// 跳转到主界面
				response.sendRedirect("main.htm");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 进入系统主界面
	@RequestMapping("main")
	public String toMain(){
		return "main";
	}
	
	
	// 加载最上级左菜单树
	@RequestMapping("menuTree")
	public void getMenuTree(HttpServletRequest request,HttpServletResponse response){
		try {
			String parentId = request.getParameter("parentId");
			currentUser = (User) request.getSession().getAttribute("currentUser");
			role = roleService.findOneRole(currentUser.getRoleId());
			String[] menuIds = role.getMenuIds().split(",");
			map = new HashMap();
			map.put("parentId",parentId);
			map.put("menuIds", menuIds);
			JSONArray jsonArray = getMenusByParentId(parentId, menuIds);
			WriterUtil.write(response, jsonArray.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 递归加载所所有树菜单
	public JSONArray getMenusByParentId(String parentId,String[] menuIds)throws Exception{
		JSONArray jsonArray=this.getMenuByParentId(parentId,menuIds);
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			if("open".equals(jsonObject.getString("state"))){
				continue;
			}else{
				jsonObject.put("children", getMenusByParentId(jsonObject.getString("id"),menuIds));
			}
		}
		return jsonArray;
	}
	
	
	// 将所有的树菜单放入easyui要求格式的json
	public JSONArray getMenuByParentId(String parentId,String[] menuIds)throws Exception{
		JSONArray jsonArray=new JSONArray();
		map= new HashMap();
		map.put("parentId",parentId);
		map.put("menuIds", menuIds);
		List<Menu> list = menuService.menuTree(map);
		for(Menu menu : list){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", menu.getMenuId());
			jsonObject.put("text", menu.getMenuName());
			jsonObject.put("iconCls", menu.getIconCls());
			JSONObject attributeObject = new JSONObject();
			attributeObject.put("menuUrl", menu.getMenuUrl());
			if(!hasChildren(menu.getMenuId(), menuIds)){
				jsonObject.put("state", "open");
			}else{
				jsonObject.put("state", menu.getState());				
			}
			jsonObject.put("attributes", attributeObject);
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	
	// 判断是不是有子孩子，人工结束递归树
	public boolean hasChildren(Integer parentId,String[] menuIds){
		boolean flag = false;
		try {
			map= new HashMap();
			map.put("parentId",parentId);
			map.put("menuIds", menuIds);
			List<Menu> list = menuService.menuTree(map);
			if (list == null || list.size()==0) {
				flag = false;
			}else {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	
	
	
	// 更新密码
	@RequestMapping("updatePassword")
	public void updatePassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String userId=request.getParameter("userId");
			String newPassword=request.getParameter("newPassword");
			user=new User();
			user.setUserId(Integer.parseInt(userId));
			user.setPassword(newPassword);
			userService.updateUser(user);
			result.put("success", "true");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", "true");
			result.put("errorMsg", "对不起！密码修改失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	//安全退出
	@SuppressWarnings("unused")
	@RequestMapping("logout")
	private void logout(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		// 记录日志
		HttpSession session = request.getSession();
		
		// 清空session
		session.invalidate();
		
		// 清空cookie
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = new Cookie(cookies[i].getName(), null);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}

		response.sendRedirect("login.jsp");
	}
	
	
	
	
	
	
	

	
	
}
