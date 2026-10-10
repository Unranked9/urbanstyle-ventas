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
			<c:set var="v" value="${venta}" />

			<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
				<a href="${ctx}/historial" class="btn btn-outline-light"><i class="bi bi-arrow-left"></i> Historial</a>
				<div class="d-flex gap-2">
					<a class="btn btn-outline-light" href="${ctx}/ventas/comprobante?id=${v.id}" target="_blank"><i class="bi bi-printer me-1"></i> Comprobante</a>
					<c:if test="${us.gestor and not v.anulada}">
						<form method="post" action="${ctx}/ventas/anular" data-confirmar="La venta quedará anulada y el stock vendido volverá al inventario. Esta acción no se puede deshacer.">
							<input type="hidden" name="id" value="${v.id}">
							<button class="btn btn-outline-danger"><i class="bi bi-x-octagon me-1"></i> Anular venta</button>
						</form>
					</c:if>
				</div>
			</div>

			<div class="row g-3">
				<div class="col-lg-4">
					<div class="card-panel p-3">
						<div class="d-flex justify-content-between align-items-start mb-2">
							<div>
								<div class="text-muted-2 small">${v.comprobante.tipo.etiqueta}</div>
								<h4 class="mb-0">${v.comprobante.numeroCompleto}</h4>
							</div>
							<span class="badge ${v.anulada ? 'bg-danger' : 'badge-stock-ok'}">${v.estado.etiqueta}</span>
						</div>
						<hr style="border-color: var(--border)">
						<div class="small">
							<div class="d-flex justify-content-between py-1"><span class="text-muted-2">Fecha</span><span>${v.fechaTexto}</span></div>
							<div class="d-flex justify-content-between py-1"><span class="text-muted-2">Vendedor</span><span><c:out value="${v.usuario.nombreCompleto}" /></span></div>
							<div class="d-flex justify-content-between py-1"><span class="text-muted-2">Cliente</span><span><c:out value="${empty v.cliente ? 'Cliente varios' : v.cliente.nombreCompleto}" /></span></div>
							<c:if test="${not empty v.comprobante.dniCliente}">
								<div class="d-flex justify-content-between py-1"><span class="text-muted-2">DNI</span><span>${v.comprobante.dniCliente}</span></div>
							</c:if>
							<c:if test="${not empty v.comprobante.rucCliente}">
								<div class="d-flex justify-content-between py-1"><span class="text-muted-2">RUC</span><span>${v.comprobante.rucCliente}</span></div>
							</c:if>
						</div>
						<hr style="border-color: var(--border)">
						<div class="totales">
							<div class="fila"><span class="text-muted-2">Subtotal</span><span>S/ <fmt:formatNumber value="${v.subtotal}" pattern="#,##0.00" /></span></div>
							<div class="fila"><span class="text-muted-2">IGV</span><span>S/ <fmt:formatNumber value="${v.igv}" pattern="#,##0.00" /></span></div>
							<div class="fila total"><span>Total</span><span>S/ <fmt:formatNumber value="${v.total}" pattern="#,##0.00" /></span></div>
						</div>
					</div>
				</div>
				<div class="col-lg-8">
					<div class="card-panel">
						<div class="table-responsive">
							<table class="table table-dark-custom">
								<thead><tr><th>Producto</th><th>Variante</th><th class="text-center">Cant.</th><th class="text-end">P. unit.</th><th class="text-end">Importe</th></tr></thead>
								<tbody>
									<c:forEach var="d" items="${v.detalles}">
										<tr>
											<td>
												<div class="d-flex align-items-center gap-2">
													<c:choose>
														<c:when test="${not empty d.variante.imagenUrl}"><img class="thumb sm" src="${ctx}/${d.variante.imagenUrl}" alt=""></c:when>
														<c:otherwise><span class="thumb sm"><i class="bi bi-image"></i></span></c:otherwise>
													</c:choose>
													<c:out value="${d.variante.producto.nombre}" />
												</div>
											</td>
											<td><c:out value="${d.variante.descripcionCorta}" /></td>
											<td class="text-center">${d.cantidad}</td>
											<td class="text-end">S/ <fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0.00" /></td>
											<td class="text-end fw-semibold">S/ <fmt:formatNumber value="${d.totalLinea}" pattern="#,##0.00" /></td>
										</tr>
									</c:forEach>
								</tbody>
								<tfoot><tr><td colspan="2" class="text-muted-2">${v.totalPrendas} prenda(s)</td><td colspan="3"></td></tr></tfoot>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
</body>
</html>
