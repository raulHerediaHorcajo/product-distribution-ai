import { Pipe, PipeTransform } from '@angular/core';
import { formatNumber } from '@utils/format-number.util';

@Pipe({
  name: 'formatPercentage',
  standalone: true,
})
export class FormatPercentagePipe implements PipeTransform {
  transform(value: number | null | undefined): string {
    return `${formatNumber(value)}%`;
  }
}
