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

			<ul class="nav nav-pills mb-3 gap-1">
				<c:forEach var="t" items="${tipos}">
					<li class="nav-item">
						<a class="nav-link ${t == tipo ? 'active bg-danger' : 'text-light'}" href="${ctx}/catalogos?tipo=${t.clave}">${t.titulo}</a>
					</li>
				</c:forEach>
			</ul>

			<div class="row g-3">
				<div class="col-lg-4">
					<form class="card-panel p-3" method="post" action="${ctx}/catalogos/guardar" id="formCatalogo">
						<h6 class="fw-semibold mb-3" id="tituloForm">Nuevo registro</h6>
						<input type="hidden" name="tipo" value="${tipo.clave}">
						<input type="hidden" name="id" id="catId">
						<div class="mb-3">
							<label class="form-label">Nombre *</label>
							<input class="form-control form-control-dark" name="nombre" id="catNombre" maxlength="${tipo.largoMaximo}" required>
						</div>
						<c:if test="${tipo == 'CATEGORIAS'}">
							<div class="mb-3">
								<label class="form-label">Descripción</label>
								<input class="form-control form-control-dark" name="descripcion" id="catDescripcion" maxlength="200">
							</div>
						</c:if>
						<div class="d-flex gap-2">
							<button class="btn btn-accent flex-fill" type="submit"><i class="bi bi-save me-1"></i> Guardar</button>
							<button class="btn btn-outline-light" type="button" id="btnLimpiar">Nuevo</button>
						</div>
					</form>
				</div>
				<div class="col-lg-8">
					<div class="card-panel">
						<div class="table-responsive">
							<table class="table table-dark-custom table-hover">
								<thead><tr><th>ID</th><th>Nombre</th><c:if test="${tipo == 'CATEGORIAS'}"><th>Descripción</th></c:if><th class="text-center" title="Cantidad de ${tipo.unidadUso} que usan cada registro">En uso</th><th class="text-end">Acciones</th></tr></thead>
								<tbody>
									<c:forEach var="r" items="${registros}">
										<c:set var="enUso" value="${empty usos[r.id] ? 0 : usos[r.id]}" />
										<tr>
											<td>${r.id}</td>
											<td class="fw-semibold"><c:out value="${r.nombre}" /></td>
											<c:if test="${tipo == 'CATEGORIAS'}"><td class="text-muted-2"><c:out value="${r.descripcion}" /></td></c:if>
											<td class="text-center">
												<span class="badge ${enUso > 0 ? 'bg-info text-dark' : 'bg-secondary'}" title="${enUso} ${tipo.unidadUso}">${enUso}</span>
											</td>
											<td class="text-end text-nowrap">
												<button type="button" class="btn btn-sm btn-outline-light btn-editar"
													data-id="${r.id}" data-nombre="<c:out value='${r.nombre}' />"
													<c:if test="${tipo == 'CATEGORIAS'}">data-descripcion="<c:out value='${r.descripcion}' />"</c:if>>
													<i class="bi bi-pencil"></i>
												</button>
												<form class="d-inline" method="post" action="${ctx}/catalogos/eliminar" data-confirmar="Se eliminará este registro. Solo es posible si ningún producto lo usa.">
													<input type="hidden" name="tipo" value="${tipo.clave}">
													<input type="hidden" name="id" value="${r.id}">
													<button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
												</form>
											</td>
										</tr>
									</c:forEach>
									<c:if test="${empty registros}"><tr><td colspan="5" class="text-center text-muted-2 py-4">Sin registros.</td></tr></c:if>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
<script>
	document.querySelectorAll('.btn-editar').forEach(function (b) {
		b.addEventListener('click', function () {
			document.getElementById('tituloForm').textContent = 'Editar registro #' + b.dataset.id;
			document.getElementById('catId').value = b.dataset.id;
			document.getElementById('catNombre').value = b.dataset.nombre;
			const d = document.getElementById('catDescripcion');
			if (d) d.value = b.dataset.descripcion || '';
			document.getElementById('catNombre').focus();
		});
	});
	document.getElementById('btnLimpiar').addEventListener('click', function () {
		document.getElementById('formCatalogo').reset();
		document.getElementById('catId').value = '';
		document.getElementById('tituloForm').textContent = 'Nuevo registro';
	});
</script>
</body>
</html>
