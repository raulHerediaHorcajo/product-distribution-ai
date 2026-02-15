import { Component, Input } from '@angular/core';
import { BaseChartDirective, provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { TooltipItem } from 'chart.js';
import { DetailedMetrics, METRICS_COLORS, CHART_COLORS } from '@models/metrics.model';
import { FormatNumberPipe } from '@pipes/format-number.pipe';
import { FormatPercentagePipe } from '@pipes/format-percentage.pipe';
import { FormatDistancePipe } from '@pipes/format-distance.pipe';
import { formatNumber } from '@utils/format-number.util';

@Component({
  selector: 'app-dashboard-metrics-detail',
  standalone: true,
  imports: [BaseChartDirective, FormatNumberPipe, FormatPercentagePipe, FormatDistancePipe],
  providers: [provideCharts(withDefaultRegisterables())],
  templateUrl: './dashboard-metrics-detail.component.html',
  styleUrl: './dashboard-metrics-detail.component.scss',
})
export class DashboardMetricsDetailComponent {
  @Input() metrics: DetailedMetrics | null = null;

  public chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
    },
  };

  public horizontalChartOptions = {
    ...this.chartOptions,
    indexAxis: 'y' as const,
    scales: {
      x: {
        beginAtZero: true,
      },
    },
    plugins: {
      ...this.chartOptions.plugins,
      tooltip: {
        callbacks: {
          label: (context: TooltipItem<'bar'>) => {
            const index = context.dataIndex;
            const warehouse = this.metrics?.unitsByWarehouse[index];
            if (warehouse) {
              return [
                `${formatNumber(warehouse.totalUnits)} (${formatNumber(warehouse.percentage)}%)`,
                `Avg Distance: ${formatNumber(warehouse.avgDistance)} km`,
              ];
            }
            return context.label || '';
          },
        },
      },
    },
  };

  public verticalChartOptions = {
    ...this.chartOptions,
    scales: {
      y: {
        beginAtZero: true,
      },
    },
    plugins: {
      ...this.chartOptions.plugins,
      tooltip: {
        callbacks: {
          label: (context: TooltipItem<'bar'>) => {
            const index = context.dataIndex;
            const product = this.metrics?.topProducts?.[index];
            if (product) {
              return `${formatNumber(product.totalQuantity)} units`;
            }
            return context.label || '';
          },
        },
      },
    },
  };

  get warehouseChartData() {
    if (!this.metrics?.unitsByWarehouse) return null;

    return {
      labels: this.metrics.unitsByWarehouse.map(w => w.warehouseId),
      datasets: [
        {
          data: this.metrics.unitsByWarehouse.map(w => w.totalUnits),
          backgroundColor: CHART_COLORS.slice(0, this.metrics.unitsByWarehouse.length),
          borderWidth: 0,
        },
      ],
    };
  }

  get topProductsChartData() {
    if (!this.metrics?.topProducts) return null;

    return {
      labels: this.metrics.topProducts.map(p => p.productId),
      datasets: [
        {
          data: this.metrics.topProducts.map(p => p.totalQuantity),
          backgroundColor: CHART_COLORS.slice(0, this.metrics.topProducts.length),
          borderWidth: 0,
        },
      ],
    };
  }

  getEfficiencyColor(score: number): string {
    if (score >= 80) return METRICS_COLORS.secondary;
    if (score >= 60) return METRICS_COLORS.accent;
    return METRICS_COLORS.danger;
  }

  getFulfillmentColor(rate: number): string {
    if (rate >= 90) return METRICS_COLORS.secondary;
    if (rate >= 70) return METRICS_COLORS.accent;
    return METRICS_COLORS.danger;
  }

  getCapacityColor(percentage: number): string {
    if (percentage >= 90) return METRICS_COLORS.danger;
    if (percentage >= 70) return METRICS_COLORS.accent;
    if (percentage >= 35) return METRICS_COLORS.secondary;
    return METRICS_COLORS.accent;
  }

  get capacityChartData() {
    if (!this.metrics?.capacityUtilization) return null;

    const percentage = this.metrics.capacityUtilization.percentage;

    return {
      labels: ['Used', 'Available'],
      datasets: [
        {
          data: [percentage, 100 - percentage],
          backgroundColor: [this.getCapacityColor(percentage), '#E5E7EB'],
          borderWidth: 0,
        },
      ],
    };
  }

  get capacityChartOptions() {
    if (!this.metrics?.capacityUtilization) return {};

    return {
      responsive: true,
      maintainAspectRatio: false,
      cutout: '70%',
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          enabled: false,
        },
      },
    };
  }
}
