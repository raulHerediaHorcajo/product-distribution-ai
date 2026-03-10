import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import type { KpiExplainResponse, KpiSlug } from '@models/kpi.model';

@Injectable({
  providedIn: 'root',
})
export class KpiAiInsightService {
  private readonly api = environment.aiServiceUrl;
  private readonly http = inject(HttpClient);

  getKpiExplanation(
    kpiSlug: KpiSlug,
    context: Record<string, unknown>
  ): Observable<KpiExplainResponse> {
    return this.http.post<KpiExplainResponse>(`${this.api}/kpi/explain`, {
      kpi_slug: kpiSlug,
      context,
    });
  }
}
