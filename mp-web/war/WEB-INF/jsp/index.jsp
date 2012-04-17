<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib prefix="web" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="overview">
	<h1>Overview</h1>
	<br/>
	<Strong>Number of licenses issued:</strong> <c:out value="${it.numberOfLicenses}" />
	<br/>
	<Strong>Number of licenses activated:</strong> <c:out value="${it.numberOfActivations}" />
	<br/>
	<strong>Last license activation:</strong> <c:out value="${it.lastActivationDate}" />
	<br/>
</web:layout>