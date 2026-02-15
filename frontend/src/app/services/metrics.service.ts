import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GlobalMetrics, DetailedMetrics, MetricsCriteria } from '@models/metrics.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MetricsService {
  private readonly api = `${environment.apiUrl}/metrics`;
  private readonly http = inject(HttpClient);

  getGlobalMetrics(): Observable<GlobalMetrics> {
    return this.http.get<GlobalMetrics>(`${this.api}/global`);
  }

  getDetailedMetrics(criteria: MetricsCriteria): Observable<DetailedMetrics> {
    let params = new HttpParams();

    if (criteria.warehouseId) {
      params = params.set('warehouseId', criteria.warehouseId);
    }

    if (criteria.storeId) {
      params = params.set('storeId', criteria.storeId);
    }

    if (criteria.productId) {
      params = params.set('productId', criteria.productId);
    }

    return this.http.get<DetailedMetrics>(`${this.api}/detailed`, { params });
  }
}
