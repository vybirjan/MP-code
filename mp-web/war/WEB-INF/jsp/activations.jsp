<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="web" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="licenses">
	<h1 class="pul-center">Activations for license <c:out value="${it.licenseName}" /></h1>
	&nbsp;<br />
		<c:if test="${it.errorMessage != null}">
		<div class="alert alert-error"><c:out value="${it.errorMessage}" /></div>
	</c:if>
	<c:if test="${it.okMessage != null}">
		<div class="alert alert-success"><c:out value="${it.okMessage}" /></div>
	</c:if>
	&nbsp;<br />
	
	<div class="well">
		<table class="table">
			<thead>
				<tr><th>Date activated</th><th>Active</th><th>Fingerprint</th><th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${it.tableItems}" var="item">
					<tr>
						<td>${item.dateActivated}</td>
						<td>${item.active}</td>
						<td>
							<c:forEach items="${item.fingerprints}" var="fingerprint">
								<c:out value="${fingerprint.name}" /> - <c:out value="${fingerprint.value}" /></br>
							</c:forEach>
						</td>
						<td>
							<div class="btn-group  pull-right">
								<a class="btn btn-small" href="/web/activations/${it.licenseId}/toggleactive/${item.id}">${item.active ? 'Deactivate' : 'Activate'}</a>
								<a class="btn btn-small btn-danger" href="/web/activations/${it.licenseId}/delete/${item.id}"><i class="icon-remove icon-white"></i> Delete</a>
							</div>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	
</web:layout>