import { Pipe, PipeTransform } from '@angular/core';
import { formatNumber } from '@utils/format-number.util';

@Pipe({
  name: 'formatNumber',
  standalone: true,
})
export class FormatNumberPipe implements PipeTransform {
  transform(value: number | null | undefined): string {
    return formatNumber(value);
  }
}
