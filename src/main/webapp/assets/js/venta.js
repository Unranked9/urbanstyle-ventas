/* =========================================================
   Pantalla "Nueva venta"
   - Busca variantes en /api/productos/buscar
   - Calcula totales en el navegador (misma regla que el servidor)
   - Confirma con /api/ventas/calcular y registra con /api/ventas/registrar
   ========================================================= */
(function () {
	const CTX = window.APP_CTX;
	const raiz = document.getElementById('appVenta');
	const IGV = Number(raiz.dataset.igv);
	const LIMITE_DNI = Number(raiz.dataset.limiteDni);

	const estado = {
		carrito: new Map(),   // varianteId -> { variante, cantidad }
		cliente: null,
		registrando: false
	};

	const $ = (id) => document.getElementById(id);

	// ---------------------------------------------------------------
	// Utilidades
	// ---------------------------------------------------------------
	function debounce(fn, ms) {
		let t;
		return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), ms); };
	}

	async function pedir(url, opciones) {
		const r = await fetch(url, opciones);
		let cuerpo = null;
		try { cuerpo = await r.json(); } catch (e) { /* respuesta sin JSON */ }
		if (r.status === 401) {
			await alerta('warning', 'Sesión expirada', 'Vuelve a iniciar sesión.');
			location.href = CTX + '/login';
			throw new Error('401');
		}
		if (!r.ok) {
			throw new Error((cuerpo && cuerpo.mensaje) || 'Error del servidor (' + r.status + ').');
		}
		return cuerpo;
	}

	function centimos(valor) { return Math.round(Number(valor) * 100); }

	function imagenHtml(ruta, clase) {
		const url = urlImagen(ruta);
		return url ? '<img class="thumb ' + clase + '" src="' + esc(url) + '" alt="">'
			: '<span class="thumb ' + clase + '"><i class="bi bi-image"></i></span>';
	}

	// ---------------------------------------------------------------
	// Búsqueda de productos
	// ---------------------------------------------------------------
	async function buscarProductos(texto) {
		const cont = $('resultados');
		try {
			const lista = await pedir(CTX + '/api/productos/buscar?q=' + encodeURIComponent(texto || ''));
			if (!lista.length) {
				cont.innerHTML = '<div class="carrito-vacio"><i class="bi bi-search"></i> Sin resultados para "' + esc(texto) + '".</div>';
				return;
			}
			cont.innerHTML = lista.map((v) => {
				const enCarrito = estado.carrito.get(v.varianteId);
				const disponible = v.stock - (enCarrito ? enCarrito.cantidad : 0);
				const clase = v.stock <= 0 ? 'sin-stock' : '';
				const badge = v.stock <= 0 ? 'badge-stock-critico' : v.stock <= 5 ? 'badge-stock-bajo' : 'badge-stock-ok';
				return '<div class="item-resultado ' + clase + '" data-id="' + v.varianteId + '">'
					+ imagenHtml(v.imagenUrl, '')
					+ '<div class="flex-grow-1" style="min-width:0">'
					+ '<div class="fw-semibold text-truncate">' + esc(v.producto) + '</div>'
					+ '<small class="text-muted-2">' + esc(v.color) + ' / ' + esc(v.talla) + ' · ' + esc(v.categoria)
					+ (v.marca ? ' · ' + esc(v.marca) : '') + '</small></div>'
					+ '<div class="text-end text-nowrap"><div class="fw-semibold">' + soles(v.precio) + '</div>'
					+ '<span class="badge ' + badge + '">Stock ' + v.stock + (enCarrito ? ' (quedan ' + disponible + ')' : '') + '</span></div>'
					+ '</div>';
			}).join('');
			cont._datos = new Map(lista.map((v) => [v.varianteId, v]));
		} catch (e) {
			cont.innerHTML = '<div class="carrito-vacio text-danger">' + esc(e.message) + '</div>';
		}
	}

	$('buscarProducto').addEventListener('input', debounce((e) => buscarProductos(e.target.value.trim()), 300));

	$('resultados').addEventListener('click', (e) => {
		const item = e.target.closest('.item-resultado');
		if (!item || item.classList.contains('sin-stock')) return;
		const v = $('resultados')._datos.get(Number(item.dataset.id));
		agregar(v);
	});

	// ---------------------------------------------------------------
	// Carrito
	// ---------------------------------------------------------------
	function agregar(v) {
		const linea = estado.carrito.get(v.varianteId);
		const actual = linea ? linea.cantidad : 0;
		if (actual + 1 > v.stock) {
			alerta('warning', 'Sin stock suficiente', 'Solo hay ' + v.stock + ' unidad(es) de ' + v.producto + ' ' + v.color + ' / ' + v.talla + '.');
			return;
		}
		estado.carrito.set(v.varianteId, { variante: v, cantidad: actual + 1 });
		pintarCarrito();
		buscarProductos($('buscarProducto').value.trim());
	}

	function pintarCarrito() {
		const cont = $('carrito');
		if (!estado.carrito.size) {
			cont.innerHTML = '<div class="carrito-vacio"><i class="bi bi-cart-x fs-3 d-block mb-1"></i>El carrito está vacío.</div>';
		} else {
			let html = '<div class="table-responsive"><table class="table table-dark-custom align-middle"><tbody>';
			for (const [id, l] of estado.carrito) {
				const v = l.variante;
				const totalLinea = centimos(v.precio) * l.cantidad / 100;
				html += '<tr data-id="' + id + '">'
					+ '<td>' + imagenHtml(v.imagenUrl, 'sm') + '</td>'
					+ '<td><div class="fw-semibold small">' + esc(v.producto) + '</div><small class="text-muted-2">'
					+ esc(v.color) + ' / ' + esc(v.talla) + ' · ' + soles(v.precio) + '</small></td>'
					+ '<td><input type="number" class="form-control form-control-sm form-control-dark input-cantidad" min="1" max="' + v.stock + '" value="' + l.cantidad + '"></td>'
					+ '<td class="text-end text-nowrap fw-semibold">' + soles(totalLinea) + '</td>'
					+ '<td><button class="btn btn-sm btn-outline-danger btn-quitar" title="Quitar"><i class="bi bi-x"></i></button></td>'
					+ '</tr>';
			}
			cont.innerHTML = html + '</tbody></table></div>';
		}
		pintarTotales();
	}

	function calcularTotales() {
		let sub = 0;
		for (const l of estado.carrito.values()) sub += centimos(l.variante.precio) * l.cantidad;
		const igv = Math.round(sub * IGV / 100);
		return { subtotal: sub / 100, igv: igv / 100, total: (sub + igv) / 100 };
	}

	function pintarTotales() {
		const t = calcularTotales();
		$('subtotal').textContent = soles(t.subtotal);
		$('igv').textContent = soles(t.igv);
		$('total').textContent = soles(t.total);
		$('cobrar').disabled = estado.carrito.size === 0;
		actualizarAyudaDocumento(t.total);
	}

	$('carrito').addEventListener('change', (e) => {
		if (!e.target.classList.contains('input-cantidad')) return;
		const id = Number(e.target.closest('tr').dataset.id);
		const l = estado.carrito.get(id);
		let cantidad = parseInt(e.target.value, 10);
		if (!cantidad || cantidad < 1) cantidad = 1;
		if (cantidad > l.variante.stock) {
			alerta('warning', 'Sin stock suficiente', 'Máximo disponible: ' + l.variante.stock + '.');
			cantidad = l.variante.stock;
		}
		l.cantidad = cantidad;
		pintarCarrito();
	});

	$('carrito').addEventListener('click', (e) => {
		const b = e.target.closest('.btn-quitar');
		if (!b) return;
		estado.carrito.delete(Number(b.closest('tr').dataset.id));
		pintarCarrito();
		buscarProductos($('buscarProducto').value.trim());
	});

	$('vaciar').addEventListener('click', () => {
		estado.carrito.clear();
		pintarCarrito();
		buscarProductos($('buscarProducto').value.trim());
	});

	// ---------------------------------------------------------------
	// Cliente
	// ---------------------------------------------------------------
	const lista = $('listaClientes');

	$('buscarCliente').addEventListener('input', debounce(async (e) => {
		const q = e.target.value.trim();
		if (q.length < 2) { lista.classList.add('d-none'); return; }
		try {
			const clientes = await pedir(CTX + '/api/clientes/buscar?q=' + encodeURIComponent(q));
			lista._datos = new Map(clientes.map((c) => [c.id, c]));
			lista.innerHTML = clientes.length
				? clientes.map((c) => '<div class="item-resultado" data-id="' + c.id + '"><i class="bi bi-person"></i><div>'
					+ '<div>' + esc(c.nombre) + (c.mayorista ? ' <span class="badge bg-info text-dark">Mayorista</span>' : '') + '</div>'
					+ '<small class="text-muted-2">' + (c.dni ? 'DNI ' + esc(c.dni) + ' · ' : '') + esc(c.email) + '</small></div></div>').join('')
				: '<div class="p-2 text-muted-2 small">Sin coincidencias. Usa "Nuevo" para registrarlo.</div>';
			lista.classList.remove('d-none');
		} catch (err) {
			lista.innerHTML = '<div class="p-2 text-danger small">' + esc(err.message) + '</div>';
			lista.classList.remove('d-none');
		}
	}, 300));

	lista.addEventListener('click', (e) => {
		const item = e.target.closest('.item-resultado');
		if (item) seleccionarCliente(lista._datos.get(Number(item.dataset.id)));
	});

	document.addEventListener('click', (e) => {
		if (!e.target.closest('#cajaBuscarCliente')) lista.classList.add('d-none');
	});

	function seleccionarCliente(c) {
		estado.cliente = c;
		$('clienteNombre').textContent = c.nombre;
		$('clienteInfo').textContent = (c.dni ? 'DNI ' + c.dni + ' · ' : '') + c.email + (c.mayorista ? ' · Mayorista' : '');
		$('clienteSeleccionado').classList.remove('d-none');
		$('cajaBuscarCliente').classList.add('d-none');
		lista.classList.add('d-none');
		$('buscarCliente').value = '';
		if (tipoComprobante() === 'BOLETA' && c.dni && !$('documento').value) $('documento').value = c.dni;
	}

	$('quitarCliente').addEventListener('click', () => {
		if (estado.cliente && $('documento').value === estado.cliente.dni) $('documento').value = '';
		estado.cliente = null;
		$('clienteSeleccionado').classList.add('d-none');
		$('cajaBuscarCliente').classList.remove('d-none');
	});

	$('formClienteRapido').addEventListener('submit', async (e) => {
		e.preventDefault();
		const form = e.target;
		try {
			const r = await pedir(CTX + '/api/clientes/guardar', {
				method: 'POST',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				body: new URLSearchParams(new FormData(form))
			});
			bootstrap.Modal.getInstance($('modalClienteRapido')).hide();
			form.reset();
			seleccionarCliente(r.cliente);
		} catch (err) {
			alerta('error', 'No se pudo registrar', err.message);
		}
	});

	// ---------------------------------------------------------------
	// Comprobante
	// ---------------------------------------------------------------
	function tipoComprobante() {
		return document.querySelector('input[name="tipoComprobante"]:checked').value;
	}

	function actualizarAyudaDocumento(total) {
		const doc = $('documento');
		if (tipoComprobante() === 'FACTURA') {
			doc.maxLength = 11;
			doc.placeholder = 'RUC del cliente (11 dígitos)';
			$('ayudaDocumento').textContent = 'La factura requiere RUC.';
		} else {
			doc.maxLength = 8;
			doc.placeholder = 'DNI del cliente' + (total > LIMITE_DNI ? '' : ' (opcional)');
			$('ayudaDocumento').textContent = total > LIMITE_DNI
				? 'Este total supera S/ ' + LIMITE_DNI.toFixed(2) + ': el DNI es obligatorio.'
				: 'Obligatorio si el total supera S/ ' + LIMITE_DNI.toFixed(2) + '.';
		}
	}

	document.querySelectorAll('input[name="tipoComprobante"]').forEach((r) => r.addEventListener('change', () => {
		$('documento').value = (tipoComprobante() === 'BOLETA' && estado.cliente && estado.cliente.dni) ? estado.cliente.dni : '';
		pintarTotales();
	}));

	// ---------------------------------------------------------------
	// Registro
	// ---------------------------------------------------------------
	function solicitud() {
		return {
			clienteId: estado.cliente ? estado.cliente.id : null,
			tipoComprobante: tipoComprobante(),
			documento: $('documento').value.trim() || null,
			items: [...estado.carrito.entries()].map(([id, l]) => ({ varianteId: id, cantidad: l.cantidad }))
		};
	}

	$('cobrar').addEventListener('click', async () => {
		if (estado.registrando || !estado.carrito.size) return;
		const datos = solicitud();
		try {
			// 1) El servidor recalcula con precios y stock actuales.
			const calc = await pedir(CTX + '/api/ventas/calcular', {
				method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(datos)
			});
			if (!calc.ok) {
				alerta('warning', 'Revisa el carrito', calc.mensaje);
				return;
			}
			const confirmacion = await Swal.fire({
				...SWAL_TEMA, icon: 'question', title: '¿Registrar la venta?',
				html: '<div class="text-start">'
					+ '<div>' + (datos.tipoComprobante === 'FACTURA' ? 'Factura' : 'Boleta')
					+ (estado.cliente ? ' para <b>' + esc(estado.cliente.nombre) + '</b>' : ' · cliente varios') + '</div>'
					+ '<div>Subtotal: ' + soles(calc.subtotal) + '</div><div>IGV: ' + soles(calc.igv) + '</div>'
					+ '<div class="fs-5 fw-bold mt-1">Total: ' + soles(calc.total) + '</div></div>',
				showCancelButton: true, confirmButtonText: 'Registrar', cancelButtonText: 'Volver'
			});
			if (!confirmacion.isConfirmed) return;

			// 2) Registro en una sola transacción.
			estado.registrando = true;
			$('cobrar').disabled = true;
			const r = await pedir(CTX + '/api/ventas/registrar', {
				method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(datos)
			});
			const fin = await Swal.fire({
				...SWAL_TEMA, icon: 'success', title: 'Venta registrada',
				html: esc(r.mensaje) + '<div class="fs-4 fw-bold mt-2">' + soles(r.total) + '</div>',
				showCancelButton: true, confirmButtonText: '<i class="bi bi-printer"></i> Ver comprobante', cancelButtonText: 'Nueva venta'
			});
			if (fin.isConfirmed) window.open(CTX + '/ventas/comprobante?id=' + r.ventaId, '_blank');
			reiniciar();
		} catch (err) {
			if (err.message !== '401') alerta('error', 'No se pudo registrar', err.message);
		} finally {
			estado.registrando = false;
			$('cobrar').disabled = estado.carrito.size === 0;
		}
	});

	function reiniciar() {
		estado.carrito.clear();
		if (estado.cliente) $('quitarCliente').click();
		$('documento').value = '';
		$('tipoBoleta').checked = true;
		pintarCarrito();
		buscarProductos($('buscarProducto').value.trim());
	}

	// Inicio
	pintarCarrito();
	buscarProductos('');
})();
