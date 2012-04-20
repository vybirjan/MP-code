<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="web" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="keys">
	<h1 class="pul-center">Application encrpytion keys</h1>
	<br />
	<c:if test="${it.error != null}">
		<div class="alert alert-error">
			<c:out value="${it.error}" />
		</div>
	</c:if>
	<c:if test="${it.success != null}">
		<div class="alert alert-success">
			<c:out value="${it.success}" />
		</div>
	</c:if>
	<br />
	<form class="form-inline" method="post" target="_self">
		<strong>Application id for new key:&nbsp;&nbsp;</strong><input type="text" class="input-small"
			name="appId" />
		<button type="submit" class="btn btn-success pull-right btn-large">
			<i class="icon-plus icon-white"></i> Generate new key
		</button>
	</form>
	<div class="well">
		<table class="table">
			<thead>
				<tr>
					<th>Application&nbsp;Id</th>
					<th>Public key</th>
					<th>Private key</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${it.tableItems}" var="record">
					<tr>
						<td><c:out value="${record.appId}" /></td>
						<td style="word-break: break-all;"><c:out
								value="${record.publicKey}" /></td>
						<td style="word-break: break-all;"><c:out
								value="${record.privateKey}" /></td>
						<td><a
							onclick="onDelete('<c:out value="${record.appId}" />', <c:out value="${record.id}" />)"
							class="btn btn-danger btn-small"><i
								class="icon-remove icon-white"></i> Delete</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<script type="text/javascript">
	function onDelete(name, id) {
		if(confirm('Do you really want to delete key for application ' + name + '?')) {
			window.location = "/web/keys/delete/" + id
		}
	}
	</script>
</web:layout>