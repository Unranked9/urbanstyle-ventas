<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="layout/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
<%@ include file="layout/head.jspf" %>
<link href="${ctx}/assets/css/reporte.css" rel="stylesheet">
</head>
<body>
<div class="layout-wrapper">
	<%@ include file="layout/sidebar.jspf" %>
	<div class="main-content">
		<%@ include file="layout/topbar.jspf" %>
		<div class="page-body">
			<%@ include file="layout/flash.jspf" %>

			<%-- Filtros (no se imprimen) --%>
			<form class="card-panel p-3 mb-3 no-print" method="get" action="${ctx}/reportes/clientes">
				<div class="row g-2 align-items-end">
					<div class="col-12 col-md-4">
						<label class="form-label">Buscar</label>
						<input class="form-control form-control-dark" name="q" value="<c:out value='${fq}' />" placeholder="Nombre, DNI o correo">
					</div>
					<div class="col-6 col-md-2">
						<label class="form-label">Estado</label>
						<select class="form-select form-select-dark" name="estado">
							<option value="activos" ${fEstado == 'activos' ? 'selected' : ''}>Activos</option>
							<option value="inactivos" ${fEstado == 'inactivos' ? 'selected' : ''}>Inactivos</option>
							<option value="todos" ${fEstado == 'todos' ? 'selected' : ''}>Todos</option>
						</select>
					</div>
					<div class="col-6 col-md-2">
						<label class="form-label">Tipo</label>
						<select class="form-select form-select-dark" name="tipo">
							<option value="todos" ${fTipo == 'todos' ? 'selected' : ''}>Todos</option>
							<option value="mayoristas" ${fTipo == 'mayoristas' ? 'selected' : ''}>Mayoristas</option>
							<option value="minoristas" ${fTipo == 'minoristas' ? 'selected' : ''}>Minoristas</option>
						</select>
					</div>
					<div class="col-12 col-md-4 d-flex gap-2 justify-content-end">
						<a class="btn btn-outline-light" href="${ctx}/reportes/clientes">Limpiar</a>
						<button class="btn btn-outline-light"><i class="bi bi-arrow-repeat me-1"></i> Generar</button>
						<button type="button" class="btn btn-accent" onclick="window.print()"><i class="bi bi-printer me-1"></i> Imprimir</button>
					</div>
				</div>
			</form>

			<%-- Hoja del reporte --%>
			<div class="hoja-reporte">
				<%@ include file="layout/reporte-encabezado.jspf" %>

				<c:choose>
					<c:when test="${empty reporte.filas}">
						<div class="reporte-vacio">No hay registros para los filtros seleccionados.</div>
					</c:when>
					<c:otherwise>
						<table class="tabla-reporte">
							<thead>
								<tr>
									<th class="centro">N°</th><th>Cliente</th><th>DNI</th><th>Teléfono</th><th>Correo</th>
									<th>Tipo</th><th>Registro</th><th>Estado</th>
									<th class="num">N° compras</th><th class="num">Total comprado (S/)</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="f" items="${reporte.filas}" varStatus="st">
									<c:set var="c" value="${f.cliente}" />
									<tr>
										<td class="centro">${st.count}</td>
										<td><c:out value="${c.nombreCompleto}" /></td>
										<td><c:out value="${empty c.dni ? '—' : c.dni}" /></td>
										<td><c:out value="${empty c.telefono ? '—' : c.telefono}" /></td>
										<td><c:out value="${c.email}" /></td>
										<td>${c.mayorista ? 'Mayorista' : 'Minorista'}</td>
										<td>${c.fechaRegistroTexto}</td>
										<td>${c.activo ? 'Activo' : 'Inactivo'}</td>
										<td class="num">${f.compras}</td>
										<td class="num"><fmt:formatNumber value="${f.totalComprado}" pattern="#,##0.00" /></td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="8">TOTAL</td>
									<td class="num">${reporte.compras}</td>
									<td class="num"><fmt:formatNumber value="${reporte.totalComprado}" pattern="#,##0.00" /></td>
								</tr>
							</tfoot>
						</table>

						<div class="reporte-resumen">
							<div class="bloque">
								<h6>Resumen</h6>
								<table>
									<tr><td>Clientes mayoristas</td><td class="num">${reporte.mayoristas}</td></tr>
									<tr><td>Clientes minoristas</td><td class="num">${reporte.minoristas}</td></tr>
									<tr class="total"><td>Total de clientes</td><td class="num">${reporte.totalClientes}</td></tr>
								</table>
							</div>
							<div class="bloque">
								<h6>Compras (solo ventas completadas)</h6>
								<table>
									<tr><td>Cantidad de compras</td><td class="num">${reporte.compras}</td></tr>
									<tr class="total"><td>Monto total comprado</td><td class="num">S/ <fmt:formatNumber value="${reporte.totalComprado}" pattern="#,##0.00" /></td></tr>
								</table>
							</div>
						</div>
					</c:otherwise>
				</c:choose>

				<div class="reporte-pie">UrbanStyle · Sistema de ventas · Reporte generado automáticamente</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
</body>
</html>
