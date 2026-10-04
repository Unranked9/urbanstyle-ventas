<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="layout/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head><%@ include file="layout/head.jspf" %></head>
<body>
<div class="layout-wrapper">
	<%@ include file="layout/sidebar.jspf" %>
	<div class="main-content">
		<%@ include file="layout/topbar.jspf" %>
		<div class="page-body">
			<%@ include file="layout/flash.jspf" %>

			<form class="card-panel p-3 mb-3" method="get" action="${ctx}/clientes">
				<div class="row g-2 align-items-end">
					<div class="col-md-6">
						<label class="form-label">Buscar</label>
						<input class="form-control form-control-dark" name="q" value="<c:out value='${fq}' />" placeholder="Nombre, DNI, correo o teléfono">
					</div>
					<div class="col-6 col-md-3">
						<label class="form-label">Estado</label>
						<select class="form-select form-select-dark" name="estado">
							<option value="activos" ${fEstado == 'activos' ? 'selected' : ''}>Activos</option>
							<option value="inactivos" ${fEstado == 'inactivos' ? 'selected' : ''}>Inactivos</option>
							<option value="todos" ${fEstado == 'todos' ? 'selected' : ''}>Todos</option>
						</select>
					</div>
					<div class="col-6 col-md-3 d-flex gap-2">
						<button class="btn btn-outline-light flex-fill"><i class="bi bi-funnel"></i> Filtrar</button>
						<button type="button" class="btn btn-accent flex-fill" data-bs-toggle="modal" data-bs-target="#modalCliente" data-modo="nuevo"><i class="bi bi-person-plus"></i> Nuevo</button>
					</div>
				</div>
			</form>

			<div class="card-panel">
				<div class="table-responsive">
					<table class="table table-dark-custom table-hover">
						<thead><tr><th>Cliente</th><th>DNI</th><th>Contacto</th><th>Usuario</th><th>Tipo</th><th>Registro</th><th>Estado</th><th class="text-end">Acciones</th></tr></thead>
						<tbody>
							<c:forEach var="c" items="${clientes}">
								<tr class="${c.activo ? '' : 'fila-inactiva'}">
									<td class="fw-semibold"><c:out value="${c.nombreCompleto}" /></td>
									<td><c:out value="${empty c.dni ? '—' : c.dni}" /></td>
									<td><div><c:out value="${c.email}" /></div><small class="text-muted-2"><c:out value="${c.telefono}" /></small></td>
									<td class="text-muted-2"><c:out value="${c.username}" /></td>
									<td><span class="badge ${c.mayorista ? 'bg-info text-dark' : 'bg-secondary'}">${c.mayorista ? 'Mayorista' : 'Minorista'}</span></td>
									<td class="text-nowrap">${c.fechaRegistroTexto}</td>
									<td><span class="badge ${c.activo ? 'badge-stock-ok' : 'bg-secondary'}">${c.activo ? 'Activo' : 'Inactivo'}</span></td>
									<td class="text-end text-nowrap">
										<button type="button" class="btn btn-sm btn-outline-light" data-bs-toggle="modal" data-bs-target="#modalCliente" data-modo="editar"
											data-id="${c.id}" data-nombres="<c:out value='${c.nombres}' />" data-apellidos="<c:out value='${c.apellidos}' />"
											data-dni="<c:out value='${c.dni}' />" data-telefono="<c:out value='${c.telefono}' />"
											data-email="<c:out value='${c.email}' />" data-username="<c:out value='${c.username}' />"
											data-mayorista="${c.mayorista}"><i class="bi bi-pencil"></i></button>
										<form class="d-inline" method="post" action="${ctx}/clientes/estado"
											data-confirmar="${c.activo ? 'El cliente no aparecerá en la búsqueda de la caja.' : '¿Activar nuevamente a este cliente?'}">
											<input type="hidden" name="id" value="${c.id}">
											<input type="hidden" name="activo" value="${c.activo ? 0 : 1}">
											<button class="btn btn-sm ${c.activo ? 'btn-outline-danger' : 'btn-outline-success'}"><i class="bi ${c.activo ? 'bi-person-dash' : 'bi-person-check'}"></i></button>
										</form>
									</td>
								</tr>
							</c:forEach>
							<c:if test="${empty clientes}"><tr><td colspan="8" class="text-center text-muted-2 py-5">No se encontraron clientes.</td></tr></c:if>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade modal-dark" id="modalCliente" tabindex="-1" aria-hidden="true">
	<div class="modal-dialog">
		<form class="modal-content" method="post" action="${ctx}/clientes/guardar">
			<div class="modal-header">
				<h5 class="modal-title" id="tituloModalCliente">Nuevo cliente</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
			</div>
			<div class="modal-body">
				<input type="hidden" name="id" id="cliId">
				<div class="row g-2">
					<div class="col-6"><label class="form-label">Nombres *</label><input class="form-control form-control-dark" name="nombres" id="cliNombres" maxlength="60" required></div>
					<div class="col-6"><label class="form-label">Apellidos *</label><input class="form-control form-control-dark" name="apellidos" id="cliApellidos" maxlength="60" required></div>
					<div class="col-6"><label class="form-label">DNI</label><input class="form-control form-control-dark" name="dni" id="cliDni" maxlength="8" pattern="\d{8}" inputmode="numeric"></div>
					<div class="col-6"><label class="form-label">Teléfono</label><input class="form-control form-control-dark" name="telefono" id="cliTelefono" maxlength="15"></div>
					<div class="col-12"><label class="form-label">Correo *</label><input type="email" class="form-control form-control-dark" name="email" id="cliEmail" maxlength="50" required></div>
					<div class="col-6"><label class="form-label">Usuario (tienda web)</label><input class="form-control form-control-dark" name="username" id="cliUsername" maxlength="50" placeholder="Se genera del correo"></div>
					<div class="col-6"><label class="form-label">Contraseña</label><input type="password" class="form-control form-control-dark" name="password" id="cliPassword" maxlength="72" placeholder="Opcional"></div>
					<div class="col-12">
						<div class="form-check form-switch mt-1">
							<input class="form-check-input" type="checkbox" name="mayorista" id="cliMayorista">
							<label class="form-check-label" for="cliMayorista">Cliente mayorista</label>
						</div>
					</div>
				</div>
				<small class="text-muted-2 d-block mt-2">La BD exige usuario y contraseña para cada cliente. Si se dejan vacíos se generan automáticamente.</small>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-outline-light" data-bs-dismiss="modal">Cancelar</button>
				<button class="btn btn-accent"><i class="bi bi-save me-1"></i> Guardar</button>
			</div>
		</form>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
<script>
	document.getElementById('modalCliente').addEventListener('show.bs.modal', function (e) {
		const b = e.relatedTarget;
		const editar = b && b.dataset.modo === 'editar';
		const d = editar ? b.dataset : {};
		document.getElementById('tituloModalCliente').textContent = editar ? 'Editar cliente' : 'Nuevo cliente';
		document.getElementById('cliId').value = d.id || '';
		document.getElementById('cliNombres').value = d.nombres || '';
		document.getElementById('cliApellidos').value = d.apellidos || '';
		document.getElementById('cliDni').value = d.dni || '';
		document.getElementById('cliTelefono').value = d.telefono || '';
		document.getElementById('cliEmail').value = d.email || '';
		document.getElementById('cliUsername').value = d.username || '';
		document.getElementById('cliPassword').value = '';
		document.getElementById('cliPassword').placeholder = editar ? 'Dejar vacío para no cambiar' : 'Opcional';
		document.getElementById('cliMayorista').checked = d.mayorista === 'true';
	});
</script>
</body>
</html>
