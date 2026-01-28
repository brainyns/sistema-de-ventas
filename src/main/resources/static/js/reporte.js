(function(){
  
  // ======================================================
  // 1. Pie chart (distribución por tipo)
  // ======================================================
  const pieCtx = document.getElementById('pieChart');
  if (pieCtx) {
    // Usamos las variables globales definidas por Thymeleaf
    new Chart(pieCtx, {
      type: 'pie',
      data: {
        labels: typeLabels,
        datasets: [{
          data: typeData,
          // colores por defecto (Chart.js los elige)
          hoverOffset: 6
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom' },
          tooltip: { enabled: true }
        }
      }
    });
  }

  // ======================================================
  // 2. Bar chart (estado del stock)
  // ======================================================
  const barCtx = document.getElementById('barChart');
  if (barCtx) {
    // Usamos las variables globales definidas por Thymeleaf
    new Chart(barCtx, {
      type: 'bar',
      data: {
        labels: stateLabels,
        datasets: [{
          label: 'Cantidad de productos',
          data: stateData,
          // no especificamos colores; Chart.js usará paleta por defecto
          // Opcional: podrías definir colores personalizados aquí para Bajo (rojo), Normal (verde), Alto (amarillo)
          backgroundColor: [
              'rgba(220, 53, 69, 0.8)',   // Rojo para 'Bajo'
              'rgba(40, 167, 69, 0.8)',  // Verde para 'Normal'
              'rgba(255, 193, 7, 0.8)'   // Amarillo para 'Alto'
          ],
          borderColor: [
              'rgba(220, 53, 69, 1)',
              'rgba(40, 167, 69, 1)',
              'rgba(255, 193, 7, 1)'
          ],
          borderWidth: 1
        }]
      },
      options: {
        indexAxis: 'y', // Hace el gráfico horizontal
        responsive: true,
        plugins: {
          legend: { display: false },
          tooltip: { enabled: true }
        },
        scales: {
          x: { beginAtZero: true, ticks: { precision:0 } }
        }
      }
    });
  }
})();