import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { BaseChartDirective, provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { ChartSpec, ChatChartData, ChatChartOptions } from '@models/chat.model';
import { CHART_COLORS } from '@models/metrics.model';

@Component({
  selector: 'app-chat-chart',
  standalone: true,
  imports: [BaseChartDirective],
  providers: [provideCharts(withDefaultRegisterables())],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (chartData) {
      <div class="chat-chart">
        @if (spec.title) {
          <h4 class="chat-chart-title">{{ spec.title }}</h4>
        }
        <div class="chat-chart-canvas-wrap">
          <canvas
            baseChart
            [data]="chartData"
            [options]="chartOptions"
            [type]="spec.type"
          >
          </canvas>
        </div>
      </div>
    }
  `,
  styles: [
    `
      .chat-chart {
        margin-top: 0.75rem;
        border-radius: 8px;
        overflow: hidden;
      }
      .chat-chart-title {
        margin: 0 0 0.5rem 0;
        font-size: 0.9rem;
        font-weight: 600;
      }
      .chat-chart-canvas-wrap {
        height: 220px;
        position: relative;
      }
    `,
  ],
})
export class ChatChartComponent implements OnChanges {
  @Input({ required: true }) spec!: ChartSpec;

  chartData: ChatChartData | null = null;
  chartOptions!: ChatChartOptions;

  ngOnChanges(changes: SimpleChanges): void {
    if ('spec' in changes) {
      this.buildChart();
    }
  }

  private buildChart(): void {
    if (!this.spec.labels?.length || !this.spec.datasets?.length) {
      this.chartData = null;
      return;
    }

    const base: ChatChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: true, position: 'top' },
      },
    };

    const isPieOrDoughnut = this.spec.type === 'pie' || this.spec.type === 'doughnut';
    this.chartOptions = isPieOrDoughnut
      ? {
          ...base,
          scales: { x: { display: false }, y: { display: false } },
        }
      : {
          ...base,
          scales: { x: { beginAtZero: true }, y: { beginAtZero: true } },
        };

    const colors = [...CHART_COLORS];
    this.chartData = {
      labels: this.spec.labels,
      datasets: this.spec.datasets.map(ds => ({
        label: ds.label,
        data: ds.data,
        backgroundColor:
          this.spec.type === 'bar' || this.spec.type === 'pie' || this.spec.type === 'doughnut'
            ? colors.slice(0, this.spec.labels.length)
            : undefined,
      })),
    };
  }
}
