<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<html>
<head>

<style type="text/css">
#contact-form-wrap {
	width: 410px;
	padding: 20px;
}

label {
	clear: both;
	float: left;
	font-size: 10px;
	color: #888;
	line-height: 22px;
}

#op {
	float: right;
	height: 35px;
	background: #eee;
}
#t{
	float: right;
	height: 35px;
	background: #eee;
}


.form-e {
	width: 141px;
	margin-bottom: 10px;
	background: #eee;
}

.form-element {
	float: right;
	width: 310px;
	padding: 0 10px;
	background: #fff;
	margin-bottom: 10px;
}
.form_

.form-element:hover {
	background: #eee;
}

.input-text {
	width: 310px;
	height: 35px;
	background: #eee;
	font-size: 11px;
	font-style: italic;
	color: #444;
	line-height: 35px;
	border: 0;
}

.input-textarea {
	width: 310px;
	min-width: 310px;
	max-width: 310px;
	height: 85px;
	margin: 5px 0;
	overflow: auto;
	font-size: 11px;
	color: #444;
	line-height: 18px;
	border: 0;
	background: #eee;
}

.contact-submit {
	display: inline-block;
	padding-right: 10px;
	margin-left: 80px;
	font: normal normal 15px 'LeagueGothic', sans-serif;
	text-decoration: none;
	background: #eee;
}
</style>
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script>
	
$(function (){
	//var url=hostval+$("#api").val();
	$("#bt").click(function() {
	 var type=$("#t").val();
		var resurl = $("#url").val();
		var pa = $("#pa").val();
		var op = $("#op").val();
	//	var cod=document.getElementsByName("code").checked.value;
		var cod=$('input:radio[name="code"]:checked').val();
		$("#resp").html("");
		var hostval = $("#host").val();
		if(type=='json'){
		$.ajax({
			type : 'POST',
			contentType : 'application/json',
			url : hostval + "/monitor/requestTest",
			data : {
				"url" : resurl,
				"pa" : pa,
				"op" : op,
				"coding":cod
			},
			success : function(resp) {
				$("#resp").html(resp);
			},
			dataType : "html"
		});
		
	}
		else if(type=='xml'){
			$.ajax({
				type : 'POST',
				contentType : 'xml',
				url : hostval + "/monitor/requestTest",
				data : {
					"url" : resurl,
					"pa" : pa,
					"op" : op,
					"coding":cod
				},
				success : function(resp) {
					$("#resp").html(resp);
				},
				dataType : "html"
			});
			
		}
		 else if (type=='formdata'){
		//	pa= eval('('+pa+')');
			$.ajax({
				type : 'POST',
				url : hostval + "/requestTest",
				data : {
					"url" : resurl,
					"pa" : pa,
					"op" : op,
					"cod":cod
				},
				success : function(resp) {
					$("#resp").html(resp);
				},
				dataType : "html"
			});
		}

	});
	});
	
function setbinding4xml(api,json){
	setbinding(json,null,'xml');
}

	function setbinding(json,submittype){
		$("#pa").val(json)
		
		if(!submittype){
			submittype= 'payload';
		}
		type=submittype;
	}
	
</script>

</head>


<body>
<select name="host" id="host">
	<option
		value="http://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>"
		selected>当前服务器地址
	</option>
	
</select>

	<section id="contact-form-wrap">
		<%
			String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
		%>

		<div id="contact-form-message"></div>

		<label for="contact-name">请求方式:</label>
		<div class="form-e">
			<select id="op" name="op">
				<option value="GET">get</option>
				<option value="POST">post</option>
			</select>
		</div>
		<br />
		<br /> <label for="contact-email">请求地址:</label>
		<div class="form-element">
			<input type="text" class="input-text" id="url" name="url" />
		</div>
		<label for="contact-name">参数格式:</label>
		<div class="form-e">
			<select id="t" name="t">
				<option value="formdata">formdata</option>
				<option value="json">json</option>
				<option value="xml">xml</option>
			</select>
		</div>
		<br />
		<br/>
		<br/>
		<br/>
		
		<label for="contact-message">请求编码:</label>
		<input type="radio" name="code" id="code" value="utf-8"/>UTF-8
		<input type="radio" name="code" id="code" value="gbk"/>GBK
		<br /> 
		<label for="contact-message">请求参数:</label>
		<div class="form-element">
			<textarea class="input-textarea" name="pa" id="pa"></textarea>
		</div>
		<!-- a href="#" class="contact-submit"><span>Send your offer</span></a> -->
		<input type="button" class="contact-submit" value="提交" id="bt" /> 
		<input type="button" onclick='setbinding4xml("/demo/recv","{\"port\":\"80\",\"name\":\"zs\"}",null,"formdata")'
 value="参数(json)" >
 <input type="button" onclick='setbinding4xml("/demo/recv","parament=PaType,parament=PaType",null,"formdata")'
 value="参数(formdata)" >
 <input type="button" onclick='setbinding4xml("/demo/recv","<?xml version=\"1.0\" encoding=\"utf-8\"?><parament><ID>1111</ID> <Mobile>18922260815</Mobile>  <Message>test</Message><parament>")'',null,"formdata")'
 value="参数(xml)" ><br/>
		<label>响应:</label>
		<div id="resp">
	</section>
</body>



</html>
