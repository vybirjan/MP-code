<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="web" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="licenses">
	<h1 class="pul-center"></h1>
	&nbsp;<br />
		<c:if test="${it.errorMessage != null}">
		<div class="alert alert-error"><c:out value="${it.errorMessage}" /></div>
	</c:if>
	<c:if test="${it.okMessage != null}">
		<div class="alert alert-success"><c:out value="${it.okMessage}" /></div>
	</c:if>
	&nbsp;<br />
	<strong>Total number of licenses:</strong>
	<c:out value="${it.totalCount}" />
	<a class="btn btn-success pull-right btn-large" href="/web/licenses/new"><i class="icon-white icon-plus"></i> Add new license</a>
	
	&nbsp;<br />

	&nbsp;<br />
	&nbsp;<br />
	
	<div class="well">
	<table class="table">
		<thead>
			<tr>
				<th>Id</th>
				<th>Number</th>
				<th>Description</th>
				<th>Valid from</th>
				<th>Valid to</th>
				<th>Active</th>
				<th />
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${it.tableRecords}" var="record">
				<tr>
					<td><c:out value="${record.id}" /></td>
					<td><c:out value="${record.number}" /></td>
					<td><c:out value="${record.description}" /></td>
					<td><c:out value="${record.dateFrom}" /></td>
					<td><c:out value="${record.dateTo}" /></td>
					<td><c:out value="${record.active}" /></td>
					<td>
						<div class="btn-group pull-right">
							<a href="/web/activations/${record.id}" class="btn btn-small"><i class="icon-th-list"></i> Show activations</a>
							<a href="/web/licenses/edit/${record.id}" class="btn btn-info btn-small"><i
								class="icon-edit icon-white"></i> Edit</a>
							<a onclick="onDelete('<c:out value="${record.number}" />', <c:out value="${record.id}" />)"
							class="btn btn-danger btn-small"><i class="icon-remove icon-white"></i>
								Delete</a>
						</div>
					</td>
				</tr>
			</c:forEach>

		</tbody>
	</table>
	</div>
	<script type="text/javascript">
	function onDelete(name, id) {
		if(confirm('Do you really want to delete license ' + name + '?')) {
			window.location = "/web/licenses/delete/" + id
		}
	}
	</script>
</web:layout>