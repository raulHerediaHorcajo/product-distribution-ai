export function formatNumber(value: number | null | undefined): string {
  if (value == null) return '';

  const formatted = value.toFixed(1);
  const withoutZero = formatted.endsWith('.0') ? formatted.slice(0, -2) : formatted;
  const numValue = Number.parseFloat(withoutZero);

  return numValue.toLocaleString('es-ES', {
    //en-US'
    useGrouping: true,
    minimumFractionDigits: withoutZero.includes('.') ? 1 : 0,
    maximumFractionDigits: 1,
  });
}
