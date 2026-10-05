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

			<form class="card-panel p-3 mb-3" method="get" action="${ctx}/productos">
				<div class="row g-2 align-items-end">
					<div class="col-md-4">
						<label class="form-label">Buscar</label>
						<input class="form-control form-control-dark" name="q" value="<c:out value='${fq}' />" placeholder="Nombre o descripción">
					</div>
					<div class="col-6 col-md-2">
						<label class="form-label">Categoría</label>
						<select class="form-select form-select-dark" name="categoria">
							<option value="">Todas</option>
							<c:forEach var="cat" items="${categorias}">
								<option value="${cat.id}" ${cat.id == fCategoria ? 'selected' : ''}><c:out value="${cat.nombre}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="col-6 col-md-2">
						<label class="form-label">Marca</label>
						<select class="form-select form-select-dark" name="marca">
							<option value="">Todas</option>
							<c:forEach var="m" items="${marcas}">
								<option value="${m.id}" ${m.id == fMarca ? 'selected' : ''}><c:out value="${m.nombre}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="col-6 col-md-2">
						<label class="form-label">Estado</label>
						<select class="form-select form-select-dark" name="estado">
							<option value="activos" ${fEstado == 'activos' ? 'selected' : ''}>Activos</option>
							<option value="inactivos" ${fEstado == 'inactivos' ? 'selected' : ''}>Inactivos</option>
							<option value="todos" ${fEstado == 'todos' ? 'selected' : ''}>Todos</option>
						</select>
					</div>
					<div class="col-6 col-md-2 d-flex gap-2">
						<button class="btn btn-outline-light flex-fill" type="submit"><i class="bi bi-funnel"></i> Filtrar</button>
						<c:if test="${us.gestor}">
							<a class="btn btn-accent flex-fill" href="${ctx}/productos/nuevo" title="Nuevo producto"><i class="bi bi-plus-lg"></i></a>
						</c:if>
					</div>
				</div>
			</form>

			<div class="card-panel">
				<div class="table-responsive">
					<table class="table table-dark-custom table-hover">
						<thead>
							<tr>
								<th>Producto</th><th>Categoría</th><th>Marca</th>
								<th class="text-end">Precio</th><th>Variantes</th><th class="text-end">Stock</th><th>Estado</th>
								<c:if test="${us.gestor}"><th class="text-end">Acciones</th></c:if>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="p" items="${productos}">
								<tr class="${p.activo ? '' : 'fila-inactiva'}">
									<td>
										<div class="d-flex align-items-center gap-2">
											<c:choose>
												<c:when test="${not empty p.imagenPrincipal}"><img class="thumb" src="${ctx}/${p.imagenPrincipal}" alt=""></c:when>
												<c:otherwise><span class="thumb"><i class="bi bi-image"></i></span></c:otherwise>
											</c:choose>
											<div>
												<div class="fw-semibold"><c:out value="${p.nombre}" /></div>
												<small class="text-muted-2"><c:out value="${p.descripcion}" /></small>
											</div>
										</div>
									</td>
									<td><c:out value="${p.categoria.nombre}" /></td>
									<td><c:out value="${empty p.marca ? '—' : p.marca.nombre}" /></td>
									<td class="text-end text-nowrap">S/ <fmt:formatNumber value="${p.precio}" pattern="#,##0.00" /></td>
									<td>
										<c:forEach var="v" items="${p.variantes}">
											<c:if test="${v.activo}">
												<span class="badge ${v.stock == 0 ? 'badge-stock-critico' : v.stock <= umbralStock ? 'badge-stock-bajo' : 'bg-secondary'} fw-normal mb-1"
													title="Stock: ${v.stock}"><c:out value="${v.descripcionCorta}" /> · ${v.stock}</span>
											</c:if>
										</c:forEach>
										<c:if test="${p.variantesActivas == 0}"><small class="text-muted-2">Sin variantes</small></c:if>
									</td>
									<td class="text-end fw-semibold">${p.stockTotal}</td>
									<td>
										<span class="badge ${p.activo ? 'badge-stock-ok' : 'bg-secondary'}">${p.activo ? 'Activo' : 'Inactivo'}</span>
									</td>
									<c:if test="${us.gestor}">
										<td class="text-end text-nowrap">
											<a class="btn btn-sm btn-outline-light" href="${ctx}/productos/editar?id=${p.id}" title="Editar"><i class="bi bi-pencil"></i></a>
											<form class="d-inline" method="post" action="${ctx}/productos/estado"
												data-confirmar="${p.activo ? 'El producto dejará de aparecer en la pantalla de venta.' : '¿Volver a activar este producto?'}">
												<input type="hidden" name="id" value="${p.id}">
												<input type="hidden" name="activo" value="${p.activo ? 0 : 1}">
												<button class="btn btn-sm ${p.activo ? 'btn-outline-danger' : 'btn-outline-success'}" title="${p.activo ? 'Desactivar' : 'Activar'}">
													<i class="bi ${p.activo ? 'bi-eye-slash' : 'bi-eye'}"></i>
												</button>
											</form>
										</td>
									</c:if>
								</tr>
							</c:forEach>
							<c:if test="${empty productos}">
								<tr><td colspan="8" class="text-center text-muted-2 py-5">No se encontraron productos con esos filtros.</td></tr>
							</c:if>
						</tbody>
					</table>
				</div>
			</div>
			<p class="text-muted-2 small mt-2 mb-0">${fn:length(productos)} producto(s).</p>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
</body>
</html>
