<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="web" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="licenses">
	<h1>Issued licenses</h1>
	&nbsp;<br />
	&nbsp;<br />
	<strong>Total number of licenses:</strong>
	<c:out value="${it.totalCount}" />
	<a class="btn btn-success pull-right btn-large" href="/web/licenses/new"><i class="icon-white icon-plus"></i> Add new license</a>
	
	&nbsp;<br />
	&nbsp;<br />

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
					<td><a href="/web/licenses/edit/${record.id}" class="btn btn-info btn-small"><i
							class="icon-edit icon-white"></i> Edit</a> <a href="/web/licenses/delete/${record.id}"
						class="btn btn-danger btn-small"><i class="icon-remove icon-white"></i>
							Delete</a></td>
				</tr>
			</c:forEach>

		</tbody>
	</table>
</web:layout>