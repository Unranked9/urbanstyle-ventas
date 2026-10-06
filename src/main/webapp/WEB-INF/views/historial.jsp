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

			<form class="card-panel p-3 mb-3" method="get" action="${ctx}/historial">
				<div class="row g-2 align-items-end">
					<div class="col-6 col-md-2"><label class="form-label">Desde</label><input type="date" class="form-control form-control-dark" name="desde" value="${filtro.desde}"></div>
					<div class="col-6 col-md-2"><label class="form-label">Hasta</label><input type="date" class="form-control form-control-dark" name="hasta" value="${filtro.hasta}"></div>
					<div class="col-6 col-md-2">
						<label class="form-label">Estado</label>
						<select class="form-select form-select-dark" name="estado">
							<option value="">Todos</option>
							<option value="completadas" ${fEstado == 'completadas' ? 'selected' : ''}>Completadas</option>
							<option value="anuladas" ${fEstado == 'anuladas' ? 'selected' : ''}>Anuladas</option>
						</select>
					</div>
					<div class="col-6 col-md-2">
						<label class="form-label">Comprobante</label>
						<select class="form-select form-select-dark" name="tipo">
							<option value="">Todos</option>
							<option value="BOLETA" ${fTipo == 'BOLETA' ? 'selected' : ''}>Boletas</option>
							<option value="FACTURA" ${fTipo == 'FACTURA' ? 'selected' : ''}>Facturas</option>
						</select>
					</div>
					<div class="col-6 col-md-2">
						<label class="form-label">Vendedor</label>
						<select class="form-select form-select-dark" name="vendedor">
							<option value="">Todos</option>
							<c:forEach var="u" items="${vendedores}">
								<option value="${u.id}" ${u.id == filtro.usuarioId ? 'selected' : ''}><c:out value="${u.nombreCompleto}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="col-6 col-md-2"><label class="form-label">Buscar</label><input class="form-control form-control-dark" name="q" value="<c:out value='${filtro.texto}' />" placeholder="Cliente, DNI, N°"></div>
					<div class="col-12 d-flex justify-content-end gap-2">
						<c:url var="urlCsv" value="/historial/csv">
							<c:param name="desde" value="${filtro.desde}" />
							<c:param name="hasta" value="${filtro.hasta}" />
							<c:param name="estado" value="${fEstado}" />
							<c:param name="tipo" value="${fTipo}" />
							<c:param name="vendedor" value="${filtro.usuarioId}" />
							<c:param name="q" value="${filtro.texto}" />
						</c:url>
						<a class="btn btn-outline-light" href="${urlCsv}"><i class="bi bi-filetype-csv me-1"></i> Exportar CSV</a>
						<a class="btn btn-outline-light" href="${ctx}/historial">Limpiar</a>
						<button class="btn btn-accent"><i class="bi bi-funnel me-1"></i> Filtrar</button>
					</div>
				</div>
			</form>

			<div class="row g-3 mb-3">
				<div class="col-6 col-md-4"><div class="stat-card"><div class="stat-label">Ventas completadas</div><div class="stat-value">${cantidadCompletadas}</div></div></div>
				<div class="col-6 col-md-4"><div class="stat-card" style="border-left-color: var(--ok)"><div class="stat-label">Monto vendido</div><div class="stat-value">S/ <fmt:formatNumber value="${totalCompletadas}" pattern="#,##0.00" /></div></div></div>
				<div class="col-12 col-md-4"><div class="stat-card" style="border-left-color: #6c757d"><div class="stat-label">Registros listados</div><div class="stat-value">${fn:length(ventas)}</div></div></div>
			</div>

			<div class="card-panel">
				<div class="table-responsive">
					<table class="table table-dark-custom table-hover">
						<thead><tr><th>#</th><th>Fecha</th><th>Comprobante</th><th>Cliente</th><th>Vendedor</th><th class="text-center">Prendas</th><th class="text-end">Total</th><th>Estado</th><th></th></tr></thead>
						<tbody>
							<c:forEach var="v" items="${ventas}">
								<tr class="${v.anulada ? 'fila-inactiva' : ''}">
									<td>${v.id}</td>
									<td class="text-nowrap">${v.fechaTexto}</td>
									<td class="text-nowrap">
										<span class="badge ${v.comprobante.tipo == 'FACTURA' ? 'bg-info text-dark' : 'bg-secondary'} me-1">${v.comprobante.tipo == 'FACTURA' ? 'F' : 'B'}</span>${v.comprobante.numeroCompleto}
									</td>
									<td><c:out value="${empty v.cliente ? 'Cliente varios' : v.cliente.nombreCompleto}" /></td>
									<td><c:out value="${v.usuario.nombreCompleto}" /></td>
									<td class="text-center">${prendas[v.id]}</td>
									<td class="text-end text-nowrap fw-semibold">S/ <fmt:formatNumber value="${v.total}" pattern="#,##0.00" /></td>
									<td><span class="badge ${v.anulada ? 'bg-danger' : 'badge-stock-ok'}">${v.estado.etiqueta}</span></td>
									<td class="text-end text-nowrap">
										<a class="btn btn-sm btn-outline-light" href="${ctx}/historial/detalle?id=${v.id}" title="Detalle"><i class="bi bi-eye"></i></a>
										<a class="btn btn-sm btn-outline-light" href="${ctx}/ventas/comprobante?id=${v.id}" target="_blank" title="Comprobante"><i class="bi bi-printer"></i></a>
									</td>
								</tr>
							</c:forEach>
							<c:if test="${empty ventas}"><tr><td colspan="9" class="text-center text-muted-2 py-5">No hay ventas con estos filtros.</td></tr></c:if>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
</body>
</html>
