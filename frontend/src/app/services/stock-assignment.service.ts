import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StockAssignment } from '@models/stock-assignment.model';
import { DashboardFilters } from '../dashboard-filters/dashboard-filters.component';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class StockAssignmentService {
  private readonly api = `${environment.apiUrl}/stock-assignments`;
  private readonly http = inject(HttpClient);

  getAll(filters?: DashboardFilters): Observable<StockAssignment[]> {
    let params = new HttpParams();

    if (filters?.storeId) {
      params = params.set('storeId', filters.storeId);
    }
    if (filters?.warehouseId) {
      params = params.set('warehouseId', filters.warehouseId);
    }
    if (filters?.productId) {
      params = params.set('productId', filters.productId);
    }

    return this.http.get<StockAssignment[]>(this.api, { params });
  }
}
