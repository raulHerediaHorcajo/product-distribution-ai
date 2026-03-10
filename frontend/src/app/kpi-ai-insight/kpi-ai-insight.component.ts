import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CdkOverlayOrigin, ConnectedPosition, OverlayModule } from '@angular/cdk/overlay';
import { AiIconComponent } from '@icons/ai-icon/ai-icon.component';
import { KpiSlug } from '@models/kpi.model';
import { KpiAiInsightService } from '@services/kpi-ai-insight.service';

@Component({
  selector: 'app-kpi-ai-insight',
  standalone: true,
  imports: [CommonModule, OverlayModule, AiIconComponent],
  templateUrl: './kpi-ai-insight.component.html',
  styleUrl: './kpi-ai-insight.component.scss',
})
export class KpiAiInsightComponent implements OnInit {
  @Input({ required: true }) origin!: CdkOverlayOrigin;
  @Input({ required: true }) slug!: KpiSlug;
  @Input({ required: true }) context!: Record<string, unknown>;

  @Output() closed = new EventEmitter<void>();

  loading = false;
  text: string | null = null;
  error: string | null = null;

  private readonly kpiAiInsightService = inject(KpiAiInsightService);

  overlayPositions: ConnectedPosition[] = [
    {
      originX: 'start',
      originY: 'top',
      overlayX: 'start',
      overlayY: 'bottom',
      offsetY: -8,
    },
  ];

  ngOnInit(): void {
    this.loadInsight();
  }

  handleClose(): void {
    this.closed.emit();
  }

  private loadInsight(): void {
    this.loading = true;
    this.error = null;
    this.text = null;

    this.kpiAiInsightService.getKpiExplanation(this.slug, this.context).subscribe({
      next: res => {
        this.text = res.explanation;
        this.loading = false;
      },
      error: err => {
        this.error = err?.message || 'Could not load the KPI explanation. Please try again.';
        this.loading = false;
      },
    });
  }
}
