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
			<c:set var="r" value="${resumen}" />

			<div class="row g-3 mb-4">
				<div class="col-6 col-lg-3">
					<div class="stat-card">
						<div class="d-flex justify-content-between">
							<div>
								<div class="stat-label">Ventas de hoy</div>
								<div class="stat-value">S/ <fmt:formatNumber value="${r.totalHoy}" pattern="#,##0.00" /></div>
								<small class="text-muted-2">${r.ventasHoy} venta(s)</small>
							</div>
							<i class="bi bi-cash-coin stat-icon" style="color: var(--accent)"></i>
						</div>
					</div>
				</div>
				<div class="col-6 col-lg-3">
					<div class="stat-card" style="border-left-color: #3498db">
						<div class="d-flex justify-content-between">
							<div>
								<div class="stat-label">Ventas del mes</div>
								<div class="stat-value">S/ <fmt:formatNumber value="${r.totalMes}" pattern="#,##0.00" /></div>
							</div>
							<i class="bi bi-calendar3 stat-icon" style="color: #3498db"></i>
						</div>
					</div>
				</div>
				<div class="col-6 col-lg-3">
					<div class="stat-card" style="border-left-color: var(--ok)">
						<div class="d-flex justify-content-between">
							<div>
								<div class="stat-label">Productos activos</div>
								<div class="stat-value">${r.productosActivos}</div>
								<small class="text-muted-2">${r.clientesActivos} clientes activos</small>
							</div>
							<i class="bi bi-box-seam stat-icon" style="color: var(--ok)"></i>
						</div>
					</div>
				</div>
				<div class="col-6 col-lg-3">
					<div class="stat-card" style="border-left-color: var(--warn)">
						<div class="d-flex justify-content-between">
							<div>
								<div class="stat-label">Stock bajo</div>
								<div class="stat-value">${r.variantesStockBajo}</div>
								<small class="text-muted-2">variantes con ≤ ${r.umbralStockBajo} unid.</small>
							</div>
							<i class="bi bi-exclamation-triangle stat-icon" style="color: var(--warn)"></i>
						</div>
					</div>
				</div>
			</div>

			<div class="row g-3 mb-4">
				<div class="col-lg-8">
					<div class="card-panel p-3 h-100">
						<h6 class="fw-semibold mb-3">Ventas de los últimos 7 días</h6>
						<div style="height: 260px"><canvas id="graficoSemana"></canvas></div>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="card-panel p-3 h-100">
						<h6 class="fw-semibold mb-3">Más vendidos (30 días)</h6>
						<c:choose>
							<c:when test="${empty r.topProductos}">
								<p class="text-muted-2 small mb-0">Aún no hay ventas registradas.</p>
							</c:when>
							<c:otherwise>
								<c:forEach var="t" items="${r.topProductos}" varStatus="st">
									<div class="d-flex justify-content-between align-items-center py-2 ${st.last ? '' : 'border-bottom'}" style="border-color: var(--border) !important">
										<div class="text-truncate me-2">
											<span class="badge bg-secondary me-1">${st.count}</span>
											<c:out value="${t.producto}" />
										</div>
										<div class="text-end text-nowrap">
											<div class="fw-semibold">${t.unidades} und.</div>
											<small class="text-muted-2">S/ <fmt:formatNumber value="${t.monto}" pattern="#,##0.00" /></small>
										</div>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>

			<div class="row g-3">
				<div class="col-lg-7">
					<div class="card-panel">
						<div class="d-flex justify-content-between align-items-center p-3 pb-2">
							<h6 class="fw-semibold mb-0">Últimas ventas</h6>
							<a href="${ctx}/historial" class="small">Ver historial</a>
						</div>
						<div class="table-responsive">
							<table class="table table-dark-custom table-hover">
								<thead><tr><th>#</th><th>Fecha</th><th>Cliente</th><th>Comprobante</th><th class="text-end">Total</th></tr></thead>
								<tbody>
									<c:forEach var="v" items="${r.ultimasVentas}">
										<tr class="${v.anulada ? 'fila-inactiva' : ''}" style="cursor: pointer" onclick="location.href='${ctx}/historial/detalle?id=${v.id}'">
											<td>${v.id}</td>
											<td class="text-nowrap">${v.fechaTexto}</td>
											<td><c:out value="${empty v.cliente ? 'Cliente varios' : v.cliente.nombreCompleto}" /></td>
											<td class="text-nowrap">${v.comprobante.numeroCompleto}
												<c:if test="${v.anulada}"><span class="badge bg-danger ms-1">Anulada</span></c:if></td>
											<td class="text-end text-nowrap">S/ <fmt:formatNumber value="${v.total}" pattern="#,##0.00" /></td>
										</tr>
									</c:forEach>
									<c:if test="${empty r.ultimasVentas}">
										<tr><td colspan="5" class="text-center text-muted-2 py-4">Sin ventas todavía.
											<a href="${ctx}/ventas/nueva">Registrar la primera</a></td></tr>
									</c:if>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<div class="col-lg-5">
					<div class="card-panel">
						<div class="d-flex justify-content-between align-items-center p-3 pb-2">
							<h6 class="fw-semibold mb-0">Variantes con stock bajo</h6>
							<a href="${ctx}/productos" class="small">Ver productos</a>
						</div>
						<div class="table-responsive">
							<table class="table table-dark-custom">
								<thead><tr><th>Producto</th><th>Variante</th><th class="text-end">Stock</th></tr></thead>
								<tbody>
									<c:forEach var="v" items="${r.stockBajo}">
										<tr>
											<td><c:out value="${v.producto.nombre}" /></td>
											<td class="text-nowrap"><c:out value="${v.descripcionCorta}" /></td>
											<td class="text-end"><span class="badge ${v.stock == 0 ? 'badge-stock-critico' : 'badge-stock-bajo'}">${v.stock}</span></td>
										</tr>
									</c:forEach>
									<c:if test="${empty r.stockBajo}">
										<tr><td colspan="3" class="text-center text-muted-2 py-4">Todo el inventario tiene stock suficiente.</td></tr>
									</c:if>
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
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script>
	(function () {
		const etiquetas = ${etiquetasJson};
		const totales = ${totalesJson};
		new Chart(document.getElementById('graficoSemana'), {
			type: 'bar',
			data: { labels: etiquetas, datasets: [{ label: 'Ventas (S/)', data: totales, backgroundColor: '#e94560', borderRadius: 4, maxBarThickness: 42 }] },
			options: {
				maintainAspectRatio: false,
				plugins: { legend: { display: false }, tooltip: { callbacks: { label: (c) => soles(c.parsed.y) } } },
				scales: {
					x: { ticks: { color: '#a0a0b0' }, grid: { display: false } },
					y: { beginAtZero: true, ticks: { color: '#a0a0b0', callback: (v) => 'S/ ' + v }, grid: { color: 'rgba(255,255,255,0.06)' } }
				}
			}
		});
	})();
</script>
</body>
</html>
