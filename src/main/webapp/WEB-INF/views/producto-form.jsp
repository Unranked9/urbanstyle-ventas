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
			<c:set var="p" value="${producto}" />

			<form method="post" action="${ctx}/productos/guardar" enctype="multipart/form-data" id="formProducto">
				<input type="hidden" name="id" value="${p.id}">

				<div class="row g-3">
					<div class="col-lg-4">
						<div class="card-panel p-3">
							<h6 class="fw-semibold mb-3">Datos del producto</h6>
							<div class="mb-3">
								<label class="form-label">Nombre *</label>
								<input class="form-control form-control-dark" name="nombre" maxlength="100" required value="<c:out value='${p.nombre}' />">
							</div>
							<div class="mb-3">
								<label class="form-label">Descripción</label>
								<textarea class="form-control form-control-dark" name="descripcion" maxlength="255" rows="3"><c:out value="${p.descripcion}" /></textarea>
							</div>
							<div class="mb-3">
								<label class="form-label">Precio (S/) *</label>
								<input class="form-control form-control-dark" name="precio" type="number" min="0.01" step="0.01" required value="${p.precio}">
								<small class="text-muted-2">Precio sin IGV. Es el mismo para todas las variantes.</small>
							</div>
							<div class="mb-3">
								<label class="form-label">Categoría *</label>
								<select class="form-select form-select-dark" name="categoriaId" required>
									<option value="">Seleccione...</option>
									<c:forEach var="cat" items="${categorias}">
										<option value="${cat.id}" ${cat.id == p.categoria.id ? 'selected' : ''}><c:out value="${cat.nombre}" /></option>
									</c:forEach>
								</select>
							</div>
							<div class="mb-3">
								<label class="form-label">Marca</label>
								<select class="form-select form-select-dark" name="marcaId">
									<option value="">Sin marca</option>
									<c:forEach var="m" items="${marcas}">
										<option value="${m.id}" ${m.id == p.marca.id ? 'selected' : ''}><c:out value="${m.nombre}" /></option>
									</c:forEach>
								</select>
							</div>
							<div class="form-check form-switch">
								<input class="form-check-input" type="checkbox" role="switch" id="activo" name="activo" ${empty p or p.activo ? 'checked' : ''}>
								<label class="form-check-label" for="activo">Producto activo (visible en ventas)</label>
							</div>
						</div>
					</div>

					<div class="col-lg-8">
						<div class="card-panel p-3">
							<div class="d-flex justify-content-between align-items-center mb-3">
								<div>
									<h6 class="fw-semibold mb-0">Variantes (color + talla)</h6>
									<small class="text-muted-2">Cada combinación tiene su propio stock e imagen. Las variantes usadas en ventas no se eliminan: se desactivan.</small>
								</div>
								<button type="button" class="btn btn-sm btn-accent text-nowrap" id="btnAgregarVariante"><i class="bi bi-plus-lg"></i> Agregar</button>
							</div>
							<div class="table-responsive">
								<table class="table table-dark-custom align-middle">
									<thead><tr><th>Imagen</th><th>Color</th><th>Talla</th><th style="width: 110px">Stock</th><th>Activa</th><th></th></tr></thead>
									<tbody id="cuerpoVariantes">
										<c:forEach var="v" items="${p.variantes}" varStatus="st">
											<tr class="fila-variante ${v.activo ? '' : 'inactiva'}">
												<td>
													<input type="hidden" name="varFila" value="e${st.index}">
													<input type="hidden" name="varId" value="${v.id}">
													<input type="hidden" name="varQuitar" value="0" class="campo-quitar">
													<input type="hidden" name="varActivo" value="${v.activo ? 1 : 0}" class="campo-activo">
													<div class="d-flex align-items-center gap-2">
														<c:choose>
															<c:when test="${not empty v.imagenUrl}"><img class="thumb vista-previa" src="${ctx}/${v.imagenUrl}" alt=""></c:when>
															<c:otherwise><span class="thumb vista-previa"><i class="bi bi-image"></i></span></c:otherwise>
														</c:choose>
														<input type="file" name="varImagen_e${st.index}" accept="image/jpeg,image/png,image/webp,image/gif" class="form-control form-control-sm form-control-dark campo-imagen" style="max-width: 190px">
														<c:if test="${not empty v.imagenUrl}">
															<button type="button" class="btn btn-sm btn-outline-danger btn-quitar-imagen" title="Quitar imagen"><i class="bi bi-x"></i></button>
														</c:if>
													</div>
												</td>
												<td>
													<select class="form-select form-select-sm form-select-dark" name="varColor" required>
														<c:forEach var="col" items="${colores}">
															<option value="${col.id}" ${col.id == v.color.id ? 'selected' : ''}><c:out value="${col.nombre}" /></option>
														</c:forEach>
													</select>
												</td>
												<td>
													<select class="form-select form-select-sm form-select-dark" name="varTalla" required>
														<c:forEach var="t" items="${tallas}">
															<option value="${t.id}" ${t.id == v.talla.id ? 'selected' : ''}><c:out value="${t.nombre}" /></option>
														</c:forEach>
													</select>
												</td>
												<td><input class="form-control form-control-sm form-control-dark" type="number" name="varStock" min="0" step="1" required value="${v.stock}"></td>
												<td>
													<div class="form-check form-switch">
														<input class="form-check-input switch-activo" type="checkbox" ${v.activo ? 'checked' : ''}>
													</div>
												</td>
												<td class="text-muted-2 small">#${v.id}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
							<p id="sinVariantes" class="text-muted-2 small mb-0 ${empty p.variantes ? '' : 'd-none'}">
								Este producto aún no tiene variantes. Agrega al menos una para poder venderlo.
							</p>
						</div>

						<div class="d-flex justify-content-end gap-2 mt-3">
							<a class="btn btn-outline-light" href="${ctx}/productos">Cancelar</a>
							<button class="btn btn-accent" type="submit"><i class="bi bi-save me-1"></i> Guardar producto</button>
						</div>
					</div>
				</div>
			</form>

			<%-- Plantilla de una fila nueva (la clona producto-form.js) --%>
			<template id="plantillaVariante">
				<tr class="fila-variante">
					<td>
						<input type="hidden" name="varFila" value="">
						<input type="hidden" name="varId" value="">
						<input type="hidden" name="varQuitar" value="0" class="campo-quitar">
						<input type="hidden" name="varActivo" value="1" class="campo-activo">
						<div class="d-flex align-items-center gap-2">
							<span class="thumb vista-previa"><i class="bi bi-image"></i></span>
							<input type="file" accept="image/jpeg,image/png,image/webp,image/gif" class="form-control form-control-sm form-control-dark campo-imagen" style="max-width: 190px">
						</div>
					</td>
					<td>
						<select class="form-select form-select-sm form-select-dark" name="varColor" required>
							<option value="">Color...</option>
							<c:forEach var="col" items="${colores}"><option value="${col.id}"><c:out value="${col.nombre}" /></option></c:forEach>
						</select>
					</td>
					<td>
						<select class="form-select form-select-sm form-select-dark" name="varTalla" required>
							<option value="">Talla...</option>
							<c:forEach var="t" items="${tallas}"><option value="${t.id}"><c:out value="${t.nombre}" /></option></c:forEach>
						</select>
					</td>
					<td><input class="form-control form-control-sm form-control-dark" type="number" name="varStock" min="0" step="1" required value="0"></td>
					<td><div class="form-check form-switch"><input class="form-check-input switch-activo" type="checkbox" checked></div></td>
					<td><button type="button" class="btn btn-sm btn-outline-danger btn-eliminar-fila" title="Quitar fila"><i class="bi bi-trash"></i></button></td>
				</tr>
			</template>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
<script src="${ctx}/assets/js/producto-form.js"></script>
</body>
</html>
