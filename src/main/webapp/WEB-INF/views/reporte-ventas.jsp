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
			<form class="card-panel p-3 mb-3 no-print" method="get" action="${ctx}/reportes/ventas">
				<div class="row g-2 align-items-end">
					<div class="col-6 col-md-2"><label class="form-label">Desde</label><input type="date" class="form-control form-control-dark" name="desde" value="${filtro.desde}" required></div>
					<div class="col-6 col-md-2"><label class="form-label">Hasta</label><input type="date" class="form-control form-control-dark" name="hasta" value="${filtro.hasta}" required></div>
					<div class="col-6 col-md-2">
						<label class="form-label">Estado</label>
						<select class="form-select form-select-dark" name="estado">
							<option value="">Todas</option>
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
					<div class="col-12 col-md-2">
						<label class="form-label">Vendedor</label>
						<select class="form-select form-select-dark" name="vendedor">
							<option value="">Todos</option>
							<c:forEach var="u" items="${vendedores}">
								<option value="${u.id}" ${u.id == filtro.usuarioId ? 'selected' : ''}><c:out value="${u.nombreCompleto}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="col-12 col-md-2 d-flex gap-2 justify-content-end">
						<button class="btn btn-outline-light" title="Generar"><i class="bi bi-arrow-repeat"></i> Generar</button>
						<button type="button" class="btn btn-accent" onclick="window.print()" title="Imprimir"><i class="bi bi-printer"></i> Imprimir</button>
					</div>
				</div>
			</form>

			<c:set var="r" value="${reporte.resumen}" />

			<%-- Hoja del reporte --%>
			<div class="hoja-reporte">
				<%@ include file="layout/reporte-encabezado.jspf" %>

				<c:if test="${reporte.truncado}">
					<div class="reporte-aviso">
						La lista muestra las primeras ${maximoFilas} ventas de ${r.registros}. Los totales sí incluyen todas.
						Reduce el rango de fechas para ver el detalle completo.
					</div>
				</c:if>

				<c:choose>
					<c:when test="${empty reporte.ventas}">
						<div class="reporte-vacio">No hay registros para los filtros seleccionados.</div>
					</c:when>
					<c:otherwise>
						<table class="tabla-reporte">
							<thead>
								<tr>
									<th class="centro">N°</th><th>Fecha</th><th>Comprobante</th><th>Cliente</th><th>Vendedor</th>
									<th class="centro">Prendas</th><th class="num">Subtotal</th><th class="num">IGV</th><th class="num">Total</th><th>Estado</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="v" items="${reporte.ventas}" varStatus="st">
									<tr class="${v.anulada ? 'anulada' : ''}">
										<td class="centro">${st.count}</td>
										<td style="white-space: nowrap">${v.fechaTexto}</td>
										<td style="white-space: nowrap">${empty v.comprobante ? '—' : v.comprobante.numeroCompleto}</td>
										<td><c:out value="${empty v.cliente ? 'Cliente varios' : v.cliente.nombreCompleto}" /></td>
										<td><c:out value="${v.usuario.nombreCompleto}" /></td>
										<td class="centro">${empty reporte.prendas[v.id] ? 0 : reporte.prendas[v.id]}</td>
										<td class="num"><fmt:formatNumber value="${v.subtotal}" pattern="#,##0.00" /></td>
										<td class="num"><fmt:formatNumber value="${v.igv}" pattern="#,##0.00" /></td>
										<td class="num"><fmt:formatNumber value="${v.total}" pattern="#,##0.00" /></td>
										<td class="estado">${v.estado.etiqueta}</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="5">TOTAL VENTAS COMPLETADAS (${r.completadas})</td>
									<td class="centro">${r.prendas}</td>
									<td class="num"><fmt:formatNumber value="${r.subtotal}" pattern="#,##0.00" /></td>
									<td class="num"><fmt:formatNumber value="${r.igv}" pattern="#,##0.00" /></td>
									<td class="num"><fmt:formatNumber value="${r.total}" pattern="#,##0.00" /></td>
									<td></td>
								</tr>
							</tfoot>
						</table>

						<div class="reporte-resumen">
							<div class="bloque">
								<h6>Resumen</h6>
								<table>
									<tr><td>Ventas completadas</td><td class="num">${r.completadas}</td></tr>
									<tr><td>Ventas anuladas</td><td class="num">${r.anuladas}</td></tr>
									<tr><td>Prendas vendidas</td><td class="num">${r.prendas}</td></tr>
									<tr><td>Op. gravada</td><td class="num">S/ <fmt:formatNumber value="${r.subtotal}" pattern="#,##0.00" /></td></tr>
									<tr><td>IGV</td><td class="num">S/ <fmt:formatNumber value="${r.igv}" pattern="#,##0.00" /></td></tr>
									<tr class="total"><td>Total vendido</td><td class="num">S/ <fmt:formatNumber value="${r.total}" pattern="#,##0.00" /></td></tr>
								</table>
							</div>
							<div class="bloque">
								<h6>Total por vendedor (ventas completadas)</h6>
								<table>
									<c:forEach var="tv" items="${r.porVendedor}">
										<tr><td><c:out value="${tv.vendedor}" /> (${tv.ventas})</td><td class="num">S/ <fmt:formatNumber value="${tv.total}" pattern="#,##0.00" /></td></tr>
									</c:forEach>
									<c:if test="${empty r.porVendedor}"><tr><td colspan="2">Sin ventas completadas.</td></tr></c:if>
								</table>
							</div>
						</div>
					</c:otherwise>
				</c:choose>

				<div class="reporte-pie">UrbanStyle · Sistema de ventas · Las ventas anuladas se listan pero no suman en los totales</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
</body>
</html>
