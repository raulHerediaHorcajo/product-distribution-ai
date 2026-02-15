import { Component, Input } from '@angular/core';
import { GlobalMetrics } from '@models/metrics.model';
import { FormatNumberPipe } from '@pipes/format-number.pipe';
import { FormatDistancePipe } from '@pipes/format-distance.pipe';

@Component({
  selector: 'app-dashboard-metrics',
  standalone: true,
  imports: [FormatNumberPipe, FormatDistancePipe],
  templateUrl: './dashboard-metrics.component.html',
  styleUrl: './dashboard-metrics.component.scss',
})
export class DashboardMetricsComponent {
  @Input() metrics: GlobalMetrics | null = null;
}
