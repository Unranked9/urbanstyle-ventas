/* Formulario de producto: filas dinámicas de variantes */
(function () {
	const cuerpo = document.getElementById('cuerpoVariantes');
	const plantilla = document.getElementById('plantillaVariante');
	const avisoVacio = document.getElementById('sinVariantes');
	let contador = 0;

	function actualizarAviso() {
		avisoVacio.classList.toggle('d-none', cuerpo.children.length > 0);
	}

	document.getElementById('btnAgregarVariante').addEventListener('click', () => {
		const fila = plantilla.content.firstElementChild.cloneNode(true);
		const clave = 'n' + (contador++);
		fila.querySelector('input[name="varFila"]').value = clave;
		fila.querySelector('.campo-imagen').name = 'varImagen_' + clave;
		cuerpo.appendChild(fila);
		actualizarAviso();
		fila.querySelector('select[name="varColor"]').focus();
	});

	cuerpo.addEventListener('click', (e) => {
		const btnEliminar = e.target.closest('.btn-eliminar-fila');
		if (btnEliminar) {
			btnEliminar.closest('tr').remove();
			actualizarAviso();
			return;
		}
		const btnQuitar = e.target.closest('.btn-quitar-imagen');
		if (btnQuitar) {
			const fila = btnQuitar.closest('tr');
			fila.querySelector('.campo-quitar').value = '1';
			const vista = fila.querySelector('.vista-previa');
			vista.outerHTML = '<span class="thumb vista-previa"><i class="bi bi-image"></i></span>';
			btnQuitar.remove();
		}
	});

	cuerpo.addEventListener('change', (e) => {
		const fila = e.target.closest('tr');
		if (e.target.classList.contains('switch-activo')) {
			fila.querySelector('.campo-activo').value = e.target.checked ? '1' : '0';
			fila.classList.toggle('inactiva', !e.target.checked);
		}
		if (e.target.classList.contains('campo-imagen') && e.target.files.length) {
			const archivo = e.target.files[0];
			if (archivo.size > 3 * 1024 * 1024) {
				alerta('warning', 'Imagen muy grande', 'El máximo es 3 MB por imagen.');
				e.target.value = '';
				return;
			}
			const url = URL.createObjectURL(archivo);
			fila.querySelector('.vista-previa').outerHTML = '<img class="thumb vista-previa" src="' + url + '" alt="">';
		}
	});

	// Validación: combinaciones color + talla no repetidas.
	document.getElementById('formProducto').addEventListener('submit', (e) => {
		const vistas = new Set();
		for (const fila of cuerpo.querySelectorAll('tr')) {
			const clave = fila.querySelector('select[name="varColor"]').value + '-' + fila.querySelector('select[name="varTalla"]').value;
			if (vistas.has(clave)) {
				e.preventDefault();
				alerta('warning', 'Variantes repetidas', 'Hay dos filas con el mismo color y talla.');
				return;
			}
			vistas.add(clave);
		}
	});
})();
