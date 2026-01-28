/**
 * Utilidades de Formato para Colombia
 * - Símbolo de moneda: $
 * - Miles: punto (.)
 * - Decimales: coma (,)
 * Ejemplo: $661.640,00
 */

/**
 * Formatea un número según el estándar colombiano
 * @param {number} number - Número a formatear
 * @param {number} decimales - Cantidad de decimales (default: 2)
 * @returns {string} Número formateado con $ al inicio
 */
function formatCurrencycolombian(number, decimales = 2) {
    if (!number && number !== 0) return '$0,00';
    
    // Asegurar que es un número
    number = parseFloat(number);
    
    // Fijar decimales
    const parts = number.toFixed(decimales).split('.');
    const integerPart = parts[0];
    const decimalPart = parts[1];
    
    // Separar miles con punto
    const integerFormatted = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    
    // Combinar con coma para decimales
    return `$${integerFormatted},${decimalPart}`;
}

/**
 * Formatea solo número sin símbolo de moneda
 * @param {number} number - Número a formatear
 * @param {number} decimales - Cantidad de decimales (default: 2)
 * @returns {string} Número formateado: 661.640,00
 */
function formatNumberColombian(number, decimales = 2) {
    if (!number && number !== 0) return '0,00';
    
    number = parseFloat(number);
    const parts = number.toFixed(decimales).split('.');
    const integerPart = parts[0];
    const decimalPart = parts[1];
    
    const integerFormatted = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    
    return `${integerFormatted},${decimalPart}`;
}

/**
 * Formatea un número sin decimales
 * @param {number} number - Número a formatear
 * @returns {string} Número formateado: 661.640
 */
function formatNumberColombianNoDecimals(number) {
    if (!number && number !== 0) return '0';
    
    number = parseInt(number);
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

/**
 * Parsea una cadena formateada en estilo colombiano a número (float).
 * Acepta entradas como: "$661.640,00" o "661.640,00" o "661640,00"
 * Devuelve un Number (ej. 661640.00)
 */
function parseColombianNumber(formatted) {
    if (formatted === null || formatted === undefined) return NaN;
    // Eliminar símbolo de moneda, espacios y cualquier letra
    let s = String(formatted).replace(/[^0-9\-,\.]/g, '');
    // Remover puntos (separador de miles) y reemplazar coma decimal por punto
    s = s.replace(/\./g, '').replace(/,/g, '.');
    const n = parseFloat(s);
    return isNaN(n) ? NaN : n;
}
