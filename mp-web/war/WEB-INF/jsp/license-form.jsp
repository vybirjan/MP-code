<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="web" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<web:layout navButton="licenses" onLoad="onLoad()">
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
			<%-- Optional ID field --%>
			<c:if test="${it.id != null}">
				<div class="control-group">
					<label class="control-label" for="idText">Id</label>
					<div class="controls">
						<input type="text" class="span5 disabled input-xlarge"
							value="${it.id}" id="idText" disabled />
					</div>
				</div>
			</c:if>

			<%-- License number field --%>
			<div
				class="control-group <c:if test="${it.numberError != null}">error</c:if>">
				<label class="control-label" for="number">License number</label>
				<div class="controls">
					<input type="text" class="span5 input-xlarge" value="${it.name}"
						id="number" name="number" />
					<c:if test="${it.numberError != null}">
						<span class="help-inline"><c:out value="${it.numberError}" /></span>
					</c:if>
				</div>
			</div>

			<%-- Description field --%>
			<div class="control-group">
				<label class="control-label" for="description">Description</label>
				<div class="controls">
					<textarea class="span5 input-xlarge" rows="3" id="description"
						name="description"><c:out value="${it.description}" /></textarea>
				</div>
			</div>

			<%-- Valid from field --%>
			<div
				class="control-group <c:if test="${it.validFromError != null}">error</c:if>">
				<label class="control-label" for="validFrom">Valid from</label>
				<div class="controls">
					<div class="input-append date" id="valid-from-wrapper"
						data-date="${empty it.validFrom ? it.now : it.validFrom}"
						data-date-format="dd-mm-yyyy">
						<input class="span4" type="text" name="validFrom"
							value="${it.validFrom}" id="validFrom" readonly> <a
							class="add-on"><i class="icon-th"></i></a>
					</div>
					<a class="btn add-on" onclick="clearInput('validFrom')"><i
						class="icon-remove"></i></a>

					<c:if test="${it.validFromError != null}">
						<span class="help-inline"><c:out
								value="${it.validFromError}" /></span>
					</c:if>
				</div>
			</div>

			<%-- Valid to field --%>
			<div
				class="control-group <c:if test="${it.validToError != null}">error</c:if>">
				<label class="control-label" for="validTo">Valid to</label>
				<div class="controls">
					<div class="input-append date" id="valid-to-wrapper"
						data-date="${empty it.validTo ? it.now : it.validTo}"
						data-date-format="dd-mm-yyyy">
						<input class="span4" type="text" name="validTo"
							value="${it.validTo}" id="validTo" readonly> <a
							class="add-on"><i class="icon-th"></i></a>
					</div>
					<a class="btn add-on" onclick="clearInput('validTo')"><i
						class="icon-remove"></i></a>

					<c:if test="${it.validFromError != null}">
						<span class="help-inline"><c:out
								value="${it.validFromError}" /></span>
					</c:if>
				</div>
			</div>

			<%-- Number of activations field --%>
			<div
				class="control-group <c:if test="${it.maxActivationsError != null}">error</c:if>">
				<label class="control-label" for="numOfActivations">Max
					number of activations</label>
				<div class="controls">
					<input data-datepicker="datepicker" type="text"
						class="span5 input-xlarge" value="${it.maxActivations}"
						id="numOfActivations" name="numOfActivations" />
					<c:if test="${it.maxActivationsError != null}">
						<span class="help-inline"><c:out
								value="${it.maxActivationsError}" /></span>
					</c:if>
				</div>
			</div>

			<%-- Active checkbox --%>
			<div class="control-group">
				<label class="control-label" for="active">Active</label>
				<div class="controls">
					<label class="checkbox"> <input type="checkbox"
						name="active" id="active"
						<c:if test="${it.active}">checked="yes"</c:if>>
					</label>
				</div>
			</div>

			<%-- Allow new activations checkbox --%>
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


			<div class="well">
				<h3>Features</h3>
				<br /> Add feature: <select name="feature" id="feature-select">
					<c:forEach items="${it.featureComboItems}" var="comboItem">
						<option value="<c:out value="${comboItem.code}" />">
							<c:out value="${comboItem.code}" /> - <c:out value="${comboItem.description}" />
						</option>
					</c:forEach>
				</select> From:
				<div class="input-append date" id="feature-from"
					data-date="${it.now}" data-date-format="dd-mm-yyyy">
					<input style="width: 70px" type="text" id="feature-valid-from"
						readonly> <a class="add-on"><i class="icon-th"></i></a>
				</div>

				To:
				<div class="input-append date" id="feature-to" data-date="${it.now}"
					data-date-format="dd-mm-yyyy">
					<input style="width: 70px" type="text" id="feature-valid-to"
						readonly> <a class="add-on"><i class="icon-th"></i></a>
				</div>

				<div class="btn-group pull-right" style="margin-bottom: 10px;">

					<a class="btn btn-success" onclick="addFeature()"><i
						class="icon-plus icon-white"></i> Add feature</a> <a class="btn"
						onclick="resetFeatureForm()"><i class="icon-remove"></i> Reset</a>
				</div>
				<table class="table">
					<thead>
						<tr>
							<th>Code</th>
							<th>Description</th>
							<th>Valid From</th>
							<th>Valid To</th>
							<th>
						</tr>
					</thead>
					<tbody id="tbl-features-body">
						<c:forEach items="${it.assignedFeatures}" var="feature">
							<tr id="row-${feature.code}">
								<td>${feature.code}</td><td><c:out value="${feature.description}" /></td>
								<td>${feature.validFrom}</td><td>${feature.validTo}</td>
								<td><a class="btn btn-danger pull-right" onclick="deleteRow('${feature.code}')"><i class="icon-remove icon-white"></i> Delete</a></td>
								<input type="hidden" name="featureId[]" value="${feature.code}">
								<input type="hidden" name="featureValidFrom[]" value="${feature.validFrom}">
								<input type="hidden" name="featureValidTo[]" value="${feature.validTo}">
							</tr>
						</c:forEach>
					</tbody>
				</table>
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

	<script type="text/javascript">
		function onLoad() {
			$('#valid-from-wrapper').datepicker();
			$('#valid-to-wrapper').datepicker();
			$('#feature-from').datepicker();
			$('#feature-to').datepicker();
		}
		function clearInput(id) {
			document.getElementById(id).value = '';
		}
		function addFeature() {
			var combo = document.getElementById('feature-select');
			var tableBody = document.getElementById('tbl-features-body');

			var featureId = combo.options[combo.selectedIndex].value;
			var featureName = combo.options[combo.selectedIndex].text;
			var dateFrom = document.getElementById('feature-valid-from').value;
			var dateTo = document.getElementById('feature-valid-to').value;

			var newRow = document.createElement('tr');
			newRow.id = 'row-' + featureId;
			newRow.innerHTML = '<td>'
					+ featureId
					+ '</td><td>'
					+ featureName
					+ '</td><td>'
					+ dateFrom
					+ '</td><td>'
					+ dateTo
					+ '<td><a class="btn btn-danger pull-right" onclick="deleteRow(\''
					+ featureId
					+ '\')"><i class="icon-remove icon-white"></i> Delete</a></td><input type="hidden" name="featureId[]" value="'+featureId+'"><input type="hidden" name="featureValidFrom[]" value="'+dateFrom+'"><input type="hidden" name="featureValidTo[]" value="'+dateTo+'">';

			tableBody.appendChild(newRow)
		}
		function resetFeatureForm() {
			clearInput('feature-valid-from');
			clearInput('feature-valid-to');
			clearInput('feature-select');
		}
		function deleteRow(id) {
			var tableBody = document.getElementById('tbl-features-body');
			var row = document.getElementById('row-' + id);

			tableBody.removeChild(row);
		}
	</script>
</web:layout>