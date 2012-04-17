<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="web" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="licenses">
	<c:choose>
		<c:when test="${it.id == null}">
			<h1>Issue new license</h1>
		</c:when>
		<c:otherwise>
			<h1>
				License
				<c:out value="${it.name}" />
			</h1>
		</c:otherwise>
	</c:choose>

	<br />
	<br />

	<form class="form-horizontal">
		<fieldset>
			<c:if test="${it.id != null}">
				<div class="control-group">
					<label class="control-label" for="idText">Id</label>
					<div class="controls">
						<input type="text" class="span3 disabled input-xlarge"
							value="${it.id}" id="idText" disabled />
					</div>
				</div>
			</c:if>
			<div class="control-group">
				<label class="control-label" for="number">License number</label>
				<div class="controls">
					<input type="text" class="span3 input-xlarge" value="${it.name}"
						id="number" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="description">Descrption</label>
				<div class="controls">
					<textarea class="span3 input-xlarge" rows="3"
						id="description"><c:out value="${it.description}" /></textarea>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="active">Active</label>
				<div class="controls">
					<label class="checkbox"> <input type="checkbox" id="active" <c:if test="${it.active}">checked="yes"</c:if> >
						
					</label>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="allowActivations">Allow new activations</label>
				<div class="controls">
					<label class="checkbox"> <input type="checkbox" id="allowActivations" <c:if test="${it.allowActivations}">checked="yes"</c:if>>
						
					</label>
				</div>
			</div>
		</fieldset>
	</form>


</web:layout>