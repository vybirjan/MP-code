<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="web" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="licenses">
	<h1>Issued licenses</h1>
	<strong>Total number of licenses:</strong> X
	<table class="table">
		<thead>
			<tr>
				<th>Number</th>
				<th>Description</th>
				<th>Valid from</th>
				<th>Valid to</th>
				<th>Active</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${it.tableRecords}" var="record">
				<tr>
					<td><c:out value="${record.number}" /></td>
					<td><c:out value="${record.description}" /></td>
					<td><c:out value="${record.dateFrom}" /></td>
					<td><c:out value="${record.dateTo}" /></td>
					<td><c:out value="${record.active}" /></td>
				</tr>
			</c:forEach>

		</tbody>
	</table>
</web:layout>