<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="web" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="features" onLoad="onLoad()">
	<h1 class="pul-center">Features</h1>
	<br />
	<c:if test="${it.errorMessage != null}">
		<div class="alert alert-error">
			<c:out value="${it.errorMessage}" />
		</div>
	</c:if>
	<c:if test="${it.okMessage != null}">
		<div class="alert alert-success">
			<c:out value="${it.okMessage}" />
		</div>
	</c:if>
	<a id="btn-form-collapse"  class="btn btn-mini" data-toggle="collapse" data-target="#form"><i class="icon-resize-vertical"></i>
		Toggle form</a>
		
		<form id="form" class="form-horizontal collapse in" method="post" target="_self" style="display: none;">
		<br/>
			<fieldset>
				<legend>Create new feature</legend>
				<div class="control-group">
					<label class="control-label" for="code">Code:</label>
					<div class="controls">
						<input type="text" class="input-xlarge" id="code" name="code" />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="description">Description:</label>
					<div class="controls">
						<textarea class="input-xlarge" id="description" name="description"></textarea>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="generate">Generate key</label>
					<div class="controls">
						<input id="key-toggle" type="checkbox" checked="checked" id="generate" name="generate" onclick="toggleKey()">
						&nbsp;&nbsp;Key tag: <input name="tag" class="span2" id="key-tag" type="text" onkeypress="return isNumberKey(event)" />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="key">Key:</label>
					<div class="controls">
						<textarea class="input-xlarge" disabled="disabled" id="key" name="key"></textarea>
					</div>
				</div>
				<button type="submit" class="btn btn-success">
					<i class="icon-plus icon-white"></i> Create feature
				</button>
			</fieldset>
		</form>
	<br />
	<div class="well">
		<table class="table">
			<thead>
				<tr>
					<th>Code</th>
					<th>Description</th>
					<th>Key tag</th>
					<th>Tagged key</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${it.tableItems}" var="record">
					<tr>
						<td><c:out value="${record.code}" /></td>
						<td style="word-break: break-all;"><c:out
								value="${record.description}" /></td>
						<td>${record.tag}</td>
						<td style="word-break: break-all;"><c:out
								value="${record.key}" /></td>
						<td><a onclick="onDelete('<c:out value="${record.code}" />')"
							class="btn btn-danger btn-small"><i
								class="icon-remove icon-white"></i> Delete</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<script type="text/javascript">
		function onLoad() {
			$(".collapse").collapse();
			$("#form").show();
		}
		
		function toggleKey() {
			document.getElementById('key').disabled = document.getElementById('key-toggle').checked;
			document.getElementById('key-tag').disabled = !document.getElementById('key-toggle').checked;
		}

		function onDelete(code) {
			if (confirm('Do you really want to delete feature ' + code + '? Feature will also be removed from all licenses.')) {
				window.location = "/web/features/delete/" + code
			}
		}
		
		function isNumberKey(evt) {
           var charCode = (evt.which) ? evt.which : event.keyCode
           if (charCode > 31 && (charCode < 48 || charCode > 57))
              return false;

           return true;
        }
	</script>
	
</web:layout>