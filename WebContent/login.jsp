<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="${path }/loginstyle/css/style.css" rel="stylesheet" type="text/css" />
    <title>用户登录</title>
    <style>
        body {
            background-image: url('images/sias.jpg');
            background-repeat: no-repeat;
            background-size: cover;
            background-position: center;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
        }
        .login-container {
            text-align: center;
            background-color: rgba(255, 255, 255, 0.8);
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
        }
        .title {
            font-size: 36px;
            color: white;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div id="web">
    <div class="login-container">
        <div class="title">
            学生考勤管理系统
        </div>
        <div class="login">
            <div class="banner">
                <iframe id="frame_banner" src="${path }/loginstyle/sban/banner.html" height="218" width="100%"  allowtransparency="true" title="test"  scrolling="no" frameborder="0"></iframe>
            </div>
            <form id="form1" name="form1" action="login.htm" method="post">
                <div class="logmain">
                    <h1>&nbsp;</h1>
                    <div class="logdv">
                        <span class="logzi">账 号：</span>
                        <input type="text" id="userName" name="userName" class="ipt" />
                    </div>
                    <div class="logdv">
                        <span class="logzi">密 码：</span>
                        <input id="password" name="password" type="password" class="ipt" />
                    </div>
                    <div class="logdv" style="height:40px;">
                        <p class="logzi">&nbsp;</p>
                        <input name="提交" onclick="javascript:document.getElementById('form1').submit()" class="btnbg" value="点击登录" />
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
