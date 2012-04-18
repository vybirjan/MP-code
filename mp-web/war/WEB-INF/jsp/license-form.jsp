<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
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
	<form class="form-horizontal" method="post" target="_self">
		<fieldset>
			<c:if test="${it.id != null}">
				<div class="control-group">
					<label class="control-label" for="idText">Id</label>
					<div class="controls">
						<input type="text" class="span5 disabled input-xlarge"
							value="${it.id}" id="idText" disabled />
					</div>
				</div>
			</c:if>
			<div class="control-group <c:if test="${it.numberError != null}">error</c:if>">
				<label class="control-label" for="number">License number</label>
				<div class="controls">
					<input type="text" class="span5 input-xlarge" value="${it.name}"
						id="number" name="number" />
					<c:if test="${it.numberError != null}"><span class="help-inline"><c:out value="${it.numberError}" /></span></c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="description">Description</label>
				<div class="controls">
					<textarea class="span5 input-xlarge" rows="3" id="description"
						name="description"><c:out value="${it.description}" /></textarea>
				</div>
			</div>
			<div class="control-group <c:if test="${it.maxActivationsError != null}">error</c:if>">
				<label class="control-label" for="numOfActivations">Max number of activations</label>
				<div class="controls">
					<input data-datepicker="datepicker" type="text"
						class="span5 input-xlarge" value="${it.maxActivations}" id="numOfActivations"
						name="numOfActivations" />
					<c:if test="${it.maxActivationsError != null}"><span class="help-inline"><c:out value="${it.maxActivationsError}" /></span></c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="active">Active</label>
				<div class="controls">
					<label class="checkbox"> <input type="checkbox"
						name="active" id="active"
						<c:if test="${it.active}">checked="yes"</c:if>>
					</label>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="allowActivations">Allow
					new activations</label>
				<div class="controls">
					<label class="checkbox"> <input type="checkbox"
						name="allowActivations" id="allowActivations"
						<c:if test="${it.allowActivations}">checked="yes"</c:if>>
					</label>
				</div>
			</div>

			<div class="form-actions">
				<button type="submit" class="btn btn-success">
					<c:choose>
						<c:when test="${it.id == null}">
							Create new license
						</c:when>
						<c:otherwise>
							Save changes
						</c:otherwise>
					</c:choose>
				</button>
				<a class="btn" href="/web/licenses">Cancel</a>
			</div>
		</fieldset>
	</form>
</web:layout>