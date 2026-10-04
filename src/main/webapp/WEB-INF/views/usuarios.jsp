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

			<div class="d-flex justify-content-between align-items-center mb-3">
				<p class="text-muted-2 mb-0 small">
					<b>Administrador</b>: acceso total · <b>Sub administrador</b>: productos, catálogos y anulaciones · <b>Vendedor</b>: ventas y clientes.
				</p>
				<button class="btn btn-accent text-nowrap" data-bs-toggle="modal" data-bs-target="#modalUsuario" data-modo="nuevo"><i class="bi bi-person-plus me-1"></i> Nuevo usuario</button>
			</div>

			<div class="card-panel">
				<div class="table-responsive">
					<table class="table table-dark-custom table-hover">
						<thead><tr><th>Usuario</th><th>Nombre</th><th>DNI</th><th>Teléfono</th><th>Rol</th><th>Registro</th><th>Estado</th><th class="text-end">Acciones</th></tr></thead>
						<tbody>
							<c:forEach var="u" items="${usuarios}">
								<tr class="${u.activo ? '' : 'fila-inactiva'}">
									<td class="fw-semibold"><c:out value="${u.username}" /><c:if test="${u.id == us.id}"> <span class="badge bg-info text-dark">Tú</span></c:if></td>
									<td><c:out value="${u.nombreCompleto}" /></td>
									<td>${u.dni}</td>
									<td><c:out value="${u.telefono}" /></td>
									<td><span class="badge ${u.rol.nombre == 'ADMIN' ? 'badge-rol' : u.rol.nombre == 'SUB_ADMIN' ? 'bg-warning text-dark' : 'bg-secondary'}">${u.rol.etiqueta}</span></td>
									<td class="text-nowrap">${u.fechaRegistroTexto}</td>
									<td><span class="badge ${u.activo ? 'badge-stock-ok' : 'bg-secondary'}">${u.activo ? 'Activo' : 'Inactivo'}</span></td>
									<td class="text-end text-nowrap">
										<button class="btn btn-sm btn-outline-light" data-bs-toggle="modal" data-bs-target="#modalUsuario" data-modo="editar"
											data-id="${u.id}" data-rol="${u.rol.id}" data-nombres="<c:out value='${u.nombres}' />" data-apellidos="<c:out value='${u.apellidos}' />"
											data-dni="${u.dni}" data-telefono="<c:out value='${u.telefono}' />" data-username="<c:out value='${u.username}' />">
											<i class="bi bi-pencil"></i></button>
										<c:if test="${u.id != us.id}">
											<form class="d-inline" method="post" action="${ctx}/usuarios/estado"
												data-confirmar="${u.activo ? 'El usuario no podrá iniciar sesión.' : '¿Activar nuevamente a este usuario?'}">
												<input type="hidden" name="id" value="${u.id}">
												<input type="hidden" name="activo" value="${u.activo ? 0 : 1}">
												<button class="btn btn-sm ${u.activo ? 'btn-outline-danger' : 'btn-outline-success'}"><i class="bi ${u.activo ? 'bi-person-lock' : 'bi-person-check'}"></i></button>
											</form>
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade modal-dark" id="modalUsuario" tabindex="-1" aria-hidden="true">
	<div class="modal-dialog">
		<form class="modal-content" method="post" action="${ctx}/usuarios/guardar">
			<div class="modal-header">
				<h5 class="modal-title" id="tituloModalUsuario">Nuevo usuario</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
			</div>
			<div class="modal-body">
				<input type="hidden" name="id" id="usrId">
				<div class="row g-2">
					<div class="col-6"><label class="form-label">Nombres *</label><input class="form-control form-control-dark" name="nombres" id="usrNombres" maxlength="60" required></div>
					<div class="col-6"><label class="form-label">Apellidos *</label><input class="form-control form-control-dark" name="apellidos" id="usrApellidos" maxlength="60" required></div>
					<div class="col-6"><label class="form-label">DNI *</label><input class="form-control form-control-dark" name="dni" id="usrDni" maxlength="8" pattern="\d{8}" inputmode="numeric" required></div>
					<div class="col-6"><label class="form-label">Teléfono</label><input class="form-control form-control-dark" name="telefono" id="usrTelefono" maxlength="15"></div>
					<div class="col-6"><label class="form-label">Usuario *</label><input class="form-control form-control-dark" name="username" id="usrUsername" maxlength="50" required></div>
					<div class="col-6">
						<label class="form-label">Rol *</label>
						<select class="form-select form-select-dark" name="rolId" id="usrRol" required>
							<c:forEach var="r" items="${roles}"><option value="${r.id}">${r.etiqueta}</option></c:forEach>
						</select>
					</div>
					<div class="col-12"><label class="form-label" id="lblPassword">Contraseña *</label><input type="password" class="form-control form-control-dark" name="password" id="usrPassword" maxlength="72" minlength="6"></div>
				</div>
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
	document.getElementById('modalUsuario').addEventListener('show.bs.modal', function (e) {
		const b = e.relatedTarget;
		const editar = b && b.dataset.modo === 'editar';
		const d = editar ? b.dataset : {};
		document.getElementById('tituloModalUsuario').textContent = editar ? 'Editar usuario' : 'Nuevo usuario';
		document.getElementById('usrId').value = d.id || '';
		document.getElementById('usrNombres').value = d.nombres || '';
		document.getElementById('usrApellidos').value = d.apellidos || '';
		document.getElementById('usrDni').value = d.dni || '';
		document.getElementById('usrTelefono').value = d.telefono || '';
		document.getElementById('usrUsername').value = d.username || '';
		const rol = document.getElementById('usrRol');
		if (d.rol) rol.value = d.rol; else rol.selectedIndex = rol.options.length - 1;
		const pass = document.getElementById('usrPassword');
		pass.value = '';
		pass.required = !editar;
		pass.placeholder = editar ? 'Dejar vacío para no cambiar' : 'Mínimo 6 caracteres';
		document.getElementById('lblPassword').textContent = editar ? 'Nueva contraseña' : 'Contraseña *';
	});
</script>
</body>
</html>
