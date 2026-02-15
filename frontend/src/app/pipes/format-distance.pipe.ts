import { Pipe, PipeTransform } from '@angular/core';
import { formatNumber } from '@utils/format-number.util';

@Pipe({
  name: 'formatDistance',
  standalone: true,
})
export class FormatDistancePipe implements PipeTransform {
  transform(value: number | null | undefined): string {
    return `${formatNumber(value)} km`;
  }
}
